package io.quarkiverse.quarkus.zookeeper.it;

import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ZookeeperTestServer implements QuarkusTestResourceLifecycleManager {

    private static final GenericContainer<?> ZOOKEEPER = new GenericContainer<>(DockerImageName.parse("zookeeper:3.8.0"))
            .withAccessToHost(true)
            .withExposedPorts(2181);

    @Override
    public Map<String, String> start() {
        ZOOKEEPER.start();
        return Map.of(
                "quarkus.zookeeper.session.connectionString",
                String.format("%s:%d", ZOOKEEPER.getHost(), ZOOKEEPER.getMappedPort(2181)));
    }

    @Override
    public void stop() {
        ZOOKEEPER.stop();
    }
}
