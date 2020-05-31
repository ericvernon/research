package classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {
    private List<RuleSet> ruleSets;

    public Population() {
        this.ruleSets = new ArrayList<>();
    }

    public Population(List<RuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public void addRuleSet(RuleSet ruleSet) {
        this.ruleSets.add(ruleSet);
    }

    public List<RuleSet> getRuleSets() {
        return this.ruleSets;
    }

    public void setFitness(List<Pattern> patterns) {
        for (RuleSet ruleSet : this.ruleSets) {
            ruleSet.setFitness(patterns);
        }
    }

    public int classify(Pattern pattern) {
        this.ruleSets.sort(Collections.reverseOrder());
        return this.ruleSets.get(0).classify(pattern);
    }
}
