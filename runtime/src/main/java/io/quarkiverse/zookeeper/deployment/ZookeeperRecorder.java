package io.quarkiverse.zookeeper.deployment;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;

import io.quarkiverse.zookeeper.ClientStatusWatcher;
import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ZookeeperRecorder {

    private ZookeeperConfiguration config;

    public ZookeeperRecorder(ZookeeperConfiguration config) {
        this.config = config;
    }

    public RuntimeValue<ZooKeeper> create(ShutdownContext shutdownContext) {
        ZooKeeper rv;
        try {
            rv = new ZooKeeper(config.session.connectionString, config.session.timeout, new ClientStatusWatcher(),
                    config.session.canBeReadOnly);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shutdownContext.addShutdownTask(() -> {
            try {
                rv.close(config.session.timeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return new RuntimeValue<>(rv);
    }
}
