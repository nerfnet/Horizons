package net.havocmc.transport.proto.signal.challenge;

import net.havocmc.islands.challenge.IslandChallenge;
import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class ChallengeStatus04 implements BufferedObject {

    private UUID playerId;

    private IslandChallenge challenge;
    private int count;

    public ChallengeStatus04 forPlayer(UUID uuid) {
        this.playerId = uuid;
        return this;
    }

    public ChallengeStatus04 challenge(IslandChallenge challenge) {
        this.challenge = challenge;
        return this;
    }

    public ChallengeStatus04 incrementWith(int amount) {
        this.count = amount;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x04";
    }

    public IslandChallenge getChallenge() {
        return challenge;
    }

    public int getCount() {
        return count;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
