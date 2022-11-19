package io.quarkiverse.zookeeper.membership.infrastructure;

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkiverse.zookeeper.config.GroupMembershipConfiguration;
import io.quarkiverse.zookeeper.membership.model.GroupMembership;
import io.quarkiverse.zookeeper.membership.model.GroupMembershipEvent;

@ApplicationScoped
public class GroupMembershipBean implements GroupMembership {

    private static final Logger LOG = Logger.getLogger(GroupMembership.class);

    private static final Set<KeeperState> CONNECTED_STATES = Set.of(KeeperState.ConnectedReadOnly,
            KeeperState.SaslAuthenticated, KeeperState.SyncConnected);

    @Inject
    Event<GroupMembershipEvent> event;
    @Inject
    ZooKeeper client;

    private volatile PartyStatus partyStatus = PartyStatus.Alone;

    @ConfigProperty(name = GroupMembershipConfiguration.NAMESPACE)
    String namespace;
    @ConfigProperty(name = GroupMembershipConfiguration.MEMBERSHIP_GROUP_ID)
    String groupId;

    private Path nsPath;
    private Path groupPath;
    private String dataNode;
    private String selfNode;

    private String name;
    {
        try {
            name = UUID.randomUUID().toString() + "-" + String.valueOf(SecureRandom.getInstanceStrong().nextLong());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
        LOG.debugf("Handling group membership on connection state change, %s", event);
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

    @PostConstruct
    public void init() {

        this.nsPath = Path.of("/", namespace);
        this.groupPath = this.nsPath.resolve(groupId);
        this.dataNode = this.groupPath.resolve("status").toString();
        this.selfNode = this.groupPath.resolve(name).toString();

        synchronized (partyStatus) {
            tryJoin();
        }
    }

    @PreDestroy
    public void leave() {
        if (client.getState().isConnected() && partyStatus.partecipating()) {
            LOG.debugf("[%s] is leaving [%s]", name, groupId);
            removeSelfNode();
        }
        partyStatus = PartyStatus.Alone;
        event.fireAsync(new GroupMembershipEvent(partyStatus));
        LOG.infof("[%s] has left the group [%s]", name, groupId);
    }

    private void tryJoin() {
        if (client.getState().isConnected() && !partyStatus.partecipating()) {
            LOG.debugf("Joining [%s] as [%s]", groupId, name);
            grantNodeHierarchy();
            partyStatus = PartyStatus.Partecipating;
            event.fireAsync(new GroupMembershipEvent(partyStatus));
            LOG.debugf("[%s] has joined the group [%s]", name, groupId);
        }
    }

    private void grantNodeHierarchy() {
        grantNamespaceNode();
        grantGroupNode();
        grantDataNode();
        grantSelfNode();
    }

    private void grantNamespaceNode() {
        var ns = this.nsPath.toString();
        if (!nodeExists(ns)) {
            LOG.tracef("Creating the namespace [%s]", ns);
            grantNode(ns, CreateMode.CONTAINER);
        }
    }

    private void grantGroupNode() {
        var group = this.groupPath.toString();
        if (!nodeExists(group)) {
            LOG.tracef("Creating the group [%s]", group);
            grantNode(group, CreateMode.CONTAINER);
        }
    }

    private void grantDataNode() {
        if (!nodeExists(this.dataNode)) {
            LOG.tracef("Creating the data node [%s]", this.dataNode);
            grantNode(this.dataNode, CreateMode.PERSISTENT);
        }
    }

    private void grantSelfNode() {
        if (!nodeExists(this.selfNode)) {
            LOG.tracef("Creating self [%s]", this.selfNode);
            grantNode(this.selfNode, CreateMode.EPHEMERAL);
        }
    }

    private void removeSelfNode() {
        // No children are expected
        if (!nodeExists(this.selfNode))
            removeNode(this.selfNode, 1);
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

    private boolean nodeExists(String path) {
        try {
            return client.exists(path, false) != null;
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
