package org.omscs.ml.a4burlap.vipi;

import org.omscs.ml.a4burlap.utils.ExperimentSettingsTracking;

public class VISettings implements ExperimentSettingsTracking {
    //    bdDomain, 0.80, hashingFactory, 0.001, 10000
    private float gamma;
    private float viDeltaThreshold;
    private int viMaxIterations;
    private String shortName;

    public VISettings(float gamma, float viDeltaThreshold, int viMaxIterations, String shortName) {
        this.gamma = gamma;
        this.viDeltaThreshold = viDeltaThreshold;
        this.viMaxIterations = viMaxIterations;
        this.shortName = shortName;
    }


    @Override
    public String experimentSettingsToLog() {
        return String.format("Name: **%s** gamma:%.3f, viDeltaThreashold:%.5f, viMaxIter:%d",this.shortName, this.gamma, this.viDeltaThreshold, this.viMaxIterations);
    }

    public float getGamma() {
        return gamma;
    }

    public float getViDeltaThreshold() {
        return viDeltaThreshold;
    }

    public int getViMaxIterations() {
        return viMaxIterations;
    }

    public String getShortName() {
        return shortName;
    }
}
