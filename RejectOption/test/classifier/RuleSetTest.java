package classifier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleSetTest {
    @Test
    public void testCompare() {
        RuleSet first = new RuleSet(null, null, null);
        RuleSet second = new RuleSet(null, null, null);
        RuleSet third = new RuleSet(null, null, null);
        first.setParetoRank(1);
        first.setCrowdingDistance(0.5);
        second.setParetoRank(2);
        second.setCrowdingDistance(1.0);
        third.setParetoRank(2);
        third.setCrowdingDistance(0.1);

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);

        assertTrue(first.compareTo(third) < 0);
        assertTrue(third.compareTo(first) > 0);

        assertTrue(second.compareTo(third) < 0);
        assertTrue(third.compareTo(second) > 0);
    }
}