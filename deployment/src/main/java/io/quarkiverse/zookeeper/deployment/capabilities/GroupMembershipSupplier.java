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

    public static class WithGroupStatus implements BooleanSupplier {

        private ZookeeperBuildTimeConfiguration zookeeperConfiguration;

        public WithGroupStatus(ZookeeperBuildTimeConfiguration zookeeperConfiguration) {
            this.zookeeperConfiguration = zookeeperConfiguration;
        }

        @Override
        public boolean getAsBoolean() {
            return zookeeperConfiguration.membership.withGroupStatus;
        }
    }

    public static class WithReactiveGroupStatus implements BooleanSupplier {

        private ZookeeperBuildTimeConfiguration zookeeperConfiguration;

        public WithReactiveGroupStatus(ZookeeperBuildTimeConfiguration zookeeperConfiguration) {
            this.zookeeperConfiguration = zookeeperConfiguration;
        }

        @Override
        public boolean getAsBoolean() {
            return zookeeperConfiguration.membership.withReactiveGroupStatus;
        }
    }
}
