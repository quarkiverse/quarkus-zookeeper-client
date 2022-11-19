package io.quarkiverse.zookeeper.membership.model;

public class StatusEntry {

    private String key;
    private byte[] value;

    public static StatusEntry of(String key, byte[] value) {
        return new StatusEntry(key, value);
    }

    private StatusEntry(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return key;
    }

    public byte[] value() {
        return value;
    }
}
