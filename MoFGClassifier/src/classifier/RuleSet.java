package classifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pittsburgh-style population of rules
 */
public class RuleSet implements Comparable<RuleSet> {
    private final List<Rule> rules;
    private int fitness;

    public RuleSet() {
        this(new ArrayList<>());
    }

    public RuleSet(List<Rule> rules) {
        this.rules = rules;
        this.fitness = 0;
    }

    public int classify(Pattern pattern) {
        Rule winningRule = this.getWinningRule(pattern);
        if (winningRule == null)
            return Rule.REJECTED_CLASS_LABEL;
        return winningRule.getClassLabel();
    }

    public Rule getWinningRule(Pattern pattern) {
        FuzzyCalculator fuzzyCalculator = FuzzyCalculator.getInstance();
        Rule bestRule = null;
        double bestScore = 0;

        for (Rule rule : this.rules) {
            double score = rule.getConfidence() * fuzzyCalculator.calculateCompatibility(rule, pattern);
            if (score > bestScore) {
                bestScore = score;
                bestRule = rule;
            } else if (score == bestScore && bestRule != null && bestRule.getClassLabel() != rule.getClassLabel()) {
                bestRule = null;
            }
        }

        return bestRule;
    }

    public void setFitness(List<Pattern> patterns) {
        this.fitness = 0;
        for (Rule rule : this.rules)
            rule.setFitness(0);
        for (Pattern pattern : patterns) {
            Rule winningRule = this.getWinningRule(pattern);
            if (winningRule != null && winningRule.getClassLabel() == pattern.classLabel) {
                winningRule.incrementFitness();
                this.fitness++;
            }
        }
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public int getFitness() {
        return this.fitness;
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    // This implementation is *not* considered consistent with equals.
    @Override
    public int compareTo(RuleSet o) {
        return this.fitness - o.fitness;
    }

    public RuleSet deepCopy() {
        RuleSet copy = new RuleSet();
        for (Rule rule : this.rules) {
            copy.addRule(rule.deepCopy());
        }
        copy.overrideFitness(this.fitness);
        return copy;
    }

    private void overrideFitness(int value) {
        this.fitness = value;
    }

}
