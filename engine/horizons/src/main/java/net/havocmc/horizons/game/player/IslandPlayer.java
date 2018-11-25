package net.havocmc.horizons.game.player;

import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.HorizonsRuntime;
import net.havocmc.horizons.game.api.API;
import net.havocmc.horizons.game.api.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Created by Giovanni on 04/03/2018.
 */
public class IslandPlayer {

    private final UUID uuid;
    private final String name;

    private NetIslandPlayer netPlayer;

    private boolean locked = false;

    private Menu currentMenu;

    public IslandPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.netPlayer = new NetIslandPlayer(this);
    }

    public UUID getIdentifier() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public IslandPlayer openMenu(Menu menu) {
        if (locked) return this;
        if (isInMenu()) closeMenu();

        currentMenu = menu;
        currentMenu.open(this);
        getPlayer().openInventory(currentMenu.getInventory());
        return this;
    }

    public IslandPlayer refresh() {
        getPlayer().setSaturation(30);
        getPlayer().setHealth(20D);
        getPlayer().setFallDistance(0F);
        getPlayer().setFoodLevel(20);
        return this;
    }

    public IslandPlayer closeMenu() {
        if (currentMenu != null)
            currentMenu.close(this);

        currentMenu = null;
        getPlayer().closeInventory();
        return this;
    }

    public IslandPlayer teleport(Location location, boolean mainThread) {
        if (mainThread)
            Bukkit.getScheduler().scheduleSyncDelayedTask(Horizons.getInstance(), () -> {
                getPlayer().teleport(location);
            });
        else getPlayer().teleport(location);
        return this;
    }

    public IslandPlayer message(String message, boolean center) {
        if (center) API.sendCenteredMessage(getPlayer(), message);
        else getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return this;
    }

    public IslandPlayer message(String message, boolean center, boolean withPrefix) {
        if (center)
            API.sendCenteredMessage(getPlayer(), withPrefix ? HorizonsRuntime.HORIZONS_PREFIX + " " + message : message);
        else
            getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', withPrefix ? HorizonsRuntime.HORIZONS_PREFIX + " " + message : message));
        return this;
    }

    public IslandPlayer message(String message, boolean center, char colourChar) {
        if (center) API.sendCenteredMessage(getPlayer(), message, colourChar);
        else getPlayer().sendMessage(ChatColor.translateAlternateColorCodes(colourChar, message));
        return this;
    }

    public IslandPlayer playSound(Sound sound) {
        getPlayer().getWorld().playSound(getPlayer().getLocation(), sound, 50F, 1F);
        return this;
    }

    public IslandPlayer playSound(Sound sound, float vol, float pitch) {
        getPlayer().getWorld().playSound(getPlayer().getLocation(), sound, vol, pitch);
        return this;
    }

    public IslandPlayer clearChat() {
        IntStream.range(0, 150).forEach(consumer -> {
            getPlayer().sendMessage("");
        });
        return this;
    }

    public IslandPlayer clearChat(boolean ifState) {
        if (ifState)
            clearChat();
        return this;
    }

    public NetIslandPlayer net() {
        return netPlayer;
    }

    public IslandPlayer lock() {
        locked = true;
        return this;
    }

    public boolean isNew() {
        return true;
    }

    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isInMenu() {
        return currentMenu != null;
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }
}
