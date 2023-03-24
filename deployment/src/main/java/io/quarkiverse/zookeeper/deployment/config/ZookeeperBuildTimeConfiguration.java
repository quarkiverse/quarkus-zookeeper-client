package io.quarkiverse.zookeeper.deployment.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public class ZookeeperBuildTimeConfiguration {

    /**
     * Group membership configuration.
     */
    @ConfigItem
    public GroupMembershipBuildTimeConfiguration membership;

    /**
     * Whether to enable health checks.
     */
    @ConfigItem(name = "health.enabled", defaultValue = "true", defaultValueDocumentation = "true")
    public boolean healthEnabled;
}
