package io.quarkiverse.quarkus.zookeeper.it;

import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ZookeeperTestServer implements QuarkusTestResourceLifecycleManager {

    // In addition to %test profile in the application.properties file
    static {
        System.setProperty("zookeeper.ssl.trustStore.location",
                MountableFile.forClasspathResource("ks.jks").getFilesystemPath());
        System.setProperty("zookeeper.ssl.trustStore.password", "changeit");
    }

    private static final GenericContainer<?> ZOOKEEPER = new GenericContainer<>(DockerImageName.parse("zookeeper:3.8.0"))
            .withAccessToHost(true)
            .withEnv("SERVER_JVMFLAGS",
                    "-Djava.security.auth.login.config=/tmp/server_jaas.conf -Dzookeeper.serverCnxnFactory=org.apache.zookeeper.server.NettyServerCnxnFactory -Dzookeeper.ssl.keyStore.location=/tmp/ks.jks -Dzookeeper.ssl.keyStore.password=changeit")
            .withCopyFileToContainer(MountableFile.forClasspathResource("server_jaas.conf"), "/tmp/server_jaas.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("all_zoo.cfg"), "/conf/zoo.cfg")
            .withCopyFileToContainer(MountableFile.forClasspathResource("ks.jks"), "/tmp/ks.jks")
            .withExposedPorts(2281);

    @Override
    public Map<String, String> start() {
        ZOOKEEPER.start();

        // In addition to %test profile in the application.properties file
        return Map.of(
                "quarkus.zookeeper.session.connection-string",
                String.format("%s:%d", ZOOKEEPER.getHost(), ZOOKEEPER.getMappedPort(2281)));
    }

    @Override
    public void stop() {
        ZOOKEEPER.stop();
    }
}
