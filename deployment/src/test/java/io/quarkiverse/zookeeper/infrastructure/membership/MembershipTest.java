package io.quarkiverse.zookeeper.infrastructure.membership;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.apache.zookeeper.ZooKeeper;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import io.quarkiverse.zookeeper.membership.model.GroupMembership;
import io.quarkus.test.QuarkusUnitTest;

public class MembershipTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withConfigurationResource("membership-connection.properties")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @SuppressWarnings("deprecation")
    // Using a fixed hostPort to match the config property as defined in the basic-connection.properties
    private static final GenericContainer<?> ZOOKEEPER = new FixedHostPortGenericContainer<>("zookeeper:3.8.0")
            .withEnv(Map.of(
                    "ZOO_AUTOPURGE_PURGEINTERVAL", "1"))
            .withFixedExposedPort(52181, 2181);

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

    @Inject
    GroupMembership membership;

    @Test
    void testConfiguration() throws InterruptedException, TimeoutException {
        assertFalse(membership.partyStatus().partecipating());
        var now = Instant.now();
        do {
            Thread.sleep(100);
        } while (!membership.partyStatus().partecipating() && Duration.between(now, Instant.now()).compareTo(TIMEOUT) == -1);
        assertTrue(membership.partyStatus().partecipating());
    }
}
