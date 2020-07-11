package classifier;

import java.util.List;

public class MichiganClassifier extends Classifier {
    public MichiganClassifier(List<Pattern> trainingPatterns, Settings settings, int randomSeed) {
        super(trainingPatterns, settings, randomSeed);
    }

    @Override
    public void train(int gen) {
        Genetics genetics = new Genetics(this.ruleFactory, this.trainingPatterns, this.random, this.settings, null);
        for (int i = 0; i < gen; i++) {
            RuleSet oldRules = this.population.getRuleSets().get(0);
            RuleSet newRules = genetics.michiganEvolution(oldRules);
            this.population = new Population();
            this.population.addRuleSet(newRules);
            population.setFitness(this.trainingPatterns);
        }
    }
}
