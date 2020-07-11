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
    }

    public abstract void train(int gen);
//        Genetics genetics = new Genetics(this.ruleFactory, this.trainingPatterns, this.random);
//        for (int i = 0; i < gen; i++) {
//            List<Pattern> misclassifiedPatterns = this.getBadPatterns();
//            this.population = genetics.evolvePopulation(this.population, misclassifiedPatterns);
//            this.population.setFitness(trainingPatterns);
//        }
//    }

    protected List<Pattern> getBadPatterns() {
        List<Pattern> badPatterns = new ArrayList<>();
        for (Pattern pattern : this.trainingPatterns) {
            if (this.classify(pattern) != pattern.classLabel)
                badPatterns.add(pattern);
        }
        return badPatterns;
    }

    protected Population buildInitialPopulation() {
        Population population = new Population();
        Util util = new Util(this.random);
        for (int ruleSetNum = 0; ruleSetNum < this.settings.nRuleSets; ruleSetNum++) {
            RuleSet ruleSet = new RuleSet();
            List<Pattern> heuristics = util.randomSubset(this.trainingPatterns, this.settings.nRules);
            for (Pattern pattern : heuristics) {
                Rule rule = this.ruleFactory.heuristicRule(pattern);
                ruleSet.addRule(rule);
            }
            double[] thresholds = new double[this.settings.nOutputClasses];
            for (int i = 0; i < thresholds.length; i++)
                thresholds[i] = this.random.nextDouble() / 2;
            ruleSet.setRejectThresholds(thresholds);

            population.addRuleSet(ruleSet);
        }
        population.setFitness(this.trainingPatterns);
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

    public Population getPopulation() {
        return population;
    }
}
