package classifier;

public class Pattern {
    public final double[] input;
    public final int classLabel;
    public Pattern(double[] inputValues, int classLabel) {
        this.input = inputValues;
        this.classLabel = classLabel;
    }
}
