package org.omscs.ml.a4burlap.mdp;

import burlap.domain.singleagent.blockdude.BlockDudeModel;
import burlap.domain.singleagent.blockdude.state.BlockDudeAgent;
import burlap.domain.singleagent.blockdude.state.BlockDudeCell;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

import static burlap.domain.singleagent.blockdude.BlockDude.ACTION_EAST;
import static burlap.domain.singleagent.blockdude.BlockDude.ACTION_PICKUP;
import static burlap.domain.singleagent.blockdude.BlockDude.ACTION_PUT_DOWN;
import static burlap.domain.singleagent.blockdude.BlockDude.ACTION_UP;
import static burlap.domain.singleagent.blockdude.BlockDude.ACTION_WEST;

public class A4BlockDudeModel extends BlockDudeModel {
    public A4BlockDudeModel(int maxx, int maxy) {
        super(maxx, maxy);
    }

    @Override
    public State sample(State s, Action a) {
        BlockDudeState bs = (BlockDudeState)s.copy();
        String aname = a.actionName();
        if(aname.equals(ACTION_WEST)){
            moveHorizontally(bs, -1);
        }
        else if(aname.equals(ACTION_EAST)){
            moveHorizontally(bs, 1);
        }
        else if(aname.equals(ACTION_UP)){
            moveUp(bs);
        }
        else if(aname.equals(ACTION_PICKUP)){
            pickupBlock(bs);
        }
        else if(aname.equals(ACTION_PUT_DOWN)){
            putdownBlock(bs);
        }
        else {
            throw new RuntimeException("Unknown action " + aname);
        }
        return bs;
    }

    @Override
    public void moveUp(BlockDudeState s) {
        BlockDudeAgent agent = s.agent.copy();
        s.agent = agent;

        int [][] map = s.map.map;

        int ax = agent.x;
        int ay = agent.y;
        int dir = agent.dir;
        boolean holding = agent.holding;

        if(dir == 0){
            dir = -1;
        }

        int nx = ax+dir;
        int ny = ay+1;

        if(nx < 0 || nx >= maxx){
            return;
        }

        int clearing = holding ? ny+1 : ny;

        int heightAtNX = greatestHeightBelow(s, map, maxx, nx, clearing);

        //in order to move up, the height of world in new x position must be at the same current agent position
        if(heightAtNX != ay){
            return ; //not a viable move up condition, so do nothing
        }

        agent.x = nx;
        agent.y = ny;

        moveCarriedBlockToNewAgentPosition(s, agent, ax, ay, nx, ny);


    }

    @Override
    public void putdownBlock(BlockDudeState s) {
        BlockDudeAgent agent = s.agent.copy();
        s.agent = agent;

        int [][] map = s.map.map;

        if(!agent.holding){
            return; //not holding a block
        }

        int ax = agent.x;
        int ay = agent.y;
        int dir = agent.dir;

        if(dir == 0){
            dir = -1;
        }


        int nx = ax + dir;

        if(nx < 0 || nx >= maxx){
            return;
        }

        int heightAtNX = greatestHeightBelow(s, map, maxx, nx, ay+1);
        if(heightAtNX > ay){
            return; //cannot drop block if walled off from throw position
        }

        BlockDudeCell block = getBlockAt(s, ax, ay+1); //carried block is one unit above agent
        s.copyBlocks();
        s.blocks.remove(block);
        block = block.copy();
        s.blocks.add(block);

        block.x = nx;
        block.y = heightAtNX+1;
        agent.holding = false;

    }
}
