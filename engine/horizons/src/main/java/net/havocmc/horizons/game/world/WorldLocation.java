package net.havocmc.horizons.game.world;

import com.google.common.collect.Lists;
import net.havocmc.islands.vector.IslandVector;
import net.havocmc.islands.vector.VectorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Created by Giovanni on 06/03/2018.
 */
public class WorldLocation extends Location implements VectorBuilder<Location> {

    private final String worldName;
    private final double[] axis;

    public WorldLocation(String worldName, double[] axis) {
        super(Bukkit.getWorld(worldName), axis[0], axis[1], axis[2]);

        this.worldName = worldName;
        this.axis = axis;
    }

    public WorldLocation(String worldName, int[] axis) {
        super(Bukkit.getWorld(worldName), axis[0], axis[1], axis[2]);

        this.worldName = worldName;
        this.axis = new double[]{axis[0], axis[1], axis[2]};
    }

    public WorldLocation(String worldName, IslandVector vector) {
        super(Bukkit.getWorld(worldName), vector.getX(), vector.getY(), vector.getZ());

        this.worldName = worldName;
        this.axis = new double[]{vector.getX(), vector.getY(), vector.getZ()};
    }

    @Override
    public Location unwrapVector() {
        return toLocation();
    }

    @Override
    public IslandVector toIslandVector() {
        return new IslandVector((int) axis[0], (int) axis[1], (int) axis[2]);
    }

    /**
     * Returns whether there is a specific player nearby in a 3D range specified by par2.
     */
    public boolean hasPlayerNearby(UUID uuid, int par2) {
        for (Entity entity : arrayEntitiesNearby(Player.class, par2)) {
            if (entity.getUniqueId().toString().equalsIgnoreCase(uuid.toString()))
                return true;
        }

        return false;
    }

    /**
     * Returns whether there are nearby entities in a 3D range specified by par1, par2, par3.
     */
    public boolean hasEntitiesNearby(int par1, int par2, int par3) {
        return this.arrayEntitiesNearby(Entity.class, par1, par2, par3).length != 0;
    }

    /**
     * Returns whether there are nearby entities in a 3D range specified by par1.
     */
    public boolean hasEntitiesNearby(int par1) {
        return this.hasEntitiesNearby(par1, par1, par1);
    }

    /**
     * Returns whether there are nearby entities of a specific type by class, in a 3D range specified by par2, par3 and par4.
     */
    public boolean hasEntitiesOfTypeNearby(Class<Entity> entityClass, int par2, int par3, int par4) {
        return this.arrayEntitiesNearby(entityClass, par2, par3, par4).length != 0;
    }

    /**
     * Returns whether there are nearby entities of a specific type by class, in a 3D range specified by par2.
     */
    public boolean hasEntitiesOfTypeNearby(Class<Entity> entityClass, int par2) {
        return this.arrayEntitiesNearby(entityClass, par2, par2, par2).length != 0;
    }

    /**
     * Returns an array of all nearby entities of a specific type by class, in a 3D range specified by par2, par3 and par4.
     */
    public <E extends Entity> Entity[] arrayEntitiesNearby(Class<E> entityClass, int par2, int par3, int par4) {
        List<Entity> entityList = Lists.newArrayList();
        if (getBlock() == null) return new Entity[0];

        getWorld().getNearbyEntities(this, par2, par3, par4).stream().filter(entityClass::isInstance).forEach(entityList::add);
        Entity[] entities = new Entity[entityList.size()];
        for (int i = 0; i < entities.length; i++) {
            entities[i] = entityList.get(i);
        }
        return entities;
    }

    /**
     * Returns an array of all nearby entities of a specific type by class, in a 3D range of par2.
     */
    public <E extends Entity> Entity[] arrayEntitiesNearby(Class<E> entityClass, int par2) {
        return this.arrayEntitiesNearby(entityClass, par2, par2, par2);
    }

    /**
     * Returns the original {@link Location}.
     */
    public Location toLocation() {
        return new Location(getWorld(), getX(), getY(), getZ());
    }

    public String getWorldName() {
        return worldName;
    }

    public double[] getAxis() {
        return axis;
    }
}
