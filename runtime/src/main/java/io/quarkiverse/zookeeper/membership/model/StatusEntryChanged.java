package io.quarkiverse.zookeeper.membership.model;

public class StatusEntryChanged {

    private StatusEntry newValue;
    private byte[] previousValue;

    private StatusEntryChanged(StatusEntry newValue, byte[] previousValue) {
        this.newValue = newValue;
        this.previousValue = previousValue;
    }

    public StatusEntry newValue() {
        return newValue;
    }

    public byte[] previousValue() {
        return previousValue;
    }
}
