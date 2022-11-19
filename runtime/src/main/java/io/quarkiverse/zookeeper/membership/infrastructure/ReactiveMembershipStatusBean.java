package io.quarkiverse.zookeeper.membership.infrastructure;

import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import io.quarkiverse.zookeeper.membership.model.ReactiveMembershipStatus;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ReactiveMembershipStatusBean implements ReactiveMembershipStatus {

    @Override
    public Uni<byte[]> put(byte[] value, String key) {
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
    public Multi<Entry<String, byte[]>> entries() {
        // TODO Auto-generated method stub
        return null;
    }

}
