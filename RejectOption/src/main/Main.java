package main;

import classifier.*;
import nsga.MOP;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Settings settings = null;
        try {
            settings = loadSettings();
        } catch (IOException ex) {
            System.out.println("Failed to read settings.");
            System.exit(1);
        }
        String startTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        ResultsMaster resultsMaster = new ResultsMaster(settings.experimentTag + "_" + startTime, settings);

        System.out.println(String.format("Started experiment %s on dataset %s at time %s",
                settings.experimentTag, settings.datasetName, startTime));

        // Not a user-defined setting... the data has already been preprocessed into 10-fold cv
        int kFoldCV = 10;
        FileData fd = new FileData();
        for (int run = 0; run < settings.nRuns; run++) {
            for (int slice = 0; slice < kFoldCV; slice++) {
                FileData.Output trainingData = fd.load(getFilenameFromDataset(settings.datasetName, run, slice, "tra"));
                FileData.Output testingData = fd.load(getFilenameFromDataset(settings.datasetName, run, slice, "tst"));
                settings.setTrainingData(trainingData.patterns).setTestingData(testingData.patterns)
                        .setNInputAttributes(trainingData.nAttributes).setNOutputClasses(trainingData.nOutputClasses)
                        .setRulesetMinRules(trainingData.nOutputClasses);

                MOP<RuleSet> mop = null;
                try {
                    Class<?> c = Class.forName("main.mop." + settings.mopName);
                    Constructor<?> ct = c.getConstructor(Settings.class);
                    mop = (MOP<RuleSet>) ct.newInstance(settings);
                } catch (Exception ex) {
                    System.out.println("Error loading MOP");
                    System.exit(-1);
                }

                int randomSeed = (run * 10) + slice;
                Classifier classifier = new Classifier(settings, mop, resultsMaster, randomSeed);
                classifier.train();
                // After training, this population is sorted by pareto rank and crowding score.
                List<RuleSet> finalPopulation = classifier.getPopulation();
                resultsMaster.recordRun(finalPopulation, String.format("%d_%d", run, slice), mop);
            }
            System.out.println("Finished run " + (run + 1) + "...");
        }


        String endTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(endTime);
//        System.out.println("Processing results...");
//        ResultsCollator.process("results\\" + startTime);
        System.out.println("Done.");
    }

    private static Settings loadSettings() throws IOException {
        Properties prop = new Properties();
        String fileName = "settings.config";
        InputStream inputStream = new FileInputStream(fileName);
        prop.load(inputStream);
        Settings settings = new Settings();

        settings.setDatasetName(prop.getProperty("dataset"));
        settings.setNRuns(Integer.parseInt(prop.getProperty("nRuns")));
        settings.setNGenerations(Integer.parseInt(prop.getProperty("nGenerations")));
        settings.setMopName(prop.getProperty("problem"));
        settings.setExperimentTag(prop.getProperty("experimentTag"));

        settings.setNAntecedents(Integer.parseInt(prop.getProperty("nAntecedents")));
        settings.setNRuleSets(Integer.parseInt(prop.getProperty("nRuleSets")));
        settings.setNRuleInitial(Integer.parseInt(prop.getProperty("nRuleInitial")));
        settings.setNRulesMax(Integer.parseInt(prop.getProperty("nRuleMax")));
        settings.setPCrossover(Double.parseDouble(prop.getProperty("pCrossover")));
        settings.setPMutation(Double.parseDouble(prop.getProperty("pMutation")));
        settings.setPDontCareHeuristicRule(Double.parseDouble(prop.getProperty("pDontCareHeuristicRule")));
        settings.setPHybridMichigan(Double.parseDouble(prop.getProperty("pHybridMichigan")));
        settings.setMichiganNReplace(Double.parseDouble(prop.getProperty("michiganFractionReplace")));

        String rejectStrategy = prop.getProperty("rejectStrategy");
        switch (rejectStrategy) {
            case "SINGLE_VARIABLE":
                settings.setRejectStrategy(Settings.RejectStrategies.SINGLE_VARIABLE);
                settings.setPMutationThreshold(Double.parseDouble(prop.getProperty("pMutationThreshold")));
                break;
            case "PER_CLASS":
                settings.setRejectStrategy(Settings.RejectStrategies.PER_CLASS);
                settings.setPMutationThreshold(Double.parseDouble(prop.getProperty("pMutationThreshold")));
                break;
            case "PER_RULE":
                settings.setRejectStrategy(Settings.RejectStrategies.PER_RULE);
                settings.setPMutationThreshold(Double.parseDouble(prop.getProperty("pMutationThreshold")));
                break;
            case "STATIC":
                settings.setRejectStrategy(Settings.RejectStrategies.STATIC);
                settings.setRejectThreshold(Double.parseDouble(prop.getProperty("rejectThreshold")));
                break;
        }

        return settings;
    }

    private static String getFilenameFromDataset(String dataset, int run, int slice, String trainOrTest) {
        if (dataset.equals("toy")) {
            return String.format("C:\\Users\\ericv\\code\\keel-data-ev\\toy\\df_%s.csv", trainOrTest);
        }

        String dataDir = null;
        try {
            Properties prop = new Properties();
            InputStream inputStream = new FileInputStream("system.config");
            prop.load(inputStream);
            dataDir = prop.getProperty("datadir");
        } catch (IOException ex) {
            System.out.println("Error loading system.config");
        }

        // Example path: keel-data-ev\pima\set-2\pima-10dobscv-4tst-normalized.dat
        return String.format("%s\\%s\\set-%d\\%s-10dobscv-%d%s-normalized.dat", dataDir, dataset, run + 1, dataset, slice + 1, trainOrTest);
//            return String.format("C:\\Users\\ericv\\code\\keel-data-ev\\%s\\set-%d\\%s-10dobscv-%d%s-normalized.dat",
//                dataset, run + 1, dataset, slice + 1, trainOrTest);
    }
}
