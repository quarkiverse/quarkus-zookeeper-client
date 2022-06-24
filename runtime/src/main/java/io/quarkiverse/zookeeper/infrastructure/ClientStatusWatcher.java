package io.quarkiverse.zookeeper.infrastructure;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ClientStatusWatcher implements Watcher {

    private static final Logger LOG = Logger.getLogger(ClientStatusWatcher.class);

    @Inject
    javax.enterprise.event.Event<WatchedEvent> bus;

    @Override
    public void process(WatchedEvent event) {

        LOG.infof("Firing asynch [%s] - [%s] - [%s]", event.getPath(), event.getState(), event.getType());

        bus.fireAsync(event);
    }
}
