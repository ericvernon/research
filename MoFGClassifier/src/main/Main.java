package main;

import classifier.*;
import classifier.Settings;
import random.MersenneTwister;

import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        Random random = new MersenneTwister(77);
        System.out.println(random.nextFloat());
        System.out.println(random.nextFloat());
        System.out.println(random.nextFloat());

        int rightM = 0;
        int rightP = 0;

        String dataset = "wine";
        FileData fd = new FileData();
        int[] metadata = fd.getMetadata(dataset);

        Settings michiganSettings = new Settings();
        michiganSettings.setNAntecedents(15)
                .setNInputAttributes(metadata[1]).setNOutputClasses(metadata[2])
                .setNRules(10).setNRuleSets(1)
                .setPCrossover(0.9).setPMutation(1.0/metadata[1])
                .setNReplace(2);

        Settings pittsburghSettings = new Settings();
        pittsburghSettings.setNAntecedents(15)
                .setNInputAttributes(metadata[1]).setNOutputClasses(metadata[2])
                .setNRules(10).setNRuleSets(200)
                .setPCrossover(0.9).setPMutation(1.0/metadata[1]);

        int numRuns = 1;
        for (int i = 0; i < numRuns; i++) {
            List<Pattern> trainingData = fd.getData(dataset);
//            Classifier michigan = new MichiganClassifier(trainingData, michiganSettings, 0);
            Classifier pittsburgh = new PittsburghClassifier(trainingData, pittsburghSettings, 5);
            long t1 = System.nanoTime();
//            michigan.train(1000);
            pittsburgh.train(10);
            long t2 = System.nanoTime();
            System.out.println("Execution time: " + ((t2 - t1) * 1e-6) + " milliseconds");

            for (Pattern pattern : trainingData) {
                int output;
//                output = michigan.classify(pattern);
//                if (output == pattern.classLabel)
//                    rightM++;

                output = pittsburgh.classify(pattern);
                if (output == pattern.classLabel)
                    rightP++;
            }

//            List<Pattern> testData = fd.getData("iris", "tst", i + 1);
//            for (Pattern pattern : testData) {
//                int output = classifier.classify(pattern);
//                if (output == pattern.classLabel)
//                    rightTst++;
//                else
//                    wrongTst++;
//            }
        }
        //System.out.println();
        //System.out.printf("[TRAIN] %d/%d (%.2f)\n", rightTr, rightTr + wrongTr, (rightTr * 100.0) / (rightTr + wrongTr));
        System.out.printf("M: %.6f\n", (rightM * 100.0) / (metadata[0] * numRuns));
        System.out.printf("P: %.6f\n", (rightP * 100.0) / (metadata[0] * numRuns));
//        System.out.printf("[TEST]  %d/%d (%.2f)\n", rightTst, rightTst + wrongTst, (rightTst * 100.0) / (rightTst + wrongTst));
    }
}
