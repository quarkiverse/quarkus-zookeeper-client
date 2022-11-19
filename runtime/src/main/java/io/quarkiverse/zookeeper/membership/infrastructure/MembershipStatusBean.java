package io.quarkiverse.zookeeper.membership.infrastructure;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import io.quarkiverse.zookeeper.membership.model.MembershipStatus;

@ApplicationScoped
public class MembershipStatusBean implements MembershipStatus {

    @Override
    public Optional<byte[]> put(byte[] value, String key) {
        // TODO Auto-generated method stub
        return Optional.empty();
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
    public Iterator<Entry<String, byte[]>> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

}
