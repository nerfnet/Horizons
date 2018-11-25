package net.havocmc.horizons.game.player.menu;

import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.SkullPrefab;
import net.havocmc.horizons.game.api.menu.ButtonBuilder;
import net.havocmc.horizons.game.api.menu.Menu;
import net.havocmc.horizons.game.api.menu.MenuId;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.islands.transport.TransportableIsland;
import net.havocmc.transport.proto.signal.island.IslandCreate03;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Giovanni on 10/03/2018.
 */
public class IslandMenu extends Menu {

    private TransportableIsland island;

    public IslandMenu(IslandPlayer player, @Nullable TransportableIsland transportableIsland) {
        super(player.getName() + "'s island", 27, 0);

        System.out.println(transportableIsland == null);

        if (transportableIsland == null) {
            newButton(
                    new ButtonBuilder(this, Material.GRASS)
                            .setLocked(true)
                            .setSlot(13)
                            .setName("&aCreate Island")
                            .setLore(Collections.singletonList("&7Click here to create an island."))
                            .create(player1 -> {
                                player1
                                        .closeMenu()
                                        .message("&aCreating island, please wait..", false, true)
                                        .playSound(Sound.BLOCK_NOTE_PLING);

                                Horizons.runtime().channel().writeAndFlush(new IslandCreate03().forPlayer(player1.getIdentifier()));
                            })
            );
            return;
        }
        this.island = transportableIsland;

        // SET
        int level = transportableIsland.getLevel();
        int gems = transportableIsland.getGems();
        int membersSize = transportableIsland.getProfiles().size();

        newButton(
                new ButtonBuilder(this, SkullPrefab.GLOBE.getSkull())
                        .setLocked(true)
                        .setSlot(4)
                        .setName("&bIsland Info")
                        .setLore(Arrays.asList("&7Level: &e" + level, "&7Gems: &e" + gems, "&7Members: &e" + membersSize))
                        .create(player1 -> {

                        })
        );

        newButton(
                new ButtonBuilder(this, Material.GRASS)
                        .setLocked(true)
                        .setSlot(11)
                        .setName("&bIsland Home")
                        .create(player1 -> {
                            // TODO
                        }));

        newButton(
                new ButtonBuilder(this, Material.SKULL_ITEM)
                        .setLocked(true)
                        .setSlot(12)
                        .setName("&eMembers")
                        .setLore(Arrays.asList("&7Click here to view and manage", "&7your island's members."))
                        .create(player1 -> {
                            // TODO
                        }));

        newButton(
                new ButtonBuilder(this, Material.EMERALD)
                        .setLocked(true)
                        .setSlot(13)
                        .setName("&aGem Shop")
                        .setLore(Arrays.asList("&7Purchase &eExpansions &7and &eTokens", "&7using your island's gems."))
                        .create(player1 -> {
                            // TODO
                        }));

        newButton(
                new ButtonBuilder(this, Material.EXP_BOTTLE)
                        .setLocked(true)
                        .setSlot(14)
                        .setName("&6Challenges")
                        .setLore(Collections.singletonList("&7View your island's challenges."))
                        .create(player1 -> {
                            player1.net().openMenu(MenuId.CHALLENGE_MENU);
                        }));

        newButton(
                new ButtonBuilder(this, Material.BED)
                        .setLocked(true)
                        .setSlot(15)
                        .setName("&dSet Island Spawn")
                        .setLore(Arrays.asList("&7Click to set your island", "&7spawn at your location."))
                        .create(player1 -> {
                            // TODO
                        }));

        newButton(
                new ButtonBuilder(this, Material.TNT)
                        .setLocked(true)
                        .setSlot(26)
                        .setName("&4Delete Island")
                        .setLore(Collections.singletonList("&7Click to delete your island."))
                        .create(player1 -> {
                            // TODO
                        }));
    }

    @Override
    public void open(IslandPlayer player) {
    }

    @Override
    public void close(IslandPlayer player) {

    }

    public TransportableIsland getIsland() {
        return island;
    }
}
