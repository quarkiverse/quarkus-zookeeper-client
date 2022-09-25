package io.quarkiverse.zookeeper.membership.model;

import io.quarkiverse.zookeeper.membership.model.GroupMembership.PartyStatus;

public class GroupMembershipEvent {

    private PartyStatus partyStatus;

    public GroupMembershipEvent(PartyStatus partyStatus) {
        this.partyStatus = partyStatus;
    }

    public final PartyStatus getPartyStatus() {
        return partyStatus;
    }
}
