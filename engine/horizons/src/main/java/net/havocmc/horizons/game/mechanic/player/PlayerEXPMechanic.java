package net.havocmc.horizons.game.mechanic.player;

import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.horizons.game.api.stream.Tuple;
import net.havocmc.horizons.game.api.mechanic.Mechanic;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.islands.ExperienceType;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Giovanni on 30/03/2018.
 */
public class PlayerEXPMechanic implements Mechanic {

    private Properties properties;

    private LinkedTreeMap<Material, Integer> minedBlocksMap;
    private LinkedTreeMap<Material, Integer> placedBlocksMap;
    private LinkedTreeMap<EntityType, Integer> entityKilledMap;

    @Override
    public void bootstrap() {
        listen();

        minedBlocksMap = properties.readGenericMap("MINED_BLOCKS_DATA");
        placedBlocksMap = properties.readGenericMap("PLACED_BLOCKS_DATA");

        if(minedBlocksMap == null)
            minedBlocksMap = new LinkedTreeMap<>();

        if(placedBlocksMap == null)
            placedBlocksMap = new LinkedTreeMap<>();

        if(entityKilledMap == null)
            entityKilledMap = new LinkedTreeMap<>();

        // Kinda ugly, however these are the default settings. :U
        if (minedBlocksMap.isEmpty()) {
            Stream.of(
                    new Tuple<Material, Integer>(Material.EMERALD_ORE, 100),
                    new Tuple<Material, Integer>(Material.DIAMOND_ORE, 100),
                    new Tuple<Material, Integer>(Material.GOLD_ORE, 30),
                    new Tuple<Material, Integer>(Material.LAPIS_ORE, 30),
                    new Tuple<Material, Integer>(Material.IRON_ORE, 10),
                    new Tuple<Material, Integer>(Material.COAL_ORE, 5),
                    new Tuple<Material, Integer>(Material.PUMPKIN, 5),
                    new Tuple<Material, Integer>(Material.MELON_BLOCK, 5),
                    new Tuple<Material, Integer>(Material.CROPS, 10),
                    new Tuple<Material, Integer>(Material.SUGAR_CANE_BLOCK, 3),
                    new Tuple<Material, Integer>(Material.CACTUS, 5),
                    new Tuple<Material, Integer>(Material.COCOA, 5),
                    new Tuple<Material, Integer>(Material.LOG, 5))
                    .forEach(tuple -> {
                        minedBlocksMap.put(tuple.a(), tuple.b());
                    });
        }

        if (placedBlocksMap.isEmpty()) {
            Stream.of(
                    new Tuple<Material, Integer>(Material.EMERALD_BLOCK, 300),
                    new Tuple<Material, Integer>(Material.DIAMOND_BLOCK, 100),
                    new Tuple<Material, Integer>(Material.GOLD_BLOCK, 100),
                    new Tuple<Material, Integer>(Material.LAPIS_BLOCK, 50),
                    new Tuple<Material, Integer>(Material.REDSTONE_BLOCK, 50),
                    new Tuple<Material, Integer>(Material.IRON_BLOCK, 20),
                    new Tuple<Material, Integer>(Material.COAL_BLOCK, 10),
                    new Tuple<Material, Integer>(Material.MOB_SPAWNER, 100),
                    new Tuple<Material, Integer>(Material.OBSIDIAN, 20),
                    new Tuple<Material, Integer>(Material.GLASS, 5),
                    new Tuple<Material, Integer>(Material.STAINED_GLASS, 5),
                    new Tuple<Material, Integer>(Material.STAINED_GLASS_PANE, 5))
                    .forEach(tuple -> {
                        placedBlocksMap.put(tuple.a(), tuple.b());
                    });
        }

        if (entityKilledMap.isEmpty()) {
            Stream.of(
                    new Tuple<EntityType, Integer>(EntityType.PIG, 5),
                    new Tuple<EntityType, Integer>(EntityType.COW, 5),
                    new Tuple<EntityType, Integer>(EntityType.CHICKEN, 5),
                    new Tuple<EntityType, Integer>(EntityType.SHEEP, 5),
                    new Tuple<EntityType, Integer>(EntityType.SPIDER, 20),
                    new Tuple<EntityType, Integer>(EntityType.CREEPER, 20),
                    new Tuple<EntityType, Integer>(EntityType.SKELETON, 20),
                    new Tuple<EntityType, Integer>(EntityType.ZOMBIE, 20),
                    new Tuple<EntityType, Integer>(EntityType.ENDERMAN, 20),
                    new Tuple<EntityType, Integer>(EntityType.BLAZE, 40))
                    .forEach(tuple -> {
                        entityKilledMap.put(tuple.a(), tuple.b());
                    });
        }
    }

    @Override
    public void exit() {
        minedBlocksMap.clear();
        placedBlocksMap.clear();
        entityKilledMap.clear();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;

        Player player = entity.getKiller();
        if(player == null) return;

        Optional<IslandPlayer> islandPlayer = Horizons.runtime().findIslandPlayer(player.getUniqueId());
        if (!islandPlayer.isPresent())
            return;

        IslandPlayer killer = islandPlayer.get();
        EntityType entityType = entity.getType();

        if (!entityKilledMap.containsKey(entityType))
            return;

        int experience = entityKilledMap.get(entityType);
        killer.net().addExperience(experience, ExperienceType.MONSTER_KILLED);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();
        Player player = event.getPlayer();

        Optional<IslandPlayer> islandPlayer = Horizons.runtime().findIslandPlayer(player.getUniqueId());
        if (!islandPlayer.isPresent()) {
            event.setCancelled(true);
            return;
        }

        IslandPlayer player1 = islandPlayer.get();

        if (!minedBlocksMap.containsKey(material)) {
            player1.net().addExperience(1, ExperienceType.BLOCK_MINED);
            return;
        }

        int experience = minedBlocksMap.get(material);
        player1.net().addExperience(experience, ExperienceType.BLOCK_MINED);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();
        Player player = event.getPlayer();

        Optional<IslandPlayer> islandPlayer = Horizons.runtime().findIslandPlayer(player.getUniqueId());
        if (!islandPlayer.isPresent()) {
            event.setCancelled(true);
            return;
        }

        IslandPlayer player1 = islandPlayer.get();

        if (!placedBlocksMap.containsKey(material)) {
            player1.net().addExperience(1, ExperienceType.BLOCK_PLACED);
            return;
        }

        int experience = placedBlocksMap.get(material);
        player1.net().addExperience(experience, ExperienceType.BLOCK_PLACED);
    }

    @Override
    public void sideLoadProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getName() {
        return "PLAYEREXP.mech";
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
