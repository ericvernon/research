package main;

import java.util.Random;

public class Scratch {
    public static void main(String[] args) {
        new Scratch().go();
    }

    public void go() {
        double a = 0.5;
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            double v = r.nextFloat();
            if (v < a) {
                System.out.println("Hit! " + v);
                a = v;
            }
        }
    }

    public void mutate(Integer input) {
        input = 0;
    }
}
