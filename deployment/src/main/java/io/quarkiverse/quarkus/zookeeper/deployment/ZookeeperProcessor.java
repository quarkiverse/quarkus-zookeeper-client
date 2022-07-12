package io.quarkiverse.quarkus.zookeeper.deployment;

import javax.enterprise.context.ApplicationScoped;

import org.apache.zookeeper.ZooKeeper;

import io.quarkiverse.quarkus.zookeeper.deployment.config.ZookeeperBuildTimeConfiguration;
import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkiverse.zookeeper.deployment.ZookeeperRecorder;
// import io.quarkiverse.quarkus.zookeeper.deployment.config.ZookeeperBuildTimeConfiguration;
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
