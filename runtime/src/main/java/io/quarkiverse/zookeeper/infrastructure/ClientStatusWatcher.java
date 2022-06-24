package io.quarkiverse.zookeeper.infrastructure;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

@ApplicationScoped
public class ClientStatusWatcher implements Watcher {

    @Inject javax.enterprise.event.Event<WatchedEvent> bus;

    @Override
    public void process(WatchedEvent event) {
        bus.fireAsync(event);
    }
}
