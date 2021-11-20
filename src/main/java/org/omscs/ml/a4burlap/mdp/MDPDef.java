package org.omscs.ml.a4burlap.mdp;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;

public interface MDPDef extends DomainGenerator {

    State getInitialState();
    HashableStateFactory getHashableStateFactory();

    void reset();
}
