package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(prefix = "quarkus.zookeeper", name = "client", phase = ConfigPhase.RUN_TIME)
public class ClientConfig {

    /**
     * This switch controls whether automatic watch resetting is enabled. Clients automatically reset watches during session
     * reconnect by default, this option allows the client to turn off this behavior by setting zookeeper.disableAutoWatchReset
     * to true.
     */
    @ConfigItem(name = "disableAutowatchReset", defaultValue = "false", defaultValueDocumentation = "false")
    public Optional<Boolean> disableAutowatchReset;

    /**
     * If you want to connect to the server secure client port, you need to set this property to true on the client. This will
     * connect to server using SSL with specified credentials. Note that it requires the Netty client.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public Optional<Boolean> secure;

    /**
     * Specifies which ClientCnxnSocket to be used. Possible values are org.apache.zookeeper.ClientCnxnSocketNIO and
     * org.apache.zookeeper.ClientCnxnSocketNetty . Default is org.apache.zookeeper.ClientCnxnSocketNIO . If you want to connect
     * to server's secure client port, you need to set this property to org.apache.zookeeper.ClientCnxnSocketNetty on client.
     */
    @ConfigItem(name = "clientCnxnSocket", defaultValue = "org.apache.zookeeper.ClientCnxnSocketNIO", defaultValueDocumentation = "org.apache.zookeeper.ClientCnxnSocketNIO")
    public Optional<String> clientCnxnSocket;

    /**
     * In the client side, it specifies the maximum size of the incoming data from the server. The default is 0xfffff(1048575)
     * bytes, or just under 1M. This is really a sanity check. The ZooKeeper server is designed to store and send data on the
     * order of kilobytes. If incoming data length is more than this value, an IOException is raised. This value of client side
     * should keep same with the server side(Setting System.setProperty("jute.maxbuffer", "xxxx") in the client side will work),
     * otherwise problems will arise.
     */
    @ConfigItem(name = "jute.maxbuffer", defaultValue = "1048575", defaultValueDocumentation = "1048575")
    public Optional<String> juteMaxBuffer;
}
