package net.havocmc.service.signal;

import io.netty.channel.Channel;
import net.havocmc.islands.ExperienceType;
import net.havocmc.islands.challenge.ChallengeBlob;
import net.havocmc.islands.challenge.IslandChallenge;
import net.havocmc.islands.transport.TransportableIsland;
import net.havocmc.service.Mercurial;
import net.havocmc.service.MercurialRuntime;
import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerIsland;
import net.havocmc.transport.GloArgs;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.bukkit.OpenMenu04;
import net.havocmc.transport.proto.signal.bukkit.Teleport04;
import net.havocmc.transport.proto.signal.challenge.ChallengeStart04;
import net.havocmc.transport.proto.signal.challenge.ChallengeStatus04;
import net.havocmc.transport.proto.signal.challenge.LazyChallengeStatus04;
import net.havocmc.transport.proto.signal.island.IslandLevelStatus03;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Giovanni on 02/03/2018.
 * <p>
 * TODO Major challenge stuff cleanup, it's a mess right now.
 */
public class Proto04Transporter implements Transporter<BufferedObject, MercurialRuntime> {

    private final MercurialRuntime runtime;

    public Proto04Transporter(MercurialRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void inWrite(BufferedObject object, Channel channel) {
        if (object instanceof ChallengeStart04) {
            ChallengeStart04 start04 = (ChallengeStart04) object;
            IslandChallenge challenge = start04.getChallenge();

            UUID playerId = start04.getPlayerId();

            Optional<Player> playerOptional = Mercurial.runtime().findPlayer(playerId);
            if (!playerOptional.isPresent()) return;
            Player player = playerOptional.get();

            if (player.container().completedChallenge(challenge)) {
                player.message("&cYou've already completed this challenge.");
                return;
            }

            if (player.container().startedChallenge(challenge)) {
                player.message("&cYou've already started this challenge, track your progression in the &nChallenge Menu&c.");
                return;
            }

            if (player.container().startedChallengeOfType(start04.getType())) {
                player.message("&cYou've already started a &l" + start04.getType() + "&c challenge!");
                return;
            }

            String challengeName = challenge.name().replaceAll("_", " ");
            player.container().startChallenge(challenge);

            player
                    .playSound("ENTITY_PLAYER_LEVELUP", 10F, 1.3F)
                    .message("&e&lCHALLENGE &a&n" + challengeName + "&a started!");

        }

        if (object instanceof LazyChallengeStatus04) {
            LazyChallengeStatus04 status04 = (LazyChallengeStatus04) object;

            UUID playerId = status04.getPlayerId();

            Optional<Player> playerOptional = Mercurial.runtime().findPlayer(playerId);
            if (!playerOptional.isPresent()) return;
            Player player = playerOptional.get();

            List<IslandChallenge> startedChallenges = null;

            // Find the correct challenge
            if (status04.getFarmable() != null) {
                startedChallenges = player.container().findStartedChallenges(status04.getFarmable());
            } else if (status04.getMineable() != null) {
                startedChallenges = player.container().findStartedChallenges(status04.getMineable());
            } else if (status04.getSlayable() != null) {
                startedChallenges = player.container().findStartedChallenges(status04.getSlayable());
            }

            if (startedChallenges == null) return;
            if (startedChallenges.get(0) == null) return;

            IslandChallenge challenge = startedChallenges.get(0);
            // No longer contains multiple challenges as of 09/06/2018 15:42, however we will keep using a List so not everything has to be changed.

            // Same logic as ChallengeStatus04
            player.container().setProgressionOfChallenge(challenge, status04.getAmount());
            int progression = player.container().getProgressionOfChallenge(challenge);
            if (progression >= challenge.get().getRequirement() && !player.container().completedChallenge(challenge)) {
                player.container().addCompletedChallenge(challenge);
                player.container().removeProgressionOf(challenge);

                int expReward = challenge.get().getExpReward();
                String challengeName = challenge.name().replaceAll("_", " ");

                player
                        .playSound("ENTITY_PLAYER_LEVELUP", 10F, 1.3F)
                        .message("&e&lCHALLENGE &a&n" + challengeName + "&a completed! &6&o[+ " + expReward + " EXP]");

                Mercurial.runtime().channel().writeAndFlush(
                        new IslandLevelStatus03()
                                .forOwner(playerId)
                                .experienceType(ExperienceType.CHALLENGE_COMPLETED)
                                .incrementExperience(expReward));
            }
        }

        if (object instanceof ChallengeStatus04) {
            ChallengeStatus04 challengeStatus04 = (ChallengeStatus04) object;
            IslandChallenge challenge = challengeStatus04.getChallenge();

            UUID playerId = challengeStatus04.getPlayerId();

            Optional<Player> playerOptional = Mercurial.runtime().findPlayer(playerId);
            if (!playerOptional.isPresent()) return;
            Player player = playerOptional.get();

            if (player.container().completedChallenge(challenge)) return;

            player.container().setProgressionOfChallenge(challenge, challengeStatus04.getCount());

            // Check if the challenge has been completed
            int progression = player.container().getProgressionOfChallenge(challenge);
            if (progression >= challenge.get().getRequirement() && !player.container().completedChallenge(challenge)) {
                player.container().addCompletedChallenge(challenge);

                int expReward = challenge.get().getExpReward();
                String challengeName = challenge.name().replaceAll("_", " ");

                player
                        .playSound("ENTITY_PLAYER_LEVELUP", 10F, 1.3F)
                        .message("&e&o[ " + challengeName + ": +" + expReward + "EXP ]");

                // Write to Proto03
                Mercurial.runtime().channel().writeAndFlush(
                        new IslandLevelStatus03()
                                .forOwner(playerId)
                                .experienceType(ExperienceType.CHALLENGE_COMPLETED)
                                .incrementExperience(expReward));
            }
        }

        if (object instanceof OpenMenu04) {
            OpenMenu04 menu04 = (OpenMenu04) object;

            int menuId = menu04.getMenu();
            UUID uuid = menu04.getPlayerId();

            Optional<Player> playerOptional = Mercurial.runtime().findPlayer(uuid);
            if (!playerOptional.isPresent()) return; // TODO error message
            Player player = playerOptional.get();

            switch (menuId) {
                case 0:
                    TransportableIsland transportableIsland = new TransportableIsland();

                    if (player.hasIsland()) {
                        PlayerIsland island = player.container().getIsland();
                        transportableIsland.create(player.container().getIsland());
                        channel.writeAndFlush(menu04.returnWith(new Object[]{GloArgs.NO_DOUBLE_GSON.toJson(transportableIsland)}));
                        return;
                    }

                    channel.writeAndFlush(menu04.returnWith(new Object[]{"$empty"}));
                    break;
                case 2:
                    ChallengeBlob challengeBlob = new ChallengeBlob(uuid);

                    // Started challenges
                    if (player.container().getChallengeProgression() != null && !player.container().getChallengeProgression().isEmpty())
                        player.container().getChallengeProgression().keySet().forEach(challenge -> {
                            int progression = player.container().getProgressionOfChallenge(challenge);
                            challengeBlob.addStartedChallenge(challenge, progression);
                        });

                    // Completed challenges
                    if (player.container().getCompletedChallenges() != null && !player.container().getCompletedChallenges().isEmpty())
                        player.container().getCompletedChallenges().forEach(challengeBlob::addCompletedChallenge);

                    channel.writeAndFlush(menu04.returnWith(new Object[]{GloArgs.NO_DOUBLE_GSON.toJson(challengeBlob)}));
                    break;
                default:
                    player.message("&cFailed to open a menu.");
                    break;
            }
        }

        if (object instanceof Teleport04) {
            Teleport04 teleport04 = (Teleport04) object;
            UUID uuid = teleport04.getPlayer();

            Optional<Player> playerOptional = runtime.findPlayer(uuid);
            if (!playerOptional.isPresent()) {
                runtime.logger().warning("Failed to handle Teleport04, player is not present.");
                return;
            }
            playerOptional.get().write(teleport04);
        }
    }

    @Override
    public MercurialRuntime getRuntime() {
        return runtime;
    }

    @Override
    public String id() {
        return "0x04";
    }
}
