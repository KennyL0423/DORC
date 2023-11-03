package main;
import grid.DBSCAN;
import grid.GDORC;
import method.*;
import utils.*;
import evaluation.Benchmark;

public class BenchMarkEntrance {

    public static void main(String[] args){
        benchmarkTest();
    }

    public static void benchmarkTest(){
        String outputPath = "./outputdata/updateExample/QDORC/eta=6_eps=15/";;
        String truthPath = "./data/updateExample/newtruthdata.dat";
        double[] rateArray = {0.015, 0.03, 0.045, 0.06, 0.075, 0.09, 0.105, 0.12, 0.135, 0.15, 0.165, 0.18};
        String[] methodArray = {"QDORC", "LDORC", "GDORC"};
        for (double rate : rateArray) {
            Benchmark bm = new Benchmark(outputPath + rate + "/repairData.dat",truthPath, outputPath+"resLog.txt",6,15);
        }
    }

}
