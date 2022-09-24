package io.quarkiverse.zookeeper.deployment.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class GroupMembershipBuildTimeConfiguration {

    /**
     * Set the value to true to enable group membership and shared group status.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public boolean enable;
}
