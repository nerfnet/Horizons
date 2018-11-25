package net.havocmc.islands;

import java.util.UUID;

/**
 * Created by Giovanni on 01/03/2018.
 */
public class IslandIdentifier {

    public static String from(String owner, UUID ownerId) {
        return owner + "$" + ownerId.toString().replaceAll("-", "");
    }
}
