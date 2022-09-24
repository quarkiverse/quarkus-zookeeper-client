package io.quarkiverse.zookeeper.deployment.capabilities;

import java.util.function.BooleanSupplier;

import io.quarkiverse.zookeeper.deployment.config.ZookeeperBuildTimeConfiguration;

public class GroupMembershipSupplier implements BooleanSupplier {

    private ZookeeperBuildTimeConfiguration zookeeperConfiguration;

    public GroupMembershipSupplier(ZookeeperBuildTimeConfiguration zookeeperConfiguration) {
        this.zookeeperConfiguration = zookeeperConfiguration;
    }

    @Override
    public boolean getAsBoolean() {
        return zookeeperConfiguration.membership.enable;
    }
}
