package io.quarkiverse.quarkus.zookeeper.it;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ZookeeperTestServer implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        return Map.of(
                "quarkus.zookeeper.session.connectionString", "localhost:2181");
    }

    @Override
    public void stop() {
    }
}
