package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = ZookeeperConfig.CONFIG_NAME, phase = ConfigPhase.RUN_TIME)
public class ZookeeperConfig {

    public static final String CONFIG_NAME = "zookeeper";

    /**
     * The default zookeeper client connection.
     */
    @ConfigItem
    public SessionConfig session;

    /**
     * Whether to enable health checks.
     */
    @ConfigItem(name = "health.enabled", defaultValue = "true", defaultValueDocumentation = "true")
    public Optional<Boolean> healthEnabled;
}
