package org.omscs.ml.a4burlap.mdp.grid;

import org.omscs.ml.a4burlap.mdp.ProblemSize;

public class A4MainGridSelector implements GridSelector{
    @Override
    public String[] selectRawGrid(ProblemSize problemSize) {
        String[] rawGridMap =  GridMaps.GRID_MAP_SMALL;
        if (problemSize == ProblemSize.LARGE) {
            rawGridMap = GridMaps.GRID_MAP_LARGE;
        }
        return rawGridMap;
    }
}
