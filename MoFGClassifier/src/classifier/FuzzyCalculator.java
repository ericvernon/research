package classifier;

import java.util.List;

public class FuzzyCalculator {

    public static int DONT_CARE = 0;
    private static FuzzyCalculator fuzzyCalculator;

    public FuzzyCalculator() {
    }

    public static FuzzyCalculator getInstance() {
        if (fuzzyCalculator == null)
            fuzzyCalculator = new FuzzyCalculator();
        return fuzzyCalculator;
    }

    public double calculateCompatibility(Rule rule, Pattern pattern) {
        return this.calculateCompatibility(rule.getAntecedents(), pattern);
    }

    public double calculateCompatibility(int[] antecedents, Pattern pattern) {
        double score = 1.0;
        for (int i = 0; i < antecedents.length; i++)
            score *= this.calculateMembershipValue(pattern.inputValues[i], antecedents[i]);
        return score;
    }

    /**
     * Calculate the membership of a certain value within a fuzzy set.
     * Currently, we assume the standard triangular shape of the triangle sets as described in the work published
     * by Dr. Ishibuchi and Dr. Nojima.
     * @param value The value to test
     * @param fuzzySet The fuzzy set to test membership into.  Must be a positive integer.
     * @return The membership value
     */
    public double calculateMembershipValue(double value, int fuzzySet) {
        if (fuzzySet == DONT_CARE)
            return 1.0;

        double littleK = this.getOrderOfInterval(fuzzySet);
        double bigK = this.getNumberIntervals(fuzzySet);
        double a = (littleK - 1) / (bigK - 1);
        double b = 1.0 / (bigK - 1);
        double membership = 1.0 - ( (Math.abs(a - value)) / b);
        return Math.max(membership, 0.0);
    }

    /**
     * Use the standard set of triangular membership functions with varying granularities.  We use multiple partitions
     * of 2, 3, 4, and 5 (or more) triangles simultaneously.  This method calculates the number of partitions in the
     * hyperspace which a given fuzzy set label belongs to.
     * @param fuzzySet The fuzzy set label to test
     * @return The number of fuzzy triangles which exist alongside that class label
     */
    private int getNumberIntervals(int fuzzySet) {
        int numberTrianglesInSet = 1;
        int totalSetsChecked = 0;
        while (fuzzySet > totalSetsChecked) {
            numberTrianglesInSet++;
            totalSetsChecked += numberTrianglesInSet;
        }
        return numberTrianglesInSet;
    }

    /**
     * Calculate the index of the fuzzy set within its own hyperspace.  For example, if we assume three triangles,
     * "small", "medium", and "large", "medium" would be considered the 2nd set.
     * The result is one-indexed.
     * @param fuzzySet The fuzzy set label to test
     * @return The one-indexed position of the fuzzy set within its hyperspace
     */
    private int getOrderOfInterval(int fuzzySet) {
        int previousSets = 0;
        int setsInCurrentSpace = 2;

        while (fuzzySet > previousSets + setsInCurrentSpace) {
            previousSets += setsInCurrentSpace;
            setsInCurrentSpace++;
        }

        return fuzzySet - previousSets;
    }

}
