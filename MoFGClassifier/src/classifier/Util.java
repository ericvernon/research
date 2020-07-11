package classifier;

import random.MersenneTwister;

import java.util.*;

public class Util {
    private Random random;

    public Util() {
        this.random = new MersenneTwister();
    }

    public Util(Random r) {
        this.random = r;
    }

    public <T extends Comparable<T>> T binaryTournament(List<T> args) {
        T first = args.get(random.nextInt(args.size()));
        T second = args.get(random.nextInt(args.size()));
        if (first.compareTo(second) >= 0)
            return first;
        else
            return second;
    }

    public <T> List<T> randomSubset(List<T> input, int size) {
        if (size > input.size()) {
            throw new IllegalArgumentException("Subset size cannot be greater than the original set size");
        }
        Set<Integer> selectedIndices = new HashSet<>();
        List<T> output = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            int index;
            do {
                index = this.random.nextInt(input.size());
            } while (selectedIndices.contains(index));
            selectedIndices.add(index);
            output.add(input.get(index));
        }
        return output;
    }
}
