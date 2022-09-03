package io.quarkiverse.zookeeper.deployment;

import javax.enterprise.context.ApplicationScoped;

import org.apache.zookeeper.Login;
import org.apache.zookeeper.ZooKeeper;

import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkiverse.zookeeper.deployment.config.ZookeeperBuildTimeConfiguration;
import io.quarkiverse.zookeeper.infrastructure.health.ZookeeperReadyCheck;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageSecurityProviderBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

class ZookeeperProcessor {

    private static final String FEATURE = ZookeeperConfiguration.EXTENSION_NAME;

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

    // WIP: java.lang.SecurityException: Configuration error: java.lang.ClassNotFoundException: sun.security.provider.ConfigFile

    //    @BuildStep
    //    NativeImageSecurityProviderBuildItem addSunProvider() {
    //        return new NativeImageSecurityProviderBuildItem("sun.security.provider.Sun");
    //    }

    //    @BuildStep
    //    JPMSExportBuildItem exportSecurityPackages() {
    //        return new JPMSExportBuildItem("java.base", "sun.security.provider");
    //    }

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
}
