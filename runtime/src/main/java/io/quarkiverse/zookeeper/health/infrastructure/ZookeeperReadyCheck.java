package io.quarkiverse.zookeeper.health.infrastructure;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;

@Readiness
@ApplicationScoped
public class ZookeeperReadyCheck implements AsyncHealthCheck {

    private static final Logger LOG = Logger.getLogger(ZookeeperReadyCheck.class);

    public static final String DEFAULT_CONNECTION_NAME = "<default>";

    private static final Set<States> CONNECTED_STATES = Set.of(
            States.CONNECTED);

    private ZookeeperConfiguration config;

    private Set<ZKChecks> checks = new HashSet<>();

    public ZookeeperReadyCheck(ZookeeperConfiguration config) {
        this.config = config;
    }

    @PostConstruct
    public void configure() {

        Iterable<InstanceHandle<ZooKeeper>> handle = Arc.container().select(ZooKeeper.class).handles();

        // Default (and only by now) client
        var handleIterator = handle.iterator();
        if (handleIterator.hasNext()) {
            var defaultClient = handleIterator.next().get();

            var connectedStates = new HashSet<>(CONNECTED_STATES);
            if (config.session.canBeReadOnly) {
                connectedStates.add(States.CONNECTEDREADONLY);
            }

            checks.add(
                    new ZKChecks(DEFAULT_CONNECTION_NAME, defaultClient, config.client.connectionTimeoutMillis,
                            connectedStates));
        } else {
            LOG.warn("No ZooKeeper clients found");
        }
    }

    @Override
    public Uni<HealthCheckResponse> call() {
        LOG.debug("Testing zookeeper connection");
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Zookeeper connection health check").up();
        if (checks.isEmpty()) {
            return Uni.createFrom().item(builder.build());
        }

        var checkList = checks.stream().map(this::checksToUni).collect(toList());
        return Uni.combine().all().unis(checkList)
                .combinedWith((var responses) -> combine(responses, builder));
    }

    @SuppressWarnings("unchecked")
    private HealthCheckResponse combine(List<?> responses, HealthCheckResponseBuilder builder) {
        for (var response : responses) {
            Tuple2<String, Boolean> useResponse = (Tuple2<String, Boolean>) response;
            if (useResponse.getItem2().booleanValue()) {
                builder.withData(useResponse.getItem1(), useResponse.getItem2());
            } else {
                builder.down().withData(useResponse.getItem1(), useResponse.getItem2());
            }
        }
        return builder.build();
    }

    private Uni<Tuple2<String, Boolean>> checksToUni(ZKChecks check) {
        return Uni.createFrom().item(check.client::getState)
                .ifNoItem().after(Duration.ofMillis(check.connectionTimeout)).recoverWithItem(States.NOT_CONNECTED)
                .onFailure().recoverWithItem(States.NOT_CONNECTED)
                .map(state -> Tuple2.of(check.name, evalState(check.client, state, check.connectedStates)));
    }

    private Boolean evalState(ZooKeeper client, States state, Set<States> connectedStates) {
        return state != States.NOT_CONNECTED && connectedStates.contains(client.getState());
    }

    private static final class ZKChecks {
        String name;
        ZooKeeper client;
        int connectionTimeout;
        Set<States> connectedStates;

        public ZKChecks(String name, ZooKeeper client, int connectionTimeout, Set<States> connectedStates) {
            this.name = name;
            this.client = client;
            this.connectionTimeout = connectionTimeout;
            this.connectedStates = connectedStates;
        }
    }
}
