package io.quarkiverse.zookeeper.deployment;

import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkiverse.zookeeper.membership.infrastructure.GroupMembershipBean;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class GroupMembershipRecorder {

    public RuntimeValue<GroupMembershipBean> create(ZookeeperConfiguration zookeeperConfiguration,
            ShutdownContext shutdownContext) {

        assert zookeeperConfiguration.membership.groupId.isPresent()
                : "Missing [quarkus.zookeeper.membership.group-id] property.";

        // TODO
        var rv = new GroupMembershipBean(null, null);
        rv.init(zookeeperConfiguration.membership.namespace, zookeeperConfiguration.membership.groupId.get());
        shutdownContext.addShutdownTask(() -> rv.leave());
        return new RuntimeValue<>(rv);
    }
}
