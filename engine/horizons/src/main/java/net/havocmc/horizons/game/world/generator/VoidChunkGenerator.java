package net.havocmc.horizons.game.world.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Old legacy "VoidChunkGenerator" from the outdated Horizons code.
 *
 * @apiNote https://github.com/blockgamecode/Horizons/blob/master/outdated/src/main/java/net/havocmc/nick/islands/islands/world/VoidChunkGenerator.java
 */
public class VoidChunkGenerator extends ChunkGenerator {

    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        byte[][] result = new byte[world.getMaxHeight() >> 4][];

        for (int x = 0; 16 > x; x++) {
            for (int z = 0; 16 > z; z++) {
                biomes.setBiome(x, z, Biome.PLAINS);
            }
        }

        return result;
    }

    private void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        if (result[y >> 4] == null)
            result[y >> 4] = new byte[4096];
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 64, 0);
    }
}