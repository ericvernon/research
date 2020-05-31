package classifier;

import java.util.Collections;
import java.util.List;

public class PittsburghClassifier extends Classifier {
    public PittsburghClassifier(List<Pattern> trainingPatterns, Settings settings, int randomSeed) {
        super(trainingPatterns, settings, randomSeed);
    }

    @Override
    public void train(int gen) {
        Genetics genetics = new Genetics(this.ruleFactory, this.trainingPatterns, this.random, this.settings);
        for (int i = 0; i < gen; i++) {
            this.population = genetics.pittsburghEvolution(this.population);
            this.population.getRuleSets().sort(Collections.reverseOrder());
            this.population.setFitness(this.trainingPatterns);
            this.population.setFitness(this.trainingPatterns);
        }
    }
}
