package classifier;

import random.MersenneTwister;

import java.util.List;
import java.util.Random;

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
}
