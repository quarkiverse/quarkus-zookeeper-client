package io.quarkiverse.zookeeper.membership.model;

public interface GroupMembership {

    /**
     * Membership status represents if the component is partecipating to the group or not.
     */
    public enum PartyStatus {
        /**
         * The component has joined the group.
         */
        Partecipating,
        /**
         * The component is outside the group.
         */
        Alone;

        /**
         * @return true if the instance is partecipating to a group.
         */
        public boolean partecipating() {
            return Partecipating.equals(this);
        }
    }

    /**
     * Name of the component used to join the group.
     *
     * @return the component name.
     */
    String name();

    /**
     * The component status in relation to group membership.
     *
     * @return the current status.
     */
    PartyStatus partyStatus();
}
