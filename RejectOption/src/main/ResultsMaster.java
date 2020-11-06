package main;

import classifier.RuleSet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ResultsMaster {

    private String dir;

    public ResultsMaster(String folder, String name) {
        try {
            this.dir = String.format("results\\%s\\%s", folder, name);
            Files.createDirectories(Paths.get(this.dir));
        } catch (IOException ex) {
            System.out.println("Error creating results master.");
        }
    }

    public void recordPopulation(int generation, List<RuleSet> population) {
        try {
            PrintWriter writer = new PrintWriter(this.dir + "\\gen" + generation + ".txt", "UTF-8");
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
        }
    }
}
