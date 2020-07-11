package nsga;

import classifier.RuleSet;

import java.util.*;

public class NSGA2 {
    private MOP mop;

    public NSGA2(MOP mop) {
        this.mop = mop;
    }

    public List<RuleSet> solve(List<RuleSet> ruleSets) {
        int nPoints = ruleSets.size();
        Map<RuleSet, Integer> dominatedBy = new HashMap<>();
        Map<RuleSet, Set<RuleSet>> dominatedSamples = new HashMap<>();

        Queue<RuleSet> f1 = new LinkedList<>();
        for (RuleSet ruleSet : ruleSets) {
            ruleSet.setObjectives(this.mop.evaluate(ruleSet));
        }

        for (int i = 0; i < nPoints; i++) {
            RuleSet first = ruleSets.get(i);
            dominatedBy.put(first, 0);
            dominatedSamples.put(first, new HashSet<>());

            for (int j = 0; j < nPoints; j++) {
                if (i == j) continue;
                RuleSet second = ruleSets.get(j);

                if (this.dominates(first, second)) {
                    dominatedSamples.get(first).add(second);
                } else if (this.dominates(second, first)) {
                    int val = dominatedBy.get(first);
                    dominatedBy.put(first, val + 1);
                }
            }

            if (dominatedBy.get(first) == 0) {
                first.setRank(1);
                f1.add(first);
            }
        }

        int i = 1;
        Queue<RuleSet> q;
        while (!f1.isEmpty()) {
            q = new LinkedList<>();
            for (RuleSet sample : f1) {
                for (RuleSet dominated : dominatedSamples.get(sample)) {
                    int val = dominatedBy.get(dominated);
                    dominatedBy.put(dominated, val - 1);
                    if (val - 1 == 0) {
                        dominated.setRank(i + 1);
                        q.add(dominated);
                    }
                }
            }
            f1 = q;
            i++;
        }

        this.setCrowdingDistance(ruleSets);

        ruleSets.sort((obj1, obj2) -> {
            if (obj1.getRank() != obj2.getRank()) {
                return obj1.getRank() - obj2.getRank();
            } else {
                return -1 * Double.compare(obj1.getCrowdingScore(), obj2.getCrowdingScore());
            }
        });

        return ruleSets;
    }

    private void setCrowdingDistance(List<RuleSet> samples) {
        int nSamples = samples.size();
        if (nSamples == 0)
            return;

        int nDimensions = this.mop.nObjectives;

        for (RuleSet sample : samples) {
            sample.setCrowdingScore(0.0);
        }

        for (int i = 0; i < nDimensions; i++) {
            final int finalI = i;
            samples.sort(Comparator.comparingDouble(a -> a.getObjectives()[finalI]));

            double fMin = samples.get(0).getObjectives()[i];
            samples.get(0).setCrowdingScore(99999.99);

            double fMax = samples.get(nSamples - 1).getObjectives()[i];
            samples.get(nSamples - 1).setCrowdingScore(99999.99);

            for (int j = 1; j < nSamples - 1; j++) {
                double distance = (samples.get(j + 1).getObjectives()[i] - samples.get(j - 1).getObjectives()[i])
                                    / (fMax - fMin);
                double current = samples.get(j).getCrowdingScore();
                samples.get(j).setCrowdingScore(current + distance);
            }
        }
    }

    // Return true if first dominates second, otherwise false
    private boolean dominates(RuleSet first, RuleSet second) {
        int nAttributes = this.mop.nObjectives;
        boolean betterFlag = false;
        for (int i = 0; i < nAttributes; i++) {
            double val1 = first.getObjectives()[i];
            double val2 = second.getObjectives()[i];
            if (val1 > val2)
                return false;
            else if (val1 < val2)
                betterFlag = true;
        }
        return betterFlag;
    }
}
