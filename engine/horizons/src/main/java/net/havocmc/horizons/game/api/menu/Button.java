package net.havocmc.horizons.game.api.menu;

import net.havocmc.horizons.game.player.IslandPlayer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Giovanni on 09/03/2018.
 */
public abstract class Button {

    private final Menu menu;
    private final int slot;
    private final ItemStack itemStack;
    private boolean locked = true;

    public Button(@Nonnull Menu menu, int slot, @Nonnull ItemStack itemStack, boolean locked) {
        this.menu = menu;
        this.slot = slot;
        this.itemStack = itemStack;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public Menu getMenu() {
        return menu;
    }

    /**
     * Called when a {@link IslandPlayer} clicks on this button.
     */
    protected abstract void onClick(IslandPlayer player);
}
