package net.havocmc.horizons.game.mechanic.player;

import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.horizons.game.api.mechanic.Mechanic;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.horizons.game.world.WorldLocation;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import net.havocmc.transport.proto.signal.player.Connect02;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Giovanni on 06/03/2018.
 */
public class PlayerLoadMechanic implements Mechanic {

    private Properties properties;

    private boolean showJoinMessages = false;
    private List<String> joinMessages;
    private WorldLocation spawnLocation;

    @Override
    @SuppressWarnings("unchecked")
    public void bootstrap() {
        listen();

        showJoinMessages = properties.read("showJoinMessages", Boolean.class);
        joinMessages = (ArrayList<String>) properties.read("joinMessages", ArrayList.class);

        String spawnString = properties.read("worldSpawn", String.class);
        String[] data = spawnString.split("@");

        try {
            double[] axis = new double[]{Double.valueOf(data[1]), Double.valueOf(data[2]), Double.valueOf(data[3])};
            spawnLocation = new WorldLocation(data[0], axis);
        } catch (Exception e) {
            Horizons.runtime().logger().warning("Failed to initialize the world spawn: " + e.getMessage());
        }
    }

    @Override
    public void exit() {

    }


    @Override
    public void sideLoadProperties(Properties properties) {
        this.properties = properties;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();
        String originName = Horizons.runtime().getInstanceName();

        if (Horizons.runtime().isClosing()) {
            player.kickPlayer("&cServer closing.");
            return;
        }

        Connect02 connect02 = new Connect02()
                .withPlayer(uuid)
                .syncData("SYNC::connect@" + originName + "@" + player.getName());
        Horizons.runtime().channel().writeAndFlush(connect02);

        player.sendMessage(ChatColor.GREEN + "Loading, please wait..");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();

        if (!(Horizons.runtime().playerConnected(sender.getUniqueId())))
            event.setCancelled(true);

        for (Player player : event.getRecipients()) {
            if (!(Horizons.runtime().playerConnected(player.getUniqueId())))
                event.getRecipients().remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAchievementReward(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            player.teleport(event.getFrom());
            return;
        }

        if (playerOptional.get().isLocked())
            player.teleport(event.getFrom());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;

        Player player = (Player) event.getEntered();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Optional<IslandPlayer> playerOptional = Horizons.runtime().findIslandPlayer(player.getUniqueId());

        if (!playerOptional.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (playerOptional.get().isLocked())
            event.setCancelled(true);
    }

    public IslandPlayer sendJoinMessages(IslandPlayer islandPlayer) {
        islandPlayer.clearChat();

        joinMessages.forEach(string -> {
            String fixedString = string
                    .replace("[@PLAYER]", islandPlayer.getName())
                    .replace("[@?BACK]", islandPlayer.isNew() ? "," : " back,");

            String finalString = fixedString.replace("[@CENTERED]", "");

            islandPlayer.message(finalString, fixedString.endsWith("[@CENTERED]"), '$');
        });

        return islandPlayer;
    }

    @Override
    public String getName() {
        return "PLAYERLOAD.mech";
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

    public WorldLocation getWorldSpawn() {
        return spawnLocation;
    }

    public boolean isShowJoinMessages() {
        return showJoinMessages;
    }
}
