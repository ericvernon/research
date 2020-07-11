package classifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Represents a Pittsburgh-style population of rules
 */
public class RuleSet implements Comparable<RuleSet> {
    private final List<Rule> rules;
    private int fitness;
    private int rank;
    private double crowdingScore;
    private double[] objectives;
    private double[] rejectThresholds = {0.0, 0.0};

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
        PriorityQueue<ScoreToRule> queue = new PriorityQueue<>(this.rules.size(), Comparator.reverseOrder());

        for (Rule rule : this.rules) {
            double score = rule.getConfidence() * fuzzyCalculator.calculateCompatibility(rule, pattern);
            if (score <= 0)
                continue;
            ScoreToRule scoreToLabel = new ScoreToRule();
            scoreToLabel.score = score;
            scoreToLabel.rule = rule;
            queue.add(scoreToLabel);
        }

        if (queue.size() == 0)
            return null;

        ScoreToRule bestRule = queue.poll();
        ScoreToRule secondBestRule = null;

        while (!queue.isEmpty()) {
            secondBestRule = queue.poll();
            if (secondBestRule.rule.getClassLabel() != bestRule.rule.getClassLabel()) {
                break;
            }
        }

        Rule winner = bestRule.rule;
        int winnerClass = winner.getClassLabel();
        double rejectThreshold = this.rejectThresholds[winnerClass];
        if (secondBestRule != null) {
            double theta = (bestRule.score - secondBestRule.score) / bestRule.score;
            if (theta < rejectThreshold)
                winner = null;
        }

        return winner;
    }

    private static class ScoreToRule implements Comparable<ScoreToRule> {
        public double score;
        public Rule rule;

        @Override
        public int compareTo(ScoreToRule scoreToLabel) {
            return Double.compare(this.score, scoreToLabel.score);
        }
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
        if (this.rank != o.getRank()) {
            return this.rank - o.getRank();
        } else {
            return Double.compare(this.crowdingScore, o.getCrowdingScore());
        }
    }

    public RuleSet deepCopy() {
        RuleSet copy = new RuleSet();
        for (Rule rule : this.rules) {
            copy.addRule(rule.deepCopy());
        }
        copy.overrideFitness(this.fitness);
        copy.setCrowdingScore(this.getCrowdingScore());
        copy.setRank(this.getRank());
        double[] obj = new double[this.objectives.length];
        System.arraycopy(this.objectives, 0, obj, 0, this.objectives.length);
        copy.setObjectives(obj);
        double[] thresholds = new double[this.rejectThresholds.length];
        System.arraycopy(this.rejectThresholds, 0, thresholds, 0, this.rejectThresholds.length);
        copy.setRejectThresholds(thresholds);
        return copy;
    }

    public List<Pattern> getBadPatterns(List<Pattern> patterns) {
        List<Pattern> badPatterns = new ArrayList<>();
        for (Pattern pattern : patterns) {
            if (this.classify(pattern) != pattern.classLabel)
                badPatterns.add(pattern);
        }
        return badPatterns;
    }

    private void overrideFitness(int value) {
        this.fitness = value;
    }


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getCrowdingScore() {
        return crowdingScore;
    }

    public void setCrowdingScore(double crowdingScore) {
        this.crowdingScore = crowdingScore;
    }

    public double[] getObjectives() {
        return objectives;
    }

    public void setObjectives(double[] objectives) {
        this.objectives = objectives;
    }

    public double[] getRejectThresholds() {
        return rejectThresholds;
    }

    public void setRejectThresholds(double[] rejectThreshold) {
        this.rejectThresholds = rejectThreshold;
    }
}
