package main;

import classifier.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ToyData {
    Random r = new Random();

    public List<Pattern> toyData(int num) {
        List<Pattern> data = new ArrayList<>();
        for (int i = 0; i < num; i++)
            data.add(get());
        return data;
    }

    private Pattern get() {
        double x = this.r.nextDouble();
        double y = this.r.nextDouble();

        int label;
        // Gets a random value from -0.05 and 0.05.
        double perturbation = (this.r.nextDouble() / 10) - 0.05;

        if (x >= 0.25 && x <= 0.75 && y >= 0.25 && y <= 0.75) {
            label = 2;
        } else if (x >= y + perturbation) {
            label = 0;
        } else {
            label = 1;
        }

        return new Pattern(new double[] { x, y}, label);
    }

    public boolean checkClass(double x, double y, double label) {
        if (x >= 0.25 && x <= 0.75 && y >= 0.25 && y <= 0.75) {
            return label == 2;
        } else if (x > y) {
            return label == 0;
        } else {
            return label == 1;
        }
    }

}
