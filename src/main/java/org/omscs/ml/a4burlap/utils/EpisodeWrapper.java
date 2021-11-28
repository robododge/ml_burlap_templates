package org.omscs.ml.a4burlap.utils;

import burlap.behavior.singleagent.Episode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import static org.omscs.ml.a4burlap.experiments.Runner.NAME_GRIDWORLD;

public class EpisodeWrapper {

  private final Episode episode;
  private final long totalWallClock;
  private final long totalValueIterations;

  public double totalReward;
  private int qConvergedAt;

  public EpisodeWrapper(Episode episode, long totalWallClock) {
    this(episode,totalWallClock,-1);
  }

  public EpisodeWrapper(Episode episode, long totalWallClock, long totalValueIterations) {
    this.episode = episode;
    this.totalWallClock = totalWallClock;
    this.totalValueIterations = totalValueIterations;
    buildTotalReward();
  }

  private void buildTotalReward() {
    List<Double> rewardSequence = this.episode.rewardSequence;

    for (Double rwrd : rewardSequence) {
      totalReward += rwrd;
    }
  }

  public long getTotalWallClock() {
    return totalWallClock;
  }

  public long getTotalValueIterations() {
    return totalValueIterations;
  }

  public double getTotalReward() {
    return totalReward;
  }

  public String getVIPIEpisodeSummary() {

    if(this.totalValueIterations <= 0) {
      throw new RuntimeException("Cannot get VIPI episode summary without VI iteration count");
    }

    int totalSteps = this.episode.numTimeSteps();
    String fmtString =
        "Total steps: %d\nTotal actions steps: %d\nTotal WallClock (MS): %d\nTotal VI Iterations: %d\nTotal Rewards: %.3f\nAverage Reward:%.3f\nReward Sequence: %s\nAction Sequence: %s";
    String outString =
        String.format(
            fmtString,
            totalSteps,
            this.episode.actionSequence.size(),
            this.totalWallClock,
            this.totalValueIterations,
            totalReward,
            totalReward / this.episode.rewardSequence.size(),
            this.episode.rewardSequence,
            this.episode.actionSequence.toString());

    return outString;
  }

  public String getQEpisodeSummary() {

    int totalSteps = this.episode.numTimeSteps();
    String fmtString =
            "Total steps: %d\nTotal actions steps: %d\nTotal WallClock (MS): %d\nTotal Rewards: %.3f\nAverage Reward:%.3f\nReward Sequence: %s\nAction Sequence: %s";
    String outString =
            String.format(
                    fmtString,
                    totalSteps,
                    this.episode.actionSequence.size(),
                    this.totalWallClock,
                    totalReward,
                    totalReward / this.episode.rewardSequence.size(),
                    this.episode.rewardSequence,
                    this.episode.actionSequence.toString());

    return outString;
  }

  private static void writeEpisodeStringToFile(String episodeString, String episodeResultFilePath) {
    if (!episodeResultFilePath.endsWith(".episode.txt")) {
      episodeResultFilePath = episodeResultFilePath + ".episode.txt";
    }

    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(episodeResultFilePath));
      out.write(episodeString + '\n');
      out.close();

    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static void writeVIPIEpisodeData(EpisodeWrapper eWrapper, String episodeResultFilePath) {

    System.out.println("Writing VI/PI episode to: " + episodeResultFilePath);
    String vipiEpisodeString = eWrapper.getVIPIEpisodeSummary();
    writeEpisodeStringToFile(vipiEpisodeString, episodeResultFilePath);
  }

  public static void writeQEpisodeData(EpisodeWrapper eWrapper, String episodeResultFilePath) {

    System.out.println("Writing Q episode to: " + episodeResultFilePath);
    String qEpisodeString = eWrapper.getQEpisodeSummary();
    writeEpisodeStringToFile(qEpisodeString, episodeResultFilePath);

  }

  public static void writeEpisodeToCSV(EpisodeWrapper eWrapper, CSVWriterGeneric csvWriter, RunResultsCsvWriterCallback resultsCallback) {

    String episodeResultCSVPath = resultsCallback.getFullCsvFileName(csvWriter);

    File tracker = new File(episodeResultCSVPath);

    System.out.println("*(*(*(*(*( Checking existance of episode file: "+tracker);

    if (!tracker.exists()) {
      System.out.println("*(*(*(*(*( Writing CSV Header for: "+tracker);
      resultsCallback.writeHeaderOfCsv(csvWriter);
    }
    resultsCallback.writeRowOfCsv(csvWriter, eWrapper);
  }

  public int getqConvergedAt() {
    return qConvergedAt;
  }

  public void setqConvergedAt(int qConvergedAt) {
    this.qConvergedAt = qConvergedAt;
  }
}
