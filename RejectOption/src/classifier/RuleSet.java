package classifier;

import nsga.MOP;
import nsga.NSGASortable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a Pittsburgh-style population of rules
 */
public class RuleSet implements Comparable<RuleSet>, NSGASortable {
    protected final List<Rule> rules;
    protected final Settings settings;
    protected final FuzzyCalculator calculator;

    protected double[] rejectThresholds;

    // Stores information about the MOP and the pareto front.
    protected int rank;
    protected double crowdingScore;
    protected double[] objectives;

    public RuleSet(List<Rule> rules, double[] rejectThresholds, Settings settings) {
        this.rules = rules;
        this.rejectThresholds = rejectThresholds;
        this.settings = settings;
        this.calculator = new FuzzyCalculator();
    }

    public int classify(Pattern pattern) {
        Rule winningRule = this.getWinningRule(pattern);
        if (winningRule == null || winningRule.getClassLabel() == Rule.REJECTED_CLASS_LABEL)
            return Rule.REJECTED_CLASS_LABEL;
        return winningRule.getClassLabel();
    }

    // Get the winning rule for a pattern, or null if no pattern has a weight * compatibility product greater than 0.
    public Rule getWinningRule(Pattern pattern) {
        ScoreToRule[] bestScores = new ScoreToRule[this.settings.nOutputClasses];
        // Seed one entry for each output class, because Arrays.sort will choke on null values
        // (It's rare but technically possible for a class to be unsupported.)
        for (int i = 0; i < this.settings.nOutputClasses; i++)
            bestScores[i] = new ScoreToRule(0.0, null);
        for (Rule rule : this.rules) {
            int label = rule.getClassLabel();
            // Rules can sometimes have the rejected class label assigned (e.g. if zero weight) - just skip these
            if (label == Rule.REJECTED_CLASS_LABEL)
                continue;
            double score = rule.getConfidence() * this.calculator.calculateCompatibility(rule, pattern);
            if (score > bestScores[label].score) {
                bestScores[label] = new ScoreToRule(score, rule);
            }
        }
        Arrays.sort(bestScores, Comparator.reverseOrder());
        ScoreToRule bestRule = bestScores[0];
        ScoreToRule secondBestRule = bestScores[1];

        Rule winner = bestRule.rule;
        double threshold = this.lookupThreshold(winner);
        if (secondBestRule != null) {
            double theta = (bestRule.score - secondBestRule.score) / bestRule.score;
            if (theta < threshold)
                winner = null;
        }

        return winner;
    }

    private double lookupThreshold(Rule winner) {
        // This can happen if no rule has a positive matching degree with the pattern
        if (winner == null)
            return 0.0;
        if (this.settings.rejectStrategy == Settings.RejectStrategies.STATIC)
            return this.settings.rejectThreshold;
        if (this.settings.rejectStrategy == Settings.RejectStrategies.SINGLE_VARIABLE)
            return this.rejectThresholds[0];
        if (this.settings.rejectStrategy == Settings.RejectStrategies.PER_CLASS)
            return this.rejectThresholds[winner.getClassLabel()];
        if (this.settings.rejectStrategy == Settings.RejectStrategies.PER_RULE)
            return winner.getRejectThreshold();
        System.out.println("Error!  Unknown reject strategy used.");
        return 0.0;
    }

    // Update the fitness of the rules based to prepare for a Michigan evolution
    // This is done when the ruleset is initialized because not every ruleset will undergo Michigan evolution
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

    public void removeZeroFitnessRules() {
        this.setRuleFitness();
        this.rules.removeIf(rule -> rule.getFitness() == 0);
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    // This implementation is *not* considered consistent with equals.
    // TODO - Pull this into a new function that's not the default comparator, since it is becoming an amalgam
    @Override
    public int compareTo(RuleSet o) {
        if (this.getRules().size() < this.settings.rulesetMinRules
                && o.getRules().size() >= this.settings.rulesetMinRules)
            return 1;
        else if (this.getRules().size() >= this.settings.rulesetMinRules
                && o.getRules().size() < this.settings.rulesetMinRules)
            return -1;

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

    public double[] getRejectThresholds() {
        return this.rejectThresholds;
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

    private static class ScoreToRule implements Comparable<ScoreToRule> {
        public double score;
        public Rule rule;

        public ScoreToRule(double score, Rule rule) {
            this.score = score;
            this.rule = rule;
        }

        @Override
        public int compareTo(ScoreToRule scoreToLabel) {
            return Double.compare(this.score, scoreToLabel.score);
        }
    }
}
