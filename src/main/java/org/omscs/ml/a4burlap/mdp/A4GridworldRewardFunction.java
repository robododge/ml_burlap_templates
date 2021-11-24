package org.omscs.ml.a4burlap.mdp;

import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

public class A4GridworldRewardFunction extends GridWorldRewardFunction {
    public A4GridworldRewardFunction(int width, int height, double initializingReward) {
        super(width, height, initializingReward);
    }


    @Override
    public double reward(State s, Action a, State sprime) {

        int x = ((GridWorldState)sprime).agent.x;
        int y = ((GridWorldState)sprime).agent.y;

        if(x >= this.width || x < 0 || y >= this.height || y < 0){
            throw new RuntimeException("GridWorld reward matrix is only defined for a " + this.width + "x" +
                    this.height +" world, but the agent transitioned to position (" + x + "," + y + "), which is outside the bounds.");
        }

        double r = this.rewardMatrix[x][y];
        return r;
    }

}
