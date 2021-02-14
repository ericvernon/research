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
        for (RuleSet ruleSet : newRuleSets) {
            ruleSet.removeZeroFitnessRules();
            if (ruleSet.getRules().size() == 0)
                ruleSet.addRule(this.factory.randomRule());
        }

        this.nsga2.solve(newRuleSets);
        Collections.sort(newRuleSets);

        // This output is correctly sorted, however the pareto ranks and crowding scores may be incorrect since
        // they were calculated using the larger population.
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
                double threshold = this.mutateThreshold(firstRules.get(i).getRejectThreshold());
                newRules.add(this.factory.rule(antecedents, threshold));
            }
            nRules = this.random.nextInt(secondRules.size()) + 1;
            for (int i = 0; i < nRules; i++) {
                int[] antecedents = secondRules.get(i).getAntecedents();
                antecedents = this.mutateAntecedents(antecedents);
                double threshold = this.mutateThreshold(secondRules.get(i).getRejectThreshold());
                newRules.add(this.factory.rule(antecedents, threshold));
            }

            if (newRules.size() > this.settings.nRulesMax)
                newRules = this.util.randomSubset(newRules, this.settings.nRulesMax);
        } else {
            // Select all rules from the first parent
            for (Rule rule : first.getRules()) {
                int[] antecedents = rule.getAntecedents();
                antecedents = this.mutateAntecedents(antecedents);
                double threshold = this.mutateThreshold(rule.getRejectThreshold());
                newRules.add(this.factory.rule(antecedents, threshold));
            }
        }

        double[] thresholds;
        if (doCrossover) {
            thresholds = this.thresholdCrossover(first.getRejectThresholds(), second.getRejectThresholds());
        } else {
            thresholds = first.getRejectThresholds();
        }
        thresholds = this.mutateThresholds(thresholds);

        // Remove rules with non-positive CFq (worthless rules)
        // It is rare but possible that we are left with none, in that case just make a random rule.
        newRules.removeIf(r -> r.getConfidence() <= 0);
        if (newRules.size() == 0)
            newRules.add(this.factory.randomRule());

        return this.factory.makeRuleSet(newRules, thresholds);
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

    private double[] thresholdCrossover(double[] first, double[] second) {
        double[] result = new double[first.length];
        for (int i = 0; i < first.length; i++) {
            double x1 = first[i];
            double x2 = second[i];
            double val = alphaBlend(x1, x2);
            result[i] = val;
        }
        return result;
    }

    private double alphaBlend(double x1, double x2) {
        // Find distance between points, multiply it by alpha
        double d = Math.abs(x2 - x1);
        double da = d * 0.5; // alpha = 0.5

        // Select a random number in the range of [min - d*a, max + d*a] where 'min' represents the smaller number
        double min = Math.min(x1, x2) - da;
        double max = Math.max(x1, x2) + da;
        double val = this.random.nextFloat(); // Random in range [0, 1] (technically [0, 1))
        val *= (max - min); // [0, (max - min)]
        val += min; // [min, max]

        // Finally, restrict the value to [0, 1] to make sense in the problem domain
        val = Math.max(0.0, val);
        val = Math.min(1.0, val);
        return val;
    }

    private double[] mutateThresholds(double[] input) {
        double[] result = new double[input.length];

        // Nudge by a value in the range [-0.05, 0.05] and then restrict to the domain [0, 1]
        for (int i = 0; i < input.length; i++) {
            if (this.random.nextFloat() < this.settings.pMutationThreshold) {
                result[i] = this.mutatedThresholdValue(input[i]);
            } else {
                result[i] = input[i];
            }
        }
        return result;
    }

    private double mutateThreshold(double input) {
        if (this.random.nextFloat() < this.settings.pMutationThreshold) {
            return this.mutatedThresholdValue(input);
        }
        return input;
    }

    private double mutatedThresholdValue(double input) {
        // Nudge by a value in the range [-0.05, 0.05] and then restrict to the domain [0, 1]
        double val = this.random.nextFloat() / 10; // [0, 0.1]
        val -= 0.05; // [-0.05, 0.05]
        input += val;
        input = Math.max(0.0, input);
        input = Math.min(1.0, input);
        return input;
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
        return this.factory.makeRuleSet(newRules, input.getRejectThresholds());
    }

    private Rule makeChildRule(Rule first, Rule second) {
        int[] antecedents = new int[this.settings.nInputAttributes];

        double threshold;
        if (this.random.nextFloat() < this.settings.pCrossover) {
            for (int i = 0; i < antecedents.length; i++) {
                if (this.random.nextFloat() < 0.5)
                    antecedents[i] = first.getAntecedents()[i];
                else
                    antecedents[i] = second.getAntecedents()[i];
            }
            threshold = alphaBlend(first.getRejectThreshold(), second.getRejectThreshold());
        } else {
            System.arraycopy(first.getAntecedents(), 0, antecedents, 0, antecedents.length);
            threshold = first.getRejectThreshold();
        }

        antecedents = this.mutateAntecedents(antecedents);
        threshold = this.mutateThreshold(threshold);
        return this.factory.rule(antecedents, threshold);
    }
}
