package org.omscs.ml.a4burlap.mdp.grid;

import org.omscs.ml.a4burlap.mdp.ProblemSize;

public interface GridSelector {

    String[] selectRawGrid(ProblemSize problemSize);
}
