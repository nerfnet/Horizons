package net.havocmc.islands.challenge;

import com.google.common.collect.Lists;
import net.havocmc.islands.challenge.preset.Farmable;
import net.havocmc.islands.challenge.preset.Mineable;
import net.havocmc.islands.challenge.preset.Slayable;
import net.havocmc.islands.challenge.type.FarmChallenge;
import net.havocmc.islands.challenge.type.MineChallenge;
import net.havocmc.islands.challenge.type.SlayChallenge;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Giovanni on 09/06/2018.
 */
public enum IslandChallenge {

    /* Mineable */
    GOLD_MINER_I(new MineChallenge(Mineable.GOLD, 5, 1)),
    GOLD_MINER_II(new MineChallenge(Mineable.GOLD, 25, 1)),
    GOLD_MINER_III(new MineChallenge(Mineable.GOLD, 100, 1)),
    DIAMOND_MINER_I(new MineChallenge(Mineable.DIAMOND, 5, 1)),
    DIAMOND_MINER_II(new MineChallenge(Mineable.DIAMOND, 25, 1)),
    DIAMOND_MINER_III(new MineChallenge(Mineable.DIAMOND, 100, 1)),
    REDSTONE_MINER_I(new MineChallenge(Mineable.REDSTONE, 5, 1)),
    REDSTONE_MINER_II(new MineChallenge(Mineable.REDSTONE, 25, 1)),
    REDSTONE_MINER_III(new MineChallenge(Mineable.REDSTONE, 100, 1)),

    /* FARMABLE */
    PUMPKIN_FARMER_I(new FarmChallenge(Farmable.PUMPKIN, 10, 1)),
    PUMPKIN_FARMER_II(new FarmChallenge(Farmable.PUMPKIN, 50, 1)),
    PUMPKIN_FARMER_III(new FarmChallenge(Farmable.PUMPKIN, 100, 1)),
    MELON_FARMER_I(new FarmChallenge(Farmable.MELON, 10, 1)),
    MELON_FARMER_II(new FarmChallenge(Farmable.MELON, 50, 1)),
    MELON_FARMER_III(new FarmChallenge(Farmable.MELON, 100, 1)),
    WHEAT_FARMER_I(new FarmChallenge(Farmable.WHEAT, 10, 1)),
    WHEAT_FARMER_II(new FarmChallenge(Farmable.WHEAT, 50, 1)),
    WHEAT_FARMER_III(new FarmChallenge(Farmable.WHEAT, 100, 1)),
    CACTUS_FARMER_I(new FarmChallenge(Farmable.CACTUS, 10, 1)),
    CACTUS_FARMER_II(new FarmChallenge(Farmable.CACTUS, 50, 1)),
    CACTUS_FARMER_III(new FarmChallenge(Farmable.CACTUS, 100, 1)),
    SUGAR_CANE_FARMER_I(new FarmChallenge(Farmable.SUGAR_CANE, 10, 1)),
    SUGAR_CANE_FARMER_II(new FarmChallenge(Farmable.SUGAR_CANE, 50, 1)),
    SUGAR_CANE_FARMER_III(new FarmChallenge(Farmable.SUGAR_CANE, 100, 1)),
    COCO_BEAN_FARMER_I(new FarmChallenge(Farmable.COCO_BEAN, 10, 1)),
    COCO_BEAN_FARMER_II(new FarmChallenge(Farmable.COCO_BEAN, 50, 1)),
    COCO_BEAN_FARMER_III(new FarmChallenge(Farmable.COCO_BEAN, 100, 1)),

    /* SLAYABLE */
    SPIDER_SLAYER_I(new SlayChallenge(Slayable.SPIDER, 5, 1)),
    SPIDER_SLAYER_II(new SlayChallenge(Slayable.SPIDER, 25, 1)),
    SPIDER_SLAYER_III(new SlayChallenge(Slayable.SPIDER, 60, 1)),
    ZOMBIE_SLAYER_I(new SlayChallenge(Slayable.ZOMBIE, 5, 1)),
    ZOMBIE_SLAYER_II(new SlayChallenge(Slayable.ZOMBIE, 25, 1)),
    ZOMBIE_SLAYER_III(new SlayChallenge(Slayable.ZOMBIE, 60, 1)),
    SKELETON_SLAYER_I(new SlayChallenge(Slayable.SKELETON, 5, 1)),
    SKELETON_SLAYER_II(new SlayChallenge(Slayable.SKELETON, 25, 1)),
    SKELETON_SLAYER_III(new SlayChallenge(Slayable.SKELETON, 60, 1)),
    CREEPER_SLAYER_I(new SlayChallenge(Slayable.CREEPER, 5, 1)),
    CREEPER_SLAYER_II(new SlayChallenge(Slayable.CREEPER, 25, 1)),
    CREEPER_SLAYER_III(new SlayChallenge(Slayable.CREEPER, 60, 1));

    private AbstractChallenge abstractChallenge;

    IslandChallenge(AbstractChallenge abstractChallenge) {
        this.abstractChallenge = abstractChallenge;
    }

    public AbstractChallenge get() {
        return abstractChallenge;
    }

    public static List<IslandChallenge> getAllChallengesByType(ChallengeType type) {
        List<IslandChallenge> islandChallenges = Lists.newArrayList();

        Arrays.stream(values()).forEach(challenge -> {
            if(challenge.get().getType() == type)
                islandChallenges.add(challenge);
        });

        return islandChallenges;
    }
}
