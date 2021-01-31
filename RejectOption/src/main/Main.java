package main;

import classifier.*;
import nsga.Evaluator;
import nsga.MOP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Settings settings = new Settings();
        settings.setNAntecedents(15)
                .setNRuleSets(200).setNRuleInitial(20).setNRulesMax(40)
                .setPCrossover(0.9).setPMutation(0.1).setPDontCareHeuristicRule(0.8)
                .setPHybridMichigan(0.5).setMichiganNReplace(0.2)
                .setRejectStrategy(Settings.RejectStrategies.PER_CLASS)
                .setPMutationThreshold(0.25)
                .setNGenerations(750);
        String startTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(startTime);

        // Runs / cross validation settings.  Assumes the data has already been prepared.  In general, I generated the
        // data "slices" using the Keel software GUI, then used a PHP script to normalize and format the data.
        // How many times k-fold cross validation should be run.
        int runs = 5;
        // The 'k' value.
        int slicesPerRun = 10;
        String dataset = "pima";
        settings.setComment("iris test for ML class");

        FileData fd = new FileData();
        for (int run = 0; run < runs; run++) {
            for (int slice = 0; slice < slicesPerRun; slice++) {
                FileData.Output trainingData = fd.load(getFilenameFromDataset(dataset, run, slice, "tra"));
                FileData.Output testingData = fd.load(getFilenameFromDataset(dataset, run, slice, "tst"));
                settings.setTrainingData(trainingData.patterns).setTestingData(testingData.patterns)
                        .setNInputAttributes(trainingData.nAttributes).setNOutputClasses(trainingData.nOutputClasses)
                        .setRulesetMinRules(trainingData.nOutputClasses)
                        .setDatasetName(dataset);

                ResultsMaster resultsMaster = new ResultsMaster(startTime,
                        String.format("run%d_slice%d", run, slice), settings);
                MOP<RuleSet> mop = new SimpleMOP(settings);
                settings.setMopName(mop.getClass().getName());
                Classifier classifier = new Classifier(settings, mop, resultsMaster, 1);
                classifier.train();
                // After training, this population is sorted by pareto rank and crowding score.
                List<RuleSet> finalPopulation = classifier.getPopulation();
                resultsMaster.finalizeOutput(finalPopulation, mop);
            }
            System.out.println("Finished run " + (run + 1) + "...");
        }


        String endTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(endTime);
        System.out.println("Processing results...");
        ResultsCollator.process("results\\" + startTime);
        System.out.println("Done.");
    }

    private static String getFilenameFromDataset(String dataset, int run, int slice, String trainOrTest) {
        // Example path: keel-data-ev\pima\set-2\pima-10dobscv-4tst-normalized.dat
//        return String.format("C:\\Users\\Eric\\data\\%s\\set-1\\%s-10dobscv-%d%s-normalized.dat",
            return String.format("C:\\Users\\ericv\\code\\keel-data-ev\\%s\\set-%d\\%s-10dobscv-%d%s-normalized.dat",
                dataset, run + 1, dataset, slice + 1, trainOrTest);
//        return String.format("C:\\Users\\ericv\\code\\keel-data-ev\\toy\\toy.csv", trainOrTest);
    }
}
