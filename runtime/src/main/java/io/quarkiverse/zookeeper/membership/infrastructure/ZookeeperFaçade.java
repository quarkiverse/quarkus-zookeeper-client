package io.quarkiverse.zookeeper.membership.infrastructure;

import java.nio.file.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

@ApplicationScoped
public class ZookeeperFaçade {

    @Inject GroupMembershipBean groupMembershipBean;
    @Inject ZooKeeper zk;

    public byte[] write(Path path, byte[] value, boolean retrievePreviousValue) {
        var lock = Path.of(groupMembershipBean.lockNode(), String.valueOf(path.hashCode())).toString();
        try {
            zk.create(lock, groupMembershipBean.name().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
        return null;
    }
}
