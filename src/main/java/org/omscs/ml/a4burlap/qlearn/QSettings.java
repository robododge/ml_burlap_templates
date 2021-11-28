package org.omscs.ml.a4burlap.qlearn;

import org.omscs.ml.a4burlap.utils.ExperimentSettingsTracking;

public class QSettings implements ExperimentSettingsTracking {

  // omain, 0.99, hashingFactory, 0.3, 0.1, 1000);
  //        agent.setLearningPolicy( new EpsilonGreedy(agent, 0.1 ));
    double gamma;
    double qInit;
    double learningRate;
    int maxEpisodeSize;

    double epsilon;
    double epsilonDecay;

    String shortName;


    double targeConvergeReward = -1;

    public QSettings(String shortName, double gamma, double qInit, double learningRate, int maxEpisodeSize, double epsilon, double epsilonDecay) {
        this.shortName = shortName;
        this.gamma = gamma;
        this.qInit = qInit;
        this.learningRate = learningRate;
        this.maxEpisodeSize = maxEpisodeSize;
        this.epsilon = epsilon;
        this.epsilonDecay = epsilonDecay;
    }

    public double getGamma() {
        return gamma;
    }

    public double getqInit() {
        return qInit;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public int getMaxEpisodeSize() {
        return maxEpisodeSize;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getEpsilonDecay() {
        return epsilonDecay;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String experimentSettingsToLog() {
        return String.format("Name: **%s** gamma:%.3f, qInit:%.5f, learning_rate:%.5f, maxEpisodeSize:%d, epsilon:%.5f, eDecay:%.5f, targetReward:%.5f",
                this.shortName, this.gamma, this.qInit, this.learningRate, this.maxEpisodeSize, this.epsilon, this.epsilonDecay, this.targeConvergeReward);
    }

    public void setTargeConvergeReward(double targeConvergeReward) {
        this.targeConvergeReward = targeConvergeReward;
    }

    public double getTargeConvergeReward() {
        return targeConvergeReward;
    }
}
