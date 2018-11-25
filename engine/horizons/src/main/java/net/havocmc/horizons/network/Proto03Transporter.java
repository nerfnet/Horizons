package net.havocmc.horizons.network;

import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.HorizonsRuntime;
import net.havocmc.horizons.game.api.API;
import net.havocmc.horizons.game.mechanic.world.GeneratorMechanic;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.islands.IslandRank;
import net.havocmc.islands.transport.TransportableIsland;
import net.havocmc.islands.vector.IslandVector;
import net.havocmc.transport.GloArgs;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.island.IslandAction03;
import net.havocmc.transport.proto.signal.island.IslandGenerate03;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Giovanni on 13/03/2018.
 */
public class Proto03Transporter implements Transporter<BufferedObject, HorizonsRuntime> {

    private final HorizonsRuntime runtime;

    public Proto03Transporter(HorizonsRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public HorizonsRuntime getRuntime() {
        return runtime;
    }

    @Override
    @SuppressWarnings("all")
    public void inWrite(BufferedObject object, Channel channel) {
        if (object instanceof IslandAction03) {
            IslandAction03<String> islandAction03 = (IslandAction03<String>) object;
            if(!islandAction03.getAction().equalsIgnoreCase("ISLAND::SHOW_STATS")) return;

            // Since letting TransportableIsland implement Serializable is unsafe, we'll use a Json string.
            TransportableIsland island = GloArgs.NO_DOUBLE_GSON.fromJson(islandAction03.getType(), TransportableIsland.class);

            UUID uuid = islandAction03.getPlayerId();
            Optional<IslandPlayer> playerOptional = runtime.findIslandPlayer(uuid);
            if (!playerOptional.isPresent())
                return;
            IslandPlayer player = playerOptional.get();

            if (island == null) {
                player.message("&cYou do not own an island.", false, true);
                return;
            }

            player
                    .message("", false, false)
                    .message("&8&m--------------------&b&l Island Info &8&m--------------------", false, false)
                    .message("&7Island Level: &d" + island.getLevel(), false, false)
                    .message("&7Gems: &a" + island.getGems(), false, false)
                    .message("&7Owner: &e" + island.getOwnerName(), false, false);

            List<String> islandManagers = Lists.newArrayList();
            island.getProfiles().forEach(profile -> {
                if (profile.getRank() == IslandRank.MANAGER)
                    islandManagers.add(profile.getName());
            });

            if (islandManagers.isEmpty())
                player.message("&7Managers: &cNone", false, false);
            else
                player.message("&7Managers: &e" + API.singleStringFromCollection(islandManagers, ","), false, false);

            if (island.getProfiles().size() == 0) {
                player.message("&7Members: &eJust you!", false, false);
            } else {
                List<String> names = Lists.newArrayList();
                island.getProfiles().forEach(islandProfile -> {
                    names.add(islandProfile.getName());
                });
                player.message("&7Members: &e" + API.singleStringFromCollection(names, ","), false, false);
            }
            player.message("&8&m---------------------------------------------------", false, false);
        }

        if (object instanceof IslandGenerate03) {
            IslandGenerate03 generate03 = (IslandGenerate03) object;
            UUID uuid = generate03.getOwnerId();

            Optional<IslandPlayer> playerOptional = runtime.findIslandPlayer(uuid);
            if (!playerOptional.isPresent())
                return;

            IslandPlayer player = playerOptional.get();
            if (!player.isOnline()) return;

            int x = generate03.getX();
            int y = generate03.getY();
            int z = generate03.getZ();

            GeneratorMechanic generator = Horizons.getMechanicFactory().getMechanic(GeneratorMechanic.class);

            player
                    .message("", false, false)
                    .message("", false, false)
                    .message("", false, false)
                    .message("", false, false)
                    .message("&6&lYOUR ISLAND", true, false)
                    .message("       &aID: &e" + generate03.getIslandId(), false, false)
                    .message("       &aCoordinates: &e" + x + " &7- &e" + z, false, false);

            generator.generateIslands(x, y, z, player);
        }
    }

    @Override
    public String id() {
        return "0x03";
    }
}
