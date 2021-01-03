package main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultsCollatorTest {

    @Test
    void testCollateSingleRun() {
        double[][] data = new double[9][7];
//        id	pareto_rank_tra	crowding_score_tra	obj_0_tra	obj_1_tra	obj_0_tst	obj_1_tst
        // Front 1: 3 solutions with 2 rules (avg 0.333..), and 2 solutions with 4 rules (avg 0.4)
        // Front 2-3: Should be truncated
        data[0] = new double[] { 0.0, 1.0, 0.0, 2.0, 0.1, 2.0, 0.2};
        data[1] = new double[] { 1.0, 1.0, 0.0, 4.0, 0.3, 4.0, 0.4};
        data[2] = new double[] { 2.0, 1.0, 0.0, 2.0, 0.1, 2.0, 0.4};
        data[3] = new double[] { 3.0, 1.0, 0.0, 2.0, 0.1, 2.0, 0.4};
        data[4] = new double[] { 4.0, 1.0, 0.0, 4.0, 0.3, 4.0, 0.4};
        data[5] = new double[] { 5.0, 1.0, 0.0, 5.0, 0.0, 5.0, 0.1};
        data[6] = new double[] { 6.0, 2.0, 0.0, 4.0, 0.7, 4.0, 0.7};
        data[7] = new double[] { 7.0, 3.0, 0.0, 3.0, 0.3, 3.0, 0.4};
        data[8] = new double[] { 8.0, 3.0, 0.0, 3.0, 0.3, 3.0, 0.6};
        double[][] results = ResultsCollator.collateSingleRun(data, 0);

        double[][] expected = new double[3][4];
        expected[0] = new double[] { 2.0, 0.1, 2.0, 1.0/3 };
        expected[1] = new double[] { 4.0, 0.3, 4.0, 0.4 };
        expected[2] = new double[] { 5.0, 0.0, 5.0, 0.1 };

        assertEquals(expected.length, results.length);
        for (int i = 0; i < expected.length; i++)
            assertArrayEquals(expected[i], results[i], 0.00001);
    }

    @Test
    void testCollateSingleRun_3D() {
        double[][] data = new double[6][9];
        data[0] = new double[] { 0.0, 1.0, 0.0, 2.0, 0.1, 0.3, 2.0, 0.20, 0.40};
        data[1] = new double[] { 1.0, 1.0, 0.0, 2.0, 0.1, 0.3, 2.0, 0.25, 0.40};
        data[2] = new double[] { 2.0, 1.0, 0.0, 2.0, 0.1, 0.3, 2.0, 0.40, 0.60};

        data[3] = new double[] { 3.0, 1.0, 0.0, 3.0, 0.3, 0.8, 3.0, 0.20, 0.70};
        data[4] = new double[] { 4.0, 1.0, 0.0, 3.0, 0.3, 0.8, 3.0, 0.30, 0.76};

        data[5] = new double[] { 5.0, 1.0, 0.0, 5.0, 0.0, 0.7, 5.0, 0.01, 0.80};
        double[][] results = ResultsCollator.collateSingleRun(data, 0);

        double[][] expected = new double[3][4];
        expected[0] = new double[] { 2.0, 0.1, 0.3, 2.0, 0.85/3, 1.4/3 };
        expected[1] = new double[] { 3.0, 0.3, 0.8, 3.0, 0.25, 0.73 };
        expected[2] = new double[] { 5.0, 0.0, 0.7, 5.0, 0.01, 0.80 };

        assertEquals(expected.length, results.length);
        for (int i = 0; i < expected.length; i++)
            assertArrayEquals(expected[i], results[i], 0.00001);
    }

    @Test
    void testCollateMultipleRuns() {
        // Test data based on Yusuke's sketch in Teams
        double[][][] data = new double[3][][];
        // First run - 5 different rule sizes
        data[0] = new double[5][4];
        data[0][0] = new double[] { 2.0, 0.45, 2.0, 0.45};
        data[0][1] = new double[] { 3.0, 0.30, 3.0, 0.32};
        data[0][2] = new double[] { 4.0, 0.20, 4.0, 0.21};
        data[0][3] = new double[] { 5.0, 0.19, 5.0, 0.25};
        data[0][4] = new double[] { 6.0, 0.15, 6.0, 0.30};

        // Second run - Only 4 different rule sizes
        data[1] = new double[4][4];
        data[1][0] = new double[] { 2.0, 0.47, 2.0, 0.48};
        data[1][1] = new double[] { 3.0, 0.37, 3.0, 0.38};
        data[1][2] = new double[] { 4.0, 0.23, 4.0, 0.22};
        data[1][3] = new double[] { 5.0, 0.22, 5.0, 0.30};

        // Third run - 3 different rule sizes
        data[2] = new double[3][4];
        data[2][0] = new double[] { 2.0, 0.44, 2.0, 0.45};
        data[2][1] = new double[] { 3.0, 0.36, 3.0, 0.36};
        data[2][2] = new double[] { 5.0, 0.21, 5.0, 0.25};

        // If at least half of the runs found a pareto-optimal solution with a given complexity, then we should average
        // the objective functions for those solutions.  Otherwise, we should drop that complexity.
        // Solution(s) of complexity 2, 3 & 5 were found in all runs - average those
        // Solution(s) of complexity 4 were found in two out of three runs.  Since 2 > 1.5, average those
        // Solution(s) of complexity 6 were only found in one run.  Since 1 < 1.5, ignore them
        double[][] expected = new double[4][4];
        expected[0] = new double[] { 2.0, (0.45 + 0.47 + 0.44) / 3, 2.0, (0.45 + 0.48 + 0.45) / 3};
        expected[1] = new double[] { 3.0, (0.30 + 0.37 + 0.36) / 3, 3.0, (0.32 + 0.38 + 0.36) / 3};
        expected[2] = new double[] { 4.0, (0.20 + 0.23) / 2, 4.0, (0.21 + 0.22) / 2};
        expected[3] = new double[] { 5.0, (0.19 + 0.22 + 0.21) / 3, 5.0, (0.25 + 0.30 + 0.25) / 3};

        double[][] results = ResultsCollator.collateMultipleRuns(data, 0);
        assertEquals(expected.length, results.length);
        for (int i = 0; i < expected.length; i++)
            assertArrayEquals(expected[i], results[i], 0.00001);
    }
}