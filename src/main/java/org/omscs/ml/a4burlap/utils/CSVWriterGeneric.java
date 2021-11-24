package org.omscs.ml.a4burlap.utils;

import de.siegmar.fastcsv.writer.CsvWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Set;

public class CSVWriterGeneric {

    private String rootPath;
    private Set<String> intermediatePaths;
    private Path fullBasePath;
    private String currentInterPath = "";
    private String currentFilePath = "";
    private String uniqueD = "";


    private CsvWriter csvWriter;
    //    private Path csvDirPath;
    private String metadata;
    private String catalogPrefix;

    public CSVWriterGeneric(String rootPath, Set<String> intermediatePaths, String metadata) {
      this(rootPath,intermediatePaths,metadata,null);
    }

    public CSVWriterGeneric(String rootPath, Set<String> intermediatePaths, String metadata, String catalogPrefix) {
        this.rootPath = rootPath;
        this.intermediatePaths = intermediatePaths;
        this.metadata = metadata;
        this.catalogPrefix = catalogPrefix;
        ensurePaths();
    }

    public CSVWriterGeneric(String rootPath, Set<String> intermediatePaths) {
        this(rootPath, intermediatePaths, null);
    }

    private void ensurePaths() {

        this.uniqueD = Utils.uniqueDirName();
        String prefix = (this.catalogPrefix != null)?this.catalogPrefix+"-" :"";
        String mergedRootName = String.format("%s%s",prefix,this.uniqueD );
        this.fullBasePath = Path.of(this.rootPath, mergedRootName);
        File rootDir = fullBasePath.toFile();
        boolean created = rootDir.mkdirs();
        if (!created)
            System.out.printf("The unique output directory for results already exists %s, exiting\n", rootDir);


        File metaFile = getMetaFile();
        if (this.metadata != null) createCatalogFile(metaFile, this.uniqueD);

        for (String intPath : this.intermediatePaths) {
            Path csvDirPath = Path.of(rootDir.toString(), intPath);
            File intermediateDir = csvDirPath.toFile();
            created = intermediateDir.mkdirs();
            if (!created)
                System.out.printf("The unique output directory for results already exists %s, exiting\n", intermediateDir);
        }


    }

    public Path getFullBasePath() {
        return fullBasePath;
    }

    public String getUniqueD() {
        return uniqueD;
    }


    private Path makeCsvPath(String intermediatePath, String fileString) {
        if (!fileString.endsWith(".csv"))
            fileString = String.format("%s.csv", fileString);
        return Path.of(this.fullBasePath.toString(), intermediatePath, fileString);
    }

    public void writeHeader(Iterable<String> values, String itermediatePath, String fileName) {
        Path csvPath = makeCsvPath(itermediatePath, fileName);
        this.currentInterPath = itermediatePath;
        this.currentFilePath = fileName;
        writeRow(values, csvPath);
    }

    public void writeRow(Iterable<String> values, String itermediatePath, String fileName) {
        Path csvPath = makeCsvPath(itermediatePath,fileName );
        writeRow(values, csvPath);
    }

    public void writeRow(Iterable<String> values) {
        Path csvPath = makeCsvPath(this.currentInterPath, this.currentFilePath);
        writeRow(values, csvPath);
    }


    private void writeRow(final Iterable<String> values, Path csvPath) {
        try (CsvWriter csv = CsvWriter.builder().build(csvPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            this.csvWriter = csv;
            this.csvWriter.writeRow(values);
        } catch (IOException e) {
            System.out.println("Cannot write to csv file at path:" + csvPath);
            e.printStackTrace();
        }

    }

    private void createCatalogFile(File outfile, String uniqueD) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String header = String.format("       RunId: %s   Run time: %s \n ===================================\n", uniqueD, sdf1.format(ts));
        try {
            FileWriter myWriter = new FileWriter(outfile);

            myWriter.write(header);
            myWriter.write(this.metadata+'\n');
            myWriter.close();
            System.out.printf("Successfully wrote catalog file to  %s\n", outfile);
        } catch (IOException e) {
            System.out.printf("An error occurred writing catalog file %s\n", outfile);
            e.printStackTrace();
        }
    }

    private File getMetaFile() {
        String prefix = (this.catalogPrefix != null)?this.catalogPrefix+"-" :"";
        String mergedName = String.format("%s%s_meta.txt",prefix,this.uniqueD );
        File metaFile = new File(this.rootPath, mergedName);
        return metaFile;
    }



    public void appendToExperimentCatalog(ExperimentSettingsTracking expSettings) {
        File catalogFile = this.getMetaFile();
        try {
            FileWriter fw = new FileWriter(catalogFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(expSettings.experimentSettingsToLog());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
