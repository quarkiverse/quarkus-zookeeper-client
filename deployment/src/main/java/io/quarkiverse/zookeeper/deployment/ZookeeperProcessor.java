package io.quarkiverse.zookeeper.deployment;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;

import org.apache.zookeeper.Login;
import org.apache.zookeeper.ZooKeeper;
import org.jboss.jandex.DotName;

import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkiverse.zookeeper.deployment.capabilities.GroupMembershipSupplier;
import io.quarkiverse.zookeeper.deployment.config.ZookeeperBuildTimeConfiguration;
import io.quarkiverse.zookeeper.health.infrastructure.ZookeeperReadyCheck;
import io.quarkiverse.zookeeper.membership.infrastructure.GroupMembershipBean;
import io.quarkiverse.zookeeper.membership.infrastructure.MembershipStatusBean;
import io.quarkiverse.zookeeper.membership.infrastructure.MembershipStatusWatcherHolder;
import io.quarkiverse.zookeeper.membership.infrastructure.ReactiveMembershipStatusBean;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AutoInjectAnnotationBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RemovedResourceBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageSecurityProviderBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

class ZookeeperProcessor {

    private static final String FEATURE = ZookeeperConfiguration.EXTENSION_NAME;
    private static final String ZK_CLIENT_ANNOTATION_NAME = "io.quarkiverse.zookeeper.deployment.ZKClient";

    @BuildStep
    FeatureBuildItem zookeeper() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    HealthBuildItem addLiveCheck(Capabilities capabilities, ZookeeperBuildTimeConfiguration config) {
        if (capabilities.isPresent(Capability.SMALLRYE_HEALTH)) {
            return new HealthBuildItem(ZookeeperReadyCheck.class.getName(),
                    config.healthEnabled);
        } else {
            return null;
        }
    }

    @BuildStep(onlyIf = GroupMembershipSupplier.class)
    HealthBuildItem addGroupMembershipReadiness(ZookeeperBuildTimeConfiguration config) {
        // TODO
        if (config.membership.enable) {
            return null;
        } else {
            return null;
        }
    }

    @BuildStep
    RemovedResourceBuildItem removeSlf4jBinding() {
        return new RemovedResourceBuildItem(new AppArtifactKey("ch.qos.logback", "logback-classic", null, "jar"),
                Collections.singleton("org/slf4j/impl/StaticLoggerBinder.class"));
    }

    @BuildStep
    ReflectiveClassBuildItem addReflectiveZKClasses() {
        return new ReflectiveClassBuildItem(true, false, false,
                org.apache.zookeeper.ClientCnxnSocketNIO.class,
                org.apache.zookeeper.ClientCnxnSocketNetty.class,
                org.apache.zookeeper.server.auth.DigestLoginModule.class);
    }

    @BuildStep
    ReflectiveClassBuildItem addReflectiveProviderClasses() {
        return new ReflectiveClassBuildItem(true, false, false,
                "sun.security.provider.ConfigFile");
    }

    @BuildStep
    NativeImageSecurityProviderBuildItem addSunSASLProvider() {
        return new NativeImageSecurityProviderBuildItem("com.sun.security.sasl.Provider");
    }

    @BuildStep
    RuntimeInitializedClassBuildItem addZKLogin() {
        return new RuntimeInitializedClassBuildItem(Login.class.getName());
    }

    @BuildStep
    AutoInjectAnnotationBuildItem injectableZKClient() {
        return new AutoInjectAnnotationBuildItem(DotName.createSimple(ZK_CLIENT_ANNOTATION_NAME));
    }

    @BuildStep
    @Record(value = ExecutionTime.RUNTIME_INIT)
    SyntheticBeanBuildItem createZookeeperBean(ZookeeperRecorder recorder,
            ShutdownContextBuildItem shutdownContextBuildItem) {

        var zk = recorder.create(shutdownContextBuildItem);
        return SyntheticBeanBuildItem
                .configure(ZooKeeper.class)
                .scope(ApplicationScoped.class)
                .runtimeValue(zk)
                .setRuntimeInit()
                .unremovable()
                .done();
    }

    @BuildStep(onlyIf = GroupMembershipSupplier.class)
    AdditionalBeanBuildItem createGroupMembershipBean() {
        return AdditionalBeanBuildItem.unremovableOf(GroupMembershipBean.class);
    }

    @BuildStep(onlyIf = { GroupMembershipSupplier.class, GroupMembershipSupplier.WithGroupStatus.class })
    AdditionalBeanBuildItem createGroupStatusBean() {
        return AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(MembershipStatusBean.class)
                .addBeanClass(MembershipStatusWatcherHolder.class)
                .build();
    }

    @BuildStep(onlyIf = { GroupMembershipSupplier.class, GroupMembershipSupplier.WithReactiveGroupStatus.class })
    AdditionalBeanBuildItem createReactiveGroupStatusBean() {
        return AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(ReactiveMembershipStatusBean.class)
                .addBeanClass(MembershipStatusWatcherHolder.class)
                .build();
    }
}
