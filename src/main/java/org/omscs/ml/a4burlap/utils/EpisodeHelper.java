package org.omscs.ml.a4burlap.utils;

import burlap.behavior.singleagent.Episode;

public class EpisodeHelper {

    public static double sumupTotalRewards(Episode episode) {
        Double totalReward = 0d;

        for (Double rwrd : episode.rewardSequence) {
            totalReward += rwrd;
        }
        return totalReward;
    }
}
