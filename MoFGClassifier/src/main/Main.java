package main;

import classifier.*;
import classifier.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

    private static String dataset;
    private static String startTimestamp;

    public static void main(String[] args) {
        dataset = "pima";

        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(time);
        startTimestamp = time;

        run();

        System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
    }

    private static void run() {
        String dataset = "pima";
        FileData fd = new FileData();

        int numRuns = 1;
        int numGenerations = 250;

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
            writeResults(hybrid, settings);
        }
    }

    private static void writeResults(Classifier classifier, Settings settings) {
        try {
            FileData fd = new FileData();
            FileData.Output training = fd.load(getFilenameFromDataset(dataset, 0, "tra"));
            FileData.Output testing = fd.load(getFilenameFromDataset(dataset, 0, "tst"));
            Files.createDirectories(Paths.get("results\\" + startTimestamp));
            Files.createDirectories(Paths.get("results\\" + startTimestamp + "\\train"));
            Files.createDirectories(Paths.get("results\\" + startTimestamp + "\\test"));

            PrintWriter settingsWriter = new PrintWriter("results\\" + startTimestamp + "\\settings.txt", "UTF-8");
            settingsWriter.println(settings.toString());
            settingsWriter.close();

            PrintWriter metricsWriterTrain = new PrintWriter("results\\" + startTimestamp + "\\train\\metrics.txt", "UTF-8");
            PrintWriter matrixWriterTrain = new PrintWriter("results\\" + startTimestamp + "\\train\\matrix.txt", "UTF-8");
            PrintWriter metricsWriterTest = new PrintWriter("results\\" + startTimestamp + "\\test\\metrics.txt", "UTF-8");
            PrintWriter matrixWriterTest = new PrintWriter("results\\" + startTimestamp + "\\test\\matrix.txt", "UTF-8");

            for (int solution = 0; solution < settings.nRuleSets; solution++) {
                RuleSet ruleSet = classifier.getPopulation().getRuleSets().get(solution);
                int[][] confusionTrain = getConfusion(ruleSet, training.patterns, settings);
                int[][] confusionTest = getConfusion(ruleSet, testing.patterns, settings);
                metricsWriterTrain.println(1 - gMean(confusionTrain) + "," + totalRejected(confusionTrain) + ", " + ruleSet.getRank());
                printMatrix(matrixWriterTrain, confusionTrain);
                metricsWriterTest.println(1 - gMean(confusionTest) + "," + totalRejected(confusionTest) + ", " + ruleSet.getRank());
                printMatrix(matrixWriterTest, confusionTest);
            }

            metricsWriterTrain.close();
            matrixWriterTrain.close();
            metricsWriterTest.close();
            matrixWriterTest.close();
        } catch (IOException ex) {
            System.out.println("ERROR!  IO Exception: " + ex.getMessage());
        }
    }

    private static int[][] getConfusion(RuleSet ruleSet, List<Pattern> patterns, Settings settings) {
        int[][] confusion = new int[settings.nOutputClasses][settings.nOutputClasses + 1];
        for (Pattern pattern : patterns) {
            int trueClass = pattern.classLabel;
            int result = ruleSet.classify(pattern);
            if (result == Rule.REJECTED_CLASS_LABEL)
                result = settings.nOutputClasses; // Put rejections in final column
            confusion[trueClass][result]++;
        }
        return confusion;
    }

    private static int totalRejected(int[][] matrix) {
        int nClasses = matrix.length;
        int total = 0;
        for (int[] ints : matrix) {
            total += ints[ints.length - 1];
        }
        return total;
    }

    private static double gMean(int[][] matrix) {
        int nClasses = matrix.length;
        double mean = 1;
        for (int i = 0; i < nClasses; i++) {
            int numerator = matrix[i][i];
            int denominator = 0;
            for (int j = 0; j < nClasses; j++) {
                denominator += matrix[i][j];
            }

            // Approximate 0/0 = 1 (this happens when all patterns are rejected)
            if (denominator != 0)
                mean *= ((double)numerator / (double)denominator);
        }
        return Math.pow(mean, 1 / (double)nClasses);
    }

    private static void printMatrix(PrintWriter writer, int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                writer.print(matrix[i][j] + " ");
            }
            writer.println();
        }
        writer.println();
    }

    private static String getFilenameFromDataset(String dataset, int fold, String trainOrTest) {
        // return String.format("C:\\Users\\Eric\\data\\%s\\set-1\\%s-10dobscv-%d%s-normalized.dat",
        return String.format("C:\\Users\\ericv\\code\\keel-data-ev\\%s\\set-1\\%s-10dobscv-%d%s-normalized.dat",
                dataset, dataset, fold + 1, trainOrTest);
    }
}
