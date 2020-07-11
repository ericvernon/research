package main;

import classifier.*;
import classifier.Settings;
import nsga.MOP;
import nsga.NSGA2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        int nCorrect = 0;
        int nRej = 0;
        int nWrong = 0;

        String dataset = "pima";
        FileData fd = new FileData();
        int[] metadata = fd.getMetadata(dataset);

        Settings hybridSettings = new Settings();
        hybridSettings.setNAntecedents(15)
                .setNInputAttributes(metadata[1]).setNOutputClasses(metadata[2])
                .setNRules(20).setNRuleSets(200)
                .setPCrossover(0.9).setPMutation(1.0/metadata[1])
                .setPDontCare(0.8).setPHybridMichigan(0.5);

        int numRuns = 1;
        int numGenerations = 2000;
        for (int i = 0; i < numRuns; i++) {
            //List<Pattern> trainingData = fd.getData(dataset);
            List<Pattern> trainingData = fd.load("C:\\Users\\ericv\\code\\keel-data-ev\\pima\\set-1\\pima-10dobscv-1tra-normalized.dat").patterns;
            hybridSettings.setTrainingPatterns(trainingData);
            Classifier hybrid = new HybridClassifier(trainingData, hybridSettings, (i * 10) + 2);

            hybrid.train(numGenerations);

            try {
                FileWriter file = new FileWriter("C:\\Users\\ericv\\Desktop\\training.txt");
                for (RuleSet ruleSet : hybrid.getPopulation().getRuleSets()) {
                    file.write(String.format("%.6f,%d,%d\n", ruleSet.getObjectives()[0], (int) ruleSet.getObjectives()[1], ruleSet.getRank()));
                }
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Pattern> testingData = fd.load("C:\\Users\\ericv\\code\\keel-data-ev\\pima\\set-1\\pima-10dobscv-1tst-normalized.dat").patterns;
            MOP mop = new MOP(testingData);
            NSGA2 nsga2 = new NSGA2(mop);
            nsga2.solve(hybrid.getPopulation().getRuleSets());

//            for (Pattern pattern : trainingData) {
//                int output;
//                output = hybrid.classify(pattern);
//                if (output == pattern.classLabel)
//                    nCorrect++;
//                else if (output == Rule.REJECTED_CLASS_LABEL)
//                    nRej++;
//                else
//                    nWrong++;
//            }

            try {
                FileWriter file = new FileWriter("C:\\Users\\ericv\\Desktop\\testing.txt");
                for (RuleSet ruleSet : hybrid.getPopulation().getRuleSets()) {
                    file.write(String.format("%.6f,%d,%d\n", ruleSet.getObjectives()[0], (int) ruleSet.getObjectives()[1], ruleSet.getRank()));
                }
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileWriter file = new FileWriter("C:\\Users\\ericv\\Desktop\\weights.txt");
                for (RuleSet ruleSet : hybrid.getPopulation().getRuleSets()) {
                    file.write(String.format("%.6f,%.6f,%.6f, %d\n", ruleSet.getObjectives()[0],
                                ruleSet.getRejectThresholds()[0], ruleSet.getRejectThresholds()[1], ruleSet.getRank()));
                }
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        int totalPatterns = metadata[0] * numRuns;
//        System.out.printf("H: %.6f %.6f %.6f\n", (nCorrect * 100.0) / totalPatterns,
//                (nRej * 100.0) / totalPatterns, (nWrong * 100.0) / totalPatterns);
    }
}
