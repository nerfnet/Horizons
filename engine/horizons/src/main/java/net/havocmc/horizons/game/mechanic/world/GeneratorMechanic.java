package net.havocmc.horizons.game.mechanic.world;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import com.boydti.fawe.object.FaweQueue;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.horizons.game.api.mechanic.Mechanic;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.horizons.game.world.SchematicLoader;
import net.havocmc.horizons.game.world.WorldLocation;
import net.havocmc.horizons.game.world.generator.VoidChunkGenerator;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 13/03/2018.
 */
public class GeneratorMechanic implements Mechanic {

    private final HashMap<String, SchematicLoader> schematicLoaders = Maps.newHashMap();
    private Properties properties;
    private String worldName;

    @Override
    public void bootstrap() {
        String loadersDir = properties.read("loadersDir", String.class);
        worldName = properties.read("generatorWorld", String.class);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Horizons.runtime().logger().info("Generator world not present, generating new..");

            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator
                    .environment(World.Environment.NORMAL)
                    .generateStructures(false)
                    .type(WorldType.FLAT)
                    .generator(new VoidChunkGenerator());

            world = worldCreator.createWorld();
        } else {
            Bukkit.unloadWorld(world.getName(), false);
        }

        File loadersDirectory = new File("mechanics" + StringUtils.removeEnd(loadersDir, "*"));

        if (!loadersDirectory.exists()) {
            Horizons.runtime().logger().warning("Schematic loaders not present, using default.");
            loadersDirectory.mkdirs();
        }


        LinkedTreeMap<String, Object> loaders = properties.readMap("loaders");
        loaders.forEach((key, value) -> {
            File loaderFile = new File(loadersDirectory, (String) value);
            SchematicLoader loader = new SchematicLoader(loaderFile.getPath(), Bukkit.getWorld(worldName));

            schematicLoaders.put(key, loader);
        });
    }

    public void generateIslands(int x, int y, int z, IslandPlayer player) {
        WorldLocation location = new WorldLocation(worldName, new int[]{x, y, z});
        Vector worldEditVector = new Vector(x, y, z);

        try {
            schematicLoaders.get("allIslands").loadSchematicAt(worldEditVector, new Consumer<Location>() {
                @Override public void accept(Location location) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Horizons.getInstance(), () -> {
                        player
                                .refresh()
                                .teleport(location.add(0, 4, 0), false)
                                .playSound(Sound.ENTITY_PLAYER_LEVELUP, 10F, 1.5F)
                                .clearChat()
                                .message("&aWelcome to your new &6Horizons&a island!", false, true);

                        FaweAPI.fixLighting(FaweAPI.getWorld(worldName),
                                new CuboidRegion(
                                        new Vector(x + 15, y + 10, z + 15),
                                        new Vector(x - 15, y - 10, z - 15)),
                                new AsyncWorld(worldName, true).getQueue(), FaweQueue.RelightMode.OPTIMAL);
                    });
                }
            });
        } catch (DataException | MaxChangedBlocksException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exit() {
        schematicLoaders.clear();
    }

    @Override
    public void sideLoadProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public File getFile() {
        return new File(getName().toLowerCase());
    }

    @Override
    public String getName() {
        return "GENERATOR.mech";
    }

    @Override
    public TransportThreadFactory getThreadFactory() {
        return Horizons.runtime().getThreadFactory();
    }

    public HashMap<String, SchematicLoader> getSchematicLoaders() {
        return schematicLoaders;
    }
}
