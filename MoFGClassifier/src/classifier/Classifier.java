package classifier;

import nsga.MOP;
import nsga.NSGA2;
import random.MersenneTwister;

import java.util.List;
import java.util.Random;


public class Classifier {

    protected int randomSeed;
    protected Settings settings;
    protected List<Pattern> trainingPatterns;
    protected Random random;
    protected RuleFactory ruleFactory;
    protected Population population;

    public Classifier(Settings settings, int randomSeed) {
        this.randomSeed = randomSeed;
        this.trainingPatterns = settings.trainingPatterns;
        this.settings = settings;
        this.random = new MersenneTwister(randomSeed);
        this.ruleFactory = new RuleFactory(trainingPatterns, settings, this.random);
        this.population = this.buildInitialPopulation();
    }

    public void train(int gen) {
        NSGA2 nsga2 = new NSGA2(new MOP(this.trainingPatterns, this.settings));
        Genetics genetics = new Genetics(this.ruleFactory, this.trainingPatterns, this.random, this.settings, nsga2);
        for (int i = 0; i < gen; i++) {
            this.population = genetics.hybridEvolution(this.population);
            this.population.setFitness(this.trainingPatterns);
        }
        nsga2.solve(this.population.getRuleSets());
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

            if (this.settings.rejectStrategy == Settings.RejectStrategies.PER_CLASS) {
                for (int i = 0; i < thresholds.length; i++)
                    thresholds[i] = this.random.nextDouble() / 2;
            } else if (this.settings.rejectStrategy == Settings.RejectStrategies.SINGLE_VARIABLE) {
                double val = this.random.nextDouble() / 2;
                for (int i = 0; i < thresholds.length; i++)
                    thresholds[i] = val;
            } else if (this.settings.rejectStrategy == Settings.RejectStrategies.STATIC) {
                for (int i = 0; i < thresholds.length; i++)
                    thresholds[i] = this.settings.rejectThreshold;
            } else {
                System.out.println("Unsupported reject strategy!");
            }
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
