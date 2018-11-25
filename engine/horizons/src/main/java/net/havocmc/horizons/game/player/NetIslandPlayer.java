package net.havocmc.horizons.game.player;

import net.havocmc.horizons.Horizons;
import net.havocmc.islands.ExperienceType;
import net.havocmc.islands.challenge.ChallengeType;
import net.havocmc.islands.challenge.IslandChallenge;
import net.havocmc.islands.challenge.preset.Farmable;
import net.havocmc.islands.challenge.preset.Mineable;
import net.havocmc.islands.challenge.preset.Slayable;
import net.havocmc.transport.proto.signal.bukkit.OpenMenu04;
import net.havocmc.transport.proto.signal.challenge.ChallengeStart04;
import net.havocmc.transport.proto.signal.challenge.ChallengeStatus04;
import net.havocmc.transport.proto.signal.challenge.LazyChallengeStatus04;
import net.havocmc.transport.proto.signal.island.IslandAction03;
import net.havocmc.transport.proto.signal.island.IslandLevelStatus03;

/**
 * Created by Giovanni on 04/04/2018.
 * <p>
 * Dummy object to send packets used by players with ease.
 */
public class NetIslandPlayer {

    private IslandPlayer player;

    public NetIslandPlayer(IslandPlayer player) {
        this.player = player;
    }

    public NetIslandPlayer openMenu(int id) {
        OpenMenu04 menu04 = new OpenMenu04().menu(id).forPlayer(player.getIdentifier());
        Horizons.runtime().channel().writeAndFlush(menu04);
        return this;
    }

    public NetIslandPlayer displayIslandInfo() {
        IslandAction03 action03 = new IslandAction03()
                .forPlayer(player.getIdentifier())
                .doAction("ISLAND::SHOW_STATS");
        Horizons.runtime().channel().writeAndFlush(action03);
        return this;
    }

    public NetIslandPlayer addExperience(int amount, ExperienceType type) {
        IslandLevelStatus03 levelStatus03 = new IslandLevelStatus03()
                .incrementExperience(amount)
                .experienceType(type)
                .forOwner(player.getIdentifier());
        Horizons.runtime().channel().writeAndFlush(levelStatus03);
        return this;
    }

    public NetIslandPlayer startChallenge(IslandChallenge challenge) {
        ChallengeStart04 start04 = new ChallengeStart04()
                .forPlayer(player.getIdentifier())
                .start(challenge);
        Horizons.runtime().channel().writeAndFlush(start04);
        return this;
    }

    public NetIslandPlayer updateChallengeStatus(IslandChallenge challenge, int incrementCount) {
        ChallengeStatus04 status04 = new ChallengeStatus04()
                .forPlayer(player.getIdentifier())
                .challenge(challenge)
                .incrementWith(incrementCount);
        Horizons.runtime().channel().writeAndFlush(status04);
        return this;
    }

    public NetIslandPlayer updateChallengeStatus(ChallengeType type, int incrementCount, Farmable farmable) {
        LazyChallengeStatus04 status04 = new LazyChallengeStatus04()
                .forPlayer(player.getIdentifier())
                .type(type)
                .incrementWith(incrementCount)
                .farmable(farmable);
        Horizons.runtime().channel().writeAndFlush(status04);
        return this;
    }

    public NetIslandPlayer updateChallengeStatus(ChallengeType type, int incrementCount, Mineable mineable) {
        LazyChallengeStatus04 status04 = new LazyChallengeStatus04()
                .forPlayer(player.getIdentifier())
                .type(type)
                .incrementWith(incrementCount)
                .mineable(mineable);
        Horizons.runtime().channel().writeAndFlush(status04);
        return this;
    }

    public NetIslandPlayer updateChallengeStatus(ChallengeType type, int incrementCount, Slayable slayable) {
        LazyChallengeStatus04 status04 = new LazyChallengeStatus04()
                .forPlayer(player.getIdentifier())
                .type(type)
                .incrementWith(incrementCount)
                .slayable(slayable);
        Horizons.runtime().channel().writeAndFlush(status04);
        return this;
    }

    public IslandPlayer previous() {
        return player;
    }
}
