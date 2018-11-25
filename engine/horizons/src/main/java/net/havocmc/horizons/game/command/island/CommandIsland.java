package net.havocmc.horizons.game.command.island;

import net.havocmc.horizons.game.api.command.CommandArgs;
import net.havocmc.horizons.game.api.command.Executable;
import net.havocmc.horizons.game.api.command.ExecutableLevel;
import net.havocmc.horizons.game.api.command.SubCommand;
import net.havocmc.horizons.game.api.menu.MenuId;
import net.havocmc.horizons.game.player.IslandPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Giovanni on 09/06/2018.
 */
@CommandArgs(name = "is", description = "Island management commands.", level = ExecutableLevel.PLAYER)
public class CommandIsland extends Executable {

    public CommandIsland() {
        registerSubCommand(new SubCommand("help", new String[]{"commands", "?"}) {
            @Override public void execute(CommandSender sender) {
                if (!(sender instanceof Player)) return;
                if (!playerFromSender(sender).isPresent()) return;

                IslandPlayer player = playerFromSender(sender).get();
                player.message("&8&m---------------&6&l HORIZONS &8&m---------------", false, false);

                getSubCommands().forEach(subCommand -> {
                    player.message("&7/is " + subCommand.getName(), false, false);
                });
            }
        });

        registerSubCommand(new SubCommand("info", new String[]{"i", "information"}) {
            @Override public void execute(CommandSender sender) {
                if (!(sender instanceof Player)) return;
                if (!playerFromSender(sender).isPresent()) return;

                IslandPlayer player = playerFromSender(sender).get();
                player.net().displayIslandInfo();
            }
        });
    }

    @Override
    protected boolean execute(String[] args, CommandSender sender) {
        Optional<IslandPlayer> playerOptional = playerFromSender(sender);
        if (!playerOptional.isPresent()) return false;
        IslandPlayer player = playerOptional.get();

        if (args.length <= 0) {
            player.net().openMenu(MenuId.ISLAND_MENU);
            return true;
        }

        return false;
    }
}
