package io.quarkiverse.zookeeper.membership.infrastructure;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkiverse.zookeeper.membership.model.MembershipStatus;
import io.quarkiverse.zookeeper.membership.model.StatusEntry;

@ApplicationScoped
public class MembershipStatusBean implements MembershipStatus {

    @Inject GroupMembershipBean groupMembershipBean;
    @Inject ZookeeperFaçade façade;

    private Set<BiConsumer<StatusEntry, byte[]>> consumers = ConcurrentHashMap.newKeySet();

    @PreDestroy
    void preDestroy() {
        consumers.clear();
    }

    @Override
    public Optional<byte[]> put(String key, byte[] value) {
        var path = createPath(groupMembershipBean.dataNode(), key);
        return Optional.ofNullable(façade.write(path, value, true));
    }

    @Override
    public Optional<byte[]> put(StatusEntry entry) {
        return put(entry.key(), entry.value());
    }

    @Override
    public Optional<byte[]> get(String key) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> clear(String key) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Set<String> keys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<StatusEntry> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addOnChangeCallback(BiConsumer<StatusEntry, byte[]> onChangeCallback) {
        consumers.add(onChangeCallback);
    }

    @Override
    public boolean removeOnChangeCallback(BiConsumer<StatusEntry, byte[]> onChangeCallback) {
        return consumers.remove(onChangeCallback);
    }

    private Path createPath(String dataNode, String key) {
        return Path.of(dataNode, key);
    }
}
