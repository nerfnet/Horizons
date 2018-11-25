package net.havocmc.transport.proto.signal.island;

import net.havocmc.transport.proto.BufferedObject;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Created by Giovanni on 01/03/2018.
 */

public class MemberUpdate03 implements BufferedObject {

    private UUID queryAuthor;
    private UUID playerUUID;
    private String islandId;
    private String updateData;

    public MemberUpdate03 data(@Nonnull String updateData) {
        this.updateData = updateData;
        return this;
    }

    public MemberUpdate03 withAuthor(@Nonnull UUID author) {
        this.queryAuthor = author;
        return this;
    }

    public MemberUpdate03 withPlayer(@Nonnull UUID uuid) {
        this.playerUUID = uuid;
        return this;
    }

    public MemberUpdate03 onIsland(@Nonnull String islandId) {
        this.islandId = islandId;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x03";
    }

    public UUID getQueryAuthor() {
        return queryAuthor;
    }

    public UUID getPlayer() {
        return playerUUID;
    }

    public String getIslandId() {
        return islandId;
    }

    public String getUpdateData() {
        return updateData;
    }
}
