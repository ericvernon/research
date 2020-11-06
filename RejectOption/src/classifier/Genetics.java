package classifier;

import nsga.NSGA2;
import random.MersenneTwisterFast;

import java.util.*;

public class Genetics {

    private final RuleFactory factory;
    private final Settings settings;
    private final MersenneTwisterFast random;
    private final NSGA2<RuleSet> nsga2;
    private final Util util;

    public Genetics(RuleFactory factory, MersenneTwisterFast random, Settings settings, NSGA2<RuleSet> nsga2) {
        this.factory = factory;
        this.settings = settings;
        this.random = random;
        this.nsga2 = nsga2;
        this.util = new Util(this.random);
    }

    public List<RuleSet> hybridEvolution(List<RuleSet> oldRuleSets) {
        List<RuleSet> newRuleSets = new ArrayList<>();

        this.nsga2.solve(oldRuleSets);
        for (int i = 0; i < this.settings.nRuleSets; i++) {
            RuleSet first = util.binaryTournament(oldRuleSets);
            RuleSet second = util.binaryTournament(oldRuleSets);
            RuleSet newRuleSet = this.makeChildRuleSet(first, second);
            if (this.random.nextFloat() < this.settings.pHybridMichigan) {
                newRuleSet.setRuleFitness();
                newRuleSet = this.michiganEvolution(newRuleSet);
            }
            newRuleSets.add(newRuleSet);
        }

        newRuleSets.addAll(oldRuleSets);
        this.nsga2.solve(newRuleSets);
        Collections.sort(newRuleSets);

        return newRuleSets.subList(0, this.settings.nRuleSets);
    }

    private RuleSet makeChildRuleSet(RuleSet first, RuleSet second) {
        List<Rule> newRules = new ArrayList<>();
        boolean doCrossover = this.random.nextFloat() < this.settings.pCrossover;
        List<Rule> firstRules = first.getRules();
        List<Rule> secondRules = second.getRules();

        if (doCrossover) {
            // Select [1, N] rules from each parent, and then randomly prune if over the limit
            int nRules = this.random.nextInt(firstRules.size()) + 1;
            for (int i = 0; i < nRules; i++) {
                int[] antecedents = firstRules.get(i).getAntecedents();
                antecedents = this.mutateAntecedents(antecedents);
                newRules.add(this.factory.rule(antecedents));
            }
            nRules = this.random.nextInt(secondRules.size()) + 1;
            for (int i = 0; i < nRules; i++) {
                int[] antecedents = secondRules.get(i).getAntecedents();
                antecedents = this.mutateAntecedents(antecedents);
                newRules.add(this.factory.rule(antecedents));
            }

            if (newRules.size() > this.settings.nRulesMax)
                newRules = this.util.randomSubset(newRules, this.settings.nRulesMax);
        } else {
            // Select all rules from the first parent
            for (Rule rule : first.getRules()) {
                int[] antecedents = rule.getAntecedents();
                antecedents = this.mutateAntecedents(antecedents);
                newRules.add(this.factory.rule(antecedents));
            }
        }

        newRules.removeIf(r -> r.getConfidence() <= 0);
        if (newRules.size() == 0)
            newRules.add(this.factory.randomRule());

        return this.factory.makeRuleSet(newRules);
    }

    private int[] mutateAntecedents(int[] antecedents) {
        int[] result = new int[antecedents.length];
        for (int i = 0; i < antecedents.length; i++) {
            if (this.random.nextDouble() < this.settings.pMutation)
                result[i] = this.random.nextInt(this.settings.nAntecedents);
            else
                result[i] = antecedents[i];
        }
        return result;
    }

    public RuleSet michiganEvolution(RuleSet input) {
        List<Rule> newRules = new ArrayList<>();
        List<Rule> oldRules = input.getRules();
        oldRules.sort(Collections.reverseOrder());

        List<Pattern> heuristicPatterns = input.getBadPatterns();

        Util util = new Util(this.random);
        int nReplace = (int)Math.ceil(this.settings.michiganNReplace * oldRules.size());
        int numHeuristicRules = Math.min(nReplace, heuristicPatterns.size());
        int numGeneticRules = nReplace - numHeuristicRules;
        int numKeep = oldRules.size() - nReplace;

        for (int i = 0; i < numKeep; i++)
            newRules.add(oldRules.get(i).deepCopy());

        for (int i = 0; i < numGeneticRules; i++) {
            Rule first = util.binaryTournament(oldRules);
            Rule second = util.binaryTournament(oldRules);
            newRules.add(this.makeChildRule(first, second));
        }

        try {
            List<Pattern> patterns = util.randomSubset(heuristicPatterns, numHeuristicRules);
            for (Pattern pattern : patterns) {
                newRules.add(this.factory.heuristicRule(pattern));
            }
        } catch (Exception ex) {
            System.out.println("Error!");
        }

        newRules.removeIf(r -> r.getConfidence() <= 0);
        return this.factory.makeRuleSet(newRules);
    }

    private Rule makeChildRule(Rule first, Rule second) {
        int[] antecedents = new int[this.settings.nInputAttributes];

        if (this.random.nextFloat() < this.settings.pCrossover) {
            for (int i = 0; i < antecedents.length; i++) {
                if (this.random.nextFloat() < 0.5)
                    antecedents[i] = first.getAntecedents()[i];
                else
                    antecedents[i] = second.getAntecedents()[i];
            }
        } else {
            System.arraycopy(first.getAntecedents(), 0, antecedents, 0, antecedents.length);
        }

        antecedents = this.mutateAntecedents(antecedents);
        return this.factory.rule(antecedents);
    }
}
