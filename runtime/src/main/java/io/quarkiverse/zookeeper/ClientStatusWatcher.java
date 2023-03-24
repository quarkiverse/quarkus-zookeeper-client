package io.quarkiverse.zookeeper;

import jakarta.enterprise.inject.spi.CDI;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.jboss.logging.Logger;

public class ClientStatusWatcher implements Watcher {

    private static final Logger LOG = Logger.getLogger(ClientStatusWatcher.class);

    @Override
    public void process(WatchedEvent event) {

        LOG.infof("Firing asynch [%s] - [%s] - [%s]", event.getPath(), event.getState(), event.getType());

        CDI.current().getBeanManager().getEvent().fireAsync(event);
    }
}
