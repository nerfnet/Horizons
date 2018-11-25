package net.havocmc.islands.challenge;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class ChallengeBlob implements Serializable {

    private final UUID blobOwner;
    private final HashMap<IslandChallenge, Integer> startedChallenges;
    private final List<IslandChallenge> completedChallenges;


    public ChallengeBlob(UUID blobOwner) {
        this.blobOwner = blobOwner;
        this.startedChallenges = Maps.newHashMap();
        this.completedChallenges = Lists.newArrayList();
    }

    public UUID getBlobOwner() {
        return blobOwner;
    }

    public void addCompletedChallenge(IslandChallenge challenge) {
        this.completedChallenges.add(challenge);
    }

    public void addStartedChallenge(IslandChallenge challenge, int progression) {
        this.startedChallenges.put(challenge, progression);
    }

    public HashMap<IslandChallenge, Integer> getStartedChallenges() {
        return startedChallenges;
    }

    public List<IslandChallenge> getCompletedChallenges() {
        return completedChallenges;
    }
}
