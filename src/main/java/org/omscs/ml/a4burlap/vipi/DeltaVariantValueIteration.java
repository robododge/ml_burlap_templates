package org.omscs.ml.a4burlap.vipi;

import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.omscs.ml.a4burlap.utils.Utils.diffTimesNano;
import static org.omscs.ml.a4burlap.utils.Utils.markStartTimeNano;
import static org.omscs.ml.a4burlap.utils.Utils.nanoToMilli;


public class DeltaVariantValueIteration extends ValueIteration implements DeltaCapable{

    List<PIVIDeltaMetric> deltaMetrics;
    /**
     * Initializers the valueFunction.
     *
     * @param domain         the domain in which to plan
     * @param gamma          the discount factor
     * @param hashingFactory the state hashing factor to use
     * @param maxDelta       when the maximum change in the value function is smaller than this value, VI will terminate.
     * @param maxIterations  when the number of VI iterations exceeds this value, VI will terminate.
     */
    public DeltaVariantValueIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxIterations);
        deltaMetrics = new ArrayList<PIVIDeltaMetric>();
    }

    @Override
    public void runVI() {

        if(!this.foundReachableStates){
            throw new RuntimeException("Cannot run VI until the reachable states have been found. Use the planFromState or performReachabilityFrom method at least once before calling runVI.");
        }

        Set<HashableState> states = valueFunction.keySet();
        long startTime, wallClockMilli;

        int i;
        for(i = 0; i < this.maxIterations; i++){

            double delta = 0d;
            startTime = markStartTimeNano();

            for(HashableState sh : states){

                double v = this.value(sh);
                double maxQ = this.performBellmanUpdateOn(sh);
                delta = Math.max(Math.abs(maxQ - v), delta);

            }
            wallClockMilli =nanoToMilli(diffTimesNano(startTime));
            deltaMetrics.add(new PIVIDeltaMetric(delta,wallClockMilli));

            if(delta < this.maxDelta){
                break; //approximated well enough; stop iterating
            }

        }

        DPrint.cl(this.debugCode, "Passes: " + i);

        this.hasRunVI = true;
    }

    public List<PIVIDeltaMetric> getDeltaMetrics() {
        return deltaMetrics;
    }
}
