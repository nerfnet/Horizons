package net.havocmc.horizons.game.api.mechanic;

import com.google.common.collect.Lists;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.horizons.game.api.command.CommandMapWrapper;
import net.havocmc.horizons.game.command.CommandAdmin;
import net.havocmc.horizons.game.command.CommandExit;
import net.havocmc.horizons.game.command.island.CommandIsland;
import net.havocmc.horizons.game.mechanic.MenuMechanic;
import net.havocmc.horizons.game.mechanic.player.PlayerChallengeMechanic;
import net.havocmc.horizons.game.mechanic.player.PlayerEXPMechanic;
import net.havocmc.horizons.game.mechanic.player.PlayerLoadMechanic;
import net.havocmc.horizons.game.mechanic.world.GeneratorMechanic;
import net.havocmc.horizons.game.mechanic.world.WeatherMechanic;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Created by Giovanni on 06/03/2018.
 */
@SuppressWarnings("unchecked")
public class MechanicFactory {

    private final Horizons horizons;
    private CopyOnWriteArrayList<Mechanic> loadedMechanics = Lists.newCopyOnWriteArrayList();
    private CommandMapWrapper commandMap;
    private boolean enabled;

    public MechanicFactory(Horizons horizons) {
        this.horizons = horizons;
    }

    public MechanicFactory bootstrap() {
        if (enabled) return this;
        Horizons.runtime().logger().info("Horizons MechanicFactory bootstrap..");

        try {
            commandMap = new CommandMapWrapper();

            Stream.of(
                    new CommandAdmin(),
                    new CommandExit(),
                    new CommandIsland()
            ).forEach(command -> {
                commandMap.register(command);
            });
        } catch (Exception e) {
            Horizons.runtime().logger().warning("Failed to start the CommandMapWrapper.");
            Bukkit.shutdown();
            return this;
        }

        Horizons.runtime().logger().info("Registering commands..");
        commandMap.register(new CommandExit());

        File rootDir = new File("mechanics");
        if (!rootDir.exists()) rootDir.mkdir();

        File bootFile = new File(rootDir, "mechanics.boot");
        if (!bootFile.exists()) {
            try {
                Files.copy(horizons.getResource("mechanics.boot"), bootFile.toPath());
            } catch (IOException e) {
                Horizons.runtime().logger().warning("Failed to copy mechanic factory boot image from parent.");
                Bukkit.shutdown();
                return this;
            }
        }

        Optional<Properties> bootProperties = Properties.load(bootFile);
        if (!bootProperties.isPresent()) {
            Horizons.runtime().logger().warning("mechanics.boot not present, exit.");
            Bukkit.shutdown();
            return this;
        }

        Properties properties = bootProperties.get();
        List<String> loadableMechanics = (ArrayList<String>) properties.read("LOAD", ArrayList.class);

        Stream.of(
                new PlayerChallengeMechanic(),
                new PlayerEXPMechanic(),
                new PlayerLoadMechanic(),

                new WeatherMechanic(),
                new GeneratorMechanic(),

                new MenuMechanic())
                .forEach(mechanic -> {
                            if (loadableMechanics.contains(mechanic.getName().toLowerCase())) {
                                File mechanicBootFile = new File(rootDir, mechanic.getFile().getName());
                                if (!mechanicBootFile.exists()) {
                                    try {
                                        Files.copy(horizons.getResource(mechanic.getFile().getName()), mechanicBootFile.toPath());
                                    } catch (IOException e) {
                                        Horizons.runtime().logger().warning("There was an exception whilst loading a mechanic: " + mechanic.getName());
                                        Bukkit.shutdown();
                                    }
                                }

                                Optional<Properties> mechanicProperties = Properties.load(mechanicBootFile);
                                if (!mechanicProperties.isPresent()) return;

                                mechanic.sideLoadProperties(mechanicProperties.get());
                                mechanic.bootstrap();

                                loadedMechanics.add(mechanic);
                            }
                        }
                );
        enabled = true;
        Horizons.runtime().logger().info("MechanicFactory enabled.");
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void kill() {
        Horizons.runtime().logger().info("Killing the MechanicFactory..");
        loadedMechanics.forEach(mechanic -> {
            mechanic.exit();
            mechanic.getProperties().flush();
        });

        loadedMechanics.clear();
        enabled = false;
        Horizons.runtime().logger().info("MechanicFactory disabled.");
    }

    public <T extends Mechanic> T getMechanic(Class<T> mechanicClass) {
        if (!enabled) return null;

        for (Mechanic mechanic : loadedMechanics) {
            if (mechanic.getClass().getSimpleName().equals(mechanicClass.getSimpleName()))
                return mechanicClass.cast(mechanic);
        }

        return null;
    }

    public CommandMapWrapper getCommandMap() {
        return commandMap;
    }
}
