package org.omscs.ml.a4burlap.experiments;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.planning.Planner;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.utils.EpisodeWrapper;
import org.omscs.ml.a4burlap.vipi.DeltaCapable;
import org.omscs.ml.a4burlap.vipi.DeltaVariantValueIteration;
import org.omscs.ml.a4burlap.vipi.PIVIDeltaMetric;
import org.omscs.ml.a4burlap.vipi.VISettings;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.omscs.ml.a4burlap.utils.EpisodeWrapper.writeVIPIEpisodeData;

public class BlockDudeVIExperiment implements RunnerVIPI {

  private MDPBlockDude mdpBlockDude;
  private VISettings viSettings;
  private CSVWriterGeneric csvWriter;

  private int episodeCount = 0;

  public BlockDudeVIExperiment(
      MDPBlockDude mdpBlockDude, VISettings viSettings, CSVWriterGeneric csvWriter) {
    this.mdpBlockDude = mdpBlockDude;
    this.viSettings = viSettings;
    this.csvWriter = csvWriter;
  }

  /**
   * Used for rolling out the policy, if the policy is poor, such that an agent will be stuck
   * continually hitting a wall, this max iteraion will be safeguard to avoid policy roll out to get
   * in infinte loop
   *
   * @return max rollout iteration count
   */
  private int getMaxRolloutIterations() {
    return mdpBlockDude.getMaxx() * mdpBlockDude.getMaxy() * 2;
  }

  @Override
  public void runAndSave(boolean visualize) {

    SADomain bdDomain = (SADomain) mdpBlockDude.generateDomain();
    HashableStateFactory hashingFactory = mdpBlockDude.getHashableStateFactory();
    State initialBdState = mdpBlockDude.getInitialState();

    Planner viPlanner =
        new DeltaVariantValueIteration(
            bdDomain,
            this.viSettings.getGamma(),
            hashingFactory,
            viSettings.getViDeltaThreshold(),
            viSettings.getViMaxIterations());

    Policy policy = null;
    Episode episode = null;

    System.out.printf("Starting VI for Blockdude: \n");
    policy = viPlanner.planFromState(initialBdState);

    episode =
        PolicyUtils.rollout(policy, initialBdState, bdDomain.getModel(), getMaxRolloutIterations());
    System.out.printf("Done episode:\n");
    List<Action> actionSeq = episode.actionSequence;

    System.out.println(actionSeq);

    // writing of the metrics to file
    List<PIVIDeltaMetric> metrics = ((DeltaCapable) viPlanner).getDeltaMetrics();
    System.out.printf("Writing %d results to csv now for BlockDudeVI experiment", metrics.size());
    String usableFileName =
        String.format("%s-%02d", this.viSettings.getShortName(), this.episodeCount);

    csvWriter.writeHeader(
        Arrays.asList(new String[] {"iter", "delta", "wallclock"}), NAME_BLOCKDUDE, usableFileName);
    long totalWallClock = 0, wallClock = 0;
    for (int i = 0; i < metrics.size(); i++) {
      PIVIDeltaMetric metric = metrics.get(i);
      wallClock = metric.getWallClockMillis();
      totalWallClock += wallClock;
      csvWriter.writeRow(
          Arrays.asList(
              new String[] {
                Integer.toString(i), Double.toString(metric.getDelta()), Long.toString(wallClock)
              }));
    }

    EpisodeWrapper eWrapper = new EpisodeWrapper(episode, totalWallClock, metrics.size());
    String baseResutlPath = csvWriter.getFullBasePath().toString();

    Path episodePath = Path.of(baseResutlPath, NAME_BLOCKDUDE, usableFileName);
    writeVIPIEpisodeData(eWrapper, episodePath.toString());
  }


  @Override
  public void incrementEpisode() {
    this.episodeCount++;
  }
}
