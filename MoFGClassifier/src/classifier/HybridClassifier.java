package classifier;

import nsga.MOP;
import nsga.NSGA2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HybridClassifier extends Classifier {
    public HybridClassifier(List<Pattern> trainingPatterns, Settings settings, int randomSeed) {
        super(trainingPatterns, settings, randomSeed);
        this.population = this.buildInitialPopulation();
    }

    @Override
    public void train(int gen) {
        NSGA2 nsga2 = new NSGA2(new MOP(this.trainingPatterns));
        Genetics genetics = new Genetics(this.ruleFactory, this.trainingPatterns, this.random, this.settings, nsga2);
        for (int i = 0; i < gen; i++) {
            this.population = genetics.hybridEvolution(this.population);
            this.population.setFitness(this.trainingPatterns);
        }
    }
}
