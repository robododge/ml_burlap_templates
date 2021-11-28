package org.omscs.ml.a4burlap.utils;

public interface RunResultsCsvWriterCallback {
    void writeHeaderOfCsv(CSVWriterGeneric csvWriter);
    void writeRowOfCsv(CSVWriterGeneric csvWriter, EpisodeWrapper eWrapper);
    void setUsableFileName(String usableFileName);
    String getFullCsvFileName(CSVWriterGeneric csvWriter);
    void setTrialIndex(int trialIndex);
}
