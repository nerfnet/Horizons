package net.havocmc.islands.challenge.type;

import net.havocmc.islands.challenge.AbstractChallenge;
import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.preset.Farmable;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class FarmChallenge extends AbstractChallenge {

    private final Farmable farmable;
    private final int count;

    public FarmChallenge(Farmable farmable, int count, int expReward) {
        super(ChallengeType.FARMING, expReward);

        this.farmable = farmable;
        this.count = count;
    }

    public Farmable getFarmable() {
        return farmable;
    }

    @Override
    public int getRequirement() {
        return count;
    }

    @Override
    public String getDescription() {
        return "Farm " + count + " " + farmable.getDefaultName().toLowerCase();
    }
}
