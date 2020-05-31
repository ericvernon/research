package classifier;

import main.FileData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import random.MersenneTwister;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierTest {
    // These tests are from programming exercise 4:
    private FileData.Output testData;
    private Settings settings;

    @BeforeEach
    public void setup() {
        FileData fd = new FileData();
        this.testData = fd.load("misc/a0_0_pima-10tra.dat");
        this.settings = new Settings()
                .setNAntecedents(15)
                .setNInputAttributes(this.testData.nAttributes)
                .setNOutputClasses(this.testData.nOutputClasses);
    }

    @Test
    public void testClassification() {
        Classifier classifier = new MichiganClassifier(this.testData.patterns, this.settings, 0);
        RuleFactory factory = new RuleFactory(this.testData.patterns, this.settings, new MersenneTwister());

        RuleSet ruleSet = new RuleSet();
        Rule rule1 = factory.rule(new int[] {0, 0, 0, 2, 0, 0, 0, 0});
        Rule rule2 = factory.rule(new int[] {0, 4, 0, 0, 3, 0, 0, 4});
        Rule rule3 = factory.rule(new int[] {0, 1, 0, 3, 1, 2, 0, 0});
        ruleSet.addRule(rule1);
        ruleSet.addRule(rule2);
        ruleSet.addRule(rule3);
        ruleSet.setFitness(this.testData.patterns);

        assertEquals(179, rule1.getFitness());
        assertEquals(136, rule2.getFitness());
        assertEquals(134, rule3.getFitness());
        assertEquals(179 + 136 + 134, ruleSet.getFitness());

        Population population = new Population();
        population.addRuleSet(ruleSet);
        classifier.setPopulation(population);

        assertEquals(0.3502, classifier.getTrainingError(), 0.00005);
    }

    @Test
    public void testFactory() {
        RuleFactory factory = new RuleFactory(this.testData.patterns, this.settings, new MersenneTwister());
        Rule rule = factory.rule(new int[] { 0, 4, 0, 3, 3, 0, 0, 0});
        assertEquals(0, rule.getClassLabel());
        assertEquals(0.504, rule.getConfidence(), 0.001);
    }
}
