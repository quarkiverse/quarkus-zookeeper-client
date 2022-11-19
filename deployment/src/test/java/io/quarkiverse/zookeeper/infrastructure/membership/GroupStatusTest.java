package io.quarkiverse.zookeeper.infrastructure.membership;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import io.quarkiverse.zookeeper.membership.model.MembershipStatus;
import io.quarkus.test.QuarkusUnitTest;

public class GroupStatusTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withConfigurationResource("group-status-connection.properties")
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
    MembershipStatus status;

    @Test
    void testPutNewProperty() throws InterruptedException, TimeoutException {
        var key = UUID.randomUUID().toString();
        var value = UUID.randomUUID().toString().getBytes();

        var rv = status.put(key, value);
        assertThat(rv).isNotNull().isEmpty();
    }

    @Test
    void testReadNewlyAddedProperty() throws InterruptedException, TimeoutException {
        var key = UUID.randomUUID().toString();
        var value = UUID.randomUUID().toString().getBytes();

        var rv = status.put(key, value);
        assertThat(rv).isNotNull().isEmpty();

        var actualValue = status.get(key);
        assertThat(actualValue).isNotNull().isNotEmpty().contains(value);
    }
}
