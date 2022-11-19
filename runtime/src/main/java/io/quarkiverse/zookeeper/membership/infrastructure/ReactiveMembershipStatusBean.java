package io.quarkiverse.zookeeper.membership.infrastructure;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Flow.Subscriber;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import io.quarkiverse.zookeeper.membership.model.ReactiveMembershipStatus;
import io.quarkiverse.zookeeper.membership.model.StatusEntry;
import io.quarkiverse.zookeeper.membership.model.StatusEntryChanged;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ReactiveMembershipStatusBean implements ReactiveMembershipStatus {

    private Set<Subscriber<StatusEntryChanged>> consumers = ConcurrentHashMap.newKeySet();

    @PreDestroy
    void preDestroy() {
        consumers.clear();
    }

    @Override
    public Uni<byte[]> put(byte[] value, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uni<byte[]> put(StatusEntry entry) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uni<byte[]> get(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uni<byte[]> clear(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uni<Set<String>> keys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addAnChangeSubscriber(Subscriber<StatusEntryChanged> onChangeSubscriber) {
        consumers.add(onChangeSubscriber);
    }

    @Override
    public boolean removeOnChangeSubscriber(Subscriber<StatusEntryChanged> onChangeSubscriber) {
        return consumers.remove(onChangeSubscriber);
    }
}
