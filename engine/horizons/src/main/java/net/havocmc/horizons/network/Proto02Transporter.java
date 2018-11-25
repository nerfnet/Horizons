package net.havocmc.horizons.network;

import io.netty.channel.Channel;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.HorizonsRuntime;
import net.havocmc.horizons.game.mechanic.player.PlayerLoadMechanic;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.player.Connect02;
import net.havocmc.transport.proto.signal.player.Message02;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Giovanni on 04/03/2018.
 */
public class Proto02Transporter implements Transporter<BufferedObject, HorizonsRuntime> {

    private final HorizonsRuntime runtime;

    public Proto02Transporter(HorizonsRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void inWrite(BufferedObject object, Channel channel) {
        if (object instanceof Connect02) {
            Connect02 connect02 = (Connect02) object;
            UUID uuid = connect02.getUniqueId();

            PlayerLoadMechanic loadMechanic = Horizons.getMechanicFactory().getMechanic(PlayerLoadMechanic.class);
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline())
                return;

            IslandPlayer islandPlayer = new IslandPlayer(uuid, player.getName());
            Horizons.runtime().addIslandPlayer(islandPlayer);

            loadMechanic.sendJoinMessages(islandPlayer).teleport(loadMechanic.getWorldSpawn(), true);
        }

        if (object instanceof Message02) {
            Message02 message02 = (Message02) object;
            UUID uuid = message02.getUniqueId();

            Optional<IslandPlayer> player = Horizons.runtime().findIslandPlayer(uuid);
            if (!player.isPresent()) return;

            IslandPlayer islandPlayer = player.get();
            for (String message : message02.getMessages())
                islandPlayer.message(message, false, '&');
        }
    }

    @Override
    public String id() {
        return "0x02";
    }

    @Override
    public HorizonsRuntime getRuntime() {
        return runtime;
    }
}
