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
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;
import org.omscs.ml.a4burlap.mdp.MDPGridWorld;
import org.omscs.ml.a4burlap.qlearn.EGreedyDecayPolicy;
import org.omscs.ml.a4burlap.qlearn.QLearnerWithMetrics;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;

import java.util.List;

public class GridWorldQLearnerExperiment implements RunnerQVis, LearningAgentFactory {

  private MDPGridWorld mdpGridWorld;
  private QSettings qSettings;
  private CSVWriterGeneric csvWriter;

  private SADomain domain;
  private HashableStateFactory hashingFactory;
  private SimulatedEnvironment simEnv;
  private boolean runVisuals;

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

    //        LearningAlgorithmExperimenter experimenter =
    //                new LearningAlgorithmExperimenter(this.simEnv, trials, episodes, this);
    //
    //        experimenter.toggleVisualPlots(false);
    //        experimenter.toggleTrialLengthInterpretation(false);
    //        experimenter.startExperiment();
    //
    //        String baseResutlPath = csvWriter.getFullBasePath().toString();
    //
    //        Path experimentPath = Path.of(baseResutlPath, NAME_GRIDWORLD,
    // this.qSettings.getShortName());
    //        experimenter.writeEpisodeDataToCSV(experimentPath.toString());

    QLearning agent = makeAgent();

    for (int i = 0; i < episodes; i++) {
      agent.runLearningEpisode(this.simEnv, -1);
      if (i < 10 || i > episodes - 10)
        System.out.printf("episode: %d, numSteps %d\n", i, agent.getLastNumSteps());
      this.simEnv.resetEnvironment();
    }

    //        int previousStepCount = 0, currStepCount=0;
    //        int streakCount = 0, i =0;
    //        boolean reachedConvergence = false;
    //        do {
    //            agent.runLearningEpisode(this.simEnv, -1);
    //            currStepCount = agent.getLastNumSteps();
    //            this.simEnv.resetEnvironment();
    //            if ( Math.abs(currStepCount - previousStepCount) < 2) {
    //                streakCount++;
    //                if (streakCount >5)
    //                    reachedConvergence = true;
    //            } else {
    //                streakCount = 0;
    //            }
    //            previousStepCount = currStepCount;
    //            if(i % 100 == 0 )
    //                System.out.printf("episode: %d, numSteps %d\n", i, agent.getLastNumSteps());
    //            i++;
    //
    //        }while ( !reachedConvergence && i < 200000 );
    //        System.out.printf("Converged a episode: %d, numSteps %d\n", i,
    // agent.getLastNumSteps());

    Policy policy = new GreedyQPolicy(agent);
    Episode episode = null;
    //        agent.setMaxQChangeForPlanningTerminaiton(0.00001);
    //        agent.initializeForPlanning(100);
    //        policy = agent.planFromState(this.mdpGridWorld.getInitialState());

    System.out.printf(
        "Running Q learning on GridWorld size wxh %d x %d\n",
        this.mdpGridWorld.getWidth(), this.mdpGridWorld.getHeight());
    episode = PolicyUtils.rollout(policy, this.simEnv, agent.getLastNumSteps() * 3);

    System.out.printf("Done episode:\n");
    List<Action> actionSeq = episode.actionSequence;
    int tstepTotal = episode.numTimeSteps();
    Double totalReward = 0d;

    for (Double rwrd : episode.rewardSequence) {
      totalReward += rwrd;
    }

    System.out.printf(
        "Optimal Policy \n- total steps %d\n- total reward %.5f\n", tstepTotal, totalReward);
    System.out.println(episode.actionSequence);

    if (runVisuals) {
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

  @Override
  public String getAgentName() {
    return this.qSettings.getShortName();
  }

  @Override
  public LearningAgent generateAgent() {
    return this.makeAgent();
  }

  private int getMaxRolloutIterations() {
    return this.mdpGridWorld.getHeight() * this.mdpGridWorld.getWidth() * 2;
  }

  @Override
  public void tooggleVisual(boolean visualOn) {
    this.runVisuals = visualOn;
  }
}
