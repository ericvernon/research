package classifier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuzzyCalculatorTest {

    @org.junit.jupiter.api.Test
    void testDontCare() {
        FuzzyCalculator fuzzyLogic = new FuzzyCalculator();
        double value;

        value = fuzzyLogic.calculateMembershipValue(1.0, FuzzyCalculator.DONT_CARE);
        assertEquals(1.0, value, 0.0001);

        value = fuzzyLogic.calculateMembershipValue(0.0, FuzzyCalculator.DONT_CARE);
        assertEquals(1.0, value, 0.0001);

        value = fuzzyLogic.calculateMembershipValue(0.12534, FuzzyCalculator.DONT_CARE);
        assertEquals(1.0, value, 0.0001);
    }

    @Test
    void testMembershipTwoTriangles() {
        FuzzyCalculator fuzzyLogic = new FuzzyCalculator();
        assertEquals(1.0, fuzzyLogic.calculateMembershipValue(0, 1), 0.00001);
        assertEquals(0.0, fuzzyLogic.calculateMembershipValue(1.0, 1), 0.00001);
        assertEquals(0.5, fuzzyLogic.calculateMembershipValue(0.5, 1), 0.00001);
        assertEquals(0.0, fuzzyLogic.calculateMembershipValue(0, 2), 0.00001);
        assertEquals(0.33, fuzzyLogic.calculateMembershipValue(0.33, 2), 0.00001);
        assertEquals(1.0, fuzzyLogic.calculateMembershipValue(1.0, 2), 0.00001);
    }

    @Test
    void testMembershipFiveTriangles() {
        FuzzyCalculator fuzzyLogic = new FuzzyCalculator();
        // Five triangles - "XS, S, M, L, XL"
        // Testing XS
        assertEquals(1.0, fuzzyLogic.calculateMembershipValue(0, 10), 0.00001);
        assertEquals(0.5, fuzzyLogic.calculateMembershipValue(0.125, 10), 0.00001);
        assertEquals(0.0, fuzzyLogic.calculateMembershipValue(0.25, 10), 0.00001);
        assertEquals(0.0, fuzzyLogic.calculateMembershipValue(0.5, 10), 0.00001);

        // Testing L
        assertEquals(0.0, fuzzyLogic.calculateMembershipValue(0, 13), 0.00001);
        assertEquals(0.0, fuzzyLogic.calculateMembershipValue(0.5, 13), 0.00001);
        assertEquals(1.0, fuzzyLogic.calculateMembershipValue(0.75, 13), 0.00001);
        assertEquals(0.5, fuzzyLogic.calculateMembershipValue(0.875, 13), 0.00001);
        assertEquals(0.25, fuzzyLogic.calculateMembershipValue(0.9375, 13), 0.00001);
        assertEquals(0.0, fuzzyLogic.calculateMembershipValue(1.0, 13), 0.00001);
    }
}