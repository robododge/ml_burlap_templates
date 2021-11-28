package org.omscs.ml.a4burlap.utils;

import java.util.Arrays;

public class EpsilonBasedResultsCallback extends GammaBasedResultsVICallback{

    private float decay;
    private float epsilon;

    public EpsilonBasedResultsCallback(String runnerName, float epsilon) {
        super(runnerName, epsilon);
        this.epsilon = epsilon;
    }

    public EpsilonBasedResultsCallback(String runnerName, float epsilon, float decay) {
        this(runnerName, epsilon);
        this.decay = decay;
    }

    @Override
    public void writeHeaderOfCsv(CSVWriterGeneric csvWriter) {
        csvWriter.writeHeader(
                Arrays.asList("run", "run2", "wallclock", "totalRewards", "convergedAt"), runnerName, trialSafeFilename());
    }

    @Override
    public void writeRowOfCsv(
            CSVWriterGeneric csvWriter,  EpisodeWrapper eWrapper) {
        csvWriter.writeRow(
                Arrays.asList(
                        Float.toString(epsilon),
                        Float.toString(decay),
                        Long.toString(eWrapper.getTotalWallClock()),
                        Double.toString(eWrapper.getTotalReward()),
                        Integer.toString(eWrapper.getqConvergedAt())),
                trialSafeFilename());
    }
}
