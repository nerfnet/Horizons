package net.havocmc.horizons.game.api.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Giovanni on 25/03/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArgs {

    /**
     * Returns the command name.
     */
    String name();

    /**
     * Returns the command description.
     */
    String description();

    /**
     * Returns the {@link ExecutableLevel} of this command, used for determining whether
     * the command should be ran or not by the {@link org.bukkit.command.CommandSender}.
     */
    ExecutableLevel level();
}
