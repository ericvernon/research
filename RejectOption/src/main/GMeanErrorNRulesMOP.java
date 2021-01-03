package main;

import classifier.Pattern;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

/**
 * This MOP has two objectives:
 * 1. Minimize (1 - GMean).  GMean is the geometric mean of the per-class accuracies, not counting rejected patterns.
 * 2. Minimize the total number of rules.
 */
public class GMeanErrorNRulesMOP extends MOP<RuleSet> {
    private final Settings settings;

    public GMeanErrorNRulesMOP(Settings settings) {
        this.settings = settings;
        this.nObjectives = 2;
    }

    @Override
    public Evaluator<RuleSet> getEvaluator() {
        /*
         * The geometric mean is calculated as follows:
         *
         *   Let ACCURACY(C) equal the number of correctly classified members of C, divided by the number of members
         *   of class C where classification was attempted.  For example, if 10 patterns were correctly classified,
         *   5 patterns were incorrectly classified, and 5 patterns were rejected, then the accuracy is 10/15.
         *
         * Then, define the g-mean as the i-th root of (ACCURACY(C1) * ACCURACY(C2) * ... ACCURACY(Ci))
         */
        return new Evaluator<RuleSet>() {
            @Override
            public double[] evaluate(RuleSet value, boolean trainingData) {
                int[] correctClassifications = new int[settings.nOutputClasses];
                int[] attemptedClassifications = new int[settings.nOutputClasses];

                for (Pattern pattern : settings.trainingData) {
                    int result = value.classify(pattern);
                    if (result == pattern.classLabel)
                        correctClassifications[result]++;
                    attemptedClassifications[pattern.classLabel]++;
                }

                double gmean = 1;
                double nClassesAttempted = 0;
                for (int i = 0; i < settings.nOutputClasses; i++) {
                    if (attemptedClassifications[i] != 0) {
                        gmean *= ((double) correctClassifications[i] / attemptedClassifications[i]);
                        nClassesAttempted++;
                    }
                }

                // Calculate g-mean against all classes attempted.  However, if no patterns were attempted at all,
                // the error rate is 100%.
                if (nClassesAttempted == 0)
                    gmean = 0;
                else
                    gmean = Math.pow(gmean, 1.0 / nClassesAttempted);

                return new double[]{
                        1 - gmean,
                        value.getRules().size()
                };
            }
        };
    }
}
