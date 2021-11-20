package org.omscs.ml.a4burlap.vipi;

import org.omscs.ml.a4burlap.utils.ExperimentSettingsTracking;

public class PISettings implements ExperimentSettingsTracking {

   // domain, gamma, hashingFactory, maxPIDelta, maxEvalDelta, maxEvaluationIterations, maxPolicyIterations

    private float gamma;
    private float piDeltaThreshold;
    private float viDeltaThreashold;
    private int viMaxIterations;
    private int piMaxIterations;
    private String shortName;

    public PISettings(float gamma, float piDeltaThreshold, float viDeltaThreashold, int viMaxIterations, int piMaxIterations, String shortName) {
        this.gamma = gamma;
        this.piDeltaThreshold = piDeltaThreshold;
        this.viDeltaThreashold = viDeltaThreashold;
        this.viMaxIterations = viMaxIterations;
        this.piMaxIterations = piMaxIterations;
        this.shortName = shortName;
    }

    public float getGamma() {
        return gamma;
    }

    public float getPiDeltaThreshold() {
        return piDeltaThreshold;
    }

    public float getViDeltaThreashold() {
        return viDeltaThreashold;
    }

    public int getViMaxIterations() {
        return viMaxIterations;
    }

    public int getPiMaxIterations() {
        return piMaxIterations;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String experimentSettingsToLog() {
        String expSettings =  String.format("Name: **%s** gamma:%.3f, piDelta:%.5f, viDelta:%.5f, piMaxIter:%d, viMaxIter:%d",
                this.shortName, this.gamma, this.piDeltaThreshold, this.viDeltaThreashold, this.piMaxIterations, this.viMaxIterations);

        return expSettings;
    }
}
