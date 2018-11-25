package net.havocmc.horizons.network;

import io.netty.channel.Channel;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.HorizonsRuntime;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.horizons.game.player.menu.ChallengeMenu;
import net.havocmc.horizons.game.player.menu.IslandMenu;
import net.havocmc.islands.challenge.ChallengeBlob;
import net.havocmc.islands.transport.TransportableIsland;
import net.havocmc.transport.GloArgs;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.bukkit.OpenMenu04;
import net.havocmc.transport.proto.signal.bukkit.PlaySound04;
import net.havocmc.transport.proto.signal.bukkit.Punish04;
import net.havocmc.transport.proto.signal.bukkit.Teleport04;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Giovanni on 03/03/2018.
 */
public class Proto04Transporter implements Transporter<BufferedObject, HorizonsRuntime> {

    private final HorizonsRuntime runtime;

    public Proto04Transporter(HorizonsRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void inWrite(BufferedObject object, Channel channel) {
        if (object instanceof PlaySound04) {
            PlaySound04 sound04 = (PlaySound04) object;

            Sound sound = null;
            try {
                sound = Sound.valueOf(sound04.getSoundName());
            } catch (Exception e) {
                runtime.logger().warning("Invalid PlaySound04 received, illegal sound name: " + sound04.getSoundName());
            }

            if (sound == null) return;
            UUID uuid = sound04.getPlayerId();

            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                player.playSound(player.getLocation(), sound, sound04.getVolume(), sound04.getPitch());
        }

        if (object instanceof OpenMenu04) {
            OpenMenu04 menu04 = (OpenMenu04) object;
            Object[] objects = menu04.getReturnPipe();

            UUID uuid = menu04.getPlayerId();

            Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(uuid);
            if (!playerOptional.isPresent()) return;
            IslandPlayer player = playerOptional.get();

            if (objects == null) return;

            switch (menu04.getMenu()) {
                case 0:
                    if (objects[0] instanceof String) {
                        String string = (String) objects[0];

                        if (string.equalsIgnoreCase("$empty"))
                            player.openMenu(new IslandMenu(player, null));

                        else
                            try {
                                player.openMenu(new IslandMenu(player, GloArgs.NO_DOUBLE_GSON.fromJson(string, TransportableIsland.class)));
                            } catch (Exception e) {
                                player.message("&cFailed to open the island menu.", false, false);
                            }
                    }
                    break;
                case 2:
                    if (objects[0] instanceof String) {
                        String string = (String) objects[0];

                        try {
                            player.openMenu(new ChallengeMenu(player, GloArgs.NO_DOUBLE_GSON.fromJson(string, ChallengeBlob.class)));
                        } catch (Exception e) {
                            player.message("&cFailed to open the challenges menu.", false, false);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        if (object instanceof Punish04) {
            Punish04 punish04 = (Punish04) object;
            UUID uuid = punish04.getPlayer();

            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                String[] readIn = punish04.getData().split("::")[1].split("@");
                String context = readIn[1];

                if (readIn[0].equalsIgnoreCase("kick"))
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Horizons.getInstance(), () -> {
                        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', context));
                    });

                if (readIn[0].equalsIgnoreCase("mute"))
                    return;

                if (readIn[0].equalsIgnoreCase("ban"))
                    return;

            }
            return;
        }

        if (object instanceof Teleport04) {
            Teleport04 teleport04 = (Teleport04) object;
            UUID uuid = teleport04.getPlayer();

            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                World world = Bukkit.getWorld(teleport04.getWorld());
                if (world == null) return;

                Location location = new Location(world, teleport04.getVector().get(0), teleport04.getVector().get(1), teleport04.getVector().get(2));
                player.teleport(location);
            }
        }
    }

    @Override
    public HorizonsRuntime getRuntime() {
        return runtime;
    }

    @Override
    public String id() {
        return "0x04";
    }
}
