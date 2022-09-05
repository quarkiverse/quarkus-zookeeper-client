package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SaslConfig {

    /**
     * Set the value to true to enable SASL authentication.
     */
    @ConfigItem(name = "sasl.enabled", defaultValue = "false", defaultValueDocumentation = "false")
    public boolean enabled;

    /**
     * Specifies the context key in the JAAS login file.
     */
    @ConfigItem(defaultValue = "Client", defaultValueDocumentation = "Client")
    public String clientconfig;

    /**
     * SALS client configuration string, e.g.
     *
     * <pre>
     * org.apache.zookeeper.server.auth.DigestLoginModule required username="test" password="passwd";
     * </pre>
     */
    @ConfigItem(name = "sasl.config-string")
    public Optional<String> configString;

    /**
     * Specifies the server principal to be used by the client for authentication, while connecting to the zookeeper server,
     * when Kerberos authentication is enabled. If this configuration is provided, then the ZooKeeper client will NOT USE any of
     * the following parameters to determine the server principal: zookeeper.sasl.client.username,
     * zookeeper.sasl.client.canonicalize.hostname, zookeeper.server.realm.
     */
    @ConfigItem
    public Optional<String> serverPrincipal;

    /**
     * Traditionally, a principal is divided into three parts: the primary, the instance, and the realm. The format of a typical
     * Kerberos V5 principal is primary/instance@REALM. zookeeper.sasl.client.username specifies the primary part of the server
     * principal.
     */
    @ConfigItem(defaultValue = "zookeeper", defaultValueDocumentation = "zookeeper")
    public String clientUsername;

    /**
     * Expecting the zookeeper.server.principal parameter is not provided, the ZooKeeper client will try to determine the
     * 'instance' (host) part of the ZooKeeper server principal. First it takes the hostname provided as the ZooKeeper server
     * connection string. Then it tries to 'canonicalize' the address by getting the fully qualified domain name belonging to
     * the address. You can disable this 'canonicalization' by setting: zookeeper.sasl.client.canonicalize.hostname=false.
     */
    @ConfigItem(defaultValue = "true", defaultValueDocumentation = "true")
    public boolean clientCanonicalizeHostname;

    /**
     * Realm part of the server principal. By default it is the client principal realm.
     */
    @ConfigItem
    public Optional<String> serverRealm;

    @Override
    public String toString() {
        return "SaslConfig [enabled=" + enabled + ", clientconfig=" + clientconfig + ", configString=" + configString
                + ", serverPrincipal=" + serverPrincipal + ", clientUsername=" + clientUsername
                + ", clientCanonicalizeHostname=" + clientCanonicalizeHostname + ", serverRealm=" + serverRealm + "]";
    }
}
