package org.omscs.ml.a4burlap.mdp.grid;

public class GridMaps {


    /*
     * The surface can be described as follows:
     *
     * X — The starting point of the agent.
     * 0 — Represents a safe cell where the agent can move.
     * 1 — Represents a wall. The agent can't move to this cell.
     * G — Represents the goal that the agent wants to achieve.
     * S — Represents a small hazard. The agent will be penalized.
     * M — Represents a medium hazard. The agent will be penalized.
     * L — Represents a large hazard. The agent will be penalized.
     */
    public static String[] GRID_MAP_SMALL =
    new String[] {
            "10011110",
            "110X0S10",
            "010M110S",
            "0M0L00M1",
            "01001010",
            "00L010S0",
            "0S001000",
            "000G00SG",
    };

    public static String[] GRID_MAP_LARGE =  new String[] {
                "111111111111111111111",
                "X00010001000100000101",
                "101110101L1010S110101",
                "100010101000100010101",
                "11101010101111S110101",
                "100010100000100000001",
                "1011101S1010101110101",
                "100010101010001000101",
                "101010101011111010111",
                "101000001000100010001",
                "1110101M111010M110101",
                "100010100010100000101",
                "101110101010101111S01",
                "100010001010001010001",
                "111011101010111010111",
                "101010001010001000101",
                "10101011101L001011101",
                "1000001S0000101010001",
                "101011110110101010101",
                "10100000001000100010G",
                "111111111111111111111",
    };
}
