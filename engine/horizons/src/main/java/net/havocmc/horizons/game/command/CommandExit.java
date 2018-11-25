package net.havocmc.horizons.game.command;

import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.command.CommandArgs;
import net.havocmc.horizons.game.api.command.Executable;
import net.havocmc.horizons.game.api.command.ExecutableLevel;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.transport.proto.signal.connection.Exit01;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by Giovanni on 25/03/2018.
 */
@CommandArgs(name = "exit", description = "Exits gracefully.", level = ExecutableLevel.OPERATOR)
public class CommandExit extends Executable {

    @Override
    protected boolean execute(String[] args, CommandSender sender) {
        if (args.length > 0) {
            String timePar = args[0];
            long time = 1L; // 0 = illegal

            try {
                time = Long.valueOf(timePar);
            } catch (NumberFormatException e) {
                return error("&c" + e.getMessage(), sender);
            }

            if (time <= 0L)
                return error("&cInvalid time, must be above 0 but is " + time, sender);

            print("&aStopping the server in &l" + time + "&a seconds..", sender);
            printWarn(sender);

            Horizons.runtime().getThreadFactory().execute(() -> {
                Horizons.runtime().channel().writeAndFlush(
                        new Exit01()
                                .info("EXIT::" + Horizons.runtime().getInstanceName())
                                .server(Horizons.getInstance().getHostString()));
            }, time, TimeUnit.SECONDS);
            return true;
        }

        printWarn(sender);
        Horizons.runtime().getThreadFactory().execute(() -> {
            Horizons.runtime().channel().writeAndFlush(
                    new Exit01()
                            .info("EXIT::" + Horizons.runtime().getInstanceName())
                            .server(Horizons.getInstance().getHostString()));
        }, 15L, TimeUnit.SECONDS);
        return false;
    }

    private void printWarn(CommandSender sender) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Optional<IslandPlayer> islandPlayer = Horizons.runtime().findIslandPlayer(player.getUniqueId());

            if (!islandPlayer.isPresent()) {
                player.sendMessage(ChatColor.RED + "WARN: This server is stopping but an account error occurred.");
                player.sendMessage(ChatColor.RED + "Your progress will " + ChatColor.BOLD + "NOT " + ChatColor.RED + "be saved.");
                return;
            }

            IslandPlayer playerObject = islandPlayer.get();

            playerObject
                    .message("", false)
                    .message("", false)
                    .clearChat(sender != player)
                    .message("&6&lHORIZONS", true)
                    .message("&cThis server is stopping, your progress will be saved", false)
                    .message("&cand your session will be moved to a new server if possible.", false)
                    .playSound(Sound.BLOCK_NOTE_PLING)
                    .lock();
        });
    }
}
