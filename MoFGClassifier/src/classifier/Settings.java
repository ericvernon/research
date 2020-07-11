package classifier;

import java.util.List;

public class Settings {
    public List<Pattern> trainingPatterns;
    public int nRules;
    public int nRuleSets;
    public int nAntecedents;
    public int nInputAttributes;
    public int nOutputClasses;
    public int nReplace;
    public double pCrossover;
    public double pMutation;
    public double pDontCare;
    public double pHybridMichigan;

    public Settings setNRules(int num) {
        this.nRules = num;
        return this;
    }

    public Settings setNRuleSets(int num) {
        this.nRuleSets = num;
        return this;
    }

    public Settings setNAntecedents(int num) {
        this.nAntecedents = num;
        return this;
    }

    public Settings setNInputAttributes(int num) {
        this.nInputAttributes = num;
        return this;
    }

    public Settings setNOutputClasses(int num) {
        this.nOutputClasses = num;
        return this;
    }

    public Settings setNReplace(int num) {
        this.nReplace = num;
        return this;
    }

    public Settings setPCrossover(double p) {
        this.pCrossover = p;
        return this;
    }

    public Settings setPMutation(double p) {
        this.pMutation = p;
        return this;
    }

    public Settings setPDontCare(double p) {
        this.pDontCare = p;
        return this;
    }

    public Settings setTrainingPatterns(List<Pattern> patterns) {
        this.trainingPatterns = patterns;
        return this;
    }

    public Settings setPHybridMichigan(double p) {
        this.pHybridMichigan = p;
        return this;
    }
}
