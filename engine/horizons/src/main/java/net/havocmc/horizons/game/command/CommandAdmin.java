package net.havocmc.horizons.game.command;

import com.google.common.collect.ImmutableSet;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.command.CommandArgs;
import net.havocmc.horizons.game.api.command.Executable;
import net.havocmc.horizons.game.api.command.ExecutableLevel;
import org.bukkit.command.CommandSender;

/**
 * Created by Giovanni on 01/04/2018.
 */
@CommandArgs(name = "admin", description = "Administrator command.", level = ExecutableLevel.OPERATOR)
public class CommandAdmin extends Executable {

    @Override
    protected boolean execute(String[] args, CommandSender sender) {
        ImmutableSet<Executable> executables = Horizons.getMechanicFactory().getCommandMap().getExecutables();

        print("&6&lHORIZONS &eAdministrator commands", sender);

        executables.forEach(executable -> {
            if (executable.getLevel().atLevel(ExecutableLevel.OPERATOR)) // OPERATOR + CONSOLE
                print("&c/" + executable.getName() + " &b| &a" + executable.getDescription(), sender);
        });
        return false;
    }
}