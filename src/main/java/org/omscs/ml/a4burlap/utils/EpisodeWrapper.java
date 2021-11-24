package org.omscs.ml.a4burlap.utils;

import burlap.behavior.singleagent.Episode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class EpisodeWrapper {

  private final Episode episode;
  private final long totalWallClock;
  private final long totalValueIterations;

  public double totalReward;

  public EpisodeWrapper(Episode episode, long totalWallClock, long totalValueIterations) {
    this.episode = episode;
    this.totalWallClock = totalWallClock;
    this.totalValueIterations = totalValueIterations;
  }

  public String getEpisodeSummary() {

    List<Double> rewardSequence = this.episode.rewardSequence;

    for (Double rwrd : rewardSequence) {
      totalReward += rwrd;
    }

    int totalSteps = this.episode.numTimeSteps();
    String fmtString =
        "Total steps: %d\nTotal actions steps: %d\nTotal WallClock (MS): %d\nTotal VI Iterations: %d\nAverage Reward:%.3f\nReward Sequence: %s\nAction Sequence: %s";
    String outString =
        String.format(
            fmtString,
            totalSteps,
            this.episode.actionSequence.size(),
            this.totalWallClock,
            this.totalValueIterations,
            totalReward / this.episode.rewardSequence.size(),
            this.episode.rewardSequence,
            this.episode.actionSequence.toString());

    return outString;
  }

  public static void writeVIPIEpisodeData(EpisodeWrapper eWrapper, String episodeResultFilePath) {
    if (!episodeResultFilePath.endsWith(".episode.txt")) {
      episodeResultFilePath = episodeResultFilePath + ".episode.txt";
    }

    System.out.println("Writing episode to: " + episodeResultFilePath);

    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(episodeResultFilePath));
      out.write(eWrapper.getEpisodeSummary() + '\n');
      out.close();

    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
