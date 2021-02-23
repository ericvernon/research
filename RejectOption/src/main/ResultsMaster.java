package main;

import classifier.Rule;
import classifier.RuleSet;
import classifier.Settings;
import nsga.Evaluator;
import nsga.MOP;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ResultsMaster {

    private String dir;
    private Settings settings;

    public ResultsMaster(String folder, Settings settings) {
        try {
            this.settings = settings;
            this.dir = String.format("results\\%s", folder);
            Files.createDirectories(Paths.get(this.dir));

            File settingsFile = new File("settings.config");
            File settingsDest = new File(this.dir + "\\settings.config");
            Files.copy(settingsFile.toPath(), settingsDest.toPath());

            PrintWriter writer = new PrintWriter(this.dir + "\\settings_obj_dump.txt", StandardCharsets.UTF_8);
            writer.println(settings.toString());
            writer.close();
        } catch (IOException ex) {
            System.out.println("Error creating results master.");
            System.out.println(ex.getMessage());
        }
    }

    public void recordRun(List<RuleSet> population, String run, MOP<RuleSet> problem) {
        try {
            String runDir = this.dir + "\\" + run;
            Files.createDirectory(Paths.get(runDir));
            PrintWriter settingsFile = new PrintWriter(runDir + "\\settings.txt", StandardCharsets.UTF_8);
            settingsFile.println(this.settings.toString());
            settingsFile.close();

            PrintWriter solutionDetails = new PrintWriter(runDir + "\\solutionDetails.txt", StandardCharsets.UTF_8);
            PrintWriter experimentResults = new PrintWriter(runDir + "\\experimentResults.txt", StandardCharsets.UTF_8);

            StringBuilder header = new StringBuilder();
            header.append("id,pareto_rank,crowding_score,");
            String[] objectiveNames = problem.getObjectiveNames();
            for (String objectiveName : objectiveNames) {
                header.append(objectiveName).append("_tra,");
            }
            for (String objectiveName : objectiveNames) {
                header.append(objectiveName).append("_tst,");
            }
            experimentResults.println(header.toString().trim());

            for (int i = 0; i < population.size(); i++) {
                RuleSet ruleSet = population.get(i);
                StringBuilder dataEntry = new StringBuilder();
                dataEntry.append(i).append(',');
                dataEntry.append(ruleSet.getParetoRank()).append(',');
                dataEntry.append(ruleSet.getCrowdingDistance()).append(',');

                Evaluator<RuleSet> evaluator = problem.getEvaluator();
                double[] trainingResults = evaluator.evaluate(ruleSet);
                assert(trainingResults.length == problem.nObjectives);
                for (int j = 0; j < trainingResults.length; j++) {
                    assert(trainingResults[j] == ruleSet.getObjectives()[j]);
                    dataEntry.append(trainingResults[j]).append(',');
                }

                double[] testingResults = evaluator.evaluate(ruleSet, false);
                assert(testingResults.length == problem.nObjectives);
                for (double testingResult : testingResults) {
                    dataEntry.append(testingResult).append(',');
                }
                experimentResults.println(dataEntry.toString().trim());

                solutionDetails.println("Solution ID: " + i);
                solutionDetails.println("N Rules: " + ruleSet.getRules().size());
                for (Rule rule : ruleSet.getRules()) {
                    solutionDetails.println(rule);
                }
                solutionDetails.println();
                solutionDetails.println("Reject Thresholds: ");
                for (double thresh : ruleSet.getRejectThresholds()) {
                    solutionDetails.println(thresh);
                }
                solutionDetails.println();
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
