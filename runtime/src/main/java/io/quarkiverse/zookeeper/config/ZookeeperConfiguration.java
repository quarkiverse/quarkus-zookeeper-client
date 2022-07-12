package io.quarkiverse.zookeeper.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class ZookeeperConfiguration {

    public static final String EXTENSION_NAME = "zookeeper";

    /**
     * The default zookeeper client configuration.
     */
    @ConfigItem
    public ClientConfig client;

    /**
     * The default zookeeper client connection.
     */
    @ConfigItem
    public SessionConfig session;

    /**
     * Whether to enable health checks.
     */
    @ConfigItem(name = "health.enabled", defaultValue = "true", defaultValueDocumentation = "true")
    public boolean healthEnabled;
}
