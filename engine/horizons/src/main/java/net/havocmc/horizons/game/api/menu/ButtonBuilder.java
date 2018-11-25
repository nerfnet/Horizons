package net.havocmc.horizons.game.api.menu;

import net.havocmc.horizons.game.api.API;
import net.havocmc.horizons.game.player.IslandPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Giovanni on 10/03/2018.
 */
public class ButtonBuilder {

    private final Menu menu;

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    private boolean locked = false;
    private int slot;

    public ButtonBuilder(Menu menu, Material material) {
        this.menu = menu;
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ButtonBuilder(Menu menu, ItemStack itemStack) {
        this.menu = menu;
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ButtonBuilder setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public ButtonBuilder setLocked(boolean par1) {
        this.locked = par1;
        return this;
    }

    public ButtonBuilder setName(String name) {
        Validate.notNull(itemMeta);
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ButtonBuilder setLore(Collection<String> lore) {
        Validate.notNull(itemMeta);
        itemMeta.setLore(lore.stream().map(API::colourFix).collect(Collectors.toList()));
        return this;
    }

    public Button create(Consumer<IslandPlayer> clickAction) {
        itemStack.setItemMeta(itemMeta);
        return new Button(menu, slot, itemStack, locked) {
            @Override protected void onClick(IslandPlayer player) {
                clickAction.accept(player);
            }
        };
    }
}
