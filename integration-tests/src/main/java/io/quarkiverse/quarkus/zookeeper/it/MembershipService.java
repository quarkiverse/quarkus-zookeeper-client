package io.quarkiverse.quarkus.zookeeper.it;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.apache.zookeeper.ZooKeeper;
import org.jboss.logging.Logger;

import io.quarkiverse.zookeeper.membership.model.GroupMembership.PartyStatus;
import io.quarkiverse.zookeeper.membership.model.GroupMembershipEvent;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class MembershipService {

    private static final Logger LOG = Logger.getLogger(MembershipService.class);

    @Inject
    ZooKeeper client;

    private volatile AtomicReference<PartyStatus> partyStatus = new AtomicReference<>(PartyStatus.Alone);

    public synchronized void onMembershipEvent(@ObservesAsync GroupMembershipEvent event) {
        LOG.infof("Receiving [%s]", event.getPartyStatus());
        partyStatus.set(event.getPartyStatus());
    }

    public Uni<PartyStatus> sayStatus() {
        return Uni.createFrom().emitter(em -> {
            waitForIt();
            em.complete(partyStatus.get());
        })
                .ifNoItem().after(Duration.ofSeconds(10)).failWith(TimeoutException::new)
                .map(PartyStatus.class::cast);
    }

    public PartyStatus sayStatusS() {
        var waitTimeout = Instant.now();
        waitForIt();
        if (Duration.between(Instant.now(), waitTimeout).abs().getSeconds() > 10) {
            throw new TimeoutException();
        }
        return partyStatus.get();
    }

    private synchronized void waitForIt() {
        if (!partyStatus.get().partecipating()) {
            try {
                wait(15_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
