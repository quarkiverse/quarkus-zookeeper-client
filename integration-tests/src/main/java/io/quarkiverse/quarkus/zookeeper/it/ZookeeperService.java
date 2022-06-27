package io.quarkiverse.quarkus.zookeeper.it;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.spi.EventContext;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.inject.Inject;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.jboss.logging.Logger;

import io.quarkus.arc.AsyncObserverExceptionHandler;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

@ApplicationScoped
public class ZookeeperService implements AsyncObserverExceptionHandler {

    private static final Logger LOG = Logger.getLogger(ZookeeperService.class);

    private static final Set<KeeperState> CONNECTED_STATES_EV = Set.of(
            KeeperState.SyncConnected,
            KeeperState.ConnectedReadOnly);

    private static final Set<States> CONNECTED_STATES_CL = Set.of(
            States.CONNECTED,
            States.CONNECTEDREADONLY);

    @Inject
    ZooKeeper client;

    public synchronized void onZKEvent(@ObservesAsync WatchedEvent event) {
        LOG.infof("Receiving [%s]", event.getState());
        if (CONNECTED_STATES_EV.contains(event.getState())) {
            notifyAll();
        }
    }

    @Override
    public void handle(Throwable throwable, ObserverMethod<?> observerMethod, EventContext<?> eventContext) {
        LOG.error("Error handling a zookeeper watched event", throwable);
    }

    public Uni<String> sayConnected() {
        final Instant requestInstant = Instant.now();
        return Uni.createFrom().item(client::getState)
                .onItem().delayIt().until(state -> {
                    if (CONNECTED_STATES_CL.contains(state)) {
                        return Uni.createFrom().item(state);
                    } else {
                        return innerSayConnected(requestInstant);
                    }
                })
                .map(state -> client.getState())
                .map(States::name);
    }

    public String sayConnectedS() {
        if (!CONNECTED_STATES_CL.contains(client.getState())) {
            waitForIt();
        }
        return client.getState().name();
    }

    private Uni<States> innerSayConnected(final Instant requestInstant) {
        return Uni.createFrom().item(client::getState)
                .onItem().delayIt().until(Unchecked.function(state -> {

                    if (timeoutWithin(requestInstant, 10, ChronoUnit.SECONDS)) {
                        throw new TimeoutException();
                    }

                    if (CONNECTED_STATES_CL.contains(state)) {
                        return Uni.createFrom().item(state);
                    } else {
                        return innerSayConnected(requestInstant);
                    }
                }));
    }

    private boolean timeoutWithin(Instant requestInstant, int amount, ChronoUnit amountUnit) {
        return Duration.of(amount, amountUnit).minus(
                Duration.between(requestInstant, Instant.now()).abs()).isNegative();
    }

    private synchronized void waitForIt() {
        try {
            wait(10_000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
