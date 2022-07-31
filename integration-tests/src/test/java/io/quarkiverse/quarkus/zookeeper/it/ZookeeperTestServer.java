package io.quarkiverse.quarkus.zookeeper.it;

import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ZookeeperTestServer implements QuarkusTestResourceLifecycleManager {

    private static final GenericContainer<?> ZOOKEEPER = new GenericContainer<>(DockerImageName.parse("zookeeper:3.8.0"))
            .withAccessToHost(true)
            .withEnv("SERVER_JVMFLAGS", "-Djava.security.auth.login.config=/tmp/server_jaas.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("server_jaas.conf"), "/tmp/server_jaas.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("sasl_zoo.cfg"), "/conf/zoo.cfg")
            .withExposedPorts(2181);

    @Override
    public Map<String, String> start() {
        ZOOKEEPER.start();
        return Map.of(
                "quarkus.zookeeper.session.connection-string",
                String.format("%s:%d", ZOOKEEPER.getHost(), ZOOKEEPER.getMappedPort(2181)),
                "quarkus.zookeeper.session.can-be-read-only", "true",
                "quarkus.zookeeper.client.auth.sasl.enabled", "true",
                "quarkus.zookeeper.client.auth.sasl.config-string",
                "org.apache.zookeeper.server.auth.DigestLoginModule required username=\"test\" password=\"passwd\";");
    }

    @Override
    public void stop() {
        ZOOKEEPER.stop();
    }
}
