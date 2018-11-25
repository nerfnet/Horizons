import net.havocmc.service.player.Player;
import net.havocmc.service.player.PlayerContainer;
import net.havocmc.service.player.PlayerIsland;
import net.havocmc.transport.GloArgs;

import java.util.UUID;

/**
 * Created by Giovanni on 28/02/2018.
 */
public class ContainerPrintArgs {

    public static void main(String[] args) {
        Player player = new Player("nerfn3tious", UUID.randomUUID(), null);
        PlayerContainer container = new PlayerContainer();
        container.setExperience(515125252);
        container.setGems(315);

        PlayerIsland island = new PlayerIsland(player);
        container.setIsland(island);

        player.setContainer(container);

        String containerData = GloArgs.gson.toJson(player);
        System.out.println(GloArgs.gson.toJson(player));
        Player player1 = GloArgs.gson.fromJson(containerData, Player.class);

        System.out.println("");
        System.out.println("");
        System.out.println(GloArgs.gson.toJson(player1));

    }
}
