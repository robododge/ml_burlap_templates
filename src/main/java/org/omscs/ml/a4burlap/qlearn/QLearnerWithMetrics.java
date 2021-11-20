package org.omscs.ml.a4burlap.qlearn;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;

public class QLearnerWithMetrics extends QLearning {
    public QLearnerWithMetrics(SADomain domain, double gamma, HashableStateFactory hashingFactory, double qInit, double learningRate, int maxEpisodeSize) {
        super(domain, gamma, hashingFactory, qInit, learningRate, maxEpisodeSize);
    }

    @Override
    public GreedyQPolicy planFromState(State initialState) {
        if(this.model == null){
            throw new RuntimeException("QLearning (and its subclasses) cannot execute planFromState because a model is not specified.");
        }

        SimulatedEnvironment env = new SimulatedEnvironment(this.domain, initialState);

        int eCount = 0;
        do{
            this.runLearningEpisode(env, this.maxEpisodeSize);
            eCount++;
            System.out.printf("maxQChangeLastEp > maxQChangeForPTerm (%f > %f)... EpisodeCount: %d . Laststeps: %d\n", maxQChangeInLastEpisode, maxQChangeForPlanningTermination, eCount, this.getLastNumSteps() );
        }while(eCount < numEpisodesForPlanning );
//        while(eCount < numEpisodesForPlanning && maxQChangeInLastEpisode > maxQChangeForPlanningTermination);


        return new GreedyQPolicy(this);
    }
}
