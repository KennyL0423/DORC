package evaluation;
import utils.*;

import java.io.*;
import java.util.ArrayList;
import utils.LogWriter;


public class Benchmark {

    public int eta;
    public double eps;

    public ArrayList<Point> predArray = new ArrayList<Point>();
    public ArrayList<Point> truthArray = new ArrayList<Point>();
    public Cluster predCluster;
    public Cluster truthCluster;

    public Benchmark(String predPath, String truthPath, int eta, double eps) {
        importData(predPath, predArray);
        importData(truthPath, truthArray);
        this.eta = eta;
        this.eps = eps;
        predCluster = new Cluster(predArray, eta, eps); // update the poi of predArray
        truthCluster = new Cluster(truthArray, eta,eps);
    }

    public Benchmark(String predPath, String truthPath, String logPath, int eta, double eps){
        importData(predPath, predArray);
        importData(truthPath, truthArray);
        this.eta = eta;
        this.eps = eps;
        predCluster = new Cluster(predArray, eta, eps); // update the poi of predArray
        truthCluster = new Cluster(truthArray, eta,eps);

        LogWriter lw = new LogWriter(logPath);
        lw.open();
        lw.log(""+getPurity()+","+getNMI()+","+getError()+"\n");
    }

    public void importData(String dataPath, ArrayList<Point> dataArray){
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(dataPath)));
            String str = "";
            while((str = br.readLine()) != null){
                String[] tmp = str.split("\t");
                Point point = new Point(tmp[0], tmp[1], tmp[2]);
                dataArray.add(point);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double getPurity(){
        int predCnt = predCluster.count;
        int truthCnt = truthCluster.count;
        int[][] cntMatrix = new int [predCnt+1][truthCnt+1];
        for (int i=0; i<predCnt+1; i++)
            for (int j=0; j<truthCnt+1; j++)
                cntMatrix[i][j]=0;

        for (Point p: predArray){
            int id = p.id;
            int truthClassId = truthArray.get(id).poi;
            int predClassId = p.poi;
            cntMatrix[predClassId+1][truthClassId+1] ++;
        }

        int purityCnt = 0;
        for (int i=1; i<predCnt+1; i++){
            int maxCnt = 0;
            for (int j=1; j<truthCnt +1; j++){
                if (cntMatrix[i][j] > maxCnt) maxCnt = cntMatrix[i][j];
            }
            purityCnt += maxCnt;
        }
        return ((double) purityCnt / (double) truthCluster.dataArray.size());
    }

    public double getNMI(){
        int n = truthArray.size();
        int[] A = new int[n];
        int[] B = new int[n];

        int pointer = 0, counter = -1;
        for (int i=0; i<n; i++) {
            A[i] = truthArray.get(i).poi;
            if (pointer>=predArray.size()) {
                B[i] = counter;
                counter --;
            }
            else {
                if (predArray.get(pointer).id != i) {
                    B[i] = counter;
                    counter --;
                }
                 else {
                    B[i] = predArray.get(pointer).poi;
                    pointer++;
                }
            }
        }
//        for (int i=0;i<n; i++){
//            System.out.print(A[i]+",");
//        }
//        System.out.print("\n");
//        for (int i=0;i<n; i++){
//            System.out.print(B[i]+",");
//        }
//        System.out.print("\n");

        ArrayList<Integer> A_ids = new ArrayList<Integer>();
        ArrayList<Integer> B_ids = new ArrayList<Integer>();
        for (int i=0; i<n; i++){
            if (!A_ids.contains(A[i])) A_ids.add((int) A[i]);
            if (!B_ids.contains(B[i])) B_ids.add((int) B[i]);
        }
        double MI=0;
        for(int i = 0; i < A_ids.size(); i++){
            for(int j = 0; j < B_ids.size(); j++){
                double px = 0;
                double py = 0;
                double pxy = 0;
                for(int s = 0; s < n; s++){
                    if(A[s] == A_ids.get(i))
                        px++;
                    if(B[s] == B_ids.get(j))
                        py++;
                    if(A[s] == (Integer) A_ids.get(i) && B[s] == (Integer) B_ids.get(j))
                        pxy++;
                }
                px = px/n;
                py = py/n;
                pxy = pxy/n;
                MI += pxy * Math.log(pxy/(px*py)+ 0.0000000000001)/ Math.log(2);
            }
        }

        double Hx = 0;
        ArrayList<Integer> acount = new ArrayList<Integer>();
        for(int i = 0; i < A_ids.size(); i++)
            acount.add(0);
        for(int i = 0; i < n ; i++){
            int index = A_ids.indexOf(A[i]);
            acount.set(index, acount.get(index) + 1);
        }
        for(int i = 0; i < acount.size(); i++)
            Hx = Hx - (acount.get(i)/(double)n) * Math.log(acount.get(i)/(double)n + 0.0000000000001)/ Math.log(2);
        double Hy = 0;
        ArrayList<Integer> bcount = new ArrayList<Integer>();
        for(int i = 0; i < B_ids.size(); i++)
            bcount.add(0);
        for(int i = 0; i < n; i++){
            int index = B_ids.indexOf(B[i]);
            bcount.set(index, bcount.get(index) + 1);
        }
        for(int i = 0; i < bcount.size(); i++)
            Hy = Hy - (bcount.get(i)/(double)n) * Math.log(bcount.get(i)/(double)n + 0.0000000000001)/ Math.log(2);

        return 2.0 * MI / (Hx+Hy);

    }

    public double getError(){
        int n = predArray.size();
        double res = 0;
        for (int i=0; i<n; i++){
            res += Math.pow(Point.getDistance(predArray.get(i), truthArray.get(i)), 2);
        }
        res = Math.sqrt(res/n);
        return res;
    }



}
