package main;

import method.QDORC;
import utils.Dataset;
import utils.LogWriter;

public class QDORCEntrance {
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

        String method = "QDORC";

        // �Ȼ�
        for (int i=0; i<20; i++){
            new Dataset().importDataset("./data/updateReal/" + 0.02 + "/noiseData.dat");
            QDORC qdorc = new QDORC(8, 8E-5);
            Dataset.clearAll();
        }

        // exp1, fix eta and eps, change rate
        outputPath = "./outputdata/updateReal/QDORC/eta=8_eps=8E-5/";
        for (double rate : rateArray) {
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/updateReal/" + rate + "/"+ i+ "/noiseData.dat");
                QDORC qdorc = new QDORC(8, 8E-5);
                qdorc.log(outputPath + rate + "/"+ i+ "/repairData.dat");
                Dataset.clearAll();
            }
            startTime = System.currentTimeMillis();
            for (int k=0; k<10000; k++){
                new Dataset().importDataset("./data/updateReal/" + rate + "/"+ 0+ "/noiseData.dat");
                QDORC qdorc = new QDORC(8, 8E-5);
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }
        // exp2, fix rate and eta, change eps
        outputPath = "./outputdata/updateReal/QDORC/rate=0.02_eta=8/";
        for (double eps : epsArray) {
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/updateReal/" + 0.02 + "/"+ i+ "/noiseData.dat");
                QDORC qdorc = new QDORC(8, eps);
                qdorc.log(outputPath + eps + "/"+ i+ "/repairData.dat");
                Dataset.clearAll();
            }
            startTime = System.currentTimeMillis();
            for (int k=0; k<10000; k++){
                new Dataset().importDataset("./data/updateReal/" + 0.02 + "/"+ 0+ "/noiseData.dat");
                QDORC qdorc = new QDORC(8, eps);
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath+method+"_time.txt", (endTime - startTime)/10000.0);
        }
        // exp3, fix rate and eps, change eta
        outputPath = "./outputdata/updateReal/"+method+"/rate=0.02_eps=8E-5/";
        for (int eta : etaArray) {
            for (int i = 0; i < 10; i++) {
                new Dataset().importDataset("./data/updateReal/" + 0.02 + "/"+ i+ "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, 8E-5);
                qdorc.log(outputPath + eta + "/"+ i+ "/repairData.dat");
                Dataset.clearAll();
            }
            startTime = System.currentTimeMillis();
            for (int k=0; k<10000; k++){
                new Dataset().importDataset("./data/updateReal/" + 0.02 + "/"+ 0+ "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, 8E-5);
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + method + "_time.txt",  ((endTime - startTime)/10000.0));
        }
    }
    
    public void runExample(){
        double[] rateArray = {0.015, 0.03, 0.045, 0.06, 0.075, 0.09, 0.105, 0.12, 0.135, 0.15, 0.165, 0.18};
        int[] etaArray = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        double[] epsArray = {11, 12, 13, 14, 15, 16, 17, 18, 19, 100};

        long startTime, endTime;
        String outputPath;

        String method = "QDORC";

        // �Ȼ�
        for (int i=0; i<20; i++){
            new Dataset().importDataset("./data/updateExample/" + 0.03 + "/noiseData.dat");
            QDORC qdorc = new QDORC(6, 15);
            Dataset.clearAll();
        }

        // exp1, fix eta and eps, change rate
        outputPath = "./outputdata/updateExample/QDORC/eta=6_eps=15/";
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();

            for (int i=0; i<9999; i++) {
                new Dataset().importDataset("./data/updateExample/" + rate + "/noiseData.dat");
                QDORC qdorc = new QDORC(6, 15);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateExample/" + rate + "/noiseData.dat");
            QDORC qdorc = new QDORC(6, 15);
            Dataset.clearAll();

            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }
        // exp2, fix rate and eta, change eps
        outputPath = "./outputdata/updateExample/QDORC/rate=0.03_eta=6/";
        for (double eps : epsArray) {
            startTime = System.currentTimeMillis();

            for (int i=0; i<9999; i++) {
                new Dataset().importDataset("./data/updateExample/" + 0.03 + "/noiseData.dat");
                QDORC qdorc = new QDORC(6, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateExample/" + 0.03 + "/noiseData.dat");
            QDORC qdorc = new QDORC(6, eps);
            Dataset.clearAll();

            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath+method+"_time.txt", (endTime - startTime)/10000.0);
        }
        // exp3, fix rate and eps, change eta
        outputPath = "./outputdata/updateExample/"+method+"/rate=0.03_eps=15/";
        for (int eta : etaArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9999; i++) {
                new Dataset().importDataset("./data/updateExample/" + 0.03 + "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, 15);
                Dataset.clearAll();
            }

            new Dataset().importDataset("./data/updateExample/" + 0.03 + "/noiseData.dat");
            QDORC qdorc = new QDORC(eta, 15);
            Dataset.clearAll();

            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + eta + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }
    }

    public void runFour(){
        double[] rateArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        double[] epsArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        int[] etaArray = {25, 50, 100, 300, 500, 700, 1000, 1200};
        int[] sizeArray = {50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000};

        String outputPath;
        long startTime, endTime;

        //exp 1, change size, fix eps and eta
        outputPath = "./outputdata/foursquare/QDORC/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/foursquare/" + size +"/"+ i +"/noiseData.dat");
                QDORC qdorc = new QDORC(500, 0.06);
                qdorc.log(outputPath + size + "/" + i+ "/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp1-"+size);
        }
        //exp 2, change rate
        outputPath = "./outputdata/foursquare/QDORC/eta=500_eps=0.06/";
        for (double rate:rateArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/foursquare/100000/" + rate + "/" + i + "/noiseData.dat");
                QDORC qdorc = new QDORC(500, 0.06);
                qdorc.log(outputPath + rate + "/" + i +"/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp2-"+rate);
        }
        //exp 3, change eps
        outputPath = "./outputdata/foursquare/QDORC/rate=0.02_eta=500/";
        for (double eps:epsArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/foursquare/100000/" + 0.1 + "/" + i + "/noiseData.dat");
                QDORC qdorc = new QDORC(500, eps);
                qdorc.log(outputPath + eps + "/" + i + "/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp3-"+eps);
        }
        //exp 4, change eta
        outputPath = "./outputdata/foursquare/QDORC/rate=0.02_eps=0.06/";
        for (int eta:etaArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/foursquare/100000/" + 0.1 +  "/" + i +"/noiseData.dat");
                QDORC qdorc = new QDORC(eta, 0.06);
                qdorc.log(outputPath + eta +  "/" + i + "/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp4-"+eta);
        }
    }

    public void runZC(){
        double[] rateArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        double[] epsArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        int[] etaArray = {25, 50, 100, 300, 500, 700, 1000, 1200};
        int[] sizeArray = {19998, 19998*2, 19998*3, 19998*4, 19998*5, 19998*6};

        String outputPath;
        long startTime, endTime;

        //exp 1, change size, fix eps and eta
        outputPath = "./outputdata/zhongchuan/QDORC/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/zhongchuan/" + size +"/"+ i +"/noiseData.dat");
                QDORC qdorc = new QDORC(500, 0.06);
                qdorc.log(outputPath + size + "/" + i+ "/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp1-"+size);
        }
        //exp 2, change rate
        outputPath = "./outputdata/zhongchuan/QDORC/eta=500_eps=0.06/";
        for (double rate:rateArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/zhongchuan/99990/" + rate + "/" + i + "/noiseData.dat");
                QDORC qdorc = new QDORC(500, 0.06);
                qdorc.log(outputPath + rate + "/" + i + "/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp2-"+rate);
        }
        //exp 3, change eps
        outputPath = "./outputdata/zhongchuan/QDORC/rate=0.02_eta=500/";
        for (double eps:epsArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/zhongchuan/99990/" + 0.1 + "/" + i + "/noiseData.dat");
                QDORC qdorc = new QDORC(500, eps);
                qdorc.log(outputPath + eps + "/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp3-"+eps);
        }
        //exp 4, change eta
        outputPath = "./outputdata/zhongchuan/QDORC/rate=0.02_eps=0.06/";
        for (int eta:etaArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/zhongchuan/99990/" + 0.1 +  "/" + i +"/noiseData.dat");
                QDORC qdorc = new QDORC(eta, 0.06);
                qdorc.log(outputPath + eta + "/repairData.dat");
                Dataset.clearAll();
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp4-"+eta);
        }
    }
}
