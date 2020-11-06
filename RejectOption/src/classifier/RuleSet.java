package classifier;

import nsga.MOP;
import nsga.NSGASortable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pittsburgh-style population of rules
 */
public class RuleSet implements Comparable<RuleSet>, NSGASortable {
    protected final List<Rule> rules;
    protected final Settings settings;
    protected final FuzzyCalculator calculator;

    // Stores information about the MOP and the pareto front.
    protected int rank;
    protected double crowdingScore;
    protected double[] objectives;

    public RuleSet(List<Rule> rules, Settings settings) {
        this.rules = rules;
        this.settings = settings;
        this.calculator = new FuzzyCalculator();
    }

    public int classify(Pattern pattern) {
        Rule winningRule = this.getWinningRule(pattern);
        if (winningRule == null || winningRule.getConfidence() == Rule.REJECTED_CLASS_LABEL)
            return Rule.REJECTED_CLASS_LABEL;
        return winningRule.getClassLabel();
    }

    // Get the winning rule for a pattern, or null if no pattern has a weight * compatibility product greater than 0.
    public Rule getWinningRule(Pattern pattern) {
        double bestScore = 0.0;
        Rule bestRule = null;
        for (Rule rule : this.rules) {
            double score = rule.getConfidence() * this.calculator.calculateCompatibility(rule, pattern);
            if (score > bestScore) {
                bestScore = score;
                bestRule = rule;
            }
        }

        return bestRule;
    }

    // Update the fitness of the rules based to prepare for a Michigan evolution
    // This is not done when the ruleset is initialized because not every ruleset will undergo Michigan evolution
    public void setRuleFitness() {
        for (Rule rule: this.rules)
            rule.setFitness(0);
        for (Pattern pattern : this.settings.trainingData) {
            Rule winner = this.getWinningRule(pattern);
            if (winner != null) {
                winner.incrementFitness();
            }
        }
    }

    // This implementation is *not* considered consistent with equals.
    @Override
    public int compareTo(RuleSet o) {
        if (this.rank != o.getParetoRank()) {
            // Lower rank is better
            return this.rank - o.getParetoRank();
        } else {
            // Higher distance is better
            return -1 * Double.compare(this.crowdingScore, o.getCrowdingDistance());
        }
    }

    public List<Pattern> getBadPatterns() {
        List<Pattern> badPatterns = new ArrayList<>();
        for (Pattern pattern : this.settings.trainingData) {
            if (this.classify(pattern) != pattern.classLabel)
                badPatterns.add(pattern);
        }
        return badPatterns;
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingScore = crowdingDistance;
    }

    public void addCrowdingDistance(double crowdingDistance) {
        this.crowdingScore += crowdingDistance;
    }

    public void setParetoRank(int paretoRank) {
        this.rank = paretoRank;
    }

    public double getCrowdingDistance() {
        return this.crowdingScore;
    }

    public int getParetoRank() {
        return this.rank;
    }

    public void setObjectives(double[] objectives) { this.objectives = objectives; }

    public double[] getObjectives() {
        return this.objectives;
    }
}
