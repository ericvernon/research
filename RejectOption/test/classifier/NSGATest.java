package classifier;

import nsga.Evaluator;
import nsga.NSGA2;
import nsga.NSGASortable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NSGATest {
    @Test
    public void testDomination() {
        NSGA2<Dummy> nsga2 = new NSGA2<>(3, new DummyEvaluator());

        Dummy d1 = new Dummy(new double[]{
                0.2, 0.2, 0.2
        });
        Dummy d2 = new Dummy(new double[]{
                0.2, 0.1, 0.2
        });
        Dummy d3 = new Dummy(new double[]{
                0.05, 0.15, 0.1
        });
        Dummy d4 = new Dummy(new double[]{
                0.05, 0.15, 0.1
        });
        // D2 and D3 dominate D1.  D2 and D3 do not dominate each other, and D4 is equal to D3.
        assertEquals(NSGA2.FIRST_DOMINATES, nsga2.dominates(d2, d1));
        assertEquals(NSGA2.SECOND_DOMINATES, nsga2.dominates(d1, d2));

        assertEquals(NSGA2.FIRST_DOMINATES, nsga2.dominates(d3, d1));
        assertEquals(NSGA2.SECOND_DOMINATES, nsga2.dominates(d1, d3));

        assertEquals(NSGA2.NEITHER_DOMINATES, nsga2.dominates(d2, d3));
        assertEquals(NSGA2.NEITHER_DOMINATES, nsga2.dominates(d3, d2));

        // Equal vectors
        assertEquals(NSGA2.NEITHER_DOMINATES, nsga2.dominates(d1, d1));
        assertEquals(NSGA2.NEITHER_DOMINATES, nsga2.dominates(d3, d4));
        assertEquals(NSGA2.NEITHER_DOMINATES, nsga2.dominates(d4, d3));

    }

    @Test
    public void testSorting() {
        NSGA2<Dummy> nsga2 = new NSGA2<>(2, new DummyEvaluator());

        // First front
        Dummy d1 = new Dummy(new double[]{
                -1.0, -1.0
        });
        // Second front
        Dummy d2 = new Dummy(new double[]{
                -1.0, -0.9
        });
        Dummy d3 = new Dummy(new double[]{
                -0.95, -0.95
        });
        // Third front.  The first three points are all crowded together.
        Dummy d4 = new Dummy(new double[]{
                -0.2, -0.25
        });
        Dummy d5 = new Dummy(new double[]{
                -0.25, -0.20
        });
        Dummy d6 = new Dummy(new double[]{
                -0.22, -0.22
        });
        Dummy d7 = new Dummy(new double[]{
                -0.4, -0.1
        });
        Dummy d8 = new Dummy(new double[]{
                -0.1, -0.4
        });
        List<Dummy> points = new ArrayList<>();
        points.add(d1);
        points.add(d2);
        points.add(d3);
        points.add(d4);
        points.add(d5);
        points.add(d6);
        points.add(d7);
        points.add(d8);
        nsga2.solve(points);

        assertEquals(1, d1.getParetoRank());
        assertEquals(2, d2.getParetoRank());
        assertEquals(2, d3.getParetoRank());
        assertEquals(3, d4.getParetoRank());
        assertEquals(3, d5.getParetoRank());
        assertEquals(3, d6.getParetoRank());
        assertEquals(3, d7.getParetoRank());
        assertEquals(3, d8.getParetoRank());

        assertEquals(Double.POSITIVE_INFINITY, d1.getCrowdingDistance());
        assertEquals(Double.POSITIVE_INFINITY, d2.getCrowdingDistance());
        assertEquals(Double.POSITIVE_INFINITY, d3.getCrowdingDistance());
        assertEquals(1.0, d4.getCrowdingDistance(), 0.001);
        assertEquals(1.0, d5.getCrowdingDistance(), 0.001);
        assertEquals(1.0/3, d6.getCrowdingDistance(), 0.001);
        assertEquals(Double.POSITIVE_INFINITY, d7.getCrowdingDistance());
        assertEquals(Double.POSITIVE_INFINITY, d8.getCrowdingDistance());

    }

    private static class Dummy implements NSGASortable {

        private double[] objectives;
        private double crowdingDistance;
        private int paretoRank;

        public Dummy(double[] objectives) {
            this.objectives = objectives;
        }

        public double[] getObjectives() {
            return this.objectives;
        }

        public void setObjectives(double[] dummy) { }

        public void setCrowdingDistance(double crowdingDistance) {
            this.crowdingDistance = crowdingDistance;
        }

        public void addCrowdingDistance(double crowdingDistance) {
            this.crowdingDistance += crowdingDistance;
        }

        public void setParetoRank(int paretoRank) {
            this.paretoRank = paretoRank;
        }

        public int getParetoRank() {
            return this.paretoRank;
        }

        public double getCrowdingDistance() {
            return this.crowdingDistance;
        }
    }

    private static class DummyEvaluator extends Evaluator<Dummy> {
        public double[] evaluate(Dummy value, boolean training) { return value.getObjectives(); }
    }
}
