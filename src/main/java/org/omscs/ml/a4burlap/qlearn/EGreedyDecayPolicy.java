package org.omscs.ml.a4burlap.qlearn;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.valuefunction.QProvider;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

public class EGreedyDecayPolicy extends EpsilonGreedy {
    double decay = -1;


    public EGreedyDecayPolicy(QProvider planner, double epsilon, double decay) {
        super(planner, epsilon);
        this.decay = decay;
    }

    @Override
    public Action action(State s) {

        Action a = super.action(s);

        if(this.decay > 0){
            this.epsilon *= decay;
        }

        return a;

    }
}
