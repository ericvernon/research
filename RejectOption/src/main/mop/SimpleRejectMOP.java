package main.mop;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

import java.util.List;

public class SimpleRejectMOP extends MOP<RuleSet> {
    private final Settings settings;

    public SimpleRejectMOP(Settings settings) {
        this.settings = settings;
        this.nObjectives = 3;
    }

    public String[] getObjectiveNames() {
        return new String[] {"nRules", "errorOnAttempted", "nRejected"};
    }

    @Override
    public Evaluator<RuleSet> getEvaluator() {
        return new Evaluator<RuleSet>() {
            @Override
            public double[] evaluate(RuleSet value, boolean trainingData) {
                int correct = 0;
                int wrong = 0;
                int rejected = 0;
                List<Pattern> data = trainingData ? settings.trainingData : settings.testingData;
                for (Pattern pattern : data) {
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
