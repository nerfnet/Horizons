package net.havocmc.horizons.game.player.menu;

import net.havocmc.horizons.game.api.menu.Menu;
import net.havocmc.horizons.game.player.IslandPlayer;
import net.havocmc.islands.transport.TransportableIsland;

/**
 * Created by Giovanni on 10/03/2018.
 */
public class MemberMenu extends Menu {

    public MemberMenu(IslandPlayer player, TransportableIsland island) {
        super(player.getName() + "'s island.", 54, 1);

    }

    @Override
    public void open(IslandPlayer player) {

    }

    @Override
    public void close(IslandPlayer player) {

    }
}
