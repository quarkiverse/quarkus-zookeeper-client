package io.quarkiverse.quarkus.zookeeper.deployment;

import io.quarkiverse.quarkus.zookeeper.deployment.config.ZookeeperBuildTimeConfig;
import io.quarkiverse.zookeeper.infrastructure.ZookeeperClientProducerBean;
import io.quarkiverse.zookeeper.infrastructure.health.ZookeeperLiveCheck;
import io.quarkiverse.zookeeper.infrastructure.health.ZookeeperReadyCheck;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

class ZookeeperProcessor {

    private static final String FEATURE = "zookeeper";

    @BuildStep
    FeatureBuildItem zookeeper() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return new AdditionalBeanBuildItem(ZookeeperClientProducerBean.class);
    }

    @BuildStep
    HealthBuildItem addLiveCheck(Capabilities capabilities, ZookeeperBuildTimeConfig config) {
        if (capabilities.isPresent(Capability.SMALLRYE_HEALTH)) {
            return new HealthBuildItem(ZookeeperLiveCheck.class.getName(),
                    config.healthEnabled.orElse(Boolean.TRUE));
        } else {
            return null;
        }
    }

    @BuildStep
    HealthBuildItem addReadyCheck(Capabilities capabilities, ZookeeperBuildTimeConfig config) {
        if (capabilities.isPresent(Capability.SMALLRYE_HEALTH)) {
            return new HealthBuildItem(ZookeeperReadyCheck.class.getName(),
                    config.healthEnabled.orElse(Boolean.TRUE));
        } else {
            return null;
        }
    }
}
