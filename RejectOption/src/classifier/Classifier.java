package classifier;

import nsga.MOP;
import nsga.NSGA2;
import random.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;

public class Classifier {

    private final Settings settings;
    private final MOP<RuleSet> mop;
    private final MersenneTwisterFast random;
    private final RuleFactory factory;
    private List<RuleSet> population;

    public Classifier(Settings settings, MOP<RuleSet> mop, int randomSeed) {
        this.settings = settings;
        this.mop = mop;
        this.random = new MersenneTwisterFast(randomSeed);
        this.factory = new RuleFactory(settings, this.random);
        this.buildInitialPopulation();
    }

    public void train(int gen) {
        NSGA2<RuleSet> nsga2 = new NSGA2<RuleSet>(this.mop.nObjectives, this.mop.getEvaluator());
        Genetics genetics = new Genetics(this.factory, this.random, this.settings, nsga2);
        for (int i = 0; i < gen; i++) {
            this.population = genetics.hybridEvolution(this.population);
        }
        nsga2.solve(this.population);
    }

    protected void buildInitialPopulation() {
        this.population = new ArrayList<>();
        for (int ruleSetNum = 0; ruleSetNum < this.settings.nRuleSets; ruleSetNum++) {
            List<Rule> rules = new ArrayList<>();
            for (int ruleNum = 0; ruleNum < this.settings.nRulesInitial; ruleNum++) {
                rules.add(this.factory.randomRule());
            }
            this.population.add(this.factory.makeRuleSet(rules));
        }
    }
}
