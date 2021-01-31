package main;

import classifier.Pattern;
import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ClassifierEval {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("bus/in"));
        List<Rule> rules = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] bits = line.split(",");
            int[] antecedents = new int[bits.length - 2];
            for (int i = 0; i < antecedents.length; i++) {
                antecedents[i] = Integer.parseInt(bits[i]);
            }
            int label = Integer.parseInt(bits[bits.length - 2]);
            double cfq = Double.parseDouble(bits[bits.length - 1]);
            Rule rule = new Rule(antecedents, label, cfq);
            rules.add(rule);
        }

        Settings settings = new Settings();
        settings.setNOutputClasses(2).setNInputAttributes(2)
                .setRejectStrategy(Settings.RejectStrategies.PER_CLASS)
                .setRejectThreshold(0.9);
        RuleSet ruleSet = new RuleSet(rules, new double[] {
                0.4,
                0.73
        }, settings);
        outputGrid(twoDGrid(ruleSet, 500), false);
    }

    public static int[][] twoDGrid(RuleSet ruleSet, int resolution) {
        double step = 1.0 / (resolution - 1);
        int[][] result = new int[resolution][resolution];

        int xIndex = 0;
        for (double x = 0; x <= 1.0; x += step) {
            int yIndex = 0;
            for (double y = 0; y <= 1.0; y+= step) {
                Pattern pattern = new Pattern(new double[] { x, y}, 0);
                int cls = ruleSet.classify(pattern);
                result[xIndex][yIndex++] = cls; // (int) (x * 100 + y * 10);
            }
            xIndex++;
        }

        return result;
    }

    public static void outputGrid(int[][] grid, boolean showInConsole) {
        String f = String.valueOf(new Random().nextInt(999999999));
        try {
            PrintWriter writer = new PrintWriter("bus/" + f);
            for (int y = grid[0].length - 1; y >= 0; y--) {
                for (int x = 0; x < grid.length; x++) {
                    writer.print(grid[x][y]);
                    writer.print(',');
                    if (showInConsole) {
                        System.out.print(grid[x][y]);
                        System.out.print(", ");
                    }
                }
                writer.println();
                if (showInConsole)
                    System.out.println();
            }
            System.out.println(f);
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
