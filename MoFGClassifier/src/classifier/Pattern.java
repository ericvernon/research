package classifier;

public class Pattern {
    public final double[] inputValues;
    public final int classLabel;
    public Pattern(double[] inputValues, int classLabel) {
        this.inputValues = inputValues;
        this.classLabel = classLabel;
    }
}
