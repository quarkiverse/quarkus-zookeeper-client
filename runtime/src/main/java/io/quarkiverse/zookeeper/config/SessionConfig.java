package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SessionConfig {

    public static final String CONNECTION_STRING = "quarkus.zookeeper.session.connectionString";
    public static final String TIMEOUT = "quarkus.zookeeper.session.timeout";
    public static final String CAN_BE_READ_ONLY = "quarkus.zookeeper.session.canBeReadOnly";

    /**
     * Comma separated host:port pairs, each corresponding to a zk server.
     */
    @ConfigItem(name = "connectionString")
    public String connectionString;

    /**
     * Session timeout in milliseconds.
     */
    @ConfigItem(defaultValue = "30000", defaultValueDocumentation = "30000")
    public Optional<Integer> timeout;

    /**
     * Whether the created client is allowed to go to read-only mode in case of partitioning. Read-only mode basically means
     * that if the client can't find any majority servers but there's partitioned server it could reach, it connects to one in
     * read-only mode.
     */
    @ConfigItem(name = "canBeReadOnly", defaultValue = "false", defaultValueDocumentation = "false")
    public Optional<Boolean> canBeReadOnly;
}
