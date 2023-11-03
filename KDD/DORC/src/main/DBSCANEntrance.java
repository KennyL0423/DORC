package main;

import grid.DBSCAN;
import grid.GDORC;
import utils.Dataset;
import utils.LogWriter;

public class DBSCANEntrance {
    public void timeLog(String outputPath, double time){
        LogWriter timeLog = new LogWriter(outputPath);
        timeLog.open();
        timeLog.log(time+"\n");
    }

    public void runRealDataset() {
        double[] rateArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        double[] epsArray = {5E-5, 6E-5, 7E-5, 8E-5, 9E-5, 1E-4, 1.1E-4, 1.2E-4, 1.3E-4, 1.4E-4, 1.5E-4};
        int[] etaArray = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

        long startTime, endTime;
        String outputPath;

        String method = "DBSCAN";

        // 热机
        for (int i = 0; i < 20; i++) {
            DBSCAN dbscan = new DBSCAN(8E-5, 8, "./data/updateReal/" + 0.02 + "/noiseData.dat");
        }

        // exp1, fix eta and eps, change rate
        outputPath = "./outputdata/updateReal/DBSCAN/eta=8_eps=8E-5/";
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 10; i++) {
                DBSCAN dbscan = new DBSCAN(8E-5, 8, "./data/updateReal/" + rate + "/"+ i+ "/noiseData.dat");
                dbscan.log(outputPath + rate + "/"+ i+ "/repairData.dat");
            }

            endTime = System.currentTimeMillis();
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10);
        }
        // exp2, fix rate and eta, change eps
        outputPath = "./outputdata/updateReal/DBSCAN/rate=0.02_eta=8/";
        for (double eps : epsArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 10; i++) {
                DBSCAN dbscan = new DBSCAN(eps, 8, "./data/updateReal/" + 0.02 + "/"+ i+ "/noiseData.dat");
                dbscan.log(outputPath + eps + "/"+ i+ "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10);
        }
        // exp3, fix rate and eps, change eta
        outputPath = "./outputdata/updateReal/" + method + "/rate=0.02_eps=8E-5/";
        for (int eta : etaArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 10; i++) {
                DBSCAN dbscan = new DBSCAN(8E-5, eta, "./data/updateReal/" + 0.02 + "/"+ i + "/noiseData.dat");
                dbscan.log(outputPath + eta + "/"+ i + "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10);
        }
    }
    
    public void runExample(){
        double[] rateArray = {0.015, 0.03, 0.045, 0.06, 0.075, 0.09, 0.105, 0.12, 0.135, 0.15, 0.165, 0.18};
        int[] etaArray = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        double[] epsArray = {11, 12, 13, 14, 15, 16, 17, 18, 19, 100};
        long startTime, endTime;
        String outputPath;

        String method = "DBSCAN";

        // 热机
        for (int i = 0; i < 20; i++) {
            DBSCAN dbscan = new DBSCAN(15, 6, "./data/updateExample/" + 0.03 + "/noiseData.dat");
        }

        // exp1, fix eta and eps, change rate
        outputPath = "./outputdata/updateExample/DBSCAN/eta=6_eps=15/";
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 19; i++) {
                DBSCAN dbscan = new DBSCAN(15, 6, "./data/updateExample/" + rate + "/noiseData.dat");
            }
            DBSCAN dbscan = new DBSCAN(15, 6, "./data/updateExample/" + rate + "/noiseData.dat");

            endTime = System.currentTimeMillis();
            dbscan.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/20.0);
        }
        // exp2, fix rate and eta, change eps
        outputPath = "./outputdata/updateExample/DBSCAN/rate=0.03_eta=6/";
        for (double eps : epsArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 19; i++) {
                DBSCAN dbscan = new DBSCAN(eps, 6, "./data/updateExample/" + 0.03 + "/noiseData.dat");
            }
            DBSCAN dbscan = new DBSCAN(eps, 6, "./data/updateExample/" + 0.03 + "/noiseData.dat");

            endTime = System.currentTimeMillis();
            dbscan.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/20.0);
        }
        // exp3, fix rate and eps, change eta
        outputPath = "./outputdata/updateExample/" + method + "/rate=0.03_eps=15/";
        for (int eta : etaArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 19; i++) {
                DBSCAN dbscan = new DBSCAN(15, eta, "./data/updateExample/" + 0.03 + "/noiseData.dat");
            }

            DBSCAN dbscan = new DBSCAN(15, eta, "./data/updateExample/" + 0.03 + "/noiseData.dat");

            endTime = System.currentTimeMillis();
            dbscan.log(outputPath + eta + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/20.0);
        }
    }
    
    public void runFour(){
        double[] rateArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16};
        double[] epsArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        int[] etaArray = {25, 50, 100, 300, 500, 700, 1000, 1200};
        int[] sizeArray = {50000, 100000, 150000};

        String outputPath;
        long startTime, endTime;
        //exp 1, change size, fix eps and eta
        outputPath = "./outputdata/foursquare/DBSCAN/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                DBSCAN dbscan = new DBSCAN(0.06, 500, "./data/foursquare/" + size + "/"+ i + "/noiseData.dat");
                dbscan.log(outputPath+size+"/" + i + "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt" ,(endTime-startTime)/10000.0);
            Dataset.clearAll();
            System.out.println("DBSCAN-exp1-"+size);
        }

        //exp 2, change rate
        outputPath = "./outputdata/foursquare/DBSCAN/eta=500_eps=0.06/";
        for (double rate:rateArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                DBSCAN dbscan = new DBSCAN(0.06, 500, "./data/foursquare/100000/" + rate + "/"+ i +"/noiseData.dat");
                dbscan.log(outputPath+rate+"/" + i +"/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt", (endTime-startTime)/10000.0);
            System.out.println("DBSCAN-exp2-"+rate);
        }
        //exp 3, change eps
        outputPath = "./outputdata/foursquare/DBSCAN/rate=0.02_eta=500/";
        for (double eps:epsArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                DBSCAN dbscan = new DBSCAN(eps, 500, "./data/foursquare/100000/" + 0.1 +  "/"+ i + "/noiseData.dat");
                dbscan.log(outputPath+eps+"/"+ i +"/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt", (endTime-startTime)/10000.0);
            System.out.println("DBSCAN-exp3-"+eps);
        }
        //exp 4, change eta
        outputPath = "./outputdata/foursquare/DBSCAN/rate=0.02_eps=0.06/";
        for (int eta:etaArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                DBSCAN dbscan = new DBSCAN(0.06, eta, "./data/foursquare/100000/" + 0.1 + "/"+ i + "/noiseData.dat");
                dbscan.log(outputPath+eta+"/"+ i +"/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt", (endTime-startTime)/10000.0);
            System.out.println("DBSCAN-exp4-"+eta);
        }
    }
    public void runZC() {
        double[] rateArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        double[] epsArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        int[] etaArray = {25, 50, 100, 300, 500, 700, 1000, 1200};
        int[] sizeArray = {19998, 19998 * 2, 19998 * 3, 19998 * 4, 19998 * 5, 19998 * 6};

        String outputPath;
        long startTime, endTime;
        //exp 1, change size, fix eps and eta
        outputPath = "./outputdata/zhongchuan/DBSCAN/change_size/";
        for (int size : sizeArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                DBSCAN dbscan = new DBSCAN(0.06, 500, "./data/zhongchuan/" + size + "/" + i + "/noiseData.dat");
                dbscan.log(outputPath + size + "/" + i + "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "DBSCAN_time.txt", (endTime - startTime) / 10000.0);
            Dataset.clearAll();
            System.out.println("DBSCAN-exp1-" + size);
        }

        //exp 2, change rate
        outputPath = "./outputdata/zhongchuan/DBSCAN/eta=500_eps=0.06/";
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                DBSCAN dbscan = new DBSCAN(0.06, 500, "./data/zhongchuan/99990/" + rate + "/" + i + "/noiseData.dat");
                dbscan.log(outputPath + rate + "/" + i + "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "DBSCAN_time.txt", (endTime - startTime) / 10000.0);
            System.out.println("DBSCAN-exp2-" + rate);
        }
        //exp 3, change eps
        outputPath = "./outputdata/zhongchuan/DBSCAN/rate=0.02_eta=500/";
        for (double eps : epsArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                DBSCAN dbscan = new DBSCAN(eps, 500, "./data/zhongchuan/99990/" + 0.1 + "/" + i + "/noiseData.dat");
                dbscan.log(outputPath + eps + "/" + i + "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "DBSCAN_time.txt", (endTime - startTime) / 10000.0);
            System.out.println("DBSCAN-exp3-" + eps);
        }
        //exp 4, change eta
        outputPath = "./outputdata/zhongchuan/DBSCAN/rate=0.02_eps=0.06/";
        for (int eta : etaArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                DBSCAN dbscan = new DBSCAN(0.06, eta, "./data/zhongchuan/99990/" + 0.1 + "/" + i + "/noiseData.dat");
                dbscan.log(outputPath + eta + "/" + i + "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "DBSCAN_time.txt", (endTime - startTime) / 10000.0);
            System.out.println("DBSCAN-exp4-" + eta);
        }
    }
}
