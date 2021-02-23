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
import java.util.regex.Matcher;

public class ClassifierEval {
    /**
     * Evaluate a classifier across a 501x501 grid.
     * The classifier (in the format of solutionDetails.txt) should be placed in bus/in
     * More options / configurability will be added as needed
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        int nAttributes = 2;
        int nClasses = 2;
        Settings.RejectStrategies strategy = Settings.RejectStrategies.PER_RULE;
        double rejectThreshold = 0.0;

        Scanner sc = new Scanner(new File("bus/in"));
        List<Rule> rules = new ArrayList<>();

        String line;
        line = sc.nextLine(); // Solution ID:
        line = sc.nextLine(); // N Rules:
        int nRules = Integer.parseInt(line.split("Rules: ")[1]);
        for (int i = 0; i < nRules; i++) {
            line = sc.nextLine();
            System.out.println(line);
            String reg = "Aq=(.*),C=(.*),CF=(.*),Rej=(.*)";
            Matcher m = java.util.regex.Pattern.compile(reg).matcher(line);
            m.find();

            String[] anteString = m.group(1).split(",");
            int[] ante = new int[nAttributes];
            for (int j = 0; j < nAttributes; j++)
                ante[j] = Integer.parseInt(anteString[j]);

            int classLabel = Integer.parseInt(m.group(2));
            double cf = Double.parseDouble(m.group(3));
            double rej = Double.parseDouble(m.group(4));

            Rule rule = new Rule(ante, rej, classLabel, cf);
            rules.add(rule);
        }

        line = sc.nextLine(); // blank
        line = sc.nextLine(); // Reject Thresholds:
        double[] classRej = new double[nClasses];
        for (int i = 0; i < nClasses; i++)
            classRej[i] = Double.parseDouble(sc.nextLine());


        StringBuilder comment = new StringBuilder();
        while (sc.hasNextLine()) {
            comment.append(sc.nextLine()).append("\n");
        }
        for (Rule rule : rules) {
            comment.append(rule.toString()).append("\n");
        }
        for (double rej : classRej) {
            comment.append(rej).append("\n");
        }

        Settings settings = new Settings();
        settings.setNOutputClasses(nClasses).setNInputAttributes(nAttributes)
                .setRejectStrategy(strategy)
                .setRejectThreshold(rejectThreshold);
        RuleSet ruleSet = new RuleSet(rules, classRej, settings);
        outputGrid(twoDGrid(ruleSet, 501), settings, comment.toString());
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
                result[xIndex][yIndex++] = cls; //(int) (x * 100 + y * 10);
            }
            xIndex++;
        }

        return result;
    }

    public static void outputGrid(int[][] grid, Settings settings, String comment) {
        String f = String.valueOf(new Random().nextInt(999999999));
        try {
            PrintWriter writer = new PrintWriter("bus/" + f);
            // This outputs the grid with the origin in the bottom-left (i.e., y=0 is the last row of file)
            for (int y = grid[0].length - 1; y >= 0; y--) {
                for (int x = 0; x < grid.length; x++) {
                    writer.print(grid[x][y]);
                    writer.print(',');
                }
                writer.println();
            }
            writer.println("---");
            writer.println("Strategy: " + settings.rejectStrategy);
            writer.println("Threshold (if STATIC): " + settings.rejectThreshold);
            writer.println(comment);
            System.out.println(f);
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
