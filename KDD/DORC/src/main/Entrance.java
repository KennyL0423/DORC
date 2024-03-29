package main;

import grid.DBSCAN;
import grid.GDORC;
import method.*;
import utils.*;
import evaluation.Benchmark;

public class Entrance {

    public static void main(String[] args){
//        foursquare_exp1();

//        foursquare_exp2();
//        foursquare_once();
        foursquare_once_eta();

//        foursquare_exp3();
//        foursquare_exp4();
//        example_exp1();
//        realout_point_one_exp();
//        debug_ldorc();
//        GPS_exp();
//        GPS_exp_eps();
//        GPS_exp_eta();
//        debug_gdorc();
    }
    public static void timeLog(String outputPath, double time){
        LogWriter timeLog = new LogWriter(outputPath);
        timeLog.open();
        timeLog.log(time+"\n");
    }

    public static void foursquare_exp1(){
        String outputPath;
        long startTime, endTime;
        int[] sizeArray = {50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000};
        /* exp1: change size
        eta=500, eps=0.06, rate= 0.1
         */
        // QDORC
//        outputPath = "./outputdata/foursquare/QDORC/change_size/";
//        for (int size : sizeArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/" + size +"/"+ i +"/noiseData.dat");
//                QDORC qdorc = new QDORC(500, 0.06);
//                qdorc.log(outputPath + size + "/" + i+ "/repairData.dat");
//                Dataset.clearAll();
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("QDORC-exp1-"+size);
//        }
//
//        // LDORC
//        outputPath = "./outputdata/foursquare/LDORC/change_size/";
//        for (int size : sizeArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/" + size +"/"+ i +"/noiseData.dat");
//                LDORC ldorc = new LDORC(500, 0.06);
//                ldorc.log(outputPath + size + "/" + i+ "/repairData.dat");
//                Dataset.clearAll();
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "LDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("LDORC-exp1-"+size);
//        }
//
//        // GDORC
//        outputPath = "./outputdata/foursquare/GDORC/change_size/";
//        for (int size : sizeArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                GDORC gdorc = new GDORC(0.06, 500, "./data/foursquare/" + size + "/"+ i + "/noiseData.dat");
//                gdorc.log(outputPath+size+"/" + i + "/repairData.dat");
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath+"GDORC_time.txt" ,(endTime-startTime)/10000.0);
//            Dataset.clearAll();
//            System.out.println("GDORC-exp1-"+size);
//        }

        // DBSCAN
        outputPath = "./outputdata/foursquare/DBSCAN/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                DBSCAN dbscan = new DBSCAN(0.1, 500, "./data/foursquare/" + size + "/"+ i + "/noiseData.dat");
                dbscan.log(outputPath+size+"/" + i + "/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt" ,(endTime-startTime)/10000.0);
            System.out.println((endTime-startTime)/10000.0);
            Dataset.clearAll();
            System.out.println("DBSCAN-exp1-"+size);
        }
    }
    public static void foursquare_once_eta(){
        String outputPath;
        long startTime, endTime;
        double rate = 0.1;
//        int eta = 300;
        double eps = 0.2;
//        int eta_list[] = {50,100,150,200,250,300,350,400,450,500};
        int eta_list[] ={300};
//        double eps_list[] = {0.02, 0.04,0.06, 0.08,0.10};
        //QDORC
//        outputPath = "./outputdataly/foursquare_once/dirty_rate=0.10_eta0.2/QDORC/";
//        for (int eta: eta_list){
//            System.out.println("QDORC: " + eta);
//            startTime = System.currentTimeMillis();
////            for (int i=0; i<10; i++) {
////                new Dataset().importDataset("./data/foursquare/100000/" + rate + "/" + i + "/noiseData.dat");
////                QDORC qdorc = new QDORC(500, 0.06);
////                qdorc.log(outputPath + rate + "/" + i +"/repairData.dat");
////                Dataset.clearAll();
////            }
//            new Dataset().importDataset("./data/foursquare/100000/" + rate + "/0/noiseData.dat");
//            QDORC qdorc = new QDORC(eta, eps);
//            qdorc.log(outputPath + eta +"/repairData.dat");
//            Dataset.clearAll();
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/1000.0);
//        }

        // LDORC
//        outputPath = "./outputdataly/foursquare_once/dirty_rate=0.10_eta0.2/LDORC/";
//        for (int eta: eta_list){
//            System.out.println("LDORC: " + eta);
//            startTime = System.currentTimeMillis();
////            for (int i=0; i<10; i++) {
////                new Dataset().importDataset("./data/foursquare/100000/" + rate + "/" + i + "/noiseData.dat");
////                LDORC ldorc = new LDORC(500, 0.06);
////                ldorc.log(outputPath + rate + "/" + i + "/repairData.dat");
////                Dataset.clearAll();
////            }
//            new Dataset().importDataset("./data/foursquare/100000/" + rate + "/0/noiseData.dat");
//            LDORC ldorc = new LDORC(eta, eps);
//            ldorc.log(outputPath + eta  + "/repairData.dat");
//            Dataset.clearAll();
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "LDORC_time.txt", (endTime-startTime)/1000.0);
//        }

        //GDORC
        outputPath = "./outputdataly/foursquare_once/dirty_rate=0.10_eta0.2/GDORC/";
        for (int eta:eta_list){
            System.out.println("GDORC: " + eta);
            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                GDORC gdorc = new GDORC(0.06, 500, "./data/foursquare/100000/" + rate + "/"+ i +"/noiseData.dat");
//                gdorc.log(outputPath+rate+"/" + i +"/repairData.dat");
//            }
            GDORC gdorc = new GDORC(eps, eta, "./data/foursquare/100000/" + rate + "/0/noiseData.dat");
//            gdorc.log(outputPath + eta + "/repairData.dat");
            endTime = System.currentTimeMillis();
//            timeLog(outputPath+"GDORC_time.txt", (endTime-startTime)/1000.0);
            System.out.println((endTime-startTime)/1000.0);

        }
//        String currentPath = System.getProperty("user.dir");
//        System.out.println("Current working directory: " + currentPath);
////        //DBSCAN_
//        outputPath = "./outputdataly/foursquare_once/dirty_rate=0.10_eta/DBSCAN/";
//        for (int eta:eta_list){
//            startTime = System.currentTimeMillis();
//////            for (int i=0; i<10; i++) {
////                DBSCAN dbscan = new DBSCAN(0.06, 500, "./data/foursquare/100000/" + rate + "/"+ i +"/noiseData.dat");
////                dbscan.log(outputPath+rate+"/" + i +"/repairData.dat");
////            }
//            DBSCAN dbscan = new DBSCAN(eps, eta,  "./data/foursquare/100000/" + rate + "/0/noiseData.dat");
//            dbscan.log(outputPath + eta + "/repairData.dat");
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath+"DBSCAN_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("DBSCAN-exp2-"+rate);
//        }
    }
    public static void foursquare_once(){
        String outputPath;
        long startTime, endTime;
        double rate = 0.10;
        int eta = 300;
//        double eps = 0.06;
        int eta_list[] = {100,200,300,400,500};
//        double eps_list[] = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10};
        double eps_list[] = {0.005, 0.01, 0.02, 0.05, 0.1, 0.2, 0.5};
//        //QDORC
        outputPath = "./outputdataly/foursquare_once/dirty_rate=0.10/QDORC/";
        for (double eps: eps_list){
            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/100000/" + rate + "/" + i + "/noiseData.dat");
//                QDORC qdorc = new QDORC(500, 0.06);
//                qdorc.log(outputPath + rate + "/" + i +"/repairData.dat");
//                Dataset.clearAll();
//            }
            new Dataset().importDataset("./data/foursquare/100000/" + rate + "/0/noiseData.dat");
            QDORC qdorc = new QDORC(eta, eps);
            qdorc.log(outputPath + eps +"/repairData.dat");
            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/1000.0);
        }

        // LDORC
        outputPath = "./outputdataly/foursquare_once/dirty_rate=0.10/LDORC/";
        for (double eps: eps_list){
            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/100000/" + rate + "/" + i + "/noiseData.dat");
//                LDORC ldorc = new LDORC(500, 0.06);
//                ldorc.log(outputPath + rate + "/" + i + "/repairData.dat");
//                Dataset.clearAll();
//            }
            new Dataset().importDataset("./data/foursquare/100000/" + rate + "/0/noiseData.dat");
            LDORC ldorc = new LDORC(eta, eps);
            ldorc.log(outputPath + eps  + "/repairData.dat");
            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "LDORC_time.txt", (endTime-startTime)/1000.0);
        }

//        //GDORC
        outputPath = "./outputdataly/foursquare_once/dirty_rate=0.10/GDORC/";
        for (double eps:eps_list){
            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                GDORC gdorc = new GDORC(0.06, 500, "./data/foursquare/100000/" + rate + "/"+ i +"/noiseData.dat");
//                gdorc.log(outputPath+rate+"/" + i +"/repairData.dat");
//            }
            GDORC gdorc = new GDORC(eps, eta, "./data/foursquare/100000/" + rate + "/0/noiseData.dat");
            gdorc.log(outputPath + eps + "/repairData.dat");
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"GDORC_time.txt", (endTime-startTime)/1000.0);
        }

//        //DBSCAN_
//        outputPath = "./outputdata/foursquare/DBSCAN/eta=500_eps=0.06/";
//        for (double rate:rateArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                DBSCAN dbscan = new DBSCAN(0.06, 500, "./data/foursquare/100000/" + rate + "/"+ i +"/noiseData.dat");
//                dbscan.log(outputPath+rate+"/" + i +"/repairData.dat");
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath+"DBSCAN_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("DBSCAN-exp2-"+rate);
//        }
    }

    public static void foursquare_exp2(){
        String outputPath;
        long startTime, endTime;
        double[] rateArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
//        double[] rateArray = {0.02};
        /* exp2: change rate
        eta=500, eps=0.06, size=100000
        */
        // TODO : eps larger, eta smaller
        int eta = 300;
        double eps = 0.06;
        //QDORC
        outputPath = "./outputdataly/foursquare/QDORC/eta=300_eps=0.06/";
        for (double rate:rateArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/foursquare/100000/" + rate + "/" + i + "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, eps);
                qdorc.log(outputPath + rate + "/" + i +"/repairData.dat");
                Dataset.clearAll();
            }
//            new Dataset().importDataset("./data/foursquare/100000/" + rate + "/0/noiseData.dat");
//            QDORC qdorc = new QDORC(500, 0.06);
//            qdorc.log(outputPath + rate +"/repairData.dat");
//            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
        }

        // LDORC
        outputPath = "./outputdataly/foursquare/LDORC/eta=300_eps=0.06/";
        for (double rate:rateArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                new Dataset().importDataset("./data/foursquare/100000/" + rate + "/" + i + "/noiseData.dat");
                LDORC ldorc = new LDORC(eta, eps);
                ldorc.log(outputPath + rate + "/" + i + "/repairData.dat");
                Dataset.clearAll();
            }
//            new Dataset().importDataset("./data/foursquare/100000/" + rate + "/0/noiseData.dat");
//            LDORC ldorc = new LDORC(500, 0.06);
//            ldorc.log(outputPath + rate  + "/repairData.dat");
//            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "LDORC_time.txt", (endTime-startTime)/10000.0);
        }

        //GDORC
        outputPath = "./outputdataly/foursquare/GDORC/eta=300_eps=0.06/";
        for (double rate:rateArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                GDORC gdorc = new GDORC(eps, eta, "./data/foursquare/100000/" + rate + "/"+ i +"/noiseData.dat");
                gdorc.log(outputPath+rate+"/" + i +"/repairData.dat");
            }
//            GDORC gdorc = new GDORC(0.06, 500, "./data/foursquare/100000/" + rate + "/0/noiseData.dat");
//            gdorc.log(outputPath+rate+"/repairData.dat");
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"GDORC_time.txt", (endTime-startTime)/10000.0);
        }

        //DBSCAN_
        outputPath = "./outputdata/foursquare/DBSCAN/eta=300_eps=0.06/";
        for (double rate:rateArray){
            startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                DBSCAN dbscan = new DBSCAN(eps, eta, "./data/foursquare/100000/" + rate + "/"+ i +"/noiseData.dat");
                dbscan.log(outputPath+rate+"/" + i +"/repairData.dat");
            }
            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("DBSCAN-exp2-"+rate);
        }
    }

    public static void foursquare_exp3(){
        String outputPath;
        long startTime, endTime;
        double[] epsArray = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18};
        /* exp3: change eps
        eta=500, rate=0.1, size=100000
        */
        //QDORC
//        outputPath = "./outputdata/foursquare/QDORC/rate=0.02_eta=500/";
//        for (double eps:epsArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/100000/" + 0.1 + "/" + i + "/noiseData.dat");
//                QDORC qdorc = new QDORC(500, eps);
//                qdorc.log(outputPath + eps + "/" + i + "/repairData.dat");
//                Dataset.clearAll();
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("QDORC-exp3-"+eps);
//        }
//
//        //LDORC
//        outputPath = "./outputdata/foursquare/LDORC/rate=0.02_eta=500/";
//        for (double eps:epsArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/100000/" + 0.1 +  "/" + i +"/noiseData.dat");
//                LDORC ldorc = new LDORC(500, eps);
//                ldorc.log(outputPath + eps + "/" + i+ "/repairData.dat");
//                Dataset.clearAll();
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "LDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("LDORC-exp3-"+eps);
//        }
//
//        //GDORC
//        outputPath = "./outputdata/foursquare/GDORC/rate=0.02_eta=500/";
//        for (double eps:epsArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                GDORC gdorc = new GDORC(eps, 500, "./data/foursquare/100000/" + 0.1 +  "/"+ i + "/noiseData.dat");
//                gdorc.log(outputPath+eps+"/"+ i +"/repairData.dat");
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath+"GDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("GDORC-exp3-"+eps);
//        }

        //DBSCAN
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
    }

    public static void foursquare_exp4(){
        String outputPath;
        long startTime, endTime;
        int[] etaArray = {25, 50, 100, 300, 500, 700, 1000, 1200};
        /* exp4: change eta
        eps=0.06, size=100000, rate=0.1
        */
        //QDORC
//        outputPath = "./outputdata/foursquare/QDORC/rate=0.02_eps=0.06/";
//        for (int eta:etaArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/100000/" + 0.1 +  "/" + i +"/noiseData.dat");
//                QDORC qdorc = new QDORC(eta, 0.06);
//                qdorc.log(outputPath + eta +  "/" + i + "/repairData.dat");
//                Dataset.clearAll();
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("QDORC-exp4-"+eta);
//        }
//        //LDORC
//        outputPath = "./outputdata/foursquare/LDORC/rate=0.02_eps=0.06/";
//        for (int eta:etaArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                new Dataset().importDataset("./data/foursquare/100000/" + 0.1 + "/" + i + "/noiseData.dat");
//                LDORC ldorc = new LDORC(eta, 0.06);
//                ldorc.log(outputPath + eta +  "/" + i+"/repairData.dat");
//                Dataset.clearAll();
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath + "LDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("LDORC-exp4-"+eta);
//        }
//        //GDORC
//        outputPath = "./outputdata/foursquare/GDORC/rate=0.02_eps=0.06/";
//        for (int eta:etaArray){
//            startTime = System.currentTimeMillis();
//            for (int i=0; i<10; i++) {
//                GDORC gdorc = new GDORC(0.06, eta, "./data/foursquare/100000/" + 0.1 + "/"+ i + "/noiseData.dat");
//                gdorc.log(outputPath+eta+"/"+ i +"/repairData.dat");
//            }
//            endTime = System.currentTimeMillis();
//            timeLog(outputPath+"GDORC_time.txt", (endTime-startTime)/10000.0);
//            System.out.println("GDORC-exp4-"+eta);
//        }
        // DBSCAN
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

    public static void minibatch(){
        String outputPath;
        long startTime, endTime;
        int[] sizeArray = {50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000};
        // QDORC
        outputPath = "./outputdata/test/QDORC/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();
            new Dataset().importDataset("./data/foursquare/" + size +"/newNoiseData.dat");
            QDORC qdorc = new QDORC(500, 0.06);
            qdorc.log(outputPath + size + "/repairData.dat");
            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "QDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("QDORC-exp1-"+size);
        }

        // LDORC
        outputPath = "./outputdata/foursquare/LDORC/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();
            new Dataset().importDataset("./data/foursquare/" + size +"/newNoiseData.dat");
            LDORC ldorc = new LDORC(500, 0.06);
            ldorc.log(outputPath + size + "/repairData.dat");
            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            timeLog(outputPath + "LDORC_time.txt", (endTime-startTime)/10000.0);
            System.out.println("LDORC-exp1-"+size);
        }

        // GDORC
        outputPath = "./outputdata/foursquare/GDORC/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();

            GDORC gdorc = new GDORC(0.06, 500, "./data/foursquare/" + size + "/newNoiseData.dat");
            gdorc.log(outputPath+size+"/repairData.dat");

            endTime = System.currentTimeMillis();
            timeLog(outputPath+"GDORC_time.txt" ,(endTime-startTime)/10000.0);
            Dataset.clearAll();
            System.out.println("GDORC-exp1-"+size);
        }

        // DBSCAN
        outputPath = "./outputdata/foursquare/DBSCAN/change_size/";
        for (int size : sizeArray){
            startTime = System.currentTimeMillis();

            DBSCAN dbscan = new DBSCAN(0.06, 500, "./data/foursquare/" + size  + "/newNoiseData.dat");
            dbscan.log(outputPath+size+ "/repairData.dat");

            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt" ,(endTime-startTime)/10000.0);
            System.out.println((endTime-startTime)/10000.0);
            Dataset.clearAll();
            System.out.println("DBSCAN-exp1-"+size);
        }
    }

    public static void realout_point_one_exp(){
        double[] rateArray = {0.03, 0.06, 0.09, 0.12, 0.15, 0.18};
        long startTime, endTime;
        String outputPath;
        int cur_eta = 8;
        double cur_eps = 8e-5;


        String method = "QDORC";
//        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/selectedRealOut/";
//        warm up engine
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            new Dataset().importDataset("./data/selectedRealOut/noiseDataOut.dat");
            QDORC qdorc = new QDORC(cur_eta, cur_eps);
            Dataset.clearAll();
        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
                QDORC qdorc = new QDORC(cur_eta, cur_eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
            QDORC qdorc = new QDORC(cur_eta, cur_eps);
            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + rate + "/" + method + "repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime) / 10000.0);
        }

        method = "LDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/selectedRealOut/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 29; i++) {
            new Dataset().importDataset("./data/selectedRealOut/noiseDataOut.dat");
            LDORC LDORC = new LDORC(cur_eta, cur_eps);
            Dataset.clearAll();
        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
                LDORC LDORC = new LDORC(cur_eta, cur_eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
            LDORC ldorc = new LDORC(cur_eta, cur_eps);
            Dataset.clearAll();
            endTime = System.currentTimeMillis();
            ldorc.log(outputPath + rate + "/" + method + "repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }


        method = "GDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/selectedRealOut/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            GDORC gdorc = new GDORC(cur_eps, cur_eta, "./data/selectedRealOut/noiseDataOut.dat");
        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                GDORC gdorc = new GDORC(cur_eps, cur_eta, "./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
            }
            GDORC gdorc = new GDORC(cur_eps, cur_eta, "./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
            endTime = System.currentTimeMillis();
            gdorc.log(outputPath + rate + "/" + method + "repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }


        // DBSCAN
        method = "DBSCAN";
        outputPath = "./outputdataly/selectedRealOut/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            DBSCAN dbscan = new DBSCAN(cur_eps, cur_eta, "./data/selectedRealOut/noiseDataOut.dat");
        }
        for (double rate : rateArray){
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                DBSCAN dbscan = new DBSCAN(cur_eps, cur_eta, "./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
            }
            DBSCAN dbscan = new DBSCAN(cur_eps, cur_eta, "./data/selectedRealOut/" + rate + "/noiseDataOut.dat");
            endTime = System.currentTimeMillis();
            dbscan.log(outputPath + rate + "/" + method + "repairData.dat");
            timeLog(outputPath + method + "_time.txt" ,(endTime-startTime)/10000.0);
            Dataset.clearAll();
        }
    }



    public static void debug_gdorc(){
        GDORC gdorc = new GDORC(15, 6, "./data/updateExample/" + "0.21" + "/noiseData.dat");
    }

    public static void debug_ldorc(){
        double rate = 0.09;
        String method = "LDORC";
        String outputPath = "./outputdataly/LDORC_0.09/";
        new Dataset().importDataset("./data/updateExample/partData.dat");
        LDORC LDORC = new LDORC(6, 15);
        Dataset.clearAll();
        LDORC.log(outputPath + rate + "/repairData.dat");
    }

    public static void example_exp1(){
        double[] rateArray = {0.015, 0.03, 0.045, 0.06, 0.075, 0.09, 0.105, 0.12, 0.135, 0.15, 0.165, 0.18, 0.195, 0.21};
//        double[] rateArray = {0.06, 0.12, 0.15, 0.18};
//        double[] rateArray = {0.18};
        long startTime, endTime;
        String outputPath;
        int eta = 6;
        double eps = 15;

        String method = "QDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateExample/QDORC/eta="+eta+"_eps="+(int)eps+"/";
//        startTime = System.currentTimeMillis();
//        for (int i = 0; i < 19; i++) {
//            new Dataset().importDataset("./data/updateExample/" + "0.03" + "/noiseData.dat");
//            QDORC qdorc = new QDORC(eta, eps);
//            Dataset.clearAll();
//        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateExample/" + rate + "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateExample/" + rate + "/noiseData.dat");
            QDORC qdorc = new QDORC(eta, eps);
            Dataset.clearAll();

            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime) / 10000.0);
        }

        method = "LDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateExample/LDORC/eta="+eta+"_eps="+(int)eps+"/";
//        startTime = System.currentTimeMillis();
//        for (int i = 0; i < 19; i++) {
//            new Dataset().importDataset("./data/updateExample/" + "0.03" + "/noiseData.dat");
//            LDORC LDORC = new LDORC(eta, eps);
//            Dataset.clearAll();
//        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateExample/" + rate + "/noiseData.dat");
                LDORC LDORC = new LDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateExample/" + rate + "/noiseData.dat");
            LDORC ldorc = new LDORC(eta, eps);
            Dataset.clearAll();
            endTime = System.currentTimeMillis();

            ldorc.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        method = "GDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateExample/GDORC/eta="+eta+"_eps="+(int)eps+"/";
//        startTime = System.currentTimeMillis();
//        for (int i = 0; i < 19; i++) {
//            GDORC gdorc = new GDORC(eps, eta, "./data/updateExample/" + "0.03" + "/noiseData.dat");
//        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                GDORC gdorc = new GDORC(eps, eta, "./data/updateExample/" + rate + "/noiseData.dat");
            }
            GDORC gdorc = new GDORC(eps, eta, "./data/updateExample/" + rate + "/noiseData.dat");
            endTime = System.currentTimeMillis();
            gdorc.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        // DBSCAN
        outputPath = "./outputdataly/updateExample/DBSCAN/eta="+eta+"_eps="+(int)eps+"/";
        startTime = System.currentTimeMillis();
//        for (int i = 0; i < 19; i++) {
//            DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateExample/" + "0.03" + "/noiseData.dat");
//        }
        for (double rate : rateArray){
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateExample/" + rate + "/noiseData.dat");
            }
            DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateExample/" + rate + "/noiseData.dat");
            dbscan.log(outputPath + rate + "/repairData.dat");

            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt" ,(endTime-startTime)/10000.0);
            Dataset.clearAll();
        }
    }
    public static void GPS_exp_eta(){
        double rate = 0.12;
        double eps = 8e-5;
        long startTime, endTime;
        String outputPath;
        int[] etaArray = {5,6,7,8,9,10,11,12,13,14};
        String std = "std=0.0001/";
//        String epsLine = "_eps=1e-4/";
        String method = "QDORC";
//        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/eta_test/" + std + "QDORC/eps="+eps + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            new Dataset().importDataset("./data/updateReal/" + std + "0.03" + "/noiseData.dat");
            QDORC qdorc = new QDORC(8, 10e-5);
            Dataset.clearAll();
        }
        for (int eta : etaArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
            QDORC qdorc = new QDORC(eta, eps);
            Dataset.clearAll();

            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime) / 10000.0);
        }

        method = "LDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/eta_test/" + std + "LDORC/eps="+eps + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            new Dataset().importDataset("./data/updateReal/" + std + "0.03" + "/noiseData.dat");
            LDORC LDORC = new LDORC(8, 10e-5);
            Dataset.clearAll();
        }
        for (int eta : etaArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
                LDORC LDORC = new LDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
            LDORC ldorc = new LDORC(eta, eps);
            Dataset.clearAll();
            endTime = System.currentTimeMillis();

            ldorc.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        method = "GDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/eta_test/" + std + "GDORC/eps="+eps + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            GDORC gdorc = new GDORC(10e-5, 8, "./data/updateReal/" + std + "0.03" + "/noiseData.dat");
        }
        for (int eta : etaArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                GDORC gdorc = new GDORC(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            }
            GDORC gdorc = new GDORC(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");

            endTime = System.currentTimeMillis();
            gdorc.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        // DBSCAN
        outputPath = "./outputdataly/updateReal/eta_test/" + std + "DBSCAN/eps="+eps + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            DBSCAN dbscan = new DBSCAN(10e-5, 8, "./data/updateReal/" + std + "0.03" + "/noiseData.dat");
        }
        for (int eta : etaArray){
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            }
            DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            dbscan.log(outputPath + eps + "/repairData.dat");

            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt" ,(endTime-startTime)/10000.0);
            Dataset.clearAll();
        }


    }
    public static void GPS_exp_eps(){
        double rate = 0.12;
        double[] epsArray = {5e-5, 6e-5, 7e-5, 8e-5, 9e-5, 10e-5, 11e-5, 12e-5, 13e-5, 14e-5, 15e-5};
        long startTime, endTime;
        String outputPath;
        int eta = 8;
        String std = "std=0.0001/";
//        String epsLine = "_eps=1e-4/";
        String method = "QDORC";
//        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/eps_test/" + std + "QDORC/eta="+eta + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            new Dataset().importDataset("./data/updateReal/" + std + "0.03" + "/noiseData.dat");
            QDORC qdorc = new QDORC(eta, 10e-5);
            Dataset.clearAll();
        }
        for (double eps : epsArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
            QDORC qdorc = new QDORC(eta, eps);
            Dataset.clearAll();

            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime) / 10000.0);
        }

        method = "LDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/eps_test/" + std + "LDORC/eta="+eta + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            new Dataset().importDataset("./data/updateReal/" + std + "0.03" + "/noiseData.dat");
            LDORC LDORC = new LDORC(eta, 10e-5);
            Dataset.clearAll();
        }
        for (double eps : epsArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
                LDORC LDORC = new LDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
            LDORC ldorc = new LDORC(eta, eps);
            Dataset.clearAll();
            endTime = System.currentTimeMillis();

            ldorc.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        method = "GDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/eps_test/" + std + "GDORC/eta="+eta + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            GDORC gdorc = new GDORC(10e-5, eta, "./data/updateReal/" + std + "0.03" + "/noiseData.dat");
        }
        for (double eps : epsArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                GDORC gdorc = new GDORC(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            }
            GDORC gdorc = new GDORC(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");

            endTime = System.currentTimeMillis();
            gdorc.log(outputPath + eps + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        // DBSCAN
        outputPath = "./outputdataly/updateReal/eps_test/" + std + "DBSCAN/eta="+eta + "_rate=" +rate + "/";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            DBSCAN dbscan = new DBSCAN(10e-5, eta, "./data/updateReal/" + std + "0.03" + "/noiseData.dat");
        }
        for (double eps : epsArray){
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            }
            DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            dbscan.log(outputPath + eps + "/repairData.dat");

            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt" ,(endTime-startTime)/10000.0);
            Dataset.clearAll();
        }
    }

    public static void GPS_exp(){
        double[] rateArray = {0.015, 0.03, 0.045, 0.06, 0.075, 0.09, 0.105, 0.12, 0.135, 0.15, 0.165, 0.18, 0.195, 0.21};
        long startTime, endTime;
        String outputPath;
        int eta = 6;
        double eps = 1e-4;
        String std = "std=0.0001/";
        String epsLine = "_eps=1e-4/";
        String method = "QDORC";
//        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/" + std + "QDORC/eta="+eta+epsLine;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            new Dataset().importDataset("./data/updateReal/" + std + "0.03" + "/noiseData.dat");
            QDORC qdorc = new QDORC(eta, eps);
            Dataset.clearAll();
        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
                QDORC qdorc = new QDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
            QDORC qdorc = new QDORC(eta, eps);
            Dataset.clearAll();

            endTime = System.currentTimeMillis();
            qdorc.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime) / 10000.0);
        }

        method = "LDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/" + std + "LDORC/eta="+eta+epsLine;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            new Dataset().importDataset("./data/updateReal/" + std + "0.03" + "/noiseData.dat");
            LDORC LDORC = new LDORC(eta, eps);
            Dataset.clearAll();
        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
                LDORC LDORC = new LDORC(eta, eps);
                Dataset.clearAll();
            }
            new Dataset().importDataset("./data/updateReal/" + std + rate + "/noiseData.dat");
            LDORC ldorc = new LDORC(eta, eps);
            Dataset.clearAll();
            endTime = System.currentTimeMillis();

            ldorc.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        method = "GDORC";
        // exp1, fix eta and eps, change rate
        outputPath = "./outputdataly/updateReal/" + std + "GDORC/eta="+eta+epsLine;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            GDORC gdorc = new GDORC(eps, eta, "./data/updateReal/" + std + "0.03" + "/noiseData.dat");
        }
        for (double rate : rateArray) {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                GDORC gdorc = new GDORC(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            }
            GDORC gdorc = new GDORC(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");

            endTime = System.currentTimeMillis();
            gdorc.log(outputPath + rate + "/repairData.dat");
            timeLog(outputPath + method + "_time.txt", (endTime - startTime)/10000.0);
        }

        // DBSCAN
        outputPath = "./outputdataly/updateReal/" + std + "DBSCAN/eta="+eta+epsLine;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateReal/" + std + "0.03" + "/noiseData.dat");
        }
        for (double rate : rateArray){
            startTime = System.currentTimeMillis();

            for (int i = 0; i < 9; i++) {
                DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            }
            DBSCAN dbscan = new DBSCAN(eps, eta, "./data/updateReal/" + std + rate + "/noiseData.dat");
            dbscan.log(outputPath + rate + "/repairData.dat");

            endTime = System.currentTimeMillis();
            timeLog(outputPath+"DBSCAN_time.txt" ,(endTime-startTime)/10000.0);
            Dataset.clearAll();
        }


    }
}
