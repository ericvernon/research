package main;

// These are effectively global variables - not a huge fan, but it's a
public class Settings {
    public static int NUM_INPUT_ATTRIBUTES = 14;
    public static int NUM_OUTPUT_CLASSES = 2;

    public static int NUM_ANTECEDENT_SETS = 15;
    public static int POPULATION_SIZE = 150;
    public static int RULES_PER_RULESET = 30;
    public static int RULES_TO_REPLACE = 10;

    public static double HYBRID_MICHIGAN_RATE = 0.0;
    public static double CROSSOVER_RATE = 0.9;
    public static double MUTATION_RATE = 1.0 / NUM_INPUT_ATTRIBUTES;
    public static double HEURISTIC_DONT_CARE = 0.8;
    public static double HEURISTIC_MICHIGAN_RATIO = 1.0;
}
