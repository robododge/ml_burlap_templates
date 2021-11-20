package org.omscs.ml.a4burlap.qlearn;

public class QSettings {

  // omain, 0.99, hashingFactory, 0.3, 0.1, 1000);
  //        agent.setLearningPolicy( new EpsilonGreedy(agent, 0.1 ));
    double gamma;
    double qInit;
    double learningRate;
    int maxEpisodeSize;

    double epsilon;
    double epsilonDecay;

    String shortName;

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
}
