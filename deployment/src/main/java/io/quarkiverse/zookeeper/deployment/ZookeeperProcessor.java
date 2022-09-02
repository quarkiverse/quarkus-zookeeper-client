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
    ReflectiveClassBuildItem addClientSocket() {
        return new ReflectiveClassBuildItem(false, false,
                org.apache.zookeeper.ClientCnxnSocketNIO.class,
                org.apache.zookeeper.ClientCnxnSocketNetty.class);
    }

    //    @BuildStep
    //    NativeImageSecurityProviderBuildItem addSunSASLProvider() {
    //        return new NativeImageSecurityProviderBuildItem("com.sun.security.sasl.Provider");
    //    }

    //    @BuildStep
    //    NativeImageSecurityProviderBuildItem addSunJGSSProvider() {
    //        return new NativeImageSecurityProviderBuildItem("sun.security.jgss.SunProvider");
    //    }

    @BuildStep
    RuntimeInitializedClassBuildItem addZKLogin() {
        return new RuntimeInitializedClassBuildItem(Login.class.getName());
    }

    //    2022-08-27 01:35:17,382 WARN  [org.apa.zoo.ClientCnxn] (main-SendThread(localhost:49400)) SASL configuration failed. Will continue connection to Zookeeper server without SASL authentication, if Zookeeper server allows it.: javax.security.auth.login.LoginException: Zookeeper client cannot authenticate using the Client section of the supplied JAAS configuration: '/tmp/jaas6839116132956926788.conf' because of a RuntimeException: java.lang.SecurityException: Configuration error: java.lang.ClassNotFoundException: sun.security.provider.ConfigFile
    //
    //    at org.apache.zookeeper.client.ZooKeeperSaslClient.<init>(ZooKeeperSaslClient.java:152)
    //    at org.apache.zookeeper.ClientCnxn$SendThread.startConnect(ClientCnxn.java:1151)
    //    at org.apache.zookeeper.ClientCnxn$SendThread.run(ClientCnxn.java:1200)
    //    at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:600)
    //    at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192)

    @BuildStep
    ReflectiveClassBuildItem addAuthRelatidReflectiveRquirements() {
        return new ReflectiveClassBuildItem(false, false,
                org.apache.zookeeper.server.auth.DigestLoginModule.class);
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
