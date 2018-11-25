package net.havocmc.horizons.game.mechanic;

import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.horizons.game.api.mechanic.Mechanic;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.File;
import java.util.Optional;

/**
 * Created by Giovanni on 04/04/2018.
 */
public class MenuMechanic implements Mechanic {

    private Properties properties;

    @Override
    public void bootstrap() {
        listen();

    }

    @Override
    public void exit() {

    }

    @Override
    public void sideLoadProperties(Properties properties) {
        this.properties = properties;
    }

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) return;
        IslandPlayer islandPlayer = playerOptional.get();

        if (!islandPlayer.isInMenu()) return;
        islandPlayer.getCurrentMenu().handleClick(event.getRawSlot(), islandPlayer, event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) return;
        IslandPlayer islandPlayer = playerOptional.get();

        if (!islandPlayer.isInMenu()) return;
        islandPlayer.closeMenu();
    }

    @Override
    public String getName() {
        return "INTERACT.mech";
    }

    @Override
    public File getFile() {
        return new File(getName().toLowerCase());
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public TransportThreadFactory getThreadFactory() {
        return Horizons.runtime().getThreadFactory();
    }
}
