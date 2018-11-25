package net.havocmc.service.island;

import net.havocmc.islands.vector.IslandVector;
import net.havocmc.service.Mercurial;
import net.havocmc.transport.bootstrap.BootstrapProperties;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Giovanni on 06/04/2018.
 *
 * @apiNote https://github.com/blockgamecode/Horizons/blob/master/outdated/src/main/java/net/havocmc/nick/islands/islands/IslandRuntime.java
 */
public class IslandRuntime {

    public static final int ISLAND_DISTANCE = 500;

    private boolean available = false;

    private IslandVector recentGenerated = new IslandVector(0, 150, 0);

    private volatile ScheduledFuture<?> experienceHandler;

    // @TODO Sink to asynchronous
    public synchronized Optional<IslandVector> generateCoordinates() {
        if (!available) return Optional.empty();

        double x = recentGenerated.getX();
        double z = recentGenerated.getZ();
        if (x < z) {
            if (-1 * x < z) {
                recentGenerated.setX(recentGenerated.getX() + ISLAND_DISTANCE);
                return Optional.of(recentGenerated);
            }
            recentGenerated.setZ(recentGenerated.getZ() + ISLAND_DISTANCE);
            return Optional.of(recentGenerated);
        }
        if (x > z) {
            if (-1 * x >= z) {
                recentGenerated.setX(recentGenerated.getX() - ISLAND_DISTANCE);
                return Optional.of(recentGenerated);
            }
            recentGenerated.setZ(recentGenerated.getZ() - ISLAND_DISTANCE);
            return Optional.of(recentGenerated);
        }
        if (x <= 0) {
            recentGenerated.setZ(recentGenerated.getZ() + ISLAND_DISTANCE);
            return Optional.of(recentGenerated);
        }
        recentGenerated.setZ(recentGenerated.getZ() - ISLAND_DISTANCE);

        return Optional.of(new IslandVector(recentGenerated.getX(), recentGenerated.getY(), recentGenerated.getZ()));
    }


    public void load() {
        File file = new File("local_vec.cache");
        if (!file.exists()) {
            available = true;
            return;
        }

        Optional<BootstrapProperties> propertiesOptional = BootstrapProperties.load(file);
        if (!propertiesOptional.isPresent()) return;

        BootstrapProperties properties = propertiesOptional.get();
        recentGenerated = properties.read("cachedRecentVector", IslandVector.class);

        available = true;

        experienceHandler = Mercurial.runtime().getThreadFactory().execute(() -> {
            Mercurial.runtime().allPlayers().forEach(player -> {
                if (!player.hasIsland()) return;
                player.container().getIsland().getIslandLevel().addExperience(1);
            });
        }, 0L, 1, TimeUnit.MINUTES);
    }

    public synchronized void close() {
        available = false;

        experienceHandler.cancel(true);

        BootstrapProperties properties = new BootstrapProperties();
        properties.put_MEMORY("cachedRecentVector", recentGenerated);

        File file = new File("local_vec.cache");
        FileOutputStream outputStream = null;
        try {
            if (!file.exists()) {
                file.createNewFile();

                outputStream = new FileOutputStream(file);
                IOUtils.write(properties.get(), outputStream);

                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
