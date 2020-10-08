package classifier;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class RuleSetWithRejection extends RuleSet {
    private final double threshold;

    public RuleSetWithRejection(List<Rule> rules, Settings settings, double threshold) {
        super(rules, settings);
        this.threshold = threshold;
    }

    public Rule getWinningRule(Pattern pattern) {
        PriorityQueue<ScoreToRule> queue = new PriorityQueue<>(this.rules.size(), Comparator.reverseOrder());

        for (Rule rule : this.rules) {
            double score = rule.getConfidence() * this.calculator.calculateCompatibility(rule, pattern);
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
        if (secondBestRule != null) {
            double theta = (bestRule.score - secondBestRule.score) / bestRule.score;
            if (theta < this.threshold)
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

}
