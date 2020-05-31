package classifier;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RuleFactoryTest {
    @Test
    public void testSimpleCalculation() {
        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(new double[] {0.25, 1.0}, 0));
        patterns.add(new Pattern(new double[] {0.5, 0.25}, 1));
        patterns.add(new Pattern(new double[] {1.0, 0.75}, 1));
        patterns.add(new Pattern(new double[] {0.5, 0.5}, 2));

        Settings settings = new Settings();
        settings.setNAntecedents(15)
                .setNInputAttributes(2)
                .setNOutputClasses(3);

        RuleFactory factory = new RuleFactory(patterns, settings, new Random());
        Rule rule = factory.rule(new int[] {3, 5});
        assertEquals(0, rule.getClassLabel());
        assertEquals(1.0, rule.getConfidence(), 0.001);

        rule = factory.rule(new int[]{4, 4});
        assertEquals(2, rule.getClassLabel());
        assertEquals(1.0/3, rule.getConfidence(), 0.001);

        rule = factory.rule(new int[]{2, 0});
        assertEquals(1, rule.getClassLabel());
        assertEquals(1.0/3, rule.getConfidence(), 0.001);

        rule = factory.rule(new int[]{0, 2});
        assertEquals(Rule.REJECTED_CLASS_LABEL, rule.getClassLabel());
    }

    @Test
    public void testRandomRule() {
        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(new double[] {0.25, 1.0, 1, 0, 1, 1, 1, 0, 0, 0.5}, 0));

        Settings settings = new Settings();
        settings.setNAntecedents(15)
                .setNInputAttributes(10)
                .setNOutputClasses(3);

        RuleFactory factory = new RuleFactory(patterns, settings, new Random());
        int[] counts = new int[15];
        for (int i = 0; i < 1500; i++) {
            Rule rule = factory.randomRule();
            int[] antecedents = rule.getAntecedents();
            assertEquals(10, antecedents.length);
            for (int j = 0; j < 10; j++) {
                counts[antecedents[j]]++;
            }
        }

        // 1500 * 10 = 15,000 antecedents generated
        for (int i = 0; i < 15; i++) {
            assertTrue(counts[i] > 900 && counts[i] < 1100);
        }
    }
}