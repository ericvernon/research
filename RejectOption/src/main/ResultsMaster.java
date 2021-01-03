package main;

import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ResultsMaster {

    private String dir;

    public ResultsMaster(String folder, String name, Settings settings) {
        try {
            this.dir = String.format("results\\%s\\%s", folder, name);
            Files.createDirectories(Paths.get(this.dir));
            PrintWriter writer = new PrintWriter(this.dir + "\\settings.txt", StandardCharsets.UTF_8);
            writer.println(settings.toString());
            writer.close();
        } catch (IOException ex) {
            System.out.println("Error creating results master.");
            System.out.println(ex.getMessage());
        }
    }

    public void recordPopulation(String generationName, List<RuleSet> population) {
        try {
            PrintWriter writer = new PrintWriter(this.dir + "\\" + generationName + ".txt", StandardCharsets.UTF_8);
            for (RuleSet ruleSet : population) {
                StringBuilder stringBuffer = new StringBuilder();
                for (double objective : ruleSet.getObjectives()) {
                    stringBuffer.append(objective).append("\t");
                }
                stringBuffer.append(ruleSet.getParetoRank()).append("\t");
                stringBuffer.append(ruleSet.getCrowdingDistance());
                writer.println(stringBuffer.toString().trim());
            }
            writer.close();
        } catch (IOException ex) {
            System.out.println("Error updating results.");
            System.out.println(ex.getMessage());
        }
    }

    public void finalizeOutput(List<RuleSet> population, MOP<RuleSet> problem) {
        try {
            PrintWriter solutionDetails = new PrintWriter(this.dir + "\\solutionDetails.txt", StandardCharsets.UTF_8);
            PrintWriter experimentResults = new PrintWriter(this.dir + "\\experimentResults.txt", StandardCharsets.UTF_8);

            StringBuilder header = new StringBuilder();
            header.append("id\tpareto_rank_tra\tcrowding_score_tra\t");
            for (int i = 0; i < problem.nObjectives; i++) {
                header.append("obj_").append(i).append("_tra\t");
            }
            for (int i = 0; i < problem.nObjectives; i++) {
                header.append("obj_").append(i).append("_tst\t");
            }
            experimentResults.println(header.toString().trim());

            for (int i = 0; i < population.size(); i++) {
                RuleSet ruleSet = population.get(i);
                StringBuilder dataEntry = new StringBuilder();
                dataEntry.append(i).append('\t');
                dataEntry.append(ruleSet.getParetoRank()).append('\t');
                dataEntry.append(ruleSet.getCrowdingDistance()).append('\t');

                Evaluator<RuleSet> evaluator = problem.getEvaluator();
                double[] trainingResults = evaluator.evaluate(ruleSet);
                assert(trainingResults.length == problem.nObjectives);
                for (int j = 0; j < trainingResults.length; j++) {
                    assert(trainingResults[j] == ruleSet.getObjectives()[j]);
                    dataEntry.append(trainingResults[j]).append('\t');
                }

                double[] testingResults = evaluator.evaluate(ruleSet, false);
                assert(testingResults.length == problem.nObjectives);
                for (int j = 0; j < testingResults.length; j++) {
                    dataEntry.append(testingResults[j]).append('\t');
                }
                experimentResults.println(dataEntry.toString().trim());

                solutionDetails.println("Solution ID: " + i);
                solutionDetails.println("N Rules:" + ruleSet.getRules().size());
                for (Rule rule : ruleSet.getRules()) {
                    solutionDetails.println(rule);
                }
                solutionDetails.println();
            }

            experimentResults.close();
            solutionDetails.close();
        } catch (IOException ex) {
            System.out.println("Error saving results.");
            System.out.println(ex.getMessage());
        }
    }
}
