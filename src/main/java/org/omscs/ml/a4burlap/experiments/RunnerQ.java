package org.omscs.ml.a4burlap.experiments;

public interface RunnerQ extends Runner {

    void runWithEpisodesAndSave(int trials, int episodes);
}
