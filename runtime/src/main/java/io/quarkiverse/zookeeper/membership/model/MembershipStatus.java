package io.quarkiverse.zookeeper.membership.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MembershipStatus {
    /**
     * Put a value, identified by the key, into the group status. It performs a
     * version test (optimistic lock) raising a runtime exception if the test fails.
     *
     * @param value the value to write.
     * @param key the key which identifies the value.
     * @return an optional array of bytes representing the previous data; an empty
     *         optional if the key has been just created.
     */
    Optional<byte[]> put(byte[] value, String key);

    /**
     * Get a value, identified by the key, from the group status. It refreshes the
     * data version accordingly to the read value.
     *
     * @param key the key which identifies the value.
     * @return an optional array of bytes representing the data; an empty optional
     *         if the key does not identifies any value within the group status.
     */
    Optional<byte[]> get(String key);

    /**
     * Removes a value, identified by the key, from the group status. It also reset
     * the data version for the given key.
     *
     * @param key the key which identifies the value.
     * @return an optional array of bytes representing the data; an empty optional
     *         if the key does not identifies any value within the group status.
     */
    Optional<byte[]> clear(String key);

    /**
     * Reads all the keys available within the group status.
     *
     * @return the set of the keys.
     */
    Set<String> keys();

    /**
     * An iterator over the group status entries.
     *
     * @return the iterator.
     */
    Iterator<Map.Entry<String, byte[]>> iterator();
}
