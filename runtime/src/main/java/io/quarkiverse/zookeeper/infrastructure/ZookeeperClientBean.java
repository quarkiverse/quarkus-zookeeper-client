package io.quarkiverse.zookeeper.infrastructure;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.quarkiverse.zookeeper.ZookeeperClient;

@ApplicationScoped
public class ZookeeperClientBean implements ZookeeperClient {

    public static final String EXTENSION_NAME = "zookeeper";

    @Override
    public String getHello() {
        return String.format("[%s] %s", UUID.randomUUID(), "Hello World!!!");
    }
}
