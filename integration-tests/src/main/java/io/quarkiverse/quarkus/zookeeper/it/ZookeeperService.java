package io.quarkiverse.quarkus.zookeeper.it;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

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
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ZookeeperService implements AsyncObserverExceptionHandler {

    private static final Logger LOG = Logger.getLogger(ZookeeperService.class);

    @Inject
    ZooKeeper client;

    private volatile AtomicBoolean authenticated = new AtomicBoolean(false);

    public synchronized void onZKEvent(@ObservesAsync WatchedEvent event) {
        LOG.infof("Receiving [%s]", event.getState());
        if (KeeperState.SaslAuthenticated == event.getState()) {
            authenticated.getAndSet(true);
            notifyAll();
        }
    }

    @Override
    public void handle(Throwable throwable, ObserverMethod<?> observerMethod, EventContext<?> eventContext) {
        LOG.error("Error handling a zookeeper watched event", throwable);
    }

    public Uni<String> sayConnected() {
        return Uni.createFrom().emitter(em -> {
            waitForIt();
            em.complete(client.getState());
        })
                .ifNoItem().after(Duration.ofSeconds(10)).failWith(TimeoutException::new)
                .map(States.class::cast).map(States::name);
    }

    public String sayConnectedS() {
        var waitTimeout = Instant.now();
        waitForIt();
        if (Duration.between(Instant.now(), waitTimeout).abs().getSeconds() > 10) {
            throw new TimeoutException();
        }
        return client.getState().name();
    }

    private synchronized void waitForIt() {
        if (!authenticated.get()) {
            try {
                wait(15_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
