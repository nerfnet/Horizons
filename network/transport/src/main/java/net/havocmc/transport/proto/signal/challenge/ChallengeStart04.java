package net.havocmc.transport.proto.signal.challenge;

import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.IslandChallenge;
import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class ChallengeStart04 implements BufferedObject {

    private UUID playerId;
    private IslandChallenge challenge;

    public ChallengeStart04 forPlayer(UUID playerId) {
        this.playerId = playerId;
        return this;
    }

    public ChallengeStart04 start(IslandChallenge challenge) {
        this.challenge = challenge;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x04";
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public IslandChallenge getChallenge() {
        return challenge;
    }

    public ChallengeType getType() {
        return challenge.get().getType();
    }
}
