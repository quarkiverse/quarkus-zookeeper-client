package io.quarkiverse.quarkus.zookeeper.deployment;

import io.quarkiverse.zookeeper.infrastructure.ZookeeperClientBean;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class ZookeeperProcessor {

    private static final String FEATURE = "zookeeper";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return new AdditionalBeanBuildItem(ZookeeperClientBean.class);
    }
}
