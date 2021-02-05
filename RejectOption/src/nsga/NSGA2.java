package nsga;

import classifier.RuleSet;

import java.util.*;

public class NSGA2<T extends NSGASortable> {
    public static final int FIRST_DOMINATES = 1;
    public static final int SECOND_DOMINATES = -1;
    public static final int NEITHER_DOMINATES = 0;

    private int dimensions;

    private Evaluator<T> evaluator;
    private int cacheCapacity = 400;
    private Map<T, double[]> cache;


    /**
     * Create a new NSGA2 sorting machine.
     * @param dimensions The list of dimensions.  FALSE represents minimization, while TRUE represents maximization.
     */
    public NSGA2(int dimensions, Evaluator<T> evaluator) {
        this.dimensions = dimensions;
        this.evaluator = evaluator;
        this.cache = new LinkedHashMap<T, double[]>(this.cacheCapacity, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry entry) {
                return this.size() > cacheCapacity;
            }
        };
    }

    /**
     * Calculate the pareto rank and crowding distance of each ruleset.
     * This method will update the pareto rank and crowding distances of the input.
     */
    public void solve(List<T> points) {
        int nPoints = points.size();
        // For each solution, count how many times it is dominated by other solutions
        Map<T, Integer> dominationCount = new HashMap<>();
        // For each solution, track the other solutions which it dominates
        Map<T, Set<T>> dominatedSolutions = new HashMap<>();

        for (T point : points) {
            point.setParetoRank(0);
            point.setCrowdingDistance(0);

            dominationCount.put(point, 0);
            dominatedSolutions.put(point, new HashSet<>());
        }

        // This implements the 'fast-non-dominated-sort' pseudocode found in the NSGA2 paper, almost verbatim
        // The main difference is that we must calculate the crowding distance for all solutions (because it is
        // used in the binary tournament selection), so each front is tracked explicitly.

        List<T> f1 = new LinkedList<>();
        // For each point, find which points it dominates, and which it is dominated by
        long time = System.nanoTime();
        for (int i = 0; i < nPoints; i++) {
            T first = points.get(i);

            for (int j = 0; j < nPoints; j++) {
                if (i == j) continue;
                T second = points.get(j);
                int dom = this.dominates(first, second);
                if (dom == FIRST_DOMINATES) {
                    dominatedSolutions.get(first).add(second);
                } else if (dom == SECOND_DOMINATES) {
                    int val = dominationCount.get(first);
                    dominationCount.put(first, val + 1);
                }
            }

            if (dominationCount.get(first) == 0) {
                first.setParetoRank(1);
                f1.add(first);
            }
        }

        int i = 1;
        List<List<T>> fronts = new ArrayList<>();
        List<T> current = f1;
        List<T> next;
        while (!current.isEmpty()) {
            next = new LinkedList<>();
            for (T sample : current) {
                for (T dominated : dominatedSolutions.get(sample)) {
                    int val = dominationCount.get(dominated);
                    dominationCount.put(dominated, val - 1);
                    if (val - 1 == 0) {
                        dominated.setParetoRank(i + 1);
                        next.add(dominated);
                    }
                }
            }
            fronts.add(current);
            current = next;
            i++;
        }

        // Set the crowding distance for each front
        for (List<T> front : fronts) {
            for (i = 0; i < dimensions; i++) {
                int frontSize = front.size();
                final int finalI = i;
                front.sort(Comparator.comparingDouble(a -> this.cacheGet(a)[finalI]));

                double fMin = this.cacheGet(front.get(0))[i];
                front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);

                double fMax = this.cacheGet(front.get(frontSize - 1))[i];
                front.get(frontSize - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

                for (int j = 1; j < frontSize - 1; j++) {
                    double distance;
                    if (fMax == fMin) {
                        distance = 0;
                    } else {
                        distance = (this.cacheGet(front.get(j + 1))[i] - this.cacheGet(front.get(j - 1))[i])
                                        / (fMax - fMin);
                    }
                    front.get(j).addCrowdingDistance(distance);
                }
            }
        }
    }

    public int dominates(T first, T second) {
        boolean firstBetter = false;
        boolean secondBetter = false;
        double[] firstObj = this.cacheGet(first);
        double[] secondObj = this.cacheGet(second);
        for (int i = 0; i < this.dimensions; i++) {
            double val1 = firstObj[i];
            double val2 = secondObj[i];
            if (val1 > val2) {
                // Second is better.  If the first one was previously better, neither can possibly dominate the other.
                if (firstBetter)
                    return NEITHER_DOMINATES;
                secondBetter = true;
            }
            else if (val1 < val2) {
                if (secondBetter)
                    return NEITHER_DOMINATES;
                firstBetter = true;
            }
        }

        // If either 'better' flag is set, that solution must be dominant
        // If neither flag is set, the vectors must be equal
        if (firstBetter)
            return FIRST_DOMINATES;
        else
            return secondBetter ? SECOND_DOMINATES : NEITHER_DOMINATES;
    }

    private double[] cacheGet(T obj) {
        if (!this.cache.containsKey(obj)) {
            this.cache.put(obj, this.evaluator.evaluate(obj));
            obj.setObjectives(this.evaluator.evaluate(obj));
        }
        return this.cache.get(obj);
    }
}
