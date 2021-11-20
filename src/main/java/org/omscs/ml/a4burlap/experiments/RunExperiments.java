package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.vipi.PISettings;
import org.omscs.ml.a4burlap.vipi.VISettings;

import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_BLOCKDUDE;
import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_GRIDWORLD;

import java.util.Set;

public class RunExperiments {

    public static void main( String[] args ){

        Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);
        CSVWriterGeneric csvWriter = new CSVWriterGeneric("output", expDirs, "Experiments First Day");
        MDPBlockDude mdpBlockDudeSM = new MDPBlockDude(ProblemSize.SMALL);
        MDPBlockDude mdpBlockDudeLRG = new MDPBlockDude(ProblemSize.LARGE);

        VISettings viSettings01 = new VISettings(0.99f, 0.001f, 1000, "vi_sm_high_gamma" );
        csvWriter.appendToExperimentCatalog(viSettings01);

        BlockDudeVIExperiment viBlockDudeExperiment = new BlockDudeVIExperiment(mdpBlockDudeSM, viSettings01, csvWriter);
        viBlockDudeExperiment.runAndSave(false);
        mdpBlockDudeSM.reset();

        PISettings piSettings01 = new PISettings(0.99f, 0.001f,
                0.001f, 1000,
                100, "pi_sm_high_gamma");
        csvWriter.appendToExperimentCatalog(piSettings01);
        BlockDudePIExperiment piBlockDudeExperiment = new BlockDudePIExperiment(mdpBlockDudeSM,piSettings01, csvWriter);
        piBlockDudeExperiment.runAndSave(false);
        mdpBlockDudeSM.reset();

        PISettings piSettingsL01 = new PISettings(0.99f, 0.001f,
                0.001f, 1000,
                100, "pi_lrg_high_gamma");
        csvWriter.appendToExperimentCatalog(piSettingsL01);
        piBlockDudeExperiment = new BlockDudePIExperiment(mdpBlockDudeLRG,piSettingsL01, csvWriter);
        piBlockDudeExperiment.runAndSave(false);
        mdpBlockDudeLRG.reset();


    }
}
