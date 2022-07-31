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

import io.quarkus.test.QuarkusUnitTest;

public class ZookeeperBasicTest {

    private static final Logger LOG = Logger.getLogger(ZookeeperBasicTest.class);

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withConfigurationResource("basic-connection.properties")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @SuppressWarnings("deprecation")
    // Using a fixed hostPort to match the config property as defined in the basic-connection.properties
    private static final GenericContainer<?> ZOOKEEPER = new FixedHostPortGenericContainer<>("zookeeper:3.8.0")
            .withEnv(Map.of(
                    "ZOO_AUTOPURGE_PURGEINTERVAL", "1"))
            .withFixedExposedPort(12181, 2181);

    @BeforeAll
    public static void startZookeeper() {
        ZOOKEEPER.start();
    }

    @AfterAll
    public static void stopZookeeper() {
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
