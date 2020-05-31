package classifier;

import java.util.Random;

public class Rule implements Comparable<Rule> {
    public static final int REJECTED_CLASS_LABEL = -1;

    private final int[] antecedents;
    private final int classLabel;
    private final double confidence; // CF in the literature
    private int fitness;

    public Rule(int[] antecedents, int classLabel, double confidence) {
        this(antecedents, classLabel, confidence, 0);
    }

    public Rule(int[] antecedents, int classLabel, double confidence, int fitness) {
        this.antecedents = antecedents;
        this.classLabel = classLabel;
        this.confidence = confidence;
        this.fitness = fitness;
    }

    public Rule deepCopy() {
        int[] antecedents  = new int[this.antecedents.length];
        System.arraycopy(this.antecedents, 0, antecedents, 0, antecedents.length);
        return new Rule(antecedents, this.classLabel, this.confidence, this.fitness);
    }

    @Override
    public int compareTo(Rule o) {
        return this.fitness - o.fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void incrementFitness() {
        this.fitness++;
    }

    public int[] getAntecedents() {
        return antecedents;
    }

    public int getClassLabel() {
        return classLabel;
    }

    public double getConfidence() {
        return confidence;
    }

    public int getFitness() {
        return this.fitness;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int antecedent : this.antecedents) {
            sb.append(antecedent).append(':');
        }
        sb.append("__").append(this.getClassLabel()).append("__").append(this.getConfidence());
        return sb.toString();
    }
}
