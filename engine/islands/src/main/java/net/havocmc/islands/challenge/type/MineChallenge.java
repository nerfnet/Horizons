package net.havocmc.islands.challenge.type;

import net.havocmc.islands.challenge.AbstractChallenge;
import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.preset.Mineable;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class MineChallenge extends AbstractChallenge {

    private final Mineable mineable;
    private final int count;

    public MineChallenge(Mineable mineable, int count, int expReward) {
        super(ChallengeType.MINING, expReward);

        this.mineable = mineable;
        this.count = count;
    }

    public Mineable getMineable() {
        return mineable;
    }

    @Override
    public int getRequirement() {
        return count;
    }

    @Override
    public String getDescription() {
        return "Mine " + count + " " + mineable.getDefaultName().toLowerCase();
    }
}
