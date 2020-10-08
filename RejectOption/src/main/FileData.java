package main;

import classifier.Pattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileData {
    public Output load(String filename) {
        Scanner sc = null;
        List<Pattern> patterns = new ArrayList<>();
        try {
            sc = new Scanner(new File(filename));
            String line = sc.nextLine();
            String bits[] = line.split(",");
            int numPatterns = Integer.parseInt(bits[0]);
            int numInputs = Integer.parseInt(bits[1]);
            int numLabels = Integer.parseInt(bits[2]);
            for (int i = 0; i < numPatterns; i++)
                patterns.add(this.parsePattern(sc.nextLine(), numInputs));
            return new Output(patterns, numInputs, numLabels);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Pattern parsePattern(String line, int numInputs) {
        String[] bits = line.split(",");
        double[] inputs = new double[numInputs];
        for (int i = 0; i < numInputs; i++)
            inputs[i] = Double.parseDouble(bits[i]);
        int output = Integer.parseInt(bits[numInputs]);
        return new Pattern(inputs, output);
    }

    public class Output {
        public List<Pattern> patterns;
        public int nAttributes;
        public int nOutputClasses;
        public Output (List<Pattern> patterns, int nAttributes, int nOutputClasses) {
            this.patterns = patterns;
            this.nAttributes = nAttributes;
            this.nOutputClasses = nOutputClasses;
        }
    }
}
