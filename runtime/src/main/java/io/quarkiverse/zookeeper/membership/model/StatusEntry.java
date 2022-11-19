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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatusEntry other = (StatusEntry) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }
}
