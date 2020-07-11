package nsga;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;

import java.util.List;

public class MOP {
    public final int nObjectives = 2;
    private List<Pattern> trainingData;

    public MOP (List<Pattern> trainingData) {
        this.trainingData = trainingData;
    }

    public double[] evaluate(RuleSet ruleSet) {
//        double error = ((double)ruleSet.getBadPatterns(this.trainingData).size() / this.trainingData.size());
//        double complexity = this.getComplexity(ruleSet);
        int[] results = this.check(ruleSet);
        int tried = results[0] + results[2];
        double errorOnTried = (double) results[2] / tried;
        double rejected = results[1];

        return new double[] { errorOnTried, rejected};
    }

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

    private double getComplexity(RuleSet ruleSet) {
        int complexity = 0;
        for (Rule rule : ruleSet.getRules()) {
            for (int ante : rule.getAntecedents()) {
                if (ante != 0)
                    complexity++;
            }
        }

        return (double)complexity;
    }
}
