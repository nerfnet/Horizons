package net.havocmc.horizons.game.api.command;

import com.google.common.collect.Lists;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.player.IslandPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Created by Giovanni on 25/03/2018.
 */
public abstract class Executable implements CommandExecutor {

    private final List<SubCommand> subCommands = Lists.newArrayList();

    String[] readAnnotation() {
        CommandArgs command = getClass().getAnnotation(CommandArgs.class);
        if (command == null)
            return new String[]{"invalid", "invalid"};
        return new String[]{command.name(), command.description(), command.level().toString()};
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String[] executableData = readAnnotation();
        ExecutableLevel level = ExecutableLevel.valueOf(executableData[2]);

        // from level to max level player > console (all)
        if (level.atLevel(ExecutableLevel.PLAYER)) { // player >
            if (!(commandSender instanceof Player)) return false;

            Player player = (Player) commandSender;
            if (checkSubCommands(strings, player)) // Player executes a sub-command but check perm first
                return true;
            return execute(strings, player);
        }

        if (level.atLevel(ExecutableLevel.OPERATOR)) { // operator >
            if (commandSender.isOp() || commandSender instanceof ConsoleCommandSender) {
                if (checkSubCommands(strings, commandSender))
                    return true;
                return execute(strings, commandSender);
            }
        }

        if (level.atLevel(ExecutableLevel.CONSOLE)) { // console >
            if (commandSender instanceof ConsoleCommandSender) {
                if (checkSubCommands(strings, commandSender))
                    return true;
                return execute(strings, commandSender);
            }
        }

        error("&cNo permission.", commandSender);
        return false;
    }

    private boolean checkSubCommands(@Nonnull String[] args, CommandSender sender) {
        if(args.length <= 0)
            return false;

        String string = args[0];
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(string)) {
                subCommand.execute(sender);
                return true;
            }
            for (String alias : subCommand.getAliases())
                if(alias.equalsIgnoreCase(string)) {
                    subCommand.execute(sender);
                    return true;
                }
        }
        return false;

    }

    protected Executable registerSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    protected boolean error(String string, CommandSender sender) {
        print(string, sender);
        return false;
    }

    protected Optional<IslandPlayer> playerFromSender(CommandSender sender) {
        if (sender instanceof Player)
            return Horizons.runtime().findIslandPlayer(((Player) sender).getUniqueId());
        return Optional.empty();
    }

    protected void print(String string, CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
    }

    public ExecutableLevel getLevel() {
        return ExecutableLevel.valueOf(readAnnotation()[2]);
    }

    public String getDescription() {
        return readAnnotation()[1];
    }

    public String getName() {
        return readAnnotation()[0];
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    protected abstract boolean execute(String[] args, CommandSender sender);
}
