package classifier;

import main.ResultsMaster;
import nsga.MOP;
import nsga.NSGA2;
import random.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Classifier {

    private final Settings settings;
    private final MOP<RuleSet> mop;
    private final ResultsMaster resultsMaster;
    private final MersenneTwisterFast random;
    private final RuleFactory factory;
    private final Util util;
    private List<RuleSet> population;

    public Classifier(Settings settings, MOP<RuleSet> mop, ResultsMaster resultsMaster, int randomSeed) {
        this.settings = settings;
        this.mop = mop;
        this.resultsMaster = resultsMaster;
        this.random = new MersenneTwisterFast(randomSeed);
        this.factory = new RuleFactory(settings, this.random);
        this.util = new Util(this.random);
        this.buildInitialPopulation();
    }

    public void train() {
        NSGA2<RuleSet> nsga2 = new NSGA2<RuleSet>(this.mop.nObjectives, this.mop.getEvaluator());
        nsga2.solve(this.population);
        this.resultsMaster.recordPopulation("gen0", this.population);
        Genetics genetics = new Genetics(this.factory, this.random, this.settings, nsga2);
        for (int i = 0; i < this.settings.nGenerations; i++) {
            this.population = genetics.hybridEvolution(this.population);
            this.resultsMaster.recordPopulation("gen" + Integer.toString(i + 1), this.population);
        }
        nsga2.solve(this.population);
        Collections.sort(this.population);
    }

    protected void buildInitialPopulation() {
        this.population = new ArrayList<>();
        for (int ruleSetNum = 0; ruleSetNum < this.settings.nRuleSets; ruleSetNum++) {
            List<Rule> rules = new ArrayList<>();
            List<Pattern> patterns = this.util.randomSubset(this.settings.trainingData, this.settings.nRulesInitial);
            for (Pattern pattern : patterns) {
                Rule r = this.factory.heuristicRule(pattern);
                if (r.getConfidence() > 0)
                    rules.add(r);
            }
            this.population.add(this.factory.makeRuleSet(rules));
        }
    }

    public List<RuleSet> getPopulation() {
        return this.population;
    }
}
