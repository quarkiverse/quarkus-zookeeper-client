package io.quarkiverse.quarkus.zookeeper.it;

import java.util.Set;

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
        return Uni.createFrom().item(client::getState)
                .onItem().delayIt().until(state -> {
                    if (CONNECTED_STATES_CL.contains(state)) {
                        return Uni.createFrom().item(state);
                    } else {
                        return sayConnected();
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

    private synchronized void waitForIt() {
        try {
            wait(10_000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
