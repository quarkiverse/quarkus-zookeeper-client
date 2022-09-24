package io.quarkiverse.zookeeper.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class GroupMembershipConfiguration {

    /**
     * Set the value to true to enable group membership and shared group status.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public boolean enable;

    @Override
    public String toString() {
        return "GroupMembershipConfiguration [enable=" + enable + "]";
    }
}
