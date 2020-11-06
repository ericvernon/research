package main;

import classifier.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        FileData fd = new FileData();
        FileData.Output trainingData = fd.load(getFilenameFromDataset("pima", 0, "tra"));
        Settings settings = new Settings();
        settings.setTrainingData(trainingData.patterns).setNInputAttributes(trainingData.nAttributes)
                .setNOutputClasses(trainingData.nOutputClasses)
                .setNAntecedents(15).setNRuleSets(200).setNRuleInitial(20).setNRulesMax(40)
                .setPCrossover(0.9).setPMutation(0.1).setPDontCareHeuristicRule(0.8)
                .setPHybridMichigan(0.5).setMichiganNReplace(0.2);

        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(time);
        long start = System.nanoTime();

        ResultsMaster resultsMaster = new ResultsMaster(time, "train");
        Classifier classifier = new Classifier(settings, new GMeanErrorNRulesMOP(settings), resultsMaster, 0);
        classifier.train(25);

        time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(time);
        System.out.println(System.nanoTime() - start);
    }

    private static String getFilenameFromDataset(String dataset, int fold, String trainOrTest) {
        return String.format("C:\\Users\\Eric\\data\\%s\\set-1\\%s-10dobscv-%d%s-normalized.dat",
                //return String.format("C:\\Users\\ericv\\code\\keel-data-ev\\%s\\set-1\\%s-10dobscv-%d%s-normalized.dat",
                dataset, dataset, fold + 1, trainOrTest);
    }
}
