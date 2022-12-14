package io.quarkiverse.zookeeper.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class ClientConfig {

    public static final String CONNECTION_TIMEOUT_MILLIS = "quarkus.zookeeper.client.connection-timeout-millis";
    public static final String CONNECTION_TIMEOUT_DEFAULT = "30000";
    public static final String REQUEST_TIMEOUT_DEFAULT = "5000";

    /**
     * Client authentication
     */
    @ConfigItem
    public SaslConfig auth;

    /**
     * Client confidentiality
     */
    @ConfigItem
    public SecureClientConfig ssl;

    /**
     * Connection timeout in millis.
     */
    @ConfigItem(defaultValue = CONNECTION_TIMEOUT_DEFAULT, defaultValueDocumentation = CONNECTION_TIMEOUT_DEFAULT)
    public int connectionTimeoutMillis;

    /**
     * Request timeout in millis.
     */
    @ConfigItem(defaultValue = REQUEST_TIMEOUT_DEFAULT, defaultValueDocumentation = REQUEST_TIMEOUT_DEFAULT)
    public int requestTimeoutMillis;

    /**
     * This switch controls whether automatic watch resetting is enabled. Clients automatically reset watches during session
     * reconnect by default, this option allows the client to turn off this behavior by setting zookeeper.disableAutoWatchReset
     * to true.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public boolean disableAutowatchReset;

    /**
     * If you want to connect to the server secure client port, you need to set this property to true on the client. This will
     * connect to server using SSL with specified credentials. Note that it requires the Netty client.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public boolean secure;

    /**
     * Specifies which ClientCnxnSocket to be used. Possible values are org.apache.zookeeper.ClientCnxnSocketNIO and
     * org.apache.zookeeper.ClientCnxnSocketNetty . Default is org.apache.zookeeper.ClientCnxnSocketNIO . If you want to connect
     * to server's secure client port, you need to set this property to org.apache.zookeeper.ClientCnxnSocketNetty on client.
     */
    @ConfigItem(defaultValue = "org.apache.zookeeper.ClientCnxnSocketNIO", defaultValueDocumentation = "org.apache.zookeeper.ClientCnxnSocketNIO")
    public String clientCnxnSocket;

    /**
     * In the client side, it specifies the maximum size of the incoming data from the server. The default is 0xfffff(1048575)
     * bytes, or just under 1M. This is really a sanity check. The ZooKeeper server is designed to store and send data on the
     * order of kilobytes. If incoming data length is more than this value, an IOException is raised. This value of client side
     * should keep same with the server side(Setting System.setProperty("jute.maxbuffer", "xxxx") in the client side will work),
     * otherwise problems will arise.
     */
    @ConfigItem(defaultValue = "1048575", defaultValueDocumentation = "1048575")
    public int juteMaxBuffer;

    @Override
    public String toString() {
        return "ClientConfig [auth=" + auth + ", ssl=" + ssl + ", connectionTimeoutMillis=" + connectionTimeoutMillis
                + ", requestTimeoutMillis=" + requestTimeoutMillis + ", disableAutowatchReset=" + disableAutowatchReset
                + ", secure=" + secure + ", clientCnxnSocket=" + clientCnxnSocket + ", juteMaxBuffer=" + juteMaxBuffer
                + "]";
    }
}
