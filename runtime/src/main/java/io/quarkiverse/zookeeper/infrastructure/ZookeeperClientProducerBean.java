package io.quarkiverse.zookeeper.infrastructure;

import java.io.IOException;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.zookeeper.ZooKeeper;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkiverse.zookeeper.config.SessionConfig;

public class ZookeeperClientProducerBean {

    public static final String EXTENSION_NAME = "zookeeper";

    @ConfigProperty(name = SessionConfig.CONNECTION_STRING)
    String cnString;

    @ConfigProperty(name = SessionConfig.TIMEOUT)
    int cnTimeout;

    @ConfigProperty(name = SessionConfig.CAN_BE_READ_ONLY)
    boolean canBeReadOnly;

    @Inject ClientStatusWatcher watcher;

    @Produces
    @Dependent
    public ZooKeeper createZKClient() throws IOException {
        return new ZooKeeper(cnString, cnTimeout, watcher, canBeReadOnly);
    }
}
