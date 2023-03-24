package io.quarkiverse.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.jboss.logging.Logger;

public class MembershipStatusWatcher implements Watcher {

    private static final Logger LOG = Logger.getLogger(MembershipStatusWatcher.class);

    private String key;

    public static MembershipStatusWatcher forKey(String key) {
        return new MembershipStatusWatcher(key);
    }

    private MembershipStatusWatcher(String key) {
        this.key = key;
    }

    @Override
    public void process(WatchedEvent event) {

        switch (event.getType()) {
            case NodeCreated:
                LOG.infof("New group status property [%s] - [%s] - [%s]", event.getPath(), event.getState(), event.getType());
                break;
            case NodeDataChanged:
                LOG.infof("Group status property changed [%s] - [%s] - [%s]", event.getPath(), event.getState(),
                        event.getType());
                break;
            case NodeDeleted:
                LOG.infof("Group status property removed [%s] - [%s] - [%s]", event.getPath(), event.getState(),
                        event.getType());
                break;
            default:
                break;

        }
        // CDI.current().getBeanManager().getEvent().fireAsync(event);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MembershipStatusWatcher other = (MembershipStatusWatcher) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }
}
