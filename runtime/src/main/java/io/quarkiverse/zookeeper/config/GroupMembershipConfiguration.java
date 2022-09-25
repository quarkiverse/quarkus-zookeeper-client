package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class GroupMembershipConfiguration {

    /**
     * Set the value to true to enable group membership and shared group status.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public boolean enable;

    /**
     * Mandatory string property to specify the group to join.
     */
    @ConfigItem
    public Optional<String> groupId;

    /**
     * Mandatory string property to specify the zookeeper root node to store the
     * groups, default value is groups.
     */
    @ConfigItem(defaultValue = "groups", defaultValueDocumentation = "groups")
    public String namespace;

    @Override
    public String toString() {
        return "GroupMembershipConfiguration [enable=" + enable + ", groupId=" + groupId + ", namespace=" + namespace
                + "]";
    }
}
