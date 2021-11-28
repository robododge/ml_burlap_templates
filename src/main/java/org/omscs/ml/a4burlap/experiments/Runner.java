package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.utils.RunResultsCsvWriterCallback;

public interface Runner {
    static final String NAME_GRIDWORLD = "gridworld";
    static final String NAME_BLOCKDUDE = "blockdude";

    void setRunResultsCSVCallback(RunResultsCsvWriterCallback runResultsCallback);
}
