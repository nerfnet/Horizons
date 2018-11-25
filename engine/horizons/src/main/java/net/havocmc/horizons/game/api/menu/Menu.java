package net.havocmc.horizons.game.api.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.havocmc.horizons.game.player.IslandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giovanni on 09/03/2018.
 */
public abstract class Menu {

    private final int id;
    private final String name;
    private final int slots;
    private HashMap<Integer, Button> buttonMap;
    private Inventory inventory;

    private List<IslandPlayer> viewers;

    public Menu(@Nonnull String name, int slots, int id) {
        this.id = id;
        this.buttonMap = Maps.newHashMap();
        this.name = name;
        this.slots = slots <= 0 ? 54 : slots;
        this.inventory = Bukkit.createInventory(null, this.slots, ChatColor.translateAlternateColorCodes('&', name));
        this.viewers = Lists.newArrayList();
    }

    public Menu newButton(Button button) {
        buttonMap.put(button.getSlot(), button);
        inventory.setItem(button.getSlot(), button.getItemStack());
        return this;
    }

    public Menu newButtons(Collection<Button> buttons) {
        buttons.forEach(button -> {
            buttonMap.put(button.getSlot(), button);
            inventory.setItem(button.getSlot(), button.getItemStack());
        });
        return this;
    }

    public Menu plainAdd(ItemStack itemStack) {
        inventory.addItem(itemStack);
        return this;
    }

    public void handleClick(int slot, IslandPlayer player, InventoryClickEvent event) {
        if (buttonMap.containsKey(slot)) {
            Button button = buttonMap.get(slot);
            event.setCancelled(button.isLocked());
            button.onClick(player);
        }
    }

    public boolean isFull() {
        return emptySlot() <= -1;
    }

    public Menu clear() {
        buttonMap.keySet().forEach(slot -> {
            inventory.remove(slot);
        });

        buttonMap.clear();
        return this;
    }

    public Menu clearButKeep(List<Integer> slots) {
        List<Integer> rmSlots = Lists.newArrayList();
        buttonMap.keySet().forEach(slot -> {
            if (slots.contains(slot)) return;
            rmSlots.add(slot);
            inventory.remove(inventory.getItem(slot));
        });

        rmSlots.forEach(slot -> {
            buttonMap.remove(slot);
        });
        return this;
    }

    public int emptySlot() {
        return inventory.firstEmpty();
    }

    public List<IslandPlayer> getViewers() {
        return viewers;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public int getSlots() {
        return slots;
    }

    /**
     * Closes this menu for the current {@link IslandPlayer} that's viewing it.
     */
    public abstract void close(IslandPlayer player);

    /**
     * Opens this menu for a {@link IslandPlayer}.
     */
    public abstract void open(IslandPlayer player);
}
