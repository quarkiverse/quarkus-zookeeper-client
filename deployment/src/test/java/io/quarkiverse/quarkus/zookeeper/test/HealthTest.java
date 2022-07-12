package io.quarkiverse.quarkus.zookeeper.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.concurrent.ExecutionException;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import io.quarkiverse.zookeeper.infrastructure.health.ZookeeperReadyCheck;
import io.quarkus.test.QuarkusUnitTest;

public class HealthTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withConfigurationResource("health-connection.properties")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @SuppressWarnings("deprecation")
    // Using a fixed hostPort to match the config property as defined in the basic-connection.properties
    private static final GenericContainer<?> ZOOKEEPER = new FixedHostPortGenericContainer<>("zookeeper:3.8.0")
            .withFixedExposedPort(22181, 2181);

    @BeforeAll
    public static void startZookeeper() {
        ZOOKEEPER.start();
    }

    @AfterAll
    public static void stopZookeeper() {
        ZOOKEEPER.stop();
    }

    @Inject
    @Any
    ZookeeperReadyCheck health;

    @Test
    void testHealth() throws InterruptedException, ExecutionException {

        var response = health.call().subscribe().asCompletionStage().get();
        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.Status.UP);
        assertThat(response.getData()).isNotEmpty();
        assertThat(response.getData().get()).hasSize(1)
                .contains(entry(ZookeeperReadyCheck.DEFAULT_CONNECTION_NAME, Boolean.TRUE));
    }
}
