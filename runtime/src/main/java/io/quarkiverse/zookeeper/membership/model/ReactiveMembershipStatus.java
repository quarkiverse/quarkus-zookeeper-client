package io.quarkiverse.zookeeper.membership.model;

import java.util.Map;
import java.util.Set;

import io.smallrye.mutiny.Multi;
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
     * A stream of groups status entries..
     *
     * @return the stream of the entries.
     */
    Multi<Map.Entry<String, byte[]>> entries();
}
