package method;

import utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class QDORC {
    ArrayList<DORCStruct> pointArray;
    public int eta;
    public double eps;
    int n;

    public QDORC(int eta, double eps){
        n = Dataset.dataset.size();
        pointArray = new ArrayList<DORCStruct>();
        this.eta = eta;
        this.eps = eps;
//        originalInitialize();
//        originalRun();
        initialize();
        run();
    }

    private void initialize(){
        // traverse all points and update the info
        for (int i=0; i<n; i++){
            DORCStruct dorc = new DORCStruct(n, Dataset.dataset.get(i));
            // update parameters w, h and c
            for (int j=0; j<n; j++){
                double dis = Point.getDistance(dorc.p, Dataset.dataset.get(j));
                if (dis <= eps)
                    dorc.c++;
            }
            // determine point type
            if(dorc.c >= eta) dorc.p.state = Point.CORE;
            else{
                dorc.p.state = Point.NOISE;
            }
            if (dorc.p.state == Point.CORE) dorc.hasVisitedNeighbor=true;
            pointArray.add(dorc);
        }
        for (DORCStruct dorc : pointArray){
            if (dorc.p.state==Point.NOISE) {
                for (int k = 0; k < n; k++) {
                    if (Point.getDistance(dorc.p, Dataset.dataset.get(k)) <= eps
                            && Dataset.dataset.get(k).state == Point.CORE) {
                        dorc.p.state = Point.EDGE;
                        break;
                    }
                }
            }
        }

        // update visited info and y
        for (DORCStruct dorc: pointArray){
            if (dorc.p.state==Point.CORE) dorc.state=DORCStruct.VISITED;
            else dorc.state=DORCStruct.NOTVISITED;
            if (dorc.c >= eta) dorc.y = 1.0;
            else dorc.y = (double)dorc.c / eta;
        }
    }

    public void run(){
        ArrayList<DORCStruct> noiseArray = new ArrayList<DORCStruct>();
        ArrayList<DORCStruct> unvisitedArray = new ArrayList<DORCStruct>();
        // update noise array and unvisited array
        for (DORCStruct dorc : pointArray){
            if (dorc.p.state == Point.NOISE){
                noiseArray.add(dorc);
                unvisitedArray.add(dorc);
            }
            if (dorc.p.state == Point.EDGE){
                unvisitedArray.add(dorc);
            }
        }
        // sort unvisitedArray by value y
        Collections.sort(unvisitedArray, new Comparator<DORCStruct>() {
            @Override
            public int compare(DORCStruct o1, DORCStruct o2) {
                if (o1.y < o2.y) return -1;
                if (o1.y > o2.y) return 1;
                return 0;
            }
        });
        // QDORC begins
        while (noiseArray.size()>0){
            System.out.println("QDORC Noise size: " + noiseArray.size());
            // get the point p_j with the maximum y
            DORCStruct dorc = unvisitedArray.get(unvisitedArray.size()-1);
            dorc.state = DORCStruct.VISITED;
            unvisitedArray.remove(unvisitedArray.size()-1);
            int self_noise = 0;
            if(noiseArray.contains(dorc)){
                self_noise = 1;
            }
            // 需要排除目前已经是neighbor的noise
            int noise_neighbor_cnt = 0;
            for( DORCStruct noise: noiseArray){
                if(dorc.p.id == noise.p.id){
                    continue;
                }
                if(Point.getDistance(dorc.p, noise.p) <= eps){
                    noise_neighbor_cnt++;
                }
            }
            if (noiseArray.size() - self_noise - noise_neighbor_cnt>=(1-dorc.y)*eta) {
                for (int k=0; k<(1-dorc.y)*eta; k++) {
                    // find point p_i in noise set with the minimum distance
                    double minv = Double.MAX_VALUE;
                    DORCStruct minDORC = null;
                    for (DORCStruct d : noiseArray) {
                        if (Point.getDistance(dorc.p, d.p) < minv) {
                            minv = Point.getDistance(dorc.p, d.p);
                            minDORC = d;
                        }
                    }
                    // update status of p_i
                    noiseArray.remove(minDORC);
                    unvisitedArray.remove(minDORC);
                    minDORC.state = DORCStruct.VISITED;
                    minDORC.redirectPoint = dorc.p;
                    dorc.c++;
                }
                // update status of p_j
                dorc.y = 1;
                noiseArray.remove(dorc);
                for (int i=0; i<n; i++)
                    if (Point.getDistance(dorc.p, Dataset.dataset.get(i))<=eps)
                        noiseArray.remove(Dataset.dataset.get(i));
            }
            else{
                Iterator<DORCStruct> iterator = noiseArray.iterator();
                while (iterator.hasNext()){
                    DORCStruct d1 = iterator.next();
                    // find the non-noise point p_k with the minimum distance
                    double minv = Double.MAX_VALUE;
                    DORCStruct minDORC = null;
                    for (DORCStruct d2: pointArray){
                        if (d2.p.state==Point.CORE || d2.p.state==Point.EDGE)
                            if (Point.getDistance(d1.p, d2.p)<minv){
                                minv = Point.getDistance(d1.p, d2.p);
                                minDORC = d2;
                            }
                    }
                    // move d1 to minDORC
                    d1.redirectPoint = minDORC.p;
                    iterator.remove();
                    unvisitedArray.remove(d1);
                    minDORC.c ++;
                }
            }
        }
        // sort by point id
        Collections.sort(pointArray, new Comparator<DORCStruct>(){
            @Override
            public int compare(DORCStruct o1, DORCStruct o2) {
                if(o1.p.id < o2.p.id)
                    return -1;
                if(o2.p.id > o2.p.id)
                    return 1;
                return 0;
            }
        });
    }
    
    public void originalInitialize(){
        for(int i = 0; i < n; i++){
            DORCStruct dorc = new DORCStruct(n, Dataset.dataset.get(i));
            for(int j = 0; j < n; j++){
                if(Point.getDistance(Dataset.dataset.get(j), dorc.p) <= this.eps){
                    dorc.c++;
                }
                if(Dataset.dataset.get(j).state == Point.CORE){
                    dorc.hasVisitedNeighbor = true;
                }
            }
            if(dorc.c >= this.eta)
                dorc.p.state = Point.CORE;
            else
                dorc.p.state = Point.NOISE;
            if(dorc.p.state == Point.CORE){
                for(int j = 0; j < n; j++){
                    if(Point.getDistance(Dataset.dataset.get(j), dorc.p) <= this.eps && i != j){
                        if(Dataset.dataset.get(j).state == Point.NOISE)
                            Dataset.dataset.get(j).state = Point.EDGE;
                    }
                }
            }
            if(dorc.p.state == Point.CORE || dorc.p.state == Point.EDGE)
                dorc.hasVisitedNeighbor = true;
            pointArray.add(dorc);
        }

        for(DORCStruct dorc : pointArray){
            if(dorc.p.state == Point.CORE){
                dorc.state = DORCStruct.VISITED;
            }else
                dorc.state = DORCStruct.NOTVISITED;
        }
    }

    public void originalRun(){
        ArrayList<DORCStruct> noiseArray = new ArrayList<DORCStruct>();
        ArrayList<DORCStruct> unvisitedArray = new ArrayList<DORCStruct>();
        for(DORCStruct dorc : pointArray){
            if(dorc.p.state == Point.NOISE){
                noiseArray.add(dorc);
                unvisitedArray.add(dorc);
            }else if(dorc.p.state == Point.EDGE){
                unvisitedArray.add(dorc);
            }
        }
        Collections.sort(unvisitedArray, new Comparator<DORCStruct>(){
            @Override
            public int compare(DORCStruct o1, DORCStruct o2) {
                if(o1.c < o2.c)
                    return -1;
                if(o1.c > o2.c)
                    return 1;
                return 0;
            }
        });

        while(noiseArray.size() > 0){
            DORCStruct dorc = unvisitedArray.get(unvisitedArray.size() - 1);
            dorc.state = DORCStruct.VISITED;
            unvisitedArray.remove(unvisitedArray.size() - 1);
            ArrayList<DORCStruct> tmpCombine = new ArrayList<DORCStruct>();
            while(dorc.c < this.eta){
                //找到noise中距离最近的点min
                DORCStruct min = null;
                double minValue = Double.MAX_VALUE;
                for(DORCStruct d : noiseArray){
                    if(minValue > Point.getDistance(d.p, dorc.p)){
                        minValue = Point.getDistance(d.p, dorc.p);
                        min = d;
                    }
                }
                if(min == null){
                    for(DORCStruct d : tmpCombine){
                        //找到non-noise中距离最近的点tmpMin
                        DORCStruct tmpMin = null;
                        double tmpMinValue = Double.MAX_VALUE;
                        for(DORCStruct tmpd : pointArray){
                            if(tmpd.p.state == Point.CORE || tmpd.p.state == Point.EDGE){
                                if(tmpMinValue > Point.getDistance(d.p, tmpd.p)){
                                    tmpMin = tmpd;
                                    tmpMinValue = Point.getDistance(d.p, tmpd.p);
                                }
                            }
                        }
                        d.redirectPoint = tmpMin.p;
                    }
                    break;
                }
                noiseArray.remove(min);
                min.state = DORCStruct.VISITED;
                min.redirectPoint = dorc.p;
                dorc.c++;
                tmpCombine.add(min);
            }
            tmpCombine.clear();
        }

        Collections.sort(pointArray, new Comparator<DORCStruct>(){
            @Override
            public int compare(DORCStruct o1, DORCStruct o2) {
                if(o1.p.id < o2.p.id)
                    return -1;
                if(o2.p.id > o2.p.id)
                    return 1;
                return 0;
            }
        });
    }

    public void log(String path){
        LogWriter repairLog = new LogWriter(path);
        repairLog.open();
        for(DORCStruct doc : pointArray){
            if(doc.redirectPoint == null){
                repairLog.log(doc.p.id+"\t"+doc.p.x+"\t"+doc.p.y+"\n");
            }else{
                repairLog.log(doc.p.id+"\t"+doc.redirectPoint.x+"\t"+doc.redirectPoint.y+"\n");
            }
        }
    }

}
