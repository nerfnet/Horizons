package net.havocmc.service.player;

import com.google.common.collect.Lists;
import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.islands.Island;
import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.IslandChallenge;
import net.havocmc.islands.challenge.preset.Farmable;
import net.havocmc.islands.challenge.preset.Mineable;
import net.havocmc.islands.challenge.preset.Slayable;
import net.havocmc.islands.challenge.type.FarmChallenge;
import net.havocmc.islands.challenge.type.MineChallenge;
import net.havocmc.islands.challenge.type.SlayChallenge;
import net.havocmc.transport.GloArgs;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Giovanni on 28/02/2018.
 * <p>
 * {@link Player}'s data model, all data is cached and live, database I/O operations are locked until the player logs out.
 */
public class PlayerContainer implements Serializable {

    private int experience;
    private int gems;
    private int money;

    private LinkedTreeMap<IslandChallenge, Integer> challengeProgression = new LinkedTreeMap<>();
    private List<IslandChallenge> completedChallenges = Lists.newArrayList();

    private transient PlayerIsland island;

    public PlayerIsland getIsland() {
        return island;
    }

    public PlayerContainer setIsland(PlayerIsland island) {
        this.island = island;
        return this;
    }

    public boolean startedChallenge(IslandChallenge challenge) {
        return challengeProgression.containsKey(challenge);
    }

    public boolean startedChallengeOfType(ChallengeType type) {
        for (IslandChallenge challenge : challengeProgression.keySet())
            if (challenge.get().getType() == type) return true;
        return false;
    }

    public List<IslandChallenge> findStartedChallenges(Mineable mineable) {
        List<IslandChallenge> challenges = Lists.newArrayList();

        challengeProgression.keySet().forEach(challenge -> {
            if (challenge.get() instanceof MineChallenge) {
                MineChallenge mineChallenge = (MineChallenge) challenge.get();
                if (mineChallenge.getMineable() == mineable)
                    challenges.add(challenge);
            }
        });

        return challenges;
    }

    public List<IslandChallenge> findStartedChallenges(Farmable farmable) {
        List<IslandChallenge> challenges = Lists.newArrayList();

        challengeProgression.keySet().forEach(challenge -> {
            if (challenge.get() instanceof FarmChallenge) {
                FarmChallenge farmChallenge = (FarmChallenge) challenge.get();
                if (farmChallenge.getFarmable() == farmable)
                    challenges.add(challenge);
            }
        });

        return challenges;
    }

    public List<IslandChallenge> findStartedChallenges(Slayable slayable) {
        List<IslandChallenge> challenges = Lists.newArrayList();

        challengeProgression.keySet().forEach(challenge -> {
            if (challenge.get() instanceof SlayChallenge) {
                SlayChallenge slayChallenge = (SlayChallenge) challenge.get();
                if (slayChallenge.getSlayable() == slayable)
                    challenges.add(challenge);
            }
        });

        return challenges;
    }

    public void addCompletedChallenge(IslandChallenge islandChallenge) {
        this.completedChallenges.add(islandChallenge);
    }

    public boolean completedChallenge(IslandChallenge islandChallenge) {
        return completedChallenges.contains(islandChallenge);
    }

    public List<IslandChallenge> getCompletedChallenges() {
        return completedChallenges;
    }

    public void setCompletedChallenges(List<IslandChallenge> completedChallenges) {
        this.completedChallenges = completedChallenges;
    }

    public LinkedTreeMap<IslandChallenge, Integer> getChallengeProgression() {
        return challengeProgression;
    }

    public void setChallengeProgression(LinkedTreeMap<IslandChallenge, Integer> challengeProgression) {
        this.challengeProgression = challengeProgression;
    }

    public int getProgressionOfChallenge(IslandChallenge challenge) {
        return challengeProgression.get(challenge);
    }

    public void setProgressionOfChallenge(IslandChallenge challenge, int progression) {
        this.challengeProgression.put(challenge, challengeProgression.get(challenge) + progression);
    }

    public void startChallenge(IslandChallenge challenge) {
        this.challengeProgression.put(challenge, 0);
    }

    public void removeProgressionOf(IslandChallenge challenge) {
        this.challengeProgression.remove(challenge);
    }

    public int getExperience() {
        return experience;
    }

    public PlayerContainer setExperience(int experience) {
        this.experience = experience;
        return this;
    }

    public int getGems() {
        return gems;
    }

    public PlayerContainer setGems(int gems) {
        this.gems = gems;
        return this;
    }

    public int getMoney() {
        return money;
    }

    public PlayerContainer setMoney(int money) {
        this.money = money;
        return this;
    }

    public PlayerContainer addMoney(int money) {
        this.money += money;
        return this;
    }

    public PlayerContainer addExperience(int experience) {
        this.experience += experience;
        return this;
    }

    public PlayerContainer addGems(int gems) {
        this.gems += gems;
        return this;
    }

    public PlayerContainer removeGems(int gems) {
        this.gems -= gems;
        if (this.gems < 0) this.gems = 0;
        return this;
    }

    public String serialize() {
        return GloArgs.NO_DOUBLE_GSON.toJson(this);
    }
}
