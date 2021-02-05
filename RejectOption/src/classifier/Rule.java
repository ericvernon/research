package classifier;

public class Rule implements Comparable<Rule> {
    public static final int REJECTED_CLASS_LABEL = -1;

    private final int[] antecedents;
    private double rejectThreshold;
    private final int classLabel;
    private final double confidence; // CF in the literature
    private int fitness;

    public Rule(int[] antecedents, double rejectThreshold, int classLabel, double confidence) {
        this(antecedents, rejectThreshold, classLabel, confidence, 0);
    }

    public Rule(int[] antecedents, double rejectThreshold, int classLabel, double confidence, int fitness) {
        this.antecedents = antecedents;
        this.rejectThreshold = rejectThreshold;
        this.classLabel = classLabel;
        this.confidence = confidence;
        this.fitness = fitness;
    }

    public Rule deepCopy() {
        int[] antecedents  = new int[this.antecedents.length];
        System.arraycopy(this.antecedents, 0, antecedents, 0, antecedents.length);
        return new Rule(antecedents, this.rejectThreshold, this.classLabel, this.confidence, this.fitness);
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

    public double getRejectThreshold() {
        return this.rejectThreshold;
    }

    public void setRejectThreshold(double threshold) {
        this.rejectThreshold = threshold;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int antecedent : this.antecedents) {
            sb.append(antecedent).append(',');
        }
        sb.append(this.getClassLabel()).append(',').append(this.getConfidence());
        return sb.toString();
    }
}
