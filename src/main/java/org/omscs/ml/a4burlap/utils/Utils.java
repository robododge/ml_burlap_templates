package org.omscs.ml.a4burlap.utils;

import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

public class Utils {

  public static String uniqueDirName() {
    LocalDateTime ldt = LocalDateTime.now();
    return String.format(
        "%02d%02d%02d%02d", ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute(), ldt.getMinute());
  }

  public static void printExerimentStartBlurb(String experimentName) {
    System.out.printf("\n*********\n* Starting: %s experiment\n*********\n", experimentName);
  }

  public static long markStartTimeNano() {
    return System.nanoTime();
  }

  public static long diffTimesNano(long start) {
    return System.nanoTime() - start;
  }

  public static long nanoToMilli(long nanoTime) {
    return (long) nanoTime / 1000000;
  }

  public static void printRunUniqeidBlurb(String experimentName, CSVWriterGeneric csvWriter) {
    System.out.printf("\n*********\n* RUN UNIQUE ID: [%s] \n*********\n", csvWriter.getUniqueD());

    File tracker = new File("tracker.ini");
//    Properties trackerProp = new Properties();

    Ini ini =new Ini();

    try {
      if (tracker.exists()) {
        InputStream in = new FileInputStream(tracker);
        ini.load(in);
//        trackerProp.load(in);
      } else {
        tracker.createNewFile();
//        File copy = File.createTempFile("tracker",".ini");
        ini = new Ini(tracker);
      }
      ini.put("default", experimentName, csvWriter.getUniqueD());
      ini.store(tracker);

//      trackerProp.setProperty(experimentName, csvWriter.getUniqueD());
//      trackerProp.store(new FileOutputStream(tracker), null);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static RunResultsCsvWriterCallback makeNewGammaCSVCallback(float gamma, String runnerName, String usableFileName) {
    GammaBasedResultsVICallback gbc = new GammaBasedResultsVICallback(runnerName,gamma);
    gbc.setUsableFileName(usableFileName);
    return gbc;
  }

  public static RunResultsCsvWriterCallback makeNewEpsilonCSVCallback(float epsilon, float decay, String runnerName, String usableFileName) {
    EpsilonBasedResultsCallback ebc = new EpsilonBasedResultsCallback(runnerName,epsilon, decay);
    ebc.setUsableFileName(usableFileName);
    return ebc;
  }

  public static RunResultsCsvWriterCallback makeNewAlphaCSVCallback(float alpha, String runnerName, String usableFileName) {
    AlphaBasedResultsCallback abc = new AlphaBasedResultsCallback(runnerName,alpha);
    abc.setUsableFileName(usableFileName);
    return abc;
  }
}
