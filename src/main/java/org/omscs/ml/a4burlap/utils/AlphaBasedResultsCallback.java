package org.omscs.ml.a4burlap.utils;

import java.util.Arrays;

public class AlphaBasedResultsCallback extends GammaBasedResultsVICallback{
    private float alpha;

    public AlphaBasedResultsCallback(String runnerName, float alpha) {
        super(runnerName, alpha);
        this.alpha = alpha;
    }



    @Override
    public void writeHeaderOfCsv(CSVWriterGeneric csvWriter) {
        csvWriter.writeHeader(
                Arrays.asList("run", "wallclock", "totalRewards", "convergedAt"), runnerName, trialSafeFilename());
    }

    @Override
    public void writeRowOfCsv(
            CSVWriterGeneric csvWriter,  EpisodeWrapper eWrapper) {
        csvWriter.writeRow(
                Arrays.asList(
                        Float.toString(alpha),
                        Long.toString(eWrapper.getTotalWallClock()),
                        Double.toString(eWrapper.getTotalReward()),
                        Integer.toString(eWrapper.getqConvergedAt())),
                trialSafeFilename());
    }
}
