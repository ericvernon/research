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

    public enum RejectStrategies {
        PER_CLASS, SINGLE_VARIABLE, STATIC,
    }
    public RejectStrategies rejectStrategy;
    public double rejectThreshold;
    public double pMutationThreshold;

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

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (!field.getName().endsWith("Data"))
                    buffer.append(field.getName()).append(" - ").append(field.get(this)).append("\n");
            }
        } catch (Exception ex) {
            buffer.append(ex.getMessage());
        }
        return buffer.toString();
    }
}
