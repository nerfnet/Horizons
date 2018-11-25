package net.havocmc.transport.proto.signal.bukkit;

import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class PlaySound04 implements BufferedObject {

    private UUID playerId;

    private String soundName;
    private float volume;
    private float pitch;

    public PlaySound04 forPlayer(UUID playerId) {
        this.playerId = playerId;
        return this;
    }

    public PlaySound04 sound(String soundName) {
        this.soundName = soundName;
        return this;
    }

    public PlaySound04 volume(float volume) {
        this.volume = volume;
        return this;
    }

    public PlaySound04 pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x04";
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getSoundName() {
        return soundName;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
