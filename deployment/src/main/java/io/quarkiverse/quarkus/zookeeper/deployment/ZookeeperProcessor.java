package io.quarkiverse.quarkus.zookeeper.deployment;

import io.quarkiverse.quarkus.zookeeper.deployment.config.ZookeeperConfig;
import io.quarkiverse.zookeeper.infrastructure.ZookeeperClientBean;
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
        return new AdditionalBeanBuildItem(ZookeeperClientBean.class);
    }

    @BuildStep
    HealthBuildItem addHealthCheck(Capabilities capabilities, ZookeeperConfig config) {
        if (capabilities.isPresent(Capability.SMALLRYE_HEALTH)) {
            return new HealthBuildItem("io.quarkiverse.zookeeper.infrastructure.health.ZookeeperHealthCheck",
                    config.healthEnabled.orElse(Boolean.TRUE));
        } else {
            return null;
        }
    }
}
