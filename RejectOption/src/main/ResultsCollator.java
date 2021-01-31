package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Generally speaking, there are two data formats:
 * 1)   "Raw" (for lack of a better term) data - This is data as created by the @ResultsMaster and has more details
 *      id, pareto_rank, crowding_score, obj0_tra, obj1_tra... , obj0_tst, obj1_tst...
 *      id, pareto_rank, crowding_score, obj0_tra, obj1_tra... , obj0_tst, obj1_tst...
 *      etc...
 *
 * 2)   "Compressed" data.  Imagine two separate solutions have the same complexity as well as the same training
 *      accuracy.  It does not necessarily follow that they share the same testing accuracy, even though the training
 *      results are identical.  Additionally, K-fold cross validation implies at least K runs, and each run will have
 *      a unique pareto front.  To solve this problem we perform a two-stage 'compression' (again, loose terminology).
 *
 *      In the first stage, results not on the Pareto front are discarded.  Then, we search for results which share
 *      the same complexity and take their average for the other objectives.  This creates a pareto front for that run.
 *
 *      In the second stage, multiple Pareto fronts are combined.  This is done similarly - we take the average of
 *      all the solutions of a given complexity.  However, if less than half of the results contain a solution of any
 *      given complexity, that complexity (and all related solutions) will be dropped from the final results.
 *
 *      To avoid confusion, 'compressed' data uses this format:
 *      obj0_tra, obj1_tra... , obj0_tst, obj1_tst...
 */
public class ResultsCollator {
    public static void main(String[] args) {
    }

    public static void process(String path) {
        File dir = new File(path);
        File[] subDirs = dir.listFiles();

        List<double[][]> allData = new ArrayList<>();
        if (subDirs == null)
            return;
        for (File subDir : subDirs) {
            if (subDir.isDirectory()) {
                File[] files = subDir.listFiles();
                if (files == null)
                    continue;
                for (File file : files) {
                    if (file.getName().equals("_experimentResults.txt")) {
                        double[][] data = loadRaw(file.getPath());
                        allData.add(collateSingleRun(data, 0));
                    }
                }
            }
        }

        double[][][] dataArr = new double[allData.size()][][];
        dataArr = allData.toArray(dataArr);
        double[][] results = collateMultipleRuns(dataArr, 0);

        try {
            PrintWriter writer = new PrintWriter(path + "\\final.txt");
            for (double[] doubles : results) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < results[0].length; j++) {
                    sb.append(doubles[j]).append(',');
                }
                writer.println(sb.toString().trim());
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /*
     * This compresses the results of a single run.
     * The number of entries in the result will be less than or equal to the number of entries in the original.
     */
    public static double[][] collateSingleRun(double[][] data, int objectiveIdx) {

        int size = data[0].length;
        // Offset the 3 initial entries
        int nObj = (size - 3) / 2;

        Map<Integer, Double[]> dataMap = new HashMap<>();
        ArrayList<double[]> result = new ArrayList<>();
        for (double[] datum : data) {
            if ((int)datum[1] != 1)
                continue;
            int nRules = (int) datum[objectiveIdx + 3];
            if (!dataMap.containsKey(nRules)) {
                Double[] zeros = new Double[nObj * 2 + 1];
                Arrays.fill(zeros, 0.0);
                dataMap.put(nRules, zeros);
            }
            Double[] totals = dataMap.get(nRules);
            totals[0]++;
            for (int j = 0; j < nObj * 2; j++) {
                totals[j + 1] += datum[j + 3];
            }
            dataMap.put(nRules, totals);
        }
        flushMap(dataMap, result, nObj, 1);

        double[][] ret = new double[result.size()][nObj * 2];
        return result.toArray(ret);
    }

    /**
     * Functionally, combining multiple runs is fairly similar to combining single runs.  There are two differences:
     * 1) Accept data as a 3D, jagged array.  The first axis represents the run.  The second axis distinguishes each
     *    solution within the run.  As in, data[Run ID][Some Index] should be a Double array of data in compressed form.
     *    It is assumed that any given complexity only appears in each run once (i.e. the data has already been
     *    processed by @collateSingleRun).
     * 2) Any complexity which appears in less than half of the runs should be discarded.
     */
    public static double[][] collateMultipleRuns(double[][][] data, int objectiveIdx) {
        int size = data[0][0].length;
        int nObj = size / 2;

        // Map # Rules --> Some temporary storage in the format of:
        // (# Instances) (Sum for Tra_1) (Sum for Tra_2)... (Sum for Tst_1) (Sum for Tst_2)
        Map<Integer, Double[]> dataMap = new HashMap<>();

        // Where we put the results
        ArrayList<double[]> result = new ArrayList<>();
        for (double[][] run : data) {
            for (double[] datum : run) {
                int nRules = (int) datum[objectiveIdx];
                if (!dataMap.containsKey(nRules)) {
                    Double[] zeros = new Double[nObj * 2 + 1];
                    Arrays.fill(zeros, 0.0);
                    dataMap.put(nRules, zeros);
                }
                Double[] totals = dataMap.get(nRules);
                totals[0]++;
                for (int j = 0; j < nObj * 2; j++) {
                    totals[j + 1] += datum[j];
                }
                dataMap.put(nRules, totals);
            }
        }

        flushMap(dataMap, result, nObj, (int)Math.ceil(data.length / 2.0));

        double[][] ret = new double[result.size()][nObj * 2];
        return result.toArray(ret);
    }

    /**
     * Takes in a map from (# Of Rules) --> (Data), where the data is defined as:
     *  [ divisor, obj1_tra, obj2_tra, ..... obj1_tst, obj2_tst...]
     * For each unique # of rules (i.e. each unique entry in the map), it will append the average values
     * to the array list
     */
    private static void flushMap(Map<Integer, Double[]> map, ArrayList<double[]> storage, int nObj, int minSupport) {
        for (Map.Entry<Integer, Double[]> entry : map.entrySet()) {
            double[] converted = new double[nObj * 2];
            Double[] val = entry.getValue();
            int divisor = val[0].intValue();
            if (divisor < minSupport)
                continue;
            for (int i = 0; i < nObj * 2; i++) {
                converted[i] = val[i + 1] / divisor;
            }
            storage.add(converted);
        }
        map.clear();
    }

    public static double[][] loadRaw(String filename) {
        Scanner sc = null;
        List<double[]> data = new ArrayList<>();
        try {
            sc = new Scanner(new File(filename));
            String line = sc.nextLine();
            String[] bits = line.split(",");
            int size = bits.length;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                bits = line.split(",");
                double[] entry = new double[size];
                for (int i = 0; i < size; i++)
                    entry[i] = Double.parseDouble(bits[i]);
                data.add(entry);
            }
            double[][] ret = new double[data.size()][size];
            return data.toArray(ret);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new double[0][0];
        }
    }

}
