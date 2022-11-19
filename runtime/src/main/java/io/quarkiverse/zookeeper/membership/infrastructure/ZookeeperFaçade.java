package io.quarkiverse.zookeeper.membership.infrastructure;

import static java.util.Collections.emptySet;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

@ApplicationScoped
public class ZookeeperFaçade {

    @Inject
    GroupMembershipBean groupMembershipBean;
    @Inject
    ZooKeeper zk;

    public byte[] write(String path, byte[] value, boolean retrievePreviousValue) {
        byte[] rv = null;

        var lock = Path.of(groupMembershipBean.lockNode(), String.valueOf(path.hashCode())).toString();
        try {
            zk.create(lock, groupMembershipBean.name().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            if (zk.exists(path, false) != null) {
                rv = zk.getData(path, false, new Stat());
                zk.setData(path, value, 0);
            } else {
                var lockTable = Path.of(groupMembershipBean.lockNode(), "table").toString();
                try {
                    zk.create(lockTable, groupMembershipBean.name().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    zk.create(path, value, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } finally {
                    try {
                        zk.delete(lockTable, 0);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        // TODO
                    } catch (KeeperException e) {
                        // TODO
                    }
                }
            }
        } catch (NodeExistsException e) {
            // TODO
        } catch (KeeperException e) {
            // TODO
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO
        } finally {
            try {
                zk.delete(lock, 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO
            } catch (KeeperException e) {
                // TODO
            }
        }
        return rv;
    }

    public byte[] read(String path) {
        byte[] rv = null;

        var lock = Path.of(groupMembershipBean.lockNode(), String.valueOf(path.hashCode())).toString();
        try {
            zk.create(lock, groupMembershipBean.name().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            if (zk.exists(path, false) != null) {
                rv = zk.getData(path, false, new Stat());
            }
        } catch (NodeExistsException e) {
            // TODO
        } catch (KeeperException e) {
            // TODO
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO
        } finally {
            try {
                zk.delete(lock, 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO
            } catch (KeeperException e) {
                // TODO
            }
        }
        return rv;
    }

    public Set<String> listNodes(String path) {
        Set<String> rv = emptySet();
        var lockTable = Path.of(groupMembershipBean.lockNode(), "table").toString();
        try {
            zk.create(lockTable, groupMembershipBean.name().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            var asd = zk.getChildren(path, false);
            rv = new HashSet<>(asd);
        } catch (NodeExistsException e) {
            // TODO
        } catch (KeeperException e) {
            // TODO
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO
        } finally {
            try {
                zk.delete(lockTable, 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO
            } catch (KeeperException e) {
                // TODO
            }
        }
        return rv;
    }
}
