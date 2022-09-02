package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SecureClientConfig {

    /**
     * Specifies the file path to a JKS containing the local credentials to be used for SSL connections.
     */
    @ConfigItem
    public Optional<String> keyStoreLocation;

    /**
     * Specifies the password to unlock the file.
     */
    @ConfigItem
    public Optional<String> keyStorePassword;

    /**
     * Specifies the file path to a JKS containing the remote credentials to be used for SSL connections.
     */
    @ConfigItem
    public Optional<String> trustStoreLocation;

    /**
     * Specifies the password to unlock the file.
     */
    @ConfigItem
    public Optional<String> trustStorePassword;

    /**
     * Specifies the file format of keys store files used to establish TLS connection to the ZooKeeper server. Values: JKS, PEM,
     * PKCS12 or null.
     */
    @ConfigItem
    public Optional<String> keyStoreType;

    /**
     * Specifies the file format of trust store files used to establish TLS connection to the ZooKeeper server. Values: JKS,
     * PEM, PKCS12 or null.
     */
    @ConfigItem
    public Optional<String> trustStoreType;

    @Override
    public String toString() {
        return "SecureClientConfig [keyStoreLocation=" + keyStoreLocation + ", keyStorePassword=" + keyStorePassword
                + ", trustStoreLocation=" + trustStoreLocation + ", trustStorePassword=" + trustStorePassword
                + ", keyStoreType=" + keyStoreType + ", trustStoreType=" + trustStoreType + "]";
    }

}
