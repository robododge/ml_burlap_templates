package org.omscs.ml.a4burlap.experiments;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;
import org.omscs.ml.a4burlap.mdp.MDPGridWorld;
import org.omscs.ml.a4burlap.mdp.grid.Coordinates;
import org.omscs.ml.a4burlap.qlearn.EGreedyDecayPolicy;
import org.omscs.ml.a4burlap.qlearn.QLearnerWithMetrics;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.utils.EpisodeWrapper;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.omscs.ml.a4burlap.utils.EpisodeHelper.sumupTotalRewards;
import static org.omscs.ml.a4burlap.utils.EpisodeWrapper.writeQEpisodeData;
import static org.omscs.ml.a4burlap.utils.Utils.diffTimesNano;
import static org.omscs.ml.a4burlap.utils.Utils.markStartTimeNano;
import static org.omscs.ml.a4burlap.utils.Utils.nanoToMilli;

public class GridWorldQLearnerExperiment implements RunnerQVis, LearningAgentFactory {

  private MDPGridWorld mdpGridWorld;
  private QSettings qSettings;
  private CSVWriterGeneric csvWriter;

  private SADomain domain;
  private HashableStateFactory hashingFactory;
  private SimulatedEnvironment simEnv;
  private boolean runVisuals;
  //set the episode to visualize, -1 means all
  private int trialToVisualize = -1;

  private int minStepsFound;
  private double currAvgReward;
  private double currTotalReward;
  private int matchCount;

  public GridWorldQLearnerExperiment(
      MDPGridWorld mdpGridWorld, QSettings qSettings, CSVWriterGeneric csvWriter) {
    this.mdpGridWorld = mdpGridWorld;
    this.qSettings = qSettings;
    this.csvWriter = csvWriter;

    this.domain = (SADomain) this.mdpGridWorld.generateDomain();
    this.hashingFactory = this.mdpGridWorld.getHashableStateFactory();

    ConstantStateGenerator constantStateGenerator =
        new ConstantStateGenerator(this.mdpGridWorld.getInitialState());
    this.simEnv = new SimulatedEnvironment(this.domain, constantStateGenerator);

    this.minStepsFound = getHeightByWith();
    this.currAvgReward = getHeightByWith() * -3;
    this.currTotalReward = getHeightByWith() * -3;
  }

  private QLearning makeAgent() {

    QLearning agent =
        new QLearnerWithMetrics(
            domain,
            this.qSettings.getGamma(),
            hashingFactory,
            this.qSettings.getqInit(),
            this.qSettings.getLearningRate(),
            this.qSettings.getMaxEpisodeSize());
    agent.setLearningPolicy(
        new EGreedyDecayPolicy(
            agent, this.qSettings.getEpsilon(), this.qSettings.getEpsilonDecay()));
    return agent;
  }

  @Override
  public void runWithEpisodesAndSave(int trials, int episodes) {

    if (trials < 0) trials = 1;

    for (int i = 0; i < trials; i++) {
      System.out.printf("** QLearning trail-%d \n", i);
      runWithEposodes(i, episodes);
    }
  }

  public void runWithEposodes(int trialNumber, int episodes) {

    String usableFileName = String.format("%s-%02d", this.qSettings.getShortName(), trialNumber);

    csvWriter.writeHeader(
        Arrays.asList(new String[] {"iter", "rewards", "numSteps", "wallclock"}),
        NAME_GRIDWORLD,
        usableFileName);
    long startTime, wallClockNano, totalWallClock = 0L;

    QLearning agent = makeAgent();

    Episode episodeAt = null;
    for (int i = 0; i < episodes; i++) {

      startTime = markStartTimeNano();
      episodeAt = agent.runLearningEpisode(this.simEnv, -1);
      wallClockNano = diffTimesNano(startTime);
      totalWallClock += wallClockNano;
      csvWriter.writeRow(
          Arrays.asList(
                  Integer.toString(i),  Double.toString(sumupTotalRewards(episodeAt)),
                  Integer.toString(agent.getLastNumSteps()),
                  Long.toString(wallClockNano)));

      //      if (i < 10 || i > episodes - 10)
      //        System.out.printf("episode: %d, numSteps %d\n", i, agent.getLastNumSteps());
      this.simEnv.resetEnvironment();

      if (i == episodes - 1) {
        if (visitedFound(episodeAt)) {
          System.out.printf("Visted Goal!! episode: %d\n", i);
        } else {
          System.out.printf("NOT Visited Goal!! by episode: %d\n", i);
        }
      }
    }

    // Take the very last policy after iterations
    Policy policy = new GreedyQPolicy(agent);
    Episode episode = null;

    System.out.printf(
        "Running Q learning on GridWorld size wxh %d x %d\n",
        this.mdpGridWorld.getWidth(), this.mdpGridWorld.getHeight());
    episode = PolicyUtils.rollout(policy, this.simEnv, agent.getLastNumSteps() * 3);

    System.out.printf("Done episode:\n");
    List<Action> actionSeq = episode.actionSequence;
    int tstepTotal = episode.numTimeSteps();
    Double totalReward = sumupTotalRewards(episode);

    System.out.printf(
        "Optimal Policy \n- total steps %d\n- total reward %.5f\n", tstepTotal, totalReward);
    //    System.out.printf(
    //        "Best Q results \n- minmal steps %d\n- best reward %.5f\n",
    //        this.minStepsFound, this.currTotalReward);
    System.out.println(episode.actionSequence);

    writeQEpisodeResult(episode, nanoToMilli(totalWallClock), usableFileName);

    if (runVisuals && (trialToVisualize == trialNumber || trialToVisualize == -1 )) {
      List<State> states =
          StateReachability.getReachableStates(
              this.mdpGridWorld.getInitialState(), domain, hashingFactory);
      ValueFunctionVisualizerGUI gui =
          GridWorldDomain.getGridWorldValueFunctionVisualization(
              states,
              this.mdpGridWorld.getWidth(),
              this.mdpGridWorld.getHeight(),
              (ValueFunction) agent,
              policy);

      gui.setTitle(this.qSettings.getShortName());
      gui.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      gui.initGUI();
    }
  }

  /**
   * A failed attempt at convergence determination for q-learning
   *
   * @param episodeAt
   * @return
   */
  public boolean converged(Episode episodeAt) {

    int lastMinSteps = this.minStepsFound;
    double newAvgReward = 0d, rewardTotal = 0d, lastAvgReward = this.currAvgReward;

    boolean foundGoal = visitedFound(episodeAt);

    if (foundGoal) {
      if (episodeAt.numActions() < this.minStepsFound) this.minStepsFound = episodeAt.numActions();

      for (double rwd : episodeAt.rewardSequence) {
        rewardTotal += rwd;
      }
      newAvgReward = rewardTotal / episodeAt.rewardSequence.size();
      if (newAvgReward > this.currAvgReward) this.currAvgReward = newAvgReward;
      if (rewardTotal > this.currTotalReward) this.currTotalReward = rewardTotal;

      if (lastMinSteps == this.minStepsFound && lastAvgReward == currAvgReward && matchCount >= 1) {
        matchCount++;
        return true;
      } else {
        matchCount = 0;
      }
    }

    return false;
  }

  /**
   * Make a determination if the gridworld goal has been visited yet
   *
   * @param episodeAt
   * @return
   */
  public boolean visitedFound(Episode episodeAt) {

    List<State> states = episodeAt.stateSequence;

    GridWorldState singleState = (GridWorldState) states.get(0).copy();

    Set<GridWorldState> goalStates = new HashSet<>();
    for (Coordinates goal : this.mdpGridWorld.getGoals()) {
      GridAgent gAgent = singleState.touchAgent();
      gAgent.x = goal.x;
      gAgent.y = goal.y + 1;
      GridWorldState gwStateTest = new GridWorldState(gAgent, singleState.locations);
      goalStates.add(gwStateTest);
    }

    boolean foundGoal =
        states.stream()
            .anyMatch(
                state -> {
                  GridWorldState gwsIn = (GridWorldState) state;
                  for (GridWorldState gws : goalStates) {
                    if (gws.agent.x == gwsIn.agent.x && gws.agent.y == gwsIn.agent.y) {
                      return true;
                    }
                  }
                  return false;
                });
    return foundGoal;
  }

  @Override
  public String getAgentName() {
    return this.qSettings.getShortName();
  }

  @Override
  public LearningAgent generateAgent() {
    return this.makeAgent();
  }

  private int getHeightByWith() {
    return this.mdpGridWorld.getHeight() * this.mdpGridWorld.getWidth();
  }

  @Override
  public void tooggleVisual(boolean visualOn, int trialToVisualize) {
    this.trialToVisualize = trialToVisualize;
    this.runVisuals = visualOn;
  }

  private void writeQEpisodeResult(
      Episode episode, Long totalWallClockMilli, String usableFileName) {
    EpisodeWrapper eWrapper = new EpisodeWrapper(episode, totalWallClockMilli);
    String baseResutlPath = csvWriter.getFullBasePath().toString();

    Path episodePath = Path.of(baseResutlPath, NAME_GRIDWORLD, usableFileName);
    writeQEpisodeData(eWrapper, episodePath.toString());
  }
}
