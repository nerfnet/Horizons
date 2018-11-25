package net.havocmc.horizons.game.api.command;

import org.bukkit.command.CommandSender;

/**
 * Created by Giovanni on 09/06/2018.
 */
public abstract class SubCommand {

    private final String name;
    private final String[] aliases;

    public SubCommand(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    /**
     * Executes this {@link SubCommand}.
     */
    public abstract void execute(CommandSender sender);

    /**
     * Returns the name of this {@link SubCommand}.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the aliases of this {@link SubCommand}.
     */
    public String[] getAliases() {
        return aliases;
    }
}
