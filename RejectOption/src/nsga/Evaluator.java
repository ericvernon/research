package nsga;

public interface Evaluator<T extends NSGASortable> {
    public double[] evaluate(T value);
}
