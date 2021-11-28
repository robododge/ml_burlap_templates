package org.omscs.ml.a4burlap.mdp;

import burlap.domain.singleagent.blockdude.BlockDudeLevelConstructor;
import burlap.domain.singleagent.blockdude.BlockDudeModel;
import burlap.domain.singleagent.blockdude.BlockDudeTF;
import burlap.domain.singleagent.blockdude.state.BlockDudeAgent;
import burlap.domain.singleagent.blockdude.state.BlockDudeCell;
import burlap.domain.singleagent.blockdude.state.BlockDudeMap;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;

import static burlap.domain.singleagent.blockdude.BlockDudeLevelConstructor.addFloor;
import static org.omscs.ml.a4burlap.mdp.ProblemSize.LARGE;
import static org.omscs.ml.a4burlap.mdp.ProblemSize.SMALL;

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.List;

public class MDPBlockDude implements MDPDef{

    public static final String VAR_X = "x";
    public static final String VAR_Y = "y";

    public static final String VAR_DIR = "dir";
    public static final String VAR_HOLD = "holding";

    public static final String VAR_MAP = "map";
    public static final String CLASS_AGENT = "agent";
    public static final String CLASS_BLOCK = "block";
    public static final String CLASS_MAP = "map";
    public static final String CLASS_EXIT = "exit";

    public static final String ACTION_UP = "up";
    public static final String ACTION_EAST = "east";
    public static final String ACTION_WEST = "west";
    public static final String ACTION_PICKUP = "pickup";
    public static final String ACTION_PUT_DOWN = "putdown";

    public static final String PF_HOLDING_BLOCK = "holdingBlock";
    public static final String PF_AT_EXIT = "atExit";

    private HashableStateFactory hsFactory;
    protected RewardFunction rf;
    protected TerminalFunction tf;

    private ProblemSize problemSize;
    private SADomain bdDomain;

    private int maxx = 25;
    private int maxy = 25;

    public MDPBlockDude(ProblemSize size) {
       this.hsFactory = new SimpleHashableStateFactory();
       this.problemSize = size;


    }


    @Override
    public State getInitialState() {
        if (this.bdDomain == null){
            generateDomain();
        }

        BlockDudeState initialBdState = null;
        switch (this.problemSize) {
            case LARGE:
//                initialBdState = (BlockDudeState) BlockDudeLevelConstructor.getLevel2(this.bdDomain);
                initialBdState = getLevelCustom(this.bdDomain);
                break;
            default:
                initialBdState = (BlockDudeState) BlockDudeLevelConstructor.getLevel1(this.bdDomain);
        }
        return initialBdState;
    }

    @Override
    public HashableStateFactory getHashableStateFactory() {
        return hsFactory;
    }

    @Override
    public Domain generateDomain() {
        if (this.bdDomain == null) {
            this.bdDomain = makeNewBdDomain();
        }
        return this.bdDomain;
    }

    @Override
    public void reset() {
        this.hsFactory = new SimpleHashableStateFactory();
    }

    private OOSADomain makeNewBdDomain() {
        OOSADomain domain = new OOSADomain();

        domain
                .addStateClass(CLASS_AGENT, BlockDudeAgent.class)
                .addStateClass(CLASS_MAP, BlockDudeMap.class)
                .addStateClass(CLASS_EXIT, BlockDudeCell.class)
                .addStateClass(CLASS_BLOCK, BlockDudeCell.class);

        domain
                .addActionType(new UniversalActionType(ACTION_EAST))
                .addActionType(new UniversalActionType(ACTION_WEST))
                .addActionType(new UniversalActionType(ACTION_UP))
                .addActionType(new UniversalActionType(ACTION_PICKUP))
                .addActionType(new UniversalActionType(ACTION_PUT_DOWN));

        OODomain.Helper.addPfsToDomain(domain, this.generatePfs());

        RewardFunction rf = this.rf;
        TerminalFunction tf = this.tf;

        if (tf == null) {
            tf = new BlockDudeTF();
        }
        if (rf == null) {
            rf = new UniformCostRF();
        }

        BlockDudeModel smodel = new A4BlockDudeModel(maxx, maxy);

        FactoredModel model = new FactoredModel(smodel, rf, tf);
        domain.setModel(model);

        return domain;

    }

    public int getMaxx() {
        return maxx;
    }

    public int getMaxy() {
        return maxy;
    }

    private List<PropositionalFunction> generatePfs() {
        return Arrays.asList(new MDPBlockDude.HoldingBlockPF(), new MDPBlockDude.AtExitPF());
    }

    public class HoldingBlockPF extends PropositionalFunction {

        public HoldingBlockPF() {
            super(PF_HOLDING_BLOCK, new String[] {CLASS_AGENT, CLASS_BLOCK});
        }

        @Override
        public boolean isTrue(OOState st, String... params) {

            BlockDudeAgent a = (BlockDudeAgent) st.object(params[0]);

            if (!a.holding) {
                return false;
            }

            BlockDudeCell b = (BlockDudeCell) st.object(params[1]);

            if (a.x == b.x && a.y == b.y - 1) {
                return true;
            }

            return false;
        }
    }

    public class AtExitPF extends PropositionalFunction {

        public AtExitPF() {
            super(PF_AT_EXIT, new String[] {CLASS_AGENT, CLASS_EXIT});
        }

        @Override
        public boolean isTrue(OOState st, String... params) {

            BlockDudeAgent a = (BlockDudeAgent) st.object(params[0]);
            BlockDudeCell e = (BlockDudeCell) st.object(params[1]);

            if (a.x == e.x && a.y == e.y) {
                return true;
            }

            return false;
        }
    }

    public static BlockDudeState getLevelCustom(Domain domain){

        int [][] map = new int[25][25];
        addFloor(map);

        map[3][1] = 1;
        map[3][2] = 1;

        map[7][1] = 1;

        map[11][1] = 1;
        map[11][2] = 1;
        map[11][3] = 1;

//        map[15][1] = 1;
//        map[15][2] = 1;

        BlockDudeState s = new BlockDudeState(
                new BlockDudeAgent(22, 1, 1, false),
                new BlockDudeMap(map),
                BlockDudeCell.exit(0, 1),
                BlockDudeCell.block("b0", 9, 1),
                BlockDudeCell.block("b1", 13, 1),
                BlockDudeCell.block("b2", 15, 1),
                BlockDudeCell.block("b3", 17, 1)
        );

        return s;
    }

}
