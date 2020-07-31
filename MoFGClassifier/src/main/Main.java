package main;

import classifier.*;
import classifier.Settings;
import nsga.MOP;
import nsga.NSGA2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        String dataset = "pima";
        FileData fd = new FileData();

        int numRuns = 1;
        int numGenerations = 200;

        System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));

        for (int i = 0; i < numRuns; i++) {
            FileData.Output trainingData = fd.load(getFilenameFromDataset(dataset, i, "tra"));

            Settings settings = new Settings();
            settings.setNAntecedents(15)
                    .setNInputAttributes(trainingData.nAttributes).setNOutputClasses(trainingData.nOutputClasses)
                    .setNRules(20).setNRuleSets(200)
                    .setPCrossover(0.9).setPMutation(1.0/trainingData.nAttributes)
                    .setPDontCare(0.8).setPHybridMichigan(0.5)
                    .setTrainingPatterns(trainingData.patterns);
            Classifier hybrid = new Classifier(settings, (i * 10) + 2);
            hybrid.train(numGenerations);

            FileData.Output testingData = fd.load(getFilenameFromDataset(dataset, i, "tst"));
            for (int z = 0; z < 3; z++) {
                tmp(hybrid.getPopulation().getRuleSets().get(z), testingData.patterns);
            }

            System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        }
    }

    private static void tmp(RuleSet ruleSet, List<Pattern> patterns) {
        int ok = 0;
        int bad = 0;
        int rej = 0;
        for (Pattern pattern : patterns) {
            int result = ruleSet.classify(pattern);
            if (result == Rule.REJECTED_CLASS_LABEL)
                rej++;
            else if (result == pattern.classLabel)
                ok++;
            else
                bad++;
        }
        System.out.println(ok + " " + bad + " " + rej);
    }

    private static String getFilenameFromDataset(String dataset, int fold, String trainOrTest) {
        return String.format("C:\\Users\\Eric\\data\\%s\\set-1\\%s-10dobscv-%d%s-normalized.dat",
                dataset, dataset, fold + 1, trainOrTest);
    }
}
