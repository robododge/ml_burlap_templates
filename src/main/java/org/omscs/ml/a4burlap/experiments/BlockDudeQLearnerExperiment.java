package org.omscs.ml.a4burlap.experiments;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.core.action.Action;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;
import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.qlearn.EGreedyDecayPolicy;
import org.omscs.ml.a4burlap.qlearn.QLearnerWithMetrics;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.utils.EpisodeWrapper;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.omscs.ml.a4burlap.utils.EpisodeHelper.sumupTotalRewards;
import static org.omscs.ml.a4burlap.utils.EpisodeWrapper.writeQEpisodeData;
import static org.omscs.ml.a4burlap.utils.Utils.diffTimesNano;
import static org.omscs.ml.a4burlap.utils.Utils.markStartTimeNano;
import static org.omscs.ml.a4burlap.utils.Utils.nanoToMilli;

public class BlockDudeQLearnerExperiment implements RunnerQ, LearningAgentFactory {

  private MDPBlockDude mdpBlockDude;
  private QSettings qSettings;
  private CSVWriterGeneric csvWriter;

  private SADomain domain;
  private HashableStateFactory hashingFactory;
  private SimulatedEnvironment simEnv;

  public BlockDudeQLearnerExperiment(
      MDPBlockDude mdpBlockDude, QSettings qSettings, CSVWriterGeneric csvWriter) {
    this.mdpBlockDude = mdpBlockDude;
    this.qSettings = qSettings;
    this.csvWriter = csvWriter;

    this.domain = (SADomain) this.mdpBlockDude.generateDomain();
    this.hashingFactory = this.mdpBlockDude.getHashableStateFactory();

    ConstantStateGenerator constantStateGenerator =
        new ConstantStateGenerator(this.mdpBlockDude.getInitialState());
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
        NAME_BLOCKDUDE,
        usableFileName);

    QLearning agent = makeAgent();

    long startTime, wallClockNano, totalWallClock = 0;
    Episode episodeAt = null;

    for (int i = 0; i < episodes; i++) {
      startTime = markStartTimeNano();
      episodeAt = agent.runLearningEpisode(this.simEnv, qSettings.getMaxEpisodeSize());
      wallClockNano = diffTimesNano(startTime);
      totalWallClock += wallClockNano;

            if(i < 10 || i > episodes -10 )
              System.out.printf("episode: %d, numSteps %d\n", i, agent.getLastNumSteps());

      csvWriter.writeRow(
          Arrays.asList(
                  Integer.toString(i), Double.toString(sumupTotalRewards(episodeAt)),
                  Integer.toString(agent.getLastNumSteps()),
                  Long.toString(wallClockNano)));
      this.simEnv.resetEnvironment();
    }

    Policy policy = new GreedyQPolicy(agent);
    Episode episode = null;
    //    agent.setMaxQChangeForPlanningTerminaiton(-1);
    //    agent.initializeForPlanning(30);
    //    policy = agent.planFromState(this.mdpBlockDude.getInitialState());

    episode = PolicyUtils.rollout(policy, this.simEnv, agent.getLastNumSteps() * 3);

    System.out.printf("Done episode:\n");
    List<Action> actionSeq = episode.actionSequence;
    int tstepTotal = episode.numTimeSteps();
    Double totalReward = sumupTotalRewards(episode);


    System.out.printf(
        "Optimal Policy \n- total steps %d\n- total reward %.5f\n", tstepTotal, totalReward);
    if (tstepTotal <= 300) System.out.println(actionSeq);

    writeQEpisodeResult(episode, nanoToMilli(totalWallClock), usableFileName);
  }

  @Override
  public String getAgentName() {
    return this.qSettings.getShortName();
  }

  @Override
  public LearningAgent generateAgent() {
    return this.makeAgent();
  }

  private void writeQEpisodeResult(
      Episode episode, Long totalWallClockMilli, String usableFileName) {
    EpisodeWrapper eWrapper = new EpisodeWrapper(episode, totalWallClockMilli);
    String baseResutlPath = csvWriter.getFullBasePath().toString();

    Path episodePath = Path.of(baseResutlPath, NAME_BLOCKDUDE, usableFileName);
    writeQEpisodeData(eWrapper, episodePath.toString());
  }
}
