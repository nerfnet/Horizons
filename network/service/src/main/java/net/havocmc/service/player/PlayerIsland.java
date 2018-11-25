package net.havocmc.service.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import net.havocmc.islands.*;
import net.havocmc.islands.transport.TransportableIsland;
import net.havocmc.islands.vector.IslandVector;
import net.havocmc.service.Mercurial;
import net.havocmc.service.MercurialRuntime;
import net.havocmc.service.island.IslandLevel;
import net.havocmc.transport.proto.signal.island.IslandGenerate03;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by Giovanni on 28/02/2018.
 */
public class PlayerIsland implements Island {

    private final IslandVector SPAWN_RELATIVE = new IslandVector(151, -75, 151);

    @Nonnull
    private transient UUID owner;
    private transient String ownerName;

    private transient List<IslandProfile> currentVisitors = Lists.newArrayList();

    private HashMap<UUID, IslandProfile> islandMembers = Maps.newHashMap();
    private List<IslandRegion> islandRegions = Lists.newArrayList();

    private IslandLevel islandLevel;

    private IslandVector spawnVector;

    public PlayerIsland(@Nonnull Player player) {
        this.owner = player.getIdentifier();
        this.ownerName = player.getName();
        this.islandLevel = new IslandLevel();
    }

    public void changeOwner(IslandProfile newOwner) {
        if (newOwner.getUniqueId().toString().equals(owner.toString())) return;

        this.owner = newOwner.getUniqueId();
        this.ownerName = newOwner.getName();

        Mercurial.runtime().database().forceIslandAt(getIdentifier(), this, future -> {
            future.messageAll(MercurialRuntime.MC_MESSAGE_PREFIX + " &a&l" + future.getOwnerName() + "&a is now the owner of this island!");
        });
    }

    @Override
    public TransportableIsland asTransportable() {
        return new TransportableIsland().create(this);
    }

    /**
     * Handles the purchase of a {@link IslandRegion}.
     */
    public void attemptBuyRegion(IslandRegion region) {
        if (Mercurial.runtime().playerConnected(owner)) return;
        Optional<Player> playerOptional = Mercurial.runtime().findPlayer(owner);

        if (!playerOptional.isPresent()) return;
        Player player = playerOptional.get();

        if (islandRegions.contains(region)) {
            player.message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cYou've already unlocked the &l" + region.getName() + "&c!");
            return;
        }

        int playerGems = player.container().getGems();
        int gemCost = region.getGemPrice();

        if (playerGems < gemCost) {
            player.message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cYou do &lNOT&c have enough gems to unlock the &l" + region.getName());
            return;
        }

        player.container().removeGems(gemCost);
        islandRegions.add(region);
        player.message(MercurialRuntime.MC_MESSAGE_PREFIX + " &aYou've unlocked the &l" + region.getName() + "&a for " + gemCost + "&lG");

        messageAllExcept(player.getIdentifier(), " &aThe &a&l" + region.getName() + "&a has been unlocked.");
    }

    /**
     * Adds a member to this {@link Island} if possible.
     */
    public void addMember(UUID queryAuthor, UUID uniqueId, IslandRank rank) {
        if (!islandMembers.containsKey(queryAuthor)) return;

        Optional<Player> player = Mercurial.runtime().findPlayer(queryAuthor);
        if (!player.isPresent()) return;

        if (islandMembers.containsKey(uniqueId)) {
            player.get().message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cThis player is already a member of this island.");
            return;
        }

        Optional<Player> memberQuery = Mercurial.runtime().findPlayer(uniqueId);
        if (!memberQuery.isPresent()) {
            player.get().message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cThis player is not online.");
            return;
        }

        Player target = memberQuery.get();
        islandMembers.put(uniqueId, new IslandProfile(uniqueId, target.getName(), rank));
        target.message(MercurialRuntime.MC_MESSAGE_PREFIX + " &aYou've been added to &l" + ownerName + "&a's island.");

        messageAll(MercurialRuntime.MC_MESSAGE_PREFIX + " &a&l" + target.getName() + "&a has been added to the island by &n" + player.get().getName());
    }

    /**
     * Updates the rank of a member's {@link IslandProfile} on this island.
     */
    public void updateRank(UUID queryAuthor, UUID uuid, IslandRank rank) {
        if (!islandMembers.containsKey(queryAuthor)) return;

        Optional<Player> player = Mercurial.runtime().findPlayer(queryAuthor);
        if (!player.isPresent()) return;

        if (!islandMembers.containsKey(uuid)) {
            player.get().message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cThis player is not a member of this island.");
            return;
        }

        islandMembers.get(uuid).setRank(rank);
        player.get().message(MercurialRuntime.MC_MESSAGE_PREFIX + " &aYou've set &l" + islandMembers.get(uuid).getName() + "&a's rank to &l" + rank.name().toUpperCase());

        Optional<Player> memberQuery = Mercurial.runtime().findPlayer(uuid);
        if (!memberQuery.isPresent()) return;
        memberQuery.get().message(MercurialRuntime.MC_MESSAGE_PREFIX + " &aYour rank has been updated to &e&n" + rank.name() + "&a on &l" + ownerName + "&a's island.");
    }

    /**
     * Removes a member's {@link IslandProfile} from this island.
     */
    public void removeMember(UUID queryAuthor, UUID uuid) {
        if (!islandMembers.containsKey(queryAuthor)) return;

        if (!islandMembers.containsKey(uuid)) {
            Optional<Player> player = Mercurial.runtime().findPlayer(queryAuthor);
            if (!player.isPresent()) return;
            player.get().message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cThis player is not a member of this island.");
            return;
        }

        IslandProfile profile = islandMembers.get(uuid);
        islandMembers.remove(uuid);

        IslandProfile authorProfile = islandMembers.get(queryAuthor);
        messageAll(MercurialRuntime.MC_MESSAGE_PREFIX + " &c&l" + profile.getName() + "&c has been removed from the island by &n" + authorProfile.getName());

        Optional<Player> memberQuery = Mercurial.runtime().findPlayer(uuid);
        if (!memberQuery.isPresent()) return;
        memberQuery.get().message(MercurialRuntime.MC_MESSAGE_PREFIX + " &cYou've been removed from &l" + ownerName + "&c's island.");

        memberQuery.get().teleport("default", new Vector<Double>());
    }

    /**
     * Messages all current island visitors.
     */
    public void messageAll(String... messages) {
        if (currentVisitors.isEmpty()) return;
        currentVisitors.forEach(profile -> {
            UUID uuid = profile.getUniqueId();
            if (!Mercurial.runtime().playerConnected(uuid)) return;

            Optional<Player> player = Mercurial.runtime().findPlayer(uuid);
            player.ifPresent(player1 -> player1.message(messages));
        });
    }

    /**
     * Messages all current island visitors except the one specified.
     */
    public void messageAllExcept(UUID uuid, String... messages) {
        if (currentVisitors.isEmpty()) return;
        currentVisitors.forEach(profile -> {
            UUID uniqueId = profile.getUniqueId();
            if (uniqueId.toString().equals(uuid.toString())) return;

            if (!Mercurial.runtime().playerConnected(uniqueId)) return;
            Optional<Player> player = Mercurial.runtime().findPlayer(uniqueId);
            player.ifPresent(player1 -> player1.message(messages));
        });
    }

    /**
     * Writes a {@link IslandGenerate03} {@link net.havocmc.transport.proto.BufferedObject} which forces the generation of an island.
     */
    public void generate(Channel channel, IslandVector vector) {
        IslandGenerate03 generate03 = new IslandGenerate03()
                .forOwner(owner)
                .generateIsland(getIdentifier())
                .x(vector.getX())
                .y(vector.getY())
                .z(vector.getZ());

        spawnVector = vector;
        spawnVector.setY(spawnVector.getY() + 10);

        channel.writeAndFlush(generate03);
    }


    @Override
    @Nonnull
    public UUID getOwner() {
        return owner;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String getIdentifier() {
        return IslandIdentifier.from(ownerName, owner);
    }

    @Override
    public HashMap<UUID, IslandProfile> getMembers() {
        return islandMembers;
    }

    @Override
    public List<IslandRegion> getAvailableRegions() {
        return islandRegions;
    }

    @Override
    public List<IslandProfile> getCurrentVisitors() {
        return currentVisitors;
    }

    @Override
    public int getLevel() {
        return islandLevel.get();
    }

    public IslandLevel getIslandLevel() {
        return islandLevel;
    }
}
