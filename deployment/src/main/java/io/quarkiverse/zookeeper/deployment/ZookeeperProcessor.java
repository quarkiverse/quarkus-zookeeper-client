package io.quarkiverse.zookeeper.deployment;

import java.util.Collections;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.zookeeper.Login;
import org.apache.zookeeper.ZooKeeper;

import io.netty.handler.ssl.OpenSsl;
import io.netty.internal.tcnative.SSL;
import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkiverse.zookeeper.deployment.config.ZookeeperBuildTimeConfiguration;
import io.quarkiverse.zookeeper.infrastructure.health.ZookeeperReadyCheck;
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
    RemovedResourceBuildItem removeSlf4jBinding() {
        return new RemovedResourceBuildItem(new AppArtifactKey("ch.qos.logback", "logback-classic", null, "jar"),
                Collections.singleton("org/slf4j/impl/StaticLoggerBinder.class"));
    }

    @BuildStep
    ReflectiveClassBuildItem addReflectiveZKClasses() {
        return ReflectiveClassBuildItem.builder(
                org.apache.zookeeper.ClientCnxnSocketNIO.class,
                org.apache.zookeeper.ClientCnxnSocketNetty.class,
                org.apache.zookeeper.server.auth.DigestLoginModule.class)
                .build();
    }

    @BuildStep
    ReflectiveClassBuildItem addReflectiveNettyClasses() {
        return ReflectiveClassBuildItem.builder(
                io.netty.channel.epoll.EpollSocketChannel.class)
                .constructors()
                .build();
    }

    @BuildStep
    ReflectiveClassBuildItem addReflectiveProviderClasses() {
        return ReflectiveClassBuildItem.builder(
                "sun.security.provider.ConfigFile",
                "com.sun.security.auth.module.Krb5LoginModule",
                "com.sun.security.auth.module.UnixLoginModule",
                "com.sun.security.auth.module.JndiLoginModule",
                "com.sun.security.auth.module.KeyStoreLoginModule",
                "com.sun.security.auth.module.NTLoginModule",
                "com.sun.security.auth.module.LdapLoginModule",
                "com.sun.jmx.remote.security.FileLoginModule")
                .build();
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
    RuntimeInitializedClassBuildItem addNettyOpenSsl() {
        return new RuntimeInitializedClassBuildItem(OpenSsl.class.getName());
    }

    @BuildStep
    RuntimeInitializedClassBuildItem addTCNativeSsl() {
        return new RuntimeInitializedClassBuildItem(SSL.class.getName());
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
}
