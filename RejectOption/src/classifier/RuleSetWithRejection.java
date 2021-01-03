//package classifier;
//
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//import java.util.PriorityQueue;
//
//public class RuleSetWithRejection extends RuleSet {
//    private final double threshold;
//
//    public RuleSetWithRejection(List<Rule> rules, Settings settings, double threshold) {
//        super(rules, settings);
//        this.threshold = threshold;
//    }
//
//    public Rule getWinningRule(Pattern pattern) {
//        ScoreToRule[] bestScores = new ScoreToRule[this.settings.nOutputClasses];
//        // Seed one entry for each output class, because Arrays.sort will choke on null values
//        // (It's rare but technically possible for a class to be unsupported.)
//        for (int i = 0; i < this.settings.nOutputClasses; i++)
//            bestScores[i] = new ScoreToRule(0.0, null);
//        bestScores[0] = new ScoreToRule(0.0, null);
//        bestScores[1] = new ScoreToRule(0.0, null);
//        for (Rule rule : this.rules) {
//            int label = rule.getClassLabel();
//            double score = rule.getConfidence() * this.calculator.calculateCompatibility(rule, pattern);
//            if (score > bestScores[label].score) {
//                bestScores[label] = new ScoreToRule(score, rule);
//            }
//        }
//        Arrays.sort(bestScores, Comparator.reverseOrder());
//        ScoreToRule bestRule = bestScores[0];
//        ScoreToRule secondBestRule = bestScores[1];
//
//        Rule winner = bestRule.rule;
//        if (secondBestRule != null) {
//            double theta = (bestRule.score - secondBestRule.score) / bestRule.score;
//            if (theta < this.threshold)
//                winner = null;
//        }
//
//        return winner;
//    }
//
//    private static class ScoreToRule implements Comparable<ScoreToRule> {
//        public double score;
//        public Rule rule;
//
//        public ScoreToRule(double score, Rule rule) {
//            this.score = score;
//            this.rule = rule;
//        }
//
//        @Override
//        public int compareTo(ScoreToRule scoreToLabel) {
//            return Double.compare(this.score, scoreToLabel.score);
//        }
//    }
//
//}
