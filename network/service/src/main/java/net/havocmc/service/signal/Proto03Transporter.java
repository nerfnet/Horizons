package net.havocmc.service.signal;

import io.netty.channel.Channel;
import net.havocmc.islands.IslandRank;
import net.havocmc.islands.IslandRegion;
import net.havocmc.islands.transport.TransportableIsland;
import net.havocmc.islands.vector.IslandVector;
import net.havocmc.service.Mercurial;
import net.havocmc.service.MercurialRuntime;
import net.havocmc.service.island.IslandLevel;
import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerIsland;
import net.havocmc.transport.GloArgs;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.bukkit.Punish04;
import net.havocmc.transport.proto.signal.island.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Giovanni on 01/03/2018.
 *
 * TODO Make "Failed to write BufferedObject" less obscure and use a global error handler for connections.
 */
public class Proto03Transporter implements Transporter<BufferedObject, MercurialRuntime> {

    private final MercurialRuntime runtime;

    public Proto03Transporter(MercurialRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    @SuppressWarnings("all")
    public void inWrite(BufferedObject object, Channel channel) {
        if(object instanceof IslandAction03) {
            IslandAction03<String> islandAction03 = (IslandAction03<String>) object;
            if(!islandAction03.getAction().equalsIgnoreCase("ISLAND::SHOW_STATS")) return;

            UUID uuid = islandAction03.getPlayerId();
            Optional<Player> playerOptional = runtime.findPlayer(uuid);

            if (!playerOptional.isPresent()) {
                Punish04 punish04 = new Punish04()
                        .player(uuid)
                        .data("UPDATE::kick@&cFailed to write BufferedObject, reconnect.");
                channel.writeAndFlush(punish04);
                return;
            }

            Player player = playerOptional.get();
            if(!player.hasIsland()) {
                player.message(runtime.MC_MESSAGE_PREFIX + " &cYou do not own an island.");
                return;
            }

            TransportableIsland transportableIsland = player.container().getIsland().asTransportable();
            channel.writeAndFlush(islandAction03.holdGeneric(GloArgs.NO_DOUBLE_GSON.toJson(transportableIsland)));
        }

        if (object instanceof IslandLevelStatus03) {
            IslandLevelStatus03 levelStatus03 = (IslandLevelStatus03) object;
            UUID uuid = levelStatus03.getOwnerId();

            Optional<Player> playerOptional = runtime.findPlayer(uuid);

            if (!playerOptional.isPresent()) {
                Punish04 punish04 = new Punish04()
                        .player(uuid)
                        .data("UPDATE::kick@&cFailed to write BufferedObject, reconnect.");
                channel.writeAndFlush(punish04);
                return;
            }

            Player player = playerOptional.get();
            if (!player.hasIsland()) return;

            IslandLevel level = player.container().getIsland().getIslandLevel();

            if (level.addExperience(levelStatus03.getExperience())) {
                player
                        .playSound("ENTITY_PLAYER_LEVELUP", 10F, 2F)
                        .message("")
                        .message("   &e[ &aLEVEL UP &e- &e&lSUMMARY &e]")
                        .message("       &aLevel: &d&l" + level.get())
                        .message("       &aLifetime EXP Gathered: &d&l" + level.getExperience())
                        .message("");
                return;
            }
        }

        if (object instanceof IslandCreate03) {
            IslandCreate03 create03 = (IslandCreate03) object;
            UUID playerId = create03.getOwnerUUID();

            Optional<Player> playerOptional = runtime.findPlayer(playerId);

            if (!playerOptional.isPresent()) {
                Punish04 punish04 = new Punish04()
                        .player(playerId)
                        .data("UPDATE::kick@&cFailed to write BufferedObject, reconnect.");
                channel.writeAndFlush(punish04);
                return;
            }

            Player player = playerOptional.get();
            if (player.hasIsland()) {
                player.message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cYou already own an island!");
                return;
            }

            Optional<IslandVector> vectorOptional = Mercurial.runtime().islandRuntime().generateCoordinates();
            if (!vectorOptional.isPresent()) {
                player.message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cFailed to generate coordinates through the island runtime.");
                runtime.logger().warning("Vectors for an island are not present, the island runtime might not be available?");
                return;
            }
            player.container().setIsland(new PlayerIsland(player)).getIsland().generate(channel, vectorOptional.get());
            return;
        }

        if (object instanceof RegionUpdate03) {
            RegionUpdate03 regionUpdate03 = (RegionUpdate03) object;
            String islandId = regionUpdate03.getIslandId();

            Optional<PlayerIsland> islandOptional = runtime.findIsland(islandId);
            if (!islandOptional.isPresent()) {
                runtime.logger().warning("Failed to handle RegionUpdate03 for island: " + islandId);
                return;
            }

            // UPDATE::QUERY@a$purchase@REGION
            String[] updateStream = regionUpdate03.getUpdateData().split("::")[1].split("@");
            PlayerIsland island = islandOptional.get();

            if (updateStream[1].equalsIgnoreCase("a$purchase")) {
                IslandRegion region = null;
                try {
                    region = IslandRegion.valueOf(updateStream[2]);
                } catch (Exception e) {
                    runtime.logger().warning("Failed to a$purchase a region on island " + islandId);
                    island.messageAll(MercurialRuntime.MC_MESSAGE_PREFIX + " &cAn error occurred.");
                    return;
                }
                island.attemptBuyRegion(region);
            }
            return;
        }

        if (object instanceof MemberUpdate03) {
            MemberUpdate03 memberUpdate03 = (MemberUpdate03) object;
            UUID uniqueId = memberUpdate03.getPlayer();
            String islandId = memberUpdate03.getIslandId();

            //UPDATE::0x$@ADD@$RANK
            String[] updateStream = memberUpdate03.getUpdateData().split("::")[1].split("@");

            Optional<PlayerIsland> islandOptional = runtime.findIsland(islandId);
            if (!islandOptional.isPresent()) {
                runtime.logger().warning("Failed to handle MemberUpdate03 for island: " + islandId);
                return;
            }

            if (updateStream[0].equalsIgnoreCase("add")) {
                IslandRank islandRank = null;
                try {
                    islandRank = IslandRank.valueOf(updateStream[1].toUpperCase());
                } catch (Exception e) {
                    runtime.logger().warning("Failed to add a player to an island, island rank is null.");
                    return;
                }

                PlayerIsland island = islandOptional.get();
                island.addMember(memberUpdate03.getQueryAuthor(), uniqueId, islandRank);
                return;
            }

            if (updateStream[0].equalsIgnoreCase("remove")) {
                PlayerIsland island = islandOptional.get();
                island.removeMember(memberUpdate03.getQueryAuthor(), uniqueId);
                return;
            }

            if (updateStream[0].equalsIgnoreCase("set")) {
                IslandRank islandRank = null;
                try {
                    islandRank = IslandRank.valueOf(updateStream[1].toUpperCase());
                } catch (Exception e) {
                    runtime.logger().warning("Failed to set the rank of an island member, island rank is null.");
                    return;
                }

                PlayerIsland island = islandOptional.get();
                island.updateRank(memberUpdate03.getQueryAuthor(), uniqueId, islandRank);
            }
        }
    }

    @Override
    public MercurialRuntime getRuntime() {
        return runtime;
    }

    @Override
    public String id() {
        return "0x03";
    }
}
