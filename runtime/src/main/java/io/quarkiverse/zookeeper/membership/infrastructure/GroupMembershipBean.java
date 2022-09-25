package io.quarkiverse.zookeeper.membership.infrastructure;

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.event.Event;
import javax.enterprise.event.ObservesAsync;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.jboss.logging.Logger;

import io.quarkiverse.zookeeper.membership.model.GroupMembership;
import io.quarkiverse.zookeeper.membership.model.GroupMembershipEvent;

public class GroupMembershipBean implements GroupMembership {

    private static final Logger LOG = Logger.getLogger(GroupMembership.class);

    private static final Set<KeeperState> CONNECTED_STATES = Set.of(KeeperState.ConnectedReadOnly,
            KeeperState.SaslAuthenticated, KeeperState.SyncConnected);

    private Event<Object> event;
    private ZooKeeper client;

    private volatile PartyStatus partyStatus = PartyStatus.Alone;

    private Path selfNode;

    private String groupId;
    private String name;
    {
        try {
            name = UUID.randomUUID().toString() + "-" + String.valueOf(SecureRandom.getInstanceStrong().nextLong());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public GroupMembershipBean(ZooKeeper client, Event<Object> event) {
        this.client = client;
        this.event = event;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public PartyStatus partyStatus() {
        return partyStatus;
    }

    public void watchConnectionState(@ObservesAsync WatchedEvent event) {
        if (EventType.None.equals(event.getType())) {
            synchronized (partyStatus) {
                if (CONNECTED_STATES.contains(event.getState())) {
                    tryJoin();
                } else {
                    leave();
                }
            }
        }
    }

    public void init(String namespace, String groupId) {
        this.groupId = groupId;

        var nsPath = Path.of("/", namespace);
        var groupPath = nsPath.resolve(groupId);
        var dataPath = groupPath.resolve("status");

        this.selfNode = groupPath.resolve(name);

        synchronized (partyStatus) {
            grantNamespaceNode(nsPath.toString());
            grantGroupNode(groupPath.toString());
            grantDataNode(dataPath.toString());
            tryJoin();
        }
    }

    public void leave() {
        if (client.getState().isConnected() && partyStatus.partecipating()) {
            LOG.debugf("[%s] is leaving [%s]", name, groupId);
            partyStatus = PartyStatus.Alone;
            event.fireAsync(new GroupMembershipEvent(partyStatus));
            removeSelfNode();
            LOG.infof("[%s] has left the group [%s]", name, groupId);
        }
    }

    private void tryJoin() {
        if (client.getState().isConnected() && !partyStatus.partecipating()) {
            LOG.debugf("Joining [%s] as [%s]", groupId, name);
            grantSelfNode();
            partyStatus = PartyStatus.Partecipating;
            event.fireAsync(new GroupMembershipEvent(partyStatus));
            LOG.debugf("[%s] has joined the group [%s]", name, groupId);
        }
    }

    private void grantNamespaceNode(String namespace) {
        grantNode(namespace, CreateMode.CONTAINER);
    }

    private void grantGroupNode(String groupNode) {
        grantNode(groupNode, CreateMode.CONTAINER);
    }

    private void grantDataNode(String dataNode) {
        grantNode(dataNode, CreateMode.PERSISTENT);
    }

    private void grantSelfNode() {
        grantNode(this.selfNode.toString(), CreateMode.EPHEMERAL);
    }

    private void removeSelfNode() {
        // No children are expected
        removeNode(this.selfNode.toString(), 1);
    }

    private void grantNode(String path, CreateMode mode) {
        try {
            var creationTs = String.valueOf(Instant.now().getEpochSecond()).getBytes();
            client.create(path, creationTs, Ids.OPEN_ACL_UNSAFE, mode);
        } catch (NodeExistsException e) {
            // Nothing to do
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void removeNode(String path, int cversion) {
        try {
            client.delete(path, cversion);
        } catch (NoNodeException e) {
            // Nothing to do
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
