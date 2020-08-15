package nsga;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;

import java.util.List;

public class MOP {
    public final int nObjectives = 2;
    private List<Pattern> trainingData;
    private Settings settings;

    public MOP (List<Pattern> trainingData, Settings settings) {
        this.trainingData = trainingData;
        this.settings = settings;
    }

    public double[] evaluate(RuleSet ruleSet) {
        int[][] matrix  = this.getConfusion(ruleSet);
        return new double[] { this.totalRejected(matrix), 1 - this.gMean(matrix)};
    }

    // Training using error % and rejected #
//    public double[] evaluate(RuleSet ruleSet) {
//        int[] results = this.check(ruleSet);
//        int tried = results[0] + results[2];
//        double errorOnTried = (double) results[2] / tried;
//        double rejected = results[1];
//
//        return new double[] { errorOnTried, rejected};
//    }

    private int[] check(RuleSet ruleSet) {
        int nRight = 0;
        int nRejected = 0;
        int nWrong = 0;
        for (Pattern pattern : this.trainingData) {
            int result = ruleSet.classify(pattern);
            if (result == pattern.classLabel)
                nRight++;
            else if (result == Rule.REJECTED_CLASS_LABEL)
                nRejected++;
            else
                nWrong++;
        }
        return new int[] { nRight, nRejected, nWrong };
    }

    private int[][] getConfusion(RuleSet ruleSet) {
        int[][] confusion = new int[settings.nOutputClasses][settings.nOutputClasses + 1];
        for (Pattern pattern : this.trainingData) {
            int trueClass = pattern.classLabel;
            int result = ruleSet.classify(pattern);
            if (result == Rule.REJECTED_CLASS_LABEL)
                result = settings.nOutputClasses; // Put rejections in final column
            confusion[trueClass][result]++;
        }
        return confusion;
    }

    private double totalRejected(int[][] matrix) {
        int nClasses = matrix.length;
        int total = 0;
        for (int[] ints : matrix) {
            total += ints[ints.length - 1];
        }
        return (double)total;
    }

    private double gMean(int[][] matrix) {
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
}
