package net.havocmc.islands.challenge.type;

import net.havocmc.islands.challenge.AbstractChallenge;
import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.preset.Slayable;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class SlayChallenge extends AbstractChallenge {

    private final Slayable slayable;
    private final int count;

    public SlayChallenge(Slayable slayable, int count, int expReward) {
        super(ChallengeType.SLAYING, expReward);

        this.slayable = slayable;
        this.count = count;
    }

    public Slayable getSlayable() {
        return slayable;
    }

    @Override
    public int getRequirement() {
        return count;
    }

    @Override
    public String getDescription() {
        return "Kill " + count + " " + slayable.name().toLowerCase() + "s";
    }
}
