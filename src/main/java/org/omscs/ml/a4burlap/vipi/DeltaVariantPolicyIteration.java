package org.omscs.ml.a4burlap.vipi;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;

import java.util.ArrayList;
import java.util.List;

public class DeltaVariantPolicyIteration extends PolicyIteration implements DeltaCapable {

    List<PIVIDeltaMetric> deltaMetrics;


    public DeltaVariantPolicyIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxPIDelta, double maxEvalDelta, int maxEvaluationIterations, int maxPolicyIterations) {
        super(domain, gamma, hashingFactory, maxPIDelta, maxEvalDelta, maxEvaluationIterations, maxPolicyIterations);
        deltaMetrics = new ArrayList<PIVIDeltaMetric>();
    }

    @Override
    public GreedyQPolicy planFromState(State initialState) {

        int iterations = 0;
        if(this.performReachabilityFrom(initialState) || !this.hasRunPlanning){

            double delta;
            long startTime, wallClockMilli;
            int lastTotalValueIterations = 0, currValueIterations = 0;

            do{
                startTime = markStartTimeNano();
                delta = this.evaluatePolicy();

                //Collect a bunch of metrics (not default to Burlap)
                wallClockMilli =nanoToMilli(diffTimesNano(startTime));
                currValueIterations = this.totalValueIterations - lastTotalValueIterations;
                lastTotalValueIterations = this.totalValueIterations;
                deltaMetrics.add(new PIVIDeltaMetric(delta,wallClockMilli, currValueIterations));
                System.out.printf("Policy Iteration %d -- delta: %f, wallClock: %d \n", iterations,delta,wallClockMilli);
                iterations++;

                this.evaluativePolicy = new GreedyQPolicy(this.getCopyOfValueFunction());

            }while(delta > this.maxPIDelta && iterations < maxPolicyIterations);



            this.hasRunPlanning = true;

        }

        DPrint.cl(this.debugCode, "Total policy iterations: " + iterations);
        this.totalPolicyIterations += iterations;

        return (GreedyQPolicy)this.evaluativePolicy;

    }


    public List<PIVIDeltaMetric> getDeltaMetrics() {
        return deltaMetrics;
    }

    public static long markStartTimeNano() {
        return System.nanoTime();
    }

    public static long diffTimesNano(long start) {
        return System.nanoTime() - start;
    }

    public static long nanoToMilli (long nanoTime) {
        return (long) nanoTime / 1000000;
    }

}
