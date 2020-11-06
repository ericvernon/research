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
        // 1: 5/9 (1+1, 1+2, 1+3, 2+1, or 3+1)
        // 2: 3/9 (2+3, 3+2, or 2+2)
        // 3: 1/9 (3+3)
        testList = new ArrayList<>();
        testList.add(3);
        testList.add(1);
        testList.add(2);
        int[] results = new int[4];
        int n = 20000;
        for (int i = 0; i < n; i++) {
            int result = util.binaryTournament(testList);
            results[result]++;
        }
        // This is admittedly unscientific, but should be good-enough
        assertEquals(0, results[0]);
        assertTrue(results[1] > n * (0.55 - 0.05) && results[1] < n * (0.55 + 0.05));
        assertTrue(results[2] > n * (0.33 - 0.05) && results[2] < n * (0.33 + 0.05));
        assertTrue(results[3] > n * (0.11 - 0.05) && results[3] < n * (0.11 + 0.05));
    }
}