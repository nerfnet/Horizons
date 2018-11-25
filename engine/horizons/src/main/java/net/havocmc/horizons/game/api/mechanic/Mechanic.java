package net.havocmc.horizons.game.api.mechanic;

import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.API;
import net.havocmc.horizons.game.api.Legacy;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.File;

/**
 * Created by Giovanni on 06/03/2018.
 */
public interface Mechanic extends Listener {

    /**
     * Starts this mechanic.
     */
    public void bootstrap();

    /**
     * Stops this mechanic.
     */
    public void exit();

    /**
     * Returns the name of this mechanic.
     */
    public String getName();

    /**
     * Returns the {@link File} containing the bootstrap properties of this mechanic.
     */
    public File getFile();

    /**
     * Returns the {@link Properties} of this mechanic.
     */
    public Properties getProperties();

    /**
     * Returns the global {@link TransportThreadFactory}.
     */
    public TransportThreadFactory getThreadFactory();

    /**
     * Loads the {@link Properties} of this mechanic.
     */
    public void sideLoadProperties(Properties properties);

    /**
     * Returns the {@link Legacy} of this mechanic.
     */
    default Legacy getLegacy() {
        return API.LEGACY;
    }

    /**
     * Registers this mechanic as a {@link Listener}.
     */
    default Mechanic listen() {
        Bukkit.getPluginManager().registerEvents(this, Horizons.getInstance());
        return this;
    }
}
