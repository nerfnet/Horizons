package net.havocmc.horizons.game.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Giovanni on 25/03/2018.
 */
class CraftCommand extends Command {

    private final Executable executable;

    CraftCommand(Executable executable) {
        super(executable.readAnnotation()[0]);
        this.executable = executable;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return executable != null && executable.onCommand(commandSender, this, s, strings);
    }
}
