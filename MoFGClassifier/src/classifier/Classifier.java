package classifier;

/*
 * Have "classify" be a function of the RuleSet --- ruleSet.classify()
 * When the factory has finished building a rule set, call ruleSet.setFitnessValues()
 */

import random.MersenneTwister;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import main.Settings;

public abstract class Classifier {

    protected int randomSeed;
    protected Settings settings;
    protected List<Pattern> trainingPatterns;
    protected Random random;
    protected RuleFactory ruleFactory;
    protected Population population;

    public Classifier(List<Pattern> trainingPatterns, Settings settings, int randomSeed) {
        this.randomSeed = randomSeed;
        this.trainingPatterns = trainingPatterns;
        this.settings = settings;
        this.random = new MersenneTwister(randomSeed);
        this.ruleFactory = new RuleFactory(trainingPatterns, settings, this.random);

        this.population = this.buildInitialPopulation();
        this.population.setFitness(trainingPatterns);
    }

    public abstract void train(int gen);
//        Genetics genetics = new Genetics(this.ruleFactory, this.trainingPatterns, this.random);
//        for (int i = 0; i < gen; i++) {
//            List<Pattern> misclassifiedPatterns = this.getBadPatterns();
//            this.population = genetics.evolvePopulation(this.population, misclassifiedPatterns);
//            this.population.setFitness(trainingPatterns);
//        }
//    }

//    private List<Pattern> getBadPatterns() {
//        List<Pattern> badPatterns = new ArrayList<>();
//        for (Pattern pattern : this.trainingPatterns) {
//            if (this.classify(pattern) != pattern.classLabel)
//                badPatterns.add(pattern);
//        }
//        return badPatterns;
//    }

    private Population buildInitialPopulation() {
        Population population = new Population();
        for (int ruleSetNum = 0; ruleSetNum < this.settings.nRuleSets; ruleSetNum++) {
            RuleSet ruleSet = new RuleSet();
            for (int ruleNum = 0; ruleNum < this.settings.nRules; ruleNum++) {
                Rule rule = this.ruleFactory.randomRule();
                ruleSet.addRule(rule);
            }
            population.addRuleSet(ruleSet);
        }
        return population;
    }

    public int classify(Pattern pattern) {
        return this.population.classify(pattern);
    }

    public double getTrainingError() {
        int nRight = 0;
        for (Pattern pattern : this.trainingPatterns) {
            int answer = this.classify(pattern);
            if (answer == pattern.classLabel)
                nRight++;
        }

        return (double)(this.trainingPatterns.size() - nRight) / this.trainingPatterns.size();
    }

    // Useful for performing diagnostics, testing, etc.
    public void setPopulation(Population population) {
        this.population = population;
        population.setFitness(this.trainingPatterns);
    }
}
