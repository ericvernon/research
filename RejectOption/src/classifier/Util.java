package classifier;

import random.MersenneTwister;
import random.MersenneTwisterFast;

import java.util.*;

public class Util {
    private final MersenneTwisterFast random;

    public Util() {
        this.random = new MersenneTwisterFast();
    }

    public Util(MersenneTwisterFast r) {
        this.random = r;
    }

    /**
     * Perform a binary tournament with replacement.  This assumes better objects come first in natural orderings.
     * (i.e., '1.0f' should defeat '2.0f' in the tournament.)
     * @param args A list of comparable objects to perform the tournament on
     * @param <T> Any comparable type
     * @return The winner of the tournament
     */
    public <T extends Comparable<T>> T binaryTournament(List<T> args) {
        T first = args.get(random.nextInt(args.size()));
        T second = args.get(random.nextInt(args.size()));
        if (first.compareTo(second) < 0)
            return first;
        else
            return second;
    }

    public <T> List<T> randomSubset(List<T> input, int size) {
        if (size > input.size()) {
            throw new IllegalArgumentException("Subset size cannot be greater than the original set size");
        }
        Set<Integer> selectedIndices = new HashSet<>();
        List<T> output = new ArrayList<>();
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
