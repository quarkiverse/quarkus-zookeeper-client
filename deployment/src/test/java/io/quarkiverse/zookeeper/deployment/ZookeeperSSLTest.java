package io.quarkiverse.zookeeper.deployment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

import io.quarkus.test.QuarkusUnitTest;

public class ZookeeperSSLTest {

    private static final Logger LOG = Logger.getLogger(ZookeeperSSLTest.class);

    static {

        System.setProperty("quarkus.zookeeper.client.ssl.trust-store-location",
                MountableFile.forClasspathResource("ks.jks").getFilesystemPath());
    }

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withConfigurationResource("ssl-connection.properties")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @SuppressWarnings("deprecation")
    // Using a fixed hostPort to match the config property as defined in the ssl-connection.properties
    private static final GenericContainer<?> ZOOKEEPER = new FixedHostPortGenericContainer<>("zookeeper:3.8.0")
            .withEnv(Map.of(
                    "ZOO_AUTOPURGE_PURGEINTERVAL", "1",
                    "SERVER_JVMFLAGS",
                    "-Dzookeeper.serverCnxnFactory=org.apache.zookeeper.server.NettyServerCnxnFactory -Dzookeeper.ssl.keyStore.location=/tmp/ks.jks -Dzookeeper.ssl.keyStore.password=changeit"))
            .withCopyFileToContainer(MountableFile.forClasspathResource("ssl_zoo.cfg"), "/conf/zoo.cfg")
            .withCopyFileToContainer(MountableFile.forClasspathResource("ks.jks"), "/tmp/ks.jks")
            .withFixedExposedPort(42281, 2281);

    @BeforeAll
    public static void startZookeeper() {
        ZOOKEEPER.start();
    }

    @AfterAll
    public static void stopZookeeper() {
        System.clearProperty("zookeeper.ssl.trustStore.location");
        System.clearProperty("zookeeper.ssl.trustStore.password");

        ZOOKEEPER.stop();
    }

    @Inject
    ZooKeeper zk;

    @Test
    void testConfiguration() throws IOException {

        assertNotNull(zk);

        final Phaser testCoordinator = new Phaser(2);
        final AtomicBoolean connected = new AtomicBoolean(false);
        zk.register(event -> {
            LOG.infof("Processing event for state [%s] and type [%s]", event.getState(), event.getType());
            if (KeeperState.SyncConnected == event.getState()) {
                LOG.info("Client connected to zookeeper");
                connected.compareAndSet(false, true);
                testCoordinator.arriveAndDeregister();
            } else if (KeeperState.Closed == event.getState()) {
                LOG.info("Client connection closed");
            }
        });

        assertFalse(connected.get());
        var testPhase = testCoordinator.arriveAndDeregister();
        try {
            testCoordinator.awaitAdvanceInterruptibly(testPhase, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        assertTrue(connected.get());
    }
}
