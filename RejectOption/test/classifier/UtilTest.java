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
        testList.add(3);
        testList.add(1);
        testList.add(2);
        int[] results = new int[4];
        for (int i = 0; i < 18000; i++) {
            int result = util.binaryTournament(testList);
            results[result]++;
        }
        assertEquals(0, results[0]);
        assertTrue(results[1] > 1800 && results[1] < 2200);
        assertTrue(results[2] > 5800 && results[2] < 6200);
        assertTrue(results[3] > 9800 && results[3] < 10200);
    }
}