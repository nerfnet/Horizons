package net.havocmc.transport.proto.signal.player;

import net.havocmc.transport.proto.BufferedObject;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Created by Giovanni on 28/02/2018.
 */
public class Message02 implements BufferedObject {

    private UUID playerUUID;
    private String[] messages;

    public Message02 forPlayer(@Nonnull UUID uuid) {
        this.playerUUID = uuid;
        return this;
    }

    public Message02 withMessage(String... messages) {
        this.messages = messages;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x02";
    }

    public UUID getUniqueId() {
        return playerUUID;
    }

    public String[] getMessages() {
        return messages;
    }
}
