package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(prefix = "quarkus.zookeeper.client", name = "ssl", phase = ConfigPhase.RUN_TIME)
public class SecureClientConfig {

    /**
     * Specifies the file path to a JKS containing the local credentials to be used for SSL connections.
     */
    @ConfigItem(name = "keyStore.location")
    public Optional<String> keyStoreLocation;

    /**
     * Specifies the password to unlock the file.
     */
    @ConfigItem(name = "keyStore.password")
    public Optional<String> keyStorePassword;

    /**
     * Specifies the file path to a JKS containing the remote credentials to be used for SSL connections.
     */
    @ConfigItem(name = "trustStore.location")
    public Optional<String> trustStoreLocation;

    /**
     * Specifies the password to unlock the file.
     */
    @ConfigItem(name = "trustStore.password")
    public Optional<String> trustStorePassword;

    /**
     * Specifies the file format of keys store files used to establish TLS connection to the ZooKeeper server. Values: JKS, PEM,
     * PKCS12 or null.
     */
    @ConfigItem(name = "keyStore.type")
    public Optional<String> keyStoreType;

    /**
     * Specifies the file format of trust store files used to establish TLS connection to the ZooKeeper server. Values: JKS,
     * PEM, PKCS12 or null.
     */
    @ConfigItem(name = "trustStore.type")
    public Optional<String> trustStoreType;
}
