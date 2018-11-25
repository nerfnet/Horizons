package net.havocmc.horizons.game.api.command;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by Giovanni on 25/03/2018.
 */
public class CommandMapWrapper {

    private final CommandMap commandMap;
    private final Set<Executable> executables = Sets.newHashSet();

    public CommandMapWrapper() throws Exception {
        Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        commandMap = (CommandMap) field.get(Bukkit.getServer());
    }

    public CommandMapWrapper register(Executable executable) {
        if (commandMap == null) return this;
        CraftCommand craftCommand = new CraftCommand(executable);
        commandMap.register("", craftCommand);

        executables.add(executable);
        return this;
    }

    public ImmutableSet<Executable> getExecutables() {
        return ImmutableSet.copyOf(executables);
    }
}
