package classifier;

import java.util.List;

public class MichiganClassifier extends Classifier {
    public MichiganClassifier(List<Pattern> trainingPatterns, Settings settings, int randomSeed) {
        super(trainingPatterns, settings, randomSeed);
    }

    @Override
    public void train(int gen) {
        Genetics genetics = new Genetics(this.ruleFactory, this.trainingPatterns, this.random, this.settings);
        for (int i = 0; i < gen; i++) {
            RuleSet ruleSet = this.population.getRuleSets().get(0);
            this.population = new Population();
            population.addRuleSet(genetics.michiganEvolution(ruleSet));
            population.setFitness(this.trainingPatterns);
        }
    }
}
