package classifier;

import nsga.NSGA2;

import java.util.*;

public class Genetics {

    private RuleFactory factory;
    private List<Pattern> trainingPatterns;
    private Settings settings;
    private Random random;
    private NSGA2 nsga2;

    public Genetics(RuleFactory factory, List<Pattern> trainingPatterns, Random random, Settings settings, NSGA2 nsga2) {
        this.factory = factory;
        this.trainingPatterns = trainingPatterns;
        this.settings = settings;
        this.random = random;
        this.nsga2 = nsga2;
    }

    public Population hybridEvolution(Population input) {
        List<RuleSet> oldRuleSets = input.getRuleSets();
        List<RuleSet> newRuleSets = new ArrayList<>();

        Util util = new Util(this.random);

        this.nsga2.solve(oldRuleSets);
        for (int i = 0; i < this.settings.nRuleSets; i++) {
            RuleSet first = util.binaryTournament(oldRuleSets);
            RuleSet second = util.binaryTournament(oldRuleSets);
            RuleSet newRuleSet = this.makeChildRuleSet(first, second);
            if (this.random.nextFloat() < this.settings.pHybridMichigan) {
                newRuleSet = this.michiganEvolution(newRuleSet);
            }
            newRuleSet.setFitness(this.trainingPatterns);
            newRuleSets.add(newRuleSet);
        }

        newRuleSets.addAll(oldRuleSets);
        this.nsga2.solve(newRuleSets);

        return new Population(newRuleSets.subList(0, this.settings.nRuleSets));
    }

    public RuleSet michiganEvolution(RuleSet input) {
        List<Rule> newRules = new ArrayList<>();
        List<Rule> oldRules = input.getRules();
        oldRules.sort(Collections.reverseOrder());

        List<Pattern> heuristicPatterns = input.getBadPatterns(this.settings.trainingPatterns);

        Util util = new Util(this.random);
        int numKeep = this.settings.nRules - this.settings.nReplace;
        int numHeuristicRules = Math.min(this.settings.nReplace / 2, heuristicPatterns.size());
        int numGeneticRules = this.settings.nReplace - numHeuristicRules;

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

        RuleSet newRuleSet = new RuleSet(newRules);
        double[] thresholds = new double[input.getRejectThresholds().length];
        System.arraycopy(input.getRejectThresholds(), 0, thresholds, 0, thresholds.length);
        newRuleSet.setRejectThresholds(thresholds);
        newRuleSet.setFitness(this.trainingPatterns);
        return newRuleSet;
    }

    public Population pittsburghEvolution(Population input) {
        List<RuleSet> newRuleSets = new ArrayList<>();
        List<RuleSet> oldRuleSets = input.getRuleSets();
        Util util = new Util(this.random);

        oldRuleSets.sort(Collections.reverseOrder());
        newRuleSets.add(oldRuleSets.get(0).deepCopy());

        for (int i = 0; i < this.settings.nRuleSets - 1; i++) {
            RuleSet first = util.binaryTournament(oldRuleSets);
            RuleSet second = util.binaryTournament(oldRuleSets);
            RuleSet newRuleSet = this.makeChildRuleSet(first, second);
            newRuleSet.setFitness(this.trainingPatterns);
            newRuleSets.add(newRuleSet);
        }

        return new Population(newRuleSets);
    }

    private RuleSet makeChildRuleSet(RuleSet first, RuleSet second) {
        RuleSet newRuleSet = new RuleSet();
        boolean doCrossover = this.random.nextFloat() < this.settings.pCrossover;
        List<Rule> firstRules = first.getRules();
        List<Rule> secondRules = second.getRules();
        for (int i = 0; i < firstRules.size(); i++) {
            int[] antecedents;
            if (doCrossover && this.random.nextFloat() < 0.5)
                antecedents = this.mutateRuleAntecedents(firstRules.get(i).deepCopy().getAntecedents());
            else
                antecedents = this.mutateRuleAntecedents(secondRules.get(i).deepCopy().getAntecedents());
            newRuleSet.addRule(this.factory.rule(antecedents));
        }

        double[] thresholds;
        if (doCrossover) {
            thresholds = thresholdCrossover(first.getRejectThresholds(), second.getRejectThresholds());
        } else {
            thresholds = second.getRejectThresholds();
        }

        if (this.random.nextDouble() < 0.25)
            thresholds = this.thresholdMutation(thresholds);
        newRuleSet.setRejectThresholds(thresholds);

        return newRuleSet;
    }

    private double[] thresholdCrossover(double[] first, double[] second) {
        double[] result = new double[first.length];
        for (int i = 0; i < first.length; i++) {
            double x1 = first[i];
            double x2 = second[i];
            double d = Math.abs(x2 - x1);
            double da = d * 0.5; // alpha = 0.5
            double min = Math.min(x1, x2) - da;
            double max = Math.max(x1, x2) + da;
            double val = this.random.nextFloat();
            val *= (max - min); // [0, (max - min)]
            val += min; // [min, max]
            val = Math.max(0.0, val);
            val = Math.min(1.0, val);
            result[i] = val;
        }
        return result;
    }

    private double[] thresholdMutation(double[] input) {
        double[] result = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            double val = this.random.nextFloat() / 10; // [0, 0.1]
            val -= 0.05; // [-0.05, 0.05]
            val += input[i];
            if (val < 0.0)
                val = 0.0;
            if (val > 1.0)
                val = 1.0;
            result[i] = val;
        }
        return result;
    }

//    public Population evolvePopulation(Population input, List<Pattern> heuristicPatterns) {
//        int size = input.getRuleSets().size();
//
////        Population newPopulation = new Population();
////        newPopulation.addRuleSet(this.evolveRuleset(input.getRuleSets().get(0), heuristicPatterns));
////
//
//        List<RuleSet> oldRuleSets = input.getRuleSets();
//        oldRuleSets.sort(Collections.reverseOrder());
//        RuleSet king = oldRuleSets.get(0);
//
//        Population newPopulation = new Population();
//        newPopulation.addRuleSet(king.deepCopy());
//
//        Util util = new Util();
//        for (int i = 1; i < size; i++) {
//            RuleSet first = util.binaryTournament(oldRuleSets);
//            RuleSet second = util.binaryTournament(oldRuleSets);
//            RuleSet newRuleSet = this.makeChildRuleSet(first, second);
//            newRuleSet.setFitness(this.trainingPatterns);
//            if (this.random.nextFloat() < Settings.HYBRID_MICHIGAN_RATE)
//                newRuleSet = this.evolveRuleset(newRuleSet, heuristicPatterns);
//            newPopulation.addRuleSet(newRuleSet);
//        }
//
//        return newPopulation;
//    }
//
//    public RuleSet evolveRuleset(RuleSet input, List<Pattern> heuristicPatterns) {
//        List<Rule> rules = input.getRules();
//
//        rules.sort(Collections.reverseOrder());
//        RuleSet newRules = new RuleSet();
//
//        Util util = new Util();
//        int numKeep = Settings.RULES_PER_RULESET - Settings.RULES_TO_REPLACE;
//        int numHeuristicRules = Math.min(Settings.RULES_TO_REPLACE / 2, heuristicPatterns.size());
//        int numGeneticRules = Settings.RULES_TO_REPLACE - numHeuristicRules;
//
//        for (int i = 0; i < numKeep; i++)
//            newRules.addRule(rules.get(i).deepCopy());
//
//        for (int i = 0; i < numGeneticRules; i++) {
//            Rule first = util.binaryTournament(rules);
//            Rule second = util.binaryTournament(rules);
//            newRules.addRule(this.makeChildRule(first, second));
//        }
//
//        Set<Integer> selectedIndices = new HashSet<>();
//        for (int i = 0; i < numHeuristicRules; i++) {
//            int index;
//            do {
//                index = this.random.nextInt(heuristicPatterns.size());
//            } while (selectedIndices.contains(index));
//            selectedIndices.add(index);
//            newRules.addRule(this.factory.heuristicRule(heuristicPatterns.get(index)));
//        }
//
//        newRules.setFitness(this.trainingPatterns);
//
//        return newRules;
//    }
//
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

        antecedents = this.mutateRuleAntecedents(antecedents);

        return this.factory.rule(antecedents);
    }

    private int[] mutateRuleAntecedents(int[] antecedents) {
        for (int i = 0; i < antecedents.length; i++) {
            if (this.random.nextFloat() < this.settings.pMutation) {
                antecedents[i] = this.random.nextInt(this.settings.nAntecedents);
            }
        }
        return antecedents;
    }

    private List<Rule> makeChildren(Rule first, Rule second) {
        int[] antecedents1 = new int[this.settings.nInputAttributes];
        int[] antecedents2 = new int[this.settings.nInputAttributes];

        if (this.random.nextFloat() < this.settings.pCrossover) {
            for (int i = 0; i < antecedents1.length; i++) {
                if (this.random.nextFloat() < 0.5) {
                    antecedents1[i] = first.getAntecedents()[i];
                    antecedents2[i] = second.getAntecedents()[i];
                } else {
                    antecedents1[i] = second.getAntecedents()[i];
                    antecedents2[i] = first.getAntecedents()[i];
                }
            }
        } else {
            System.arraycopy(first.getAntecedents(), 0, antecedents1, 0, antecedents1.length);
            System.arraycopy(second.getAntecedents(), 0, antecedents2, 0, antecedents2.length);
        }

        for (int i = 0; i < antecedents1.length; i++) {
            if (this.random.nextFloat() < this.settings.pMutation) {
                antecedents1[i] = this.random.nextInt(this.settings.nAntecedents);
            }
            if (this.random.nextFloat() < this.settings.pMutation) {
                antecedents2[i] = this.random.nextInt(this.settings.nAntecedents);
            }
        }

        List<Rule> newRules = new ArrayList<>();
        newRules.add(this.factory.rule(antecedents1));
        newRules.add(this.factory.rule(antecedents2));
        return newRules;
    }
//
//    private RuleSet makeChildRuleSet (RuleSet first, RuleSet second) {
//        if (this.random.nextFloat() < Settings.CROSSOVER_RATE) {
//            List<Rule> newRules = new ArrayList<>();
//            List<Rule> firstRules = first.getRules();
//            List<Rule> secondRules = second.getRules();
//            for (int i = 0; i < firstRules.size(); i++) {
//                if (this.random.nextFloat() < 0.5)
//                    newRules.add(mutateRule(firstRules.get(i)));
//                else
//                    newRules.add(mutateRule(secondRules.get(i)));
//            }
//            return new RuleSet(newRules);
//        } else {
//            return mutateRuleSet(first);
//        }
//    }
//
//    // Mutating every rule comprising a ruleset satisfies the definition of mutating the ruleset
//    private RuleSet mutateRuleSet(RuleSet input) {
//        RuleSet newRuleSet = new RuleSet();
//        for (Rule rule : input.getRules()) {
//            newRuleSet.addRule(this.mutateRule(rule));
//        }
//        return newRuleSet;
//    }
//
//    private Rule mutateRule(Rule input) {
//        int length = input.getAntecedents().length;
//        int[] antecedents = new int[length];
//        for (int i = 0; i < length; i++) {
//            int newAntecedent;
//            if (this.random.nextFloat() < Settings.MUTATION_RATE) {
//                newAntecedent = this.random.nextInt(Settings.NUM_ANTECEDENT_SETS);
//            } else {
//                newAntecedent = input.getAntecedents()[i];
//            }
//            antecedents[i] = newAntecedent;
//        }
//        return this.factory.rule(antecedents);
//    }
}
