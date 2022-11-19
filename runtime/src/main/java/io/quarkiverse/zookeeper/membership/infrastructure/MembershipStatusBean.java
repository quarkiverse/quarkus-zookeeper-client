package io.quarkiverse.zookeeper.membership.infrastructure;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkiverse.zookeeper.membership.model.MembershipStatus;
import io.quarkiverse.zookeeper.membership.model.StatusEntry;
import io.smallrye.common.constraint.Assert;

@ApplicationScoped
public class MembershipStatusBean implements MembershipStatus {

    @Inject
    GroupMembershipBean groupMembershipBean;
    @Inject
    ZookeeperFaçade façade;

    private Set<BiConsumer<StatusEntry, byte[]>> consumers = ConcurrentHashMap.newKeySet();

    @PreDestroy
    void preDestroy() {
        consumers.clear();
    }

    @Override
    public Optional<byte[]> put(String key, byte[] value) {

        groupMembershipBean.assertPartecipating();
        Assert.checkNotNullParam(key, value);

        var path = createPath(groupMembershipBean.dataNode(), key).toString();
        return Optional.ofNullable(façade.write(path, value, true));
    }

    @Override
    public Optional<byte[]> put(StatusEntry entry) {

        groupMembershipBean.assertPartecipating();
        Assert.checkNotNullParam("entry", entry);

        return put(entry.key(), entry.value());
    }

    @Override
    public Optional<byte[]> get(String key) {

        groupMembershipBean.assertPartecipating();
        Assert.checkNotNullParam("key", key);

        var path = createPath(groupMembershipBean.dataNode(), key).toString();
        return Optional.ofNullable(façade.read(path));
    }

    @Override
    public Optional<byte[]> clear(String key) {

        groupMembershipBean.assertPartecipating();
        Assert.checkNotNullParam("key", key);

        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Set<String> keys() {

        groupMembershipBean.assertPartecipating();

        return façade.listNodes(groupMembershipBean.dataNode());
    }

    @Override
    public void addOnChangeCallback(BiConsumer<StatusEntry, byte[]> onChangeCallback) {

        groupMembershipBean.assertPartecipating();
        Assert.checkNotNullParam("onChangeCallback", onChangeCallback);

        consumers.add(onChangeCallback);
    }

    @Override
    public boolean removeOnChangeCallback(BiConsumer<StatusEntry, byte[]> onChangeCallback) {

        groupMembershipBean.assertPartecipating();
        Assert.checkNotNullParam("onChangeCallback", onChangeCallback);

        return consumers.remove(onChangeCallback);
    }

    private Path createPath(String dataNode, String key) {
        return Path.of(dataNode, key);
    }
}
