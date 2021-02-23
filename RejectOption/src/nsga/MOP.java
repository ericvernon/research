package nsga;

public abstract class MOP<T extends NSGASortable> {
    public int nObjectives;
    public abstract String[] getObjectiveNames();
    public abstract Evaluator<T> getEvaluator();
}
