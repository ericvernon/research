package classifier;

import main.FileData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import random.MersenneTwister;
import sun.tracing.dtrace.DTraceProviderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PopulationTest {

    @Test
    public void testSetPopulationFitness() {
        FileData fd = new FileData();
        FileData.Output testData = fd.load("misc/a0_0_pima-10tra.dat");

        Settings settings = new Settings();
        settings.setNAntecedents(15).setNRuleSets(5).setNRules(20)
        .setNInputAttributes(testData.nAttributes).setNOutputClasses(testData.nOutputClasses);
        RuleFactory factory = new RuleFactory(testData.patterns, settings, new Random());

        HashMap<RuleSet, Integer> initialFitness = new HashMap<>();
        HashMap<Rule, Integer> initialRuleFitness = new HashMap<>();
        Population population = new Population();
        for (int i = 0; i < settings.nRuleSets; i++) {
            RuleSet ruleSet = new RuleSet();
            for (int j = 0; j < settings.nRules; j++) {
                ruleSet.addRule(factory.randomRule());
            }
            ruleSet.setFitness(testData.patterns);
            initialFitness.put(ruleSet, ruleSet.getFitness());
            for (Rule rule : ruleSet.getRules()) {
                initialRuleFitness.put(rule, rule.getFitness());
            }
            population.addRuleSet(ruleSet);
        }

        population.setFitness(testData.patterns);
        population.setFitness(testData.patterns);
        population.setFitness(testData.patterns);
        population.setFitness(testData.patterns);

        for (RuleSet ruleSet : population.getRuleSets()) {
            for (Rule rule : ruleSet.getRules()) {
                assertEquals(initialRuleFitness.get(rule), rule.getFitness());
            }
            assertEquals(initialFitness.get(ruleSet), ruleSet.getFitness());
        }

    }
}
