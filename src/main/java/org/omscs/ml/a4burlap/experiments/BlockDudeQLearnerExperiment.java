package org.omscs.ml.a4burlap.experiments;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;
import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;

import java.nio.file.Path;

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
    EpsilonGreedy eGreedy = new EpsilonGreedy(this.qSettings.getEpsilon());
    QLearning agent = new QLearning(domain, this.qSettings.getGamma(), hashingFactory, this.qSettings.getqInit(), this.qSettings.getLearningRate(), this.qSettings.getMaxEpisodeSize());
    agent.setLearningPolicy(new EpsilonGreedy(agent, 0.1));
    return agent;
  }

  @Override
  public void runWithEpisodesAndSave(int trials, int episodes) {

    LearningAlgorithmExperimenter experimenter =
        new LearningAlgorithmExperimenter(this.simEnv, trials, episodes, this);

    //    experimenter.setUpPlottingConfiguration(
    //        500,
    //        250,
    //        2,
    //        1000,
    //        TrialMode.MOST_RECENT_AND_AVERAGE,
    //        PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
    //        PerformanceMetric.AVERAGE_EPISODE_REWARD);
    experimenter.startExperiment();

    String baseResutlPath = csvWriter.getFullBasePath().toString();

    Path experimentPath = Path.of(baseResutlPath, NAME_BLOCKDUDE, this.qSettings.getShortName());
    experimenter.writeEpisodeDataToCSV(experimentPath.toString());
  }

  @Override
  public String getAgentName() {
    return this.qSettings.getShortName();
  }

  @Override
  public LearningAgent generateAgent() {
    return this.makeAgent();
  }
}
