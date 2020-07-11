package classifier;

//import main.Settings;

import main.Watch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleFactory {
    private List<Pattern> trainingPatterns;
    private Settings settings;
    private FuzzyCalculator fuzzyCalculator;
    private Random random;

    public RuleFactory(List<Pattern> trainingPatterns, Settings settings,
                       Random random) {
        this.trainingPatterns = trainingPatterns;
        this.settings = settings;
        this.fuzzyCalculator = FuzzyCalculator.getInstance();
        this.random = random;
    }

    public Rule randomRule() {
        int[] antecedents = new int[this.settings.nInputAttributes];
        for (int i = 0; i < antecedents.length; i++)
            antecedents[i] = this.random.nextInt(this.settings.nAntecedents);

        return this.rule(antecedents);
    }

    public Rule heuristicRule(Pattern pattern) {
        int[] antecedents = new int[this.settings.nInputAttributes];
        for (int i = 0; i < antecedents.length; i++) {
            double total = 0.0;
            double[] values = new double[this.settings.nAntecedents];
            for (int j = 1; j < values.length; j++) { // Start at 1 to exclude "Don't care"
                double value = this.fuzzyCalculator.calculateMembershipValue(pattern.inputValues[i], j);
                total += value;
                values[j] = value;
            }

            for (int j = 1; j < values.length; j++) {
                values[j] = values[j] / total;
            }

            int winningIndex = 0;
            double selection = this.random.nextDouble();
            double cumulativeProbability = 0.0;
            for (int j = 1; j < values.length && winningIndex == 0; j++) {
                cumulativeProbability += values[j];
                if (cumulativeProbability > selection)
                    winningIndex = j;
            }

            antecedents[i] = winningIndex;
        }

        for (int i = 0; i < antecedents.length; i++) {
            boolean dontCare = this.random.nextDouble() < this.settings.pDontCare;
            if (dontCare)
                antecedents[i] = FuzzyCalculator.DONT_CARE;
        }

        return this.rule(antecedents);
    }

    public Rule rule(int[] antecedents) {
        LabelAndWeight labelAndWeight = this.calculateRule(antecedents);
        return new Rule(antecedents, labelAndWeight.label, labelAndWeight.weight);
    }

    private LabelAndWeight calculateRule(int[] antecedents) {
        double[] scores = new double[this.settings.nOutputClasses];
        double best = 0.0;
        int bestIndex = 0;
        for (int possibleLabel = 0; possibleLabel < scores.length; possibleLabel++) {
            double score = this.calculateConfidenceInLabel(antecedents, possibleLabel);
            scores[possibleLabel] = score;
            if (score > best) {
                best = score;
                bestIndex = possibleLabel;
            }
        }

        double weight = best;
        for (int label = 0; label < scores.length; label++) {
            if (label != bestIndex)
                weight -= scores[label];
        }

        int classLabel;
        if (weight < 0)
            classLabel = Rule.REJECTED_CLASS_LABEL;
        else
            classLabel = bestIndex;
        weight = Math.max(0.0, weight);

        return new LabelAndWeight(classLabel, weight);
    }

    private double calculateConfidenceInLabel(int[] antecedents, int classLabel) {
        double labelCompatibility = 0;
        double totalCompatibility = 0;
        for (Pattern pattern : this.trainingPatterns) {
            double compatibility = this.fuzzyCalculator.calculateCompatibility(antecedents, pattern);
            if (pattern.classLabel == classLabel)
                labelCompatibility += compatibility;
            totalCompatibility += compatibility;
        }

        if (totalCompatibility == 0.0)
            return 0;
        return labelCompatibility / totalCompatibility;
    }

    private static class LabelAndWeight {
        private int label;
        private double weight;
        private LabelAndWeight(int label, double weight) {
            this.label = label;
            this.weight = weight;
        }
    }
}
