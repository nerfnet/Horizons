package net.havocmc.transport.proto.signal.island;

import net.havocmc.islands.ExperienceType;
import net.havocmc.transport.proto.BufferedObject;

import java.util.UUID;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class IslandLevelStatus03 implements BufferedObject {

    private UUID ownerId;
    private int experience;
    private ExperienceType type;

    public IslandLevelStatus03 forOwner(UUID ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public IslandLevelStatus03 incrementExperience(int amount) {
        this.experience = amount;
        return this;
    }

    public IslandLevelStatus03 experienceType(ExperienceType experienceType) {
        this.type = experienceType;
        return this;
    }

    @Override
    public String getTransporterId() {
        return "0x03";
    }

    public int getExperience() {
        return experience;
    }

    public ExperienceType getType() {
        return type;
    }

    public UUID getOwnerId() {
        return ownerId;
    }
}
