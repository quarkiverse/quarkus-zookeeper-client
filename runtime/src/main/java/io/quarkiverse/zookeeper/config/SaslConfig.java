package io.quarkiverse.zookeeper.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(prefix = "quarkus.zookeeper.client", name = "auth", phase = ConfigPhase.RUN_TIME)
public class SaslConfig {

    /**
     * Set the value to true to enable SASL authentication.
     */
    @ConfigItem(defaultValue = "false", defaultValueDocumentation = "false")
    public Optional<Boolean> client;

    /**
     * Specifies the context key in the JAAS login file.
     */
    @ConfigItem(name = "clientconfig", defaultValue = "Client", defaultValueDocumentation = "Client")
    public Optional<String> clientconfig;

    /**
     * Specifies the server principal to be used by the client for authentication, while connecting to the zookeeper server,
     * when Kerberos authentication is enabled. If this configuration is provided, then the ZooKeeper client will NOT USE any of
     * the following parameters to determine the server principal: zookeeper.sasl.client.username,
     * zookeeper.sasl.client.canonicalize.hostname, zookeeper.server.realm.
     */
    @ConfigItem(name = "serverPrincipal")
    public Optional<String> serverPrincipal;

    /**
     * Traditionally, a principal is divided into three parts: the primary, the instance, and the realm. The format of a typical
     * Kerberos V5 principal is primary/instance@REALM. zookeeper.sasl.client.username specifies the primary part of the server
     * principal.
     */
    @ConfigItem(name = "clientUsername", defaultValue = "zookeeper", defaultValueDocumentation = "zookeeper")
    public Optional<String> clientUsername;

    /**
     * Expecting the zookeeper.server.principal parameter is not provided, the ZooKeeper client will try to determine the
     * 'instance' (host) part of the ZooKeeper server principal. First it takes the hostname provided as the ZooKeeper server
     * connection string. Then it tries to 'canonicalize' the address by getting the fully qualified domain name belonging to
     * the address. You can disable this 'canonicalization' by setting: zookeeper.sasl.client.canonicalize.hostname=false.
     */
    @ConfigItem(name = "clientCanonicalizeHostname")
    public Optional<Boolean> clientCanonicalizeHostname;

    /**
     * Realm part of the server principal. By default it is the client principal realm.
     */
    @ConfigItem(name = "serverRealm")
    public Optional<String> serverRealm;
}
