package net.havocmc.transport.proto.signal.challenge;

import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.preset.Farmable;
import net.havocmc.islands.challenge.preset.Mineable;
import net.havocmc.islands.challenge.preset.Slayable;
import net.havocmc.transport.proto.BufferedObject;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by Giovanni on 09/06/2018.
 * <p>
 * A challenge update packet which is identical to {@link ChallengeStatus04}, except a lazy challenge status
 * update looks for started challenges of a specific {@link net.havocmc.islands.challenge.ChallengeType} and updates them.
 */
public class LazyChallengeStatus04 implements BufferedObject {

    private UUID playerId;

    private ChallengeType challengeType;
    private int amount;

    @Nullable
    private Farmable farmable;
    @Nullable
    private Slayable slayable;
    @Nullable
    private Mineable mineable;

    public LazyChallengeStatus04 farmable(Farmable farmable) {
        this.farmable = farmable;
        return this;
    }

    public LazyChallengeStatus04 slayable(Slayable slayable) {
        this.slayable = slayable;
        return this;
    }

    public LazyChallengeStatus04 mineable(Mineable mineable) {
        this.mineable = mineable;
        return this;
    }

    public LazyChallengeStatus04 forPlayer(UUID uuid) {
        this.playerId = uuid;
        return this;
    }


    public LazyChallengeStatus04 type(ChallengeType type) {
        this.challengeType = type;
        return this;
    }

    public LazyChallengeStatus04 incrementWith(int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x04";
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }

    public int getAmount() {
        return amount;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    @Nullable
    public Farmable getFarmable() {
        return farmable;
    }

    @Nullable
    public Slayable getSlayable() {
        return slayable;
    }

    @Nullable
    public Mineable getMineable() {
        return mineable;
    }
}
