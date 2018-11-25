package net.havocmc.horizons.game.mechanic.player;

import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.horizons.game.api.mechanic.Mechanic;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.preset.Farmable;
import net.havocmc.islands.challenge.preset.Mineable;
import net.havocmc.islands.challenge.preset.Slayable;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.util.Optional;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class PlayerChallengeMechanic implements Mechanic {

    private Properties properties;

    @Override
    public void bootstrap() {
        listen();
    }

    @Override
    public void exit() {

    }

    @EventHandler
    public void onEntitySlay(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;

        Player player = entity.getKiller();

        Optional<IslandPlayer> islandPlayer = Horizons.runtime().findIslandPlayer(player.getUniqueId());
        if (!islandPlayer.isPresent())
            return;

        IslandPlayer killer = islandPlayer.get();
        EntityType entityType = entity.getType();

        switch (entityType) {
            case CREEPER:
                killer.net().updateChallengeStatus(ChallengeType.SLAYING, 1, Slayable.CREEPER);
                break;
            case SPIDER:
                killer.net().updateChallengeStatus(ChallengeType.SLAYING, 1, Slayable.SPIDER);
                break;
            case SKELETON:
                killer.net().updateChallengeStatus(ChallengeType.SLAYING, 1, Slayable.SKELETON);
                break;
            case ZOMBIE:
                killer.net().updateChallengeStatus(ChallengeType.SLAYING, 1, Slayable.ZOMBIE);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onItemFarm(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();

        if (event.getPlayer() == null) return;
        Player player = event.getPlayer();

        Optional<IslandPlayer> islandPlayer = Horizons.runtime().findIslandPlayer(player.getUniqueId());
        if (!islandPlayer.isPresent())
            return;

        IslandPlayer farmer = islandPlayer.get();

        switch (material) {
            case PUMPKIN:
                farmer.net().updateChallengeStatus(ChallengeType.FARMING, 1, Farmable.PUMPKIN);
                break;
            case MELON_BLOCK:
                farmer.net().updateChallengeStatus(ChallengeType.FARMING, 1, Farmable.MELON);
                break;
            case CROPS:
                farmer.net().updateChallengeStatus(ChallengeType.FARMING, 1, Farmable.WHEAT);
                break;
            case CACTUS:
                farmer.net().updateChallengeStatus(ChallengeType.FARMING, 1, Farmable.CACTUS);
                break;
            case COCOA:
                farmer.net().updateChallengeStatus(ChallengeType.FARMING, 1, Farmable.COCO_BEAN);
                break;
            case SUGAR_CANE_BLOCK:
                farmer.net().updateChallengeStatus(ChallengeType.FARMING, 1, Farmable.SUGAR_CANE);
                break;
            case GOLD_ORE:
                farmer.net().updateChallengeStatus(ChallengeType.MINING, 1, Mineable.GOLD);
                break;
            case DIAMOND_ORE:
                farmer.net().updateChallengeStatus(ChallengeType.MINING, 1, Mineable.DIAMOND);
                break;
            case REDSTONE_ORE:
                farmer.net().updateChallengeStatus(ChallengeType.MINING, 1, Mineable.REDSTONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void sideLoadProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getName() {
        return "PLAYERCHALLENGE.mech";
    }

    @Override
    public File getFile() {
        return new File(getName().toLowerCase());
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public TransportThreadFactory getThreadFactory() {
        return Horizons.runtime().getThreadFactory();
    }
}
