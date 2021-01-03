package nsga;

public abstract class Evaluator<T extends NSGASortable> {
    public double[] evaluate(T value) {
        return this.evaluate(value, true);
    }
    public abstract double[] evaluate(T value, boolean trainingData);
}
