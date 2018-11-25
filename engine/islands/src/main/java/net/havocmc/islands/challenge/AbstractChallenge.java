package net.havocmc.islands.challenge;

/**
 * Created by Giovanni on 09/06/2018.
 */
public abstract class AbstractChallenge {

    private final ChallengeType type;
    private final int expReward;

    public AbstractChallenge(ChallengeType type, int expReward) {
        this.type = type;
        this.expReward = expReward;
    }

    public ChallengeType getType() {
        return type;
    }

    public int getExpReward() {
        return expReward;
    }

    public abstract String getDescription();

    public abstract int getRequirement();
}
