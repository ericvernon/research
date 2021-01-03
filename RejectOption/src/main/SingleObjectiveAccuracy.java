package main;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

import java.util.List;

public class SingleObjectiveAccuracy extends MOP<RuleSet> {
    private final Settings settings;

    public SingleObjectiveAccuracy(Settings settings) {
        this.settings = settings;
        this.nObjectives = 1;
    }

    @Override
    public Evaluator<RuleSet> getEvaluator() {
        return new Evaluator<RuleSet>() {
            @Override
            public double[] evaluate(RuleSet value, boolean trainingData) {
                int correct = 0;
                int incorrect = 0;
                List<Pattern> evaluationSet = trainingData ? settings.trainingData : settings.testingData;
                for (Pattern pattern : evaluationSet) {
                    int result = value.classify(pattern);
                    if (result == pattern.classLabel)
                        correct++;
                    else
                        incorrect++;
                }

                return new double[] {
                        (double)incorrect / (correct + incorrect),
                };
            }
        };
    }
}
