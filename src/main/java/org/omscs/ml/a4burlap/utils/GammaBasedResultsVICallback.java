package org.omscs.ml.a4burlap.utils;

import java.util.Arrays;

public class GammaBasedResultsVICallback implements RunResultsCsvWriterCallback {

  protected String runnerName;
  protected float gamma;
  private String usableCsvFileName;



  private int trialIndex = -1;

  public GammaBasedResultsVICallback(String runnerName, float gamma) {
    this.runnerName = runnerName;
    this.gamma = gamma;
  }

  @Override
  public void writeHeaderOfCsv(CSVWriterGeneric csvWriter) {
    csvWriter.writeHeader(
        Arrays.asList("run", "wallclock", "totalRewards", "viEvals"), runnerName, trialSafeFilename());
  }

  @Override
  public void writeRowOfCsv(
      CSVWriterGeneric csvWriter,  EpisodeWrapper eWrapper) {
    csvWriter.writeRow(
        Arrays.asList(
            Float.toString(gamma),
            Long.toString(eWrapper.getTotalWallClock()),
            Double.toString(eWrapper.getTotalReward()),
            Long.toString(eWrapper.getTotalValueIterations())),
        trialSafeFilename());
  }

  @Override
  public void setUsableFileName(String usableFileName) {
    this.usableCsvFileName = usableFileName;
  }

  @Override
  public String getFullCsvFileName(CSVWriterGeneric csvWriter) {

    return csvWriter.makeCsvPathString(runnerName, trialSafeFilename());
  }

  protected String trialSafeFilename() {
    String currUsableName = this.usableCsvFileName;
    if (this.trialIndex > -1){
      currUsableName = String.format("%s-%02d", this.usableCsvFileName, this.trialIndex);
    }
    return currUsableName;
  }

  public void setTrialIndex(int trialIndex) {
    this.trialIndex = trialIndex;

  }
}
