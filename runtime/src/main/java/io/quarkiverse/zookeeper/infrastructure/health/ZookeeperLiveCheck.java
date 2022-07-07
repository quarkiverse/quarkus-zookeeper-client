package io.quarkiverse.zookeeper.infrastructure.health;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.jboss.logging.Logger;

import io.quarkiverse.zookeeper.config.ClientConfig;
import io.quarkiverse.zookeeper.config.SessionConfig;
import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Liveness
@ApplicationScoped
public class ZookeeperLiveCheck implements AsyncHealthCheck {

    private static final Logger LOG = Logger.getLogger(ZookeeperLiveCheck.class);

    private static final Set<States> CONNECTED_STATES = Set.of(
            States.CONNECTED);

    private static final String EXTENSION_NAME = "zookeeper";

    @ConfigProperty(name = SessionConfig.CAN_BE_READ_ONLY)
    boolean canBeReadOnly;

    @ConfigProperty(name = ClientConfig.CONNECTION_TIMEOUT_MILLIS, defaultValue = ClientConfig.CONNECTION_TIMEOUT_DEFAULT)
    int connectionTimeout;

    @Inject
    ZooKeeper client;

    @Override
    public Uni<HealthCheckResponse> call() {

        var useStates = new HashSet<>(CONNECTED_STATES);
        if (canBeReadOnly) {
            useStates.add(States.CONNECTEDREADONLY);
        }

        return Uni.createFrom().item(client::getState)
                .repeat().until(useStates::contains)
                .ifNoItem().after(Duration.ofMillis(connectionTimeout))
                .recoverWithMulti(Multi.createFrom().item(States.NOT_CONNECTED))
                .select().last().toUni()
                .map(state -> {
                    if (States.NOT_CONNECTED == state || !useStates.contains(client.getState())) {
                        LOG.warnf("Zookeeper is not ready within [%d] millis to go live", connectionTimeout);
                        return HealthCheckResponse.down(EXTENSION_NAME);
                    } else {
                        return HealthCheckResponse.up(EXTENSION_NAME);
                    }
                });
    }
}
