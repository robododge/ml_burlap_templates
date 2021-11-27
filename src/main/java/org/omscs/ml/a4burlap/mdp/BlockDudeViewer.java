package org.omscs.ml.a4burlap.mdp;

import burlap.domain.singleagent.blockdude.BlockDudeLevelConstructor;
import burlap.domain.singleagent.blockdude.BlockDudeVisualizer;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;

public class BlockDudeViewer {

    public static void main(String[] args){

        MDPBlockDude ebd = new MDPBlockDude(ProblemSize.SMALL);
        SADomain domain = (SADomain) ebd.generateDomain();


        State s = BlockDudeLevelConstructor.getLevel1(domain);
//        State s = MDPBlockDude.getLevelCustom(domain);

        Visualizer v = BlockDudeVisualizer.getVisualizer(ebd.getMaxx(), ebd.getMaxx());


        VisualExplorer exp = new VisualExplorer(domain, v, s);

        exp.addKeyAction("w", MDPBlockDude.ACTION_UP, "");
        exp.addKeyAction("d", MDPBlockDude.ACTION_EAST, "");
        exp.addKeyAction("a", MDPBlockDude.ACTION_WEST, "");
        exp.addKeyAction("s", MDPBlockDude.ACTION_PICKUP, "");
        exp.addKeyAction("x", MDPBlockDude.ACTION_PUT_DOWN, "");

        exp.initGUI();

    }

}
