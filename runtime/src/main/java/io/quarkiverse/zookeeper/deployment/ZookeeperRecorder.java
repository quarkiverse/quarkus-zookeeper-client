package io.quarkiverse.zookeeper.deployment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.common.ZKConfig;

import io.quarkiverse.zookeeper.ClientStatusWatcher;
import io.quarkiverse.zookeeper.config.ZookeeperConfiguration;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.smallrye.mutiny.unchecked.Unchecked;

@Recorder
public class ZookeeperRecorder {

    private ZookeeperConfiguration config;

    public ZookeeperRecorder(ZookeeperConfiguration config) {
        this.config = config;
    }

    public RuntimeValue<ZooKeeper> create(ShutdownContext shutdownContext) {

        var cfg = createZKClientConfig();

        ZooKeeper rv;
        try {
            rv = new ZooKeeper(config.session.connectionString, config.session.timeout, new ClientStatusWatcher(),
                    config.session.canBeReadOnly, cfg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shutdownContext.addShutdownTask(() -> {
            try {
                rv.close(config.session.timeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.clearProperty("java.security.auth.login.config");
        });

        return new RuntimeValue<>(rv);
    }

    private ZKClientConfig createZKClientConfig() {

        var cfg = new ZKClientConfig();

        cfg.setProperty(ZKConfig.JUTE_MAXBUFFER, String.valueOf(config.client.juteMaxBuffer));
        cfg.setProperty(ZKClientConfig.DISABLE_AUTO_WATCH_RESET, String.valueOf(config.client.disableAutowatchReset));
        cfg.setProperty(ZKClientConfig.ZOOKEEPER_CLIENT_CNXN_SOCKET, String.valueOf(config.client.clientCnxnSocket));
        cfg.setProperty(ZKClientConfig.ZOOKEEPER_REQUEST_TIMEOUT, String.valueOf(config.client.requestTimeoutMillis));

        cfg.setProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME, String.valueOf(config.client.auth.clientUsername));
        cfg.setProperty(ZKClientConfig.ZK_SASL_CLIENT_CANONICALIZE_HOSTNAME,
                String.valueOf(config.client.auth.clientCanonicalizeHostname));
        cfg.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, String.valueOf(config.client.auth.clientconfig));
        cfg.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, String.valueOf(config.client.auth.enabled));

        config.client.auth.serverPrincipal.map(String::valueOf)
                .ifPresent(value -> cfg.setProperty(ZKClientConfig.ZOOKEEPER_SERVER_PRINCIPAL, value));
        config.client.auth.serverRealm.map(String::valueOf)
                .ifPresent(value -> cfg.setProperty(ZKClientConfig.ZOOKEEPER_SERVER_REALM, value));

        var uncheckedCreateConfigFile = Unchecked
                .function((String value) -> createConfigFile(config.client.auth.clientconfig, value));
        config.client.auth.configString.map(uncheckedCreateConfigFile)
                .ifPresent(filePath -> System.setProperty("java.security.auth.login.config", filePath));

        cfg.setProperty(ZKClientConfig.SECURE_CLIENT, String.valueOf(config.client.secure));
        cfg.setProperty(ZKClientConfig.ZOOKEEPER_CLIENT_CNXN_SOCKET, config.client.clientCnxnSocket);

        return cfg;
    }

    private String createConfigFile(String clientconfig, String value) throws IOException {
        var content = String.format("%s { %s };", clientconfig, value);
        var jaasFile = File.createTempFile("jaas", ".conf");
        jaasFile.deleteOnExit();

        return Files.writeString(jaasFile.toPath(), content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                .toAbsolutePath().toString();
    }
}
