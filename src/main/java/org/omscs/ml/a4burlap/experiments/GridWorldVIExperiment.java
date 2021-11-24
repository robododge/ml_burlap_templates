package org.omscs.ml.a4burlap.experiments;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import org.omscs.ml.a4burlap.mdp.MDPGridWorld;
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

public class GridWorldVIExperiment implements RunnerVIPI {

  private VISettings viSettings;
  private CSVWriterGeneric csvWriter;
  private MDPGridWorld mdpGridWorld;

  private int episodeCount = 0;

  public GridWorldVIExperiment(
          MDPGridWorld mdpGridWorld, VISettings viSettings, CSVWriterGeneric csvWriter ) {
    this.viSettings = viSettings;
    this.csvWriter = csvWriter;
    this.mdpGridWorld = mdpGridWorld;
  }

  @Override
  public void runAndSave(boolean visualize) {

    SADomain gwDomain = (SADomain) mdpGridWorld.generateDomain();
    HashableStateFactory hashingFactory = mdpGridWorld.getHashableStateFactory();
    State initialGWState = mdpGridWorld.getInitialState();

    Planner viPlanner =
            new DeltaVariantValueIteration(
                    gwDomain,
                    this.viSettings.getGamma(),
                    hashingFactory,
                    viSettings.getViDeltaThreshold(),
                    viSettings.getViMaxIterations());

    Policy policy = null;
    Episode episode = null;

    System.out.printf("Starting VI for GridWorld: \n");
    policy = viPlanner.planFromState(initialGWState);

    episode =
        PolicyUtils.rollout(policy, initialGWState, gwDomain.getModel(), getMaxRolloutIterations());
    System.out.printf("Done episode:\n");
    List<Action> actionSeq = episode.actionSequence;

    System.out.println(actionSeq);//System.out.println(episode.stateSequence);

    // writing of the metrics to file
    List<PIVIDeltaMetric> metrics = ((DeltaCapable) viPlanner).getDeltaMetrics();
    System.out.printf("Writing %d results to csv now for GridWorldVI experiment", metrics.size());
    String usableFileName =
        String.format("%s-%02d", this.viSettings.getShortName(), this.episodeCount);

    csvWriter.writeHeader(
            Arrays.asList(new String[] {"iter", "delta", "wallclock"}), NAME_GRIDWORLD, usableFileName);
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

    Path episodePath = Path.of(baseResutlPath, NAME_GRIDWORLD, usableFileName);
    writeVIPIEpisodeData(eWrapper, episodePath.toString());
    System.out.printf(
            "Optimal Policy \n- total steps %d\n- total reward %.5f\n", episode.actionSequence.size(), eWrapper.totalReward);

    if (visualize) {
      runWithGui((ValueFunction) viPlanner, initialGWState, gwDomain, policy, hashingFactory);
    }
  }

  public void runWithGui(
      ValueFunction valueFunction,
      State initialState,
      SADomain domain,
      Policy policy,
      HashableStateFactory hashingFactory) {

    List<State> states = StateReachability.getReachableStates(initialState, domain, hashingFactory);
    ValueFunctionVisualizerGUI gui =
        GridWorldDomain.getGridWorldValueFunctionVisualization(
            states, this.mdpGridWorld.getWidth(), this.mdpGridWorld.getHeight(), valueFunction, policy);

    gui.setTitle(this.viSettings.getShortName());
    gui.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    gui.initGUI();
  }

  private int getMaxRolloutIterations() {
    return this.mdpGridWorld.getHeight() * this.mdpGridWorld.getWidth() * 2;
  }

  @Override
  public void incrementEpisode() {
    this.episodeCount++;
  }
}
