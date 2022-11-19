package io.quarkiverse.zookeeper.membership.infrastructure;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;

import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoWatcherException;
import org.apache.zookeeper.Watcher.WatcherType;
import org.apache.zookeeper.ZooKeeper;

import com.oracle.svm.core.annotate.Inject;

import io.quarkiverse.zookeeper.MembershipStatusWatcher;
import io.quarkiverse.zookeeper.membership.model.GroupMembershipEvent;

@ApplicationScoped
public class MembershipStatusWatcherHolder {

    @Inject
    ZooKeeper zookeeper;
    @Inject
    GroupMembershipBean groupMembership;

    private AtomicBoolean watching = new AtomicBoolean(false);

    public synchronized void onGroupMembership(@ObservesAsync GroupMembershipEvent event) {
        if (event.getPartyStatus().partecipating()) {
            startWatching(groupMembership.dataNode(), groupMembership.name());
        } else {
            stopWatching(groupMembership.dataNode(), groupMembership.name());
        }
    }

    private void startWatching(String path, String name) {
        try {
            zookeeper.addWatch(path, MembershipStatusWatcher.forKey(name), AddWatchMode.PERSISTENT);
            watching.set(true);
        } catch (KeeperException e) {
            // TODO
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO
        }
    }

    private void stopWatching(String path, String name) {
        try {
            zookeeper.removeWatches(path, MembershipStatusWatcher.forKey(name), WatcherType.Any, true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO
        } catch (NoWatcherException e) {
            // Deliberately not managed
        } catch (KeeperException e) {
            // TODO
        } finally {
            watching.set(false);
        }
    }
}
