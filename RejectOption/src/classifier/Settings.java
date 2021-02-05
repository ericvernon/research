package classifier;

import nsga.MOP;

import java.lang.reflect.Field;
import java.util.List;

public class Settings {
    public List<Pattern> trainingData;
    public List<Pattern> testingData;
    public int nInputAttributes;
    public int nOutputClasses;
    public int nAntecedents;
    public int nRuleSets;
    public int nRulesInitial;
    public int nRulesMax;
    public double pCrossover;
    public double pMutation;
    public double pHybridMichigan;
    public double pDontCareHeuristicRule;
    public double michiganNReplace;
    public int nGenerations;
    public int rulesetMinRules;

    /*
     * STATIC: A single, user specified reject threshold is used which never changes
     * SINGLE_VARIABLE: Each ruleset evolves a single variable to serve as the reject threshold
     * PER_CLASS: Each ruleset evolves a c-length vector, where each entry represents the reject threshold for class c
     * PER_RULE: Each rule contains a threshold which can be evolved in addition to its antecedents
     *
     * The reject method is applied in the RuleSet.getWinningRule method.  To simplify the design (and increase
     * the explanation / maintenance cost ;)), each Ruleset always evolves a c-length vector.  In the SINGLE_VARIABLE
     * case, only the first entry is used and the rest ignored.  In all other cases, the vector is ignored entirely.
     * Similarly, Rules always evolve a reject threshold, but that is only used when PER_RULE is set.
     */
    public enum RejectStrategies {
        PER_CLASS, SINGLE_VARIABLE, STATIC, PER_RULE,
    }
    public RejectStrategies rejectStrategy;
    public double rejectThreshold;
    public double pMutationThreshold;

    // These are comments and (should) have no effect on the actual algorithm.
    // They should be set by whatever code is instantiating and running the classifier.
    public String mopName;
    public String comment;
    public String dataset;

    public Settings setNInputAttributes(int nInputAttributes) {
        this.nInputAttributes = nInputAttributes;
        return this;
    }

    public Settings setNAntecedents(int nAntecedents) {
        this.nAntecedents = nAntecedents;
        return this;
    }

    public Settings setNOutputClasses(int nOutputClasses) {
        this.nOutputClasses = nOutputClasses;
        return this;
    }

    public Settings setTrainingData(List<Pattern> trainingData) {
        this.trainingData = trainingData;
        return this;
    }

    public Settings setTestingData(List<Pattern> testingData) {
        this.testingData = testingData;
        return this;
    }

    public Settings setNRuleSets(int nRuleSets) {
        this.nRuleSets = nRuleSets;
        return this;
    }

    public Settings setNRuleInitial(int nRules) {
        this.nRulesInitial = nRules;
        return this;
    }

    public Settings setNRulesMax(int nRules) {
        this.nRulesMax = nRules;
        return this;
    }

    public Settings setPCrossover(double pCrossover) {
        this.pCrossover = pCrossover;
        return this;
    }

    public Settings setPMutation(double pMutation) {
        this.pMutation = pMutation;
        return this;
    }

    public Settings setPHybridMichigan(double pHybridMichigan) {
        this.pHybridMichigan = pHybridMichigan;
        return this;
    }

    public Settings setMichiganNReplace(double michiganNReplace) {
        this.michiganNReplace = michiganNReplace;
        return this;
    }

    public Settings setPDontCareHeuristicRule(double pDontCareHeuristicRule) {
        this.pDontCareHeuristicRule = pDontCareHeuristicRule;
        return this;
    }

    public Settings setRejectStrategy(RejectStrategies strategy) {
        this.rejectStrategy = strategy;
        return this;
    }
    public Settings setRejectThreshold(double rejectThreshold) {
        this.rejectThreshold = rejectThreshold;
        return this;
    }
    public Settings setPMutationThreshold(double pMutation) {
        this.pMutationThreshold = pMutation;
        return this;
    }

    public Settings setNGenerations(int nGenerations) {
        this.nGenerations = nGenerations;
        return this;
    }

    public Settings setRulesetMinRules(int nRules) {
        this.rulesetMinRules = nRules;
        return this;
    }

    public Settings setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Settings setDatasetName(String datasetName) {
        this.dataset = datasetName;
        return this;
    }

    public Settings setMopName(String mopName) {
        this.mopName = mopName;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (!field.getName().endsWith("Data"))
                    buffer.append(field.getName()).append(" - ").append(field.get(this)).append("\n");
            }
            buffer.append("# Training - ").append(this.trainingData.size()).append("\n");
            buffer.append("# Testing - ").append(this.testingData.size()).append("\n");
        } catch (Exception ex) {
            buffer.append(ex.getMessage());
        }
        return buffer.toString();
    }
}
