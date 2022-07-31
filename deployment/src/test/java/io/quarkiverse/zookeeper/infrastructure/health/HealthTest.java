package io.quarkiverse.zookeeper.infrastructure.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.eclipse.microprofile.health.HealthCheckResponse;
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
import io.smallrye.mutiny.Uni;

public class HealthTest {

    private static final Logger LOG = Logger.getLogger(HealthTest.class);

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withConfigurationResource("health-connection.properties")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @SuppressWarnings("deprecation")
    // Using a fixed hostPort to match the config property as defined in the health-connection.properties
    private static final GenericContainer<?> ZOOKEEPER = new FixedHostPortGenericContainer<>("zookeeper:3.8.0")
            .withEnv(Map.of(
                    "ZOO_AUTOPURGE_PURGEINTERVAL", "1"))
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
    void testHealth() throws InterruptedException, ExecutionException, TimeoutException {

        assertNotNull(health);

        LOG.info("Waiting for UP status");
        var response = Uni.createFrom().voidItem()
                .flatMap(res -> health.call())
                .repeat().until(res -> HealthCheckResponse.Status.UP == res.getStatus())
                .ifNoItem().after(Duration.ofSeconds(30)).recoverWithCompletion()
                .select().last().toUni()
                .log("Refreshing status")
                .flatMap(res -> health.call())
                .subscribe().asCompletionStage()
                .get(30, TimeUnit.SECONDS);

        LOG.info("Assertion on the default connection");
        assertThat(response).isNotNull()
                .extracting(HealthCheckResponse::getData).asInstanceOf(InstanceOfAssertFactories.OPTIONAL)
                .isNotEmpty()
                .containsInstanceOf(Map.class).map(Map.class::cast)
                .contains(Map.of(ZookeeperReadyCheck.DEFAULT_CONNECTION_NAME, Boolean.TRUE));
    }
}
