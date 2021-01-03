package main;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

import java.util.List;

public class TwoObjMop extends MOP<RuleSet> {
    private final Settings settings;

    public TwoObjMop(Settings settings) {
        this.settings = settings;
        this.nObjectives = 2;
    }

    @Override
    public Evaluator<RuleSet> getEvaluator() {
        return new Evaluator<>() {
            @Override
            public double[] evaluate(RuleSet value, boolean trainingData) {
                int correct = 0;
                List<Pattern> data = trainingData ? settings.trainingData : settings.testingData;
                for (Pattern pattern : data) {
                    int result = value.classify(pattern);
                    if (result == pattern.classLabel)
                        correct++;
                }

                return new double[]{
                        (double) value.getRules().size(),
                        1 - ((double) correct / data.size()),
                };
            }
        };
    }
}
