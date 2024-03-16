package io.quarkiverse.zookeeper.deployment;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.inject.Inject;

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

public class ZookeeperSASLTest {

    private static final Logger LOG = Logger.getLogger(ZookeeperSASLTest.class);

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withConfigurationResource("sasl-connection.properties")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @SuppressWarnings("deprecation")
    // Using a fixed hostPort to match the config property as defined in the basic-connection.properties
    private static final GenericContainer<?> ZOOKEEPER = new FixedHostPortGenericContainer<>("zookeeper:3.9.2")
            .withEnv("SERVER_JVMFLAGS", "-Djava.security.auth.login.config=/tmp/server_jaas.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("server_jaas.conf"), "/tmp/server_jaas.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("sasl_zoo.cfg"), "/conf/zoo.cfg")
            .withFixedExposedPort(32181, 2181);

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
            if (KeeperState.SaslAuthenticated == event.getState()) {
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
