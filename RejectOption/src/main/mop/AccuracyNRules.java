package main.mop;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

import java.util.List;

public class AccuracyNRules extends MOP<RuleSet> {
    private final Settings settings;

    public AccuracyNRules(Settings settings) {
        this.settings = settings;
        this.nObjectives = 2;
    }

    public String[] getObjectiveNames() {
        return new String[] {"simpleError", "nRules"};
    }

    @Override
    public Evaluator<RuleSet> getEvaluator() {
        return new Evaluator<RuleSet>() {
            @Override
            public double[] evaluate(RuleSet value, boolean trainingData) {
                int wrong = 0;
                List<Pattern> data = trainingData ? settings.trainingData : settings.testingData;
                for (Pattern pattern : data) {
                    int result = value.classify(pattern);
                    if (result != pattern.classLabel)
                        wrong++;
                }

                return new double[] {
                        (double)wrong / data.size(),
                        (double)value.getRules().size(),
                };
            }
        };
    }
}
