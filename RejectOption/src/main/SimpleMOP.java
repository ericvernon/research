package main;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

public class SimpleMOP extends MOP<RuleSet> {
    private final Settings settings;

    public SimpleMOP(Settings settings) {
        this.settings = settings;
        this.nObjectives = 3;
    }

    @Override
    public Evaluator<RuleSet> getEvaluator() {
        return new Evaluator<RuleSet>() {
            @Override
            public double[] evaluate(RuleSet value) {
                int correct = 0;
                int wrong = 0;
                int rejected = 0;
                for (Pattern pattern : settings.trainingData) {
                    int result = value.classify(pattern);
                    if (result == pattern.classLabel)
                        correct++;
                    else if (result == Rule.REJECTED_CLASS_LABEL)
                        rejected++;
                    else
                        wrong++;
                }

                return new double[] {
                        (double)value.getRules().size(),
                        (double)wrong / (wrong + correct),
                        (double)rejected,
                };
            }
        };
    }
}
