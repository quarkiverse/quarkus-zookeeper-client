package io.quarkiverse.quarkus.zookeeper.deployment.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = io.quarkiverse.zookeeper.config.ZookeeperConfig.CONFIG_NAME, phase = ConfigPhase.BUILD_TIME)
public class ZookeeperConfig {

    /**
     * Whether to enable health checks.
     */
    @ConfigItem(name = "health.enabled", defaultValue = "true", defaultValueDocumentation = "true")
    public Optional<Boolean> healthEnabled;
}
