package org.omscs.ml.a4burlap.experiments;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.utils.EpisodeWrapper;
import org.omscs.ml.a4burlap.vipi.DeltaCapable;
import org.omscs.ml.a4burlap.vipi.DeltaVariantPolicyIteration;
import org.omscs.ml.a4burlap.vipi.PISettings;
import org.omscs.ml.a4burlap.vipi.PIVIDeltaMetric;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.omscs.ml.a4burlap.utils.EpisodeWrapper.writeVIPIEpisodeData;

public class BlockDudePIExperiment implements RunnerVIPI {
  private MDPBlockDude mdpBlockDude;
  private PISettings piSettings;
  private CSVWriterGeneric csvWriter;

  private int episodeCount = 0;

  public BlockDudePIExperiment(
      MDPBlockDude mdpBlockDude, PISettings piSettings, CSVWriterGeneric csvWriter) {
    this.mdpBlockDude = mdpBlockDude;
    this.piSettings = piSettings;
    this.csvWriter = csvWriter;
  }

  @Override
  public void runAndSaveMulti( int episodes) {
    for (int i = 0; i < episodes; i++) {
      runAndSave(false);
      incrementEpisode();
    }
  }

  @Override
  public void runAndSave(boolean visualize) {
    SADomain bdDomain = (SADomain) mdpBlockDude.generateDomain();
    HashableStateFactory hashingFactory = mdpBlockDude.getHashableStateFactory();
    State initialBdState = mdpBlockDude.getInitialState();

    Planner piPlanner =
        new DeltaVariantPolicyIteration(
            bdDomain,
            this.piSettings.getGamma(),
            hashingFactory,
            this.piSettings.getPiDeltaThreshold(),
            this.piSettings.getViDeltaThreashold(),
            this.piSettings.getViMaxIterations(),
            this.piSettings.getPiMaxIterations());

    Policy policy = null;
    Episode episode = null;

    System.out.printf("Starting PI for Blockdude: \n");
    policy = piPlanner.planFromState(initialBdState);

    episode =
        PolicyUtils.rollout(policy, initialBdState, bdDomain.getModel(), getMaxRolloutIterations());
    System.out.printf("Done episode:\n");
    List<Action> actionSeq = episode.actionSequence;
    System.out.println(actionSeq);

    // writing of the metrics to file
    List<PIVIDeltaMetric> metrics = ((DeltaCapable) piPlanner).getDeltaMetrics();
    System.out.printf("Writing %d results to csv now for BlockDudePI experiment", metrics.size());
    String usableFileName =
        String.format("%s-%02d", this.piSettings.getShortName(), this.episodeCount);

    csvWriter.writeHeader(
        Arrays.asList(new String[] {"iter", "delta", "wallclock", "evals"}), NAME_BLOCKDUDE, usableFileName);
    long totalWallClock = 0, wallClock = 0, valueIterations = 0;
    for (int i = 0; i < metrics.size(); i++) {
      PIVIDeltaMetric metric = metrics.get(i);
      wallClock = metric.getWallClockMillis();
      valueIterations = metric.getViIterations();
      totalWallClock += wallClock;
      csvWriter.writeRow(
          Arrays.asList(
              new String[] {
                Integer.toString(i), Double.toString(metric.getDelta()), Long.toString(wallClock), Long.toString(valueIterations)
              }));
    }

    EpisodeWrapper eWrapper = new EpisodeWrapper(episode, totalWallClock, ((PolicyIteration)piPlanner).getTotalValueIterations() );
    String baseResutlPath = csvWriter.getFullBasePath().toString();

    Path episodePath = Path.of(baseResutlPath, NAME_BLOCKDUDE, usableFileName);
    writeVIPIEpisodeData(eWrapper, episodePath.toString());
  }

  private int getMaxRolloutIterations() {
    return mdpBlockDude.getMaxx() * mdpBlockDude.getMaxy() * 2;
  }


  @Override
  public void incrementEpisode() {
    this.episodeCount++;
  }
}
