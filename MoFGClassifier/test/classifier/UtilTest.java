package classifier;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {
    @Test
    public void binaryTournamentTest() {
        Util util = new Util();
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(1);
        assertEquals(1, util.binaryTournament(testList));

        // In binary tournament with replacement, we expect the following results:
        // 1: 1/9 (both candidates were 1)
        // 2: 3/9 (1+2, 2+1, or 2+2)
        // 3: 5/9 (one or both candidates were 3)
        testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testList.add(3);
        int[] results = new int[4];
        for (int i = 0; i < 1800; i++) {
            int result = util.binaryTournament(testList);
            results[result]++;
        }
        // Roughly 3 standard deviations of what's expected.
        assertEquals(0, results[0]);
        assertTrue(results[1] > 160 && results[1] < 235);
        assertTrue(results[2] > 535 && results[2] < 660);
        assertTrue(results[3] > 935 && results[3] < 1050);
    }
}