package net.havocmc.horizons.game.player.menu;

import net.havocmc.horizons.game.api.menu.ButtonBuilder;
import net.havocmc.horizons.game.api.menu.Menu;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.islands.challenge.ChallengeBlob;
import net.havocmc.islands.challenge.IslandChallenge;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class ChallengeMenu extends Menu {

    public ChallengeMenu(IslandPlayer player, ChallengeBlob blob) {
        super(player.getName() + "'s island.", 54, 2);

        // Challenges started and/or completed
        if (!blob.getCompletedChallenges().isEmpty()) {
            blob.getCompletedChallenges().forEach(challenge -> {
                newButton(new ButtonBuilder(this, Material.ENCHANTED_BOOK)
                        .setLocked(true)
                        .setSlot(emptySlot())
                        .setName("&d&l" + challenge.name().replaceAll("_", " "))
                        .setLore(Arrays.asList("&7&o" + challenge.get().getDescription(), "", "&aChallenge completed"))
                        .create(player1 -> {
                            // Do nothing, the challenge has been completed
                        }));
            });
        }

        if (!blob.getStartedChallenges().isEmpty()) {
            blob.getStartedChallenges().keySet().forEach(challenge -> {
                String progression = "&7Progression: &funknown";
                int progressionAmount = blob.getStartedChallenges().get(challenge);
                int requirement = challenge.get().getRequirement();

                if(progressionAmount >= requirement / 2)
                    progression = "&7Progression: &a" + progressionAmount + "&7/&b" + requirement;
                else progression = "&7Progression: &c" + progressionAmount + "&7/&b" + requirement;

                newButton(new ButtonBuilder(this, Material.BOOK_AND_QUILL)
                        .setLocked(true)
                        .setSlot(emptySlot())
                        .setName("&6&l" + challenge.name().replaceAll("_", " "))
                        .setLore(Arrays.asList(
                                "&7&o" + challenge.get().getDescription(),
                                "",
                                progression,
                                "",
                                "&aChallenge started"))
                        .create(player1 -> {
                            // Do nothing, challenge already started
                        }));
            });
        }

        // Challenges which aren't started/completed(if it's not completed then it has never been started, duh)
        Stream.of(IslandChallenge.values()).forEach(challenge -> {
            if (blob.getStartedChallenges().containsKey(challenge) || blob.getCompletedChallenges().contains(challenge))
                return;
            newButton(new ButtonBuilder(this, Material.BOOK)
                    .setLocked(true)
                    .setSlot(emptySlot())
                    .setName("&6&l" + challenge.name().replaceAll("_", " "))
                    .setLore(Arrays.asList("&7&o" + challenge.get().getDescription(), "", "&aClick to start challenge"))
                    .create(player1 -> {
                        player1.closeMenu();
                        player1.net().startChallenge(challenge);
                    }));
        });
    }

    @Override
    public void close(IslandPlayer player) {

    }

    @Override
    public void open(IslandPlayer player) {

    }
}
