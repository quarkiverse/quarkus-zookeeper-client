package io.quarkiverse.zookeeper.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SessionConfig {

    public static final String CONNECTION_STRING = "quarkus.zookeeper.session.connection-string";
    public static final String TIMEOUT = "quarkus.zookeeper.session.timeout";
    public static final String TIMEOUT_DEFAULT = "30000";
    public static final String CAN_BE_READ_ONLY = "quarkus.zookeeper.session.can-be-read-only";

    /**
     * Comma separated host:port pairs, each corresponding to a zk server.
     */
    @ConfigItem
    public String connectionString;

    /**
     * Session timeout in milliseconds.
     */
    @ConfigItem(defaultValue = TIMEOUT_DEFAULT, defaultValueDocumentation = TIMEOUT_DEFAULT)
    public int timeout;

    /**
     * Whether the created client is allowed to go to read-only mode in case of partitioning. Read-only mode basically means
     * that if the client can't find any majority servers but there's partitioned server it could reach, it connects to one in
     * read-only mode.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public boolean canBeReadOnly;
}
