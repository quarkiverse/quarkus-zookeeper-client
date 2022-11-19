package io.quarkiverse.zookeeper.membership.model;

import java.util.Set;
import java.util.concurrent.Flow.Subscriber;

import io.smallrye.mutiny.Uni;

public interface ReactiveMembershipStatus {
    /**
     * Put a value, identified by the key, into the group status. It performs a
     * version test (optimistic lock) raising a runtime exception if the test fails.
     *
     * @param value the value to write.
     * @param key the key which identifies the value.
     * @return the action to put a value within the group status holding a null item
     *         if the key has been just created.
     */
    Uni<byte[]> put(byte[] value, String key);

    /**
     * Put a value, identified by the key, into the group status. It performs a
     * version test (optimistic lock) raising a runtime exception if the test fails.
     *
     * @param entry the status entry.
     * @return the action to put a value within the group status holding a null item
     *         if the key has been just created.
     */
    Uni<byte[]> put(StatusEntry entry);

    /**
     * Get a value, identified by the key, from the group status. It refreshes the
     * data version accordingly to the read value.
     *
     * @param key the key which identifies the value.
     * @return the action to put a value within the group status holding a null item
     *         if the key does not identifies any value within the group status.
     */
    Uni<byte[]> get(String key);

    /**
     * Removes a value, identified by the key, from the group status. It also reset
     * the data version for the given key.
     *
     * @param key the key which identifies the value.
     * @return the action to put a value within the group status holding a null item
     *         if the key does not identifies any value within the group status.
     */
    Uni<byte[]> clear(String key);

    /**
     * Reads all the keys available within the group status.
     *
     * @return the action to retrieve the set of the keys.
     */
    Uni<Set<String>> keys();

    /**
     * Add a subscriber for the group status changes.
     *
     * @param onChangeSubscriber the subscriber to manage the current status in form
     *        of {@link StatusEntryChanged}.
     */
    void addAnChangeSubscriber(Subscriber<StatusEntryChanged> onChangeSubscriber);

    /**
     * Removes a subscriber for the group status changes.
     *
     * @param onChangeSubscriber the subscriber expected to manage the current
     *        status in form of {@link StatusEntryChanged}.
     */
    boolean removeOnChangeSubscriber(Subscriber<StatusEntryChanged> onChangeSubscriber);
}
