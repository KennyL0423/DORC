package method;

import utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class LDORC {
    public ArrayList<Leader> leaders;
    ArrayList<DORCStruct> pointArray;
    public int eta;
    public double eps;
    public double tau;
    int n;

    public LDORC(int eta, double eps){
        n = Dataset.dataset.size();
        this.leaders = new ArrayList<Leader>();
        pointArray = new ArrayList<DORCStruct>();
        this.eta = eta;
        this.eps = eps;
        this.tau = eps/5;
        initialize();
        newrun();
    }

    private void initialize(){
        for (int i=0; i<n; i++){
            DORCStruct dorc = new DORCStruct(n, Dataset.dataset.get(i));
            pointArray.add(dorc);
        }
        leaderCluster();
        getEpsilonNeighbor();
    }

    private void leaderCluster(){
        for(DORCStruct dorc : pointArray){
            Leader l = null;
            for(Leader leader : leaders){
                if(Point.getDistance(dorc.p, leader.leader) <= tau){
                    l = leader;
                    break;
                }
            }
            if(l == null){
                Leader newLeader = new Leader();
                newLeader.leader = dorc.p;
                newLeader.followers.add(dorc.p);
                newLeader.count = 1;
                leaders.add(newLeader);
            }else{
                l.followers.add(dorc.p);
                l.count++;
            }
        }
    }

    private void getEpsilonNeighbor(){
        for(DORCStruct dorc : pointArray) {
            for (Leader leader : leaders) {
                if ( Point.getDistance(leader.leader, dorc.p) <= this.eps - tau) {
                    dorc.c += leader.count;
                }
            }
            if (dorc.c >= this.eta)
                dorc.p.state = Point.CORE;
            if(dorc.p.state == Point.CORE){
                for(Leader leader : leaders){
                    if(Point.getDistance(leader.leader, dorc.p) <= this.eps - tau){
                        for(Point followers : leader.followers)
                            if(followers.state == Point.NOISE) {
                                followers.state = Point.EDGE;
                            }
                    }
                }
            }else{
                if(dorc.p.state != Point.EDGE){
                    dorc.p.state = Point.NOISE;
                }
            }
        }
        for (DORCStruct dorc: pointArray){
            if (dorc.p.state==Point.CORE) dorc.state=DORCStruct.VISITED;
            else dorc.state=DORCStruct.NOTVISITED;
            if (dorc.c >= eta) dorc.y = 1.0;
            else dorc.y = (double)dorc.c / eta;
        }
    }


    private void run(){
        ArrayList<DORCStruct> noiseArray = new ArrayList<DORCStruct>();
        ArrayList<DORCStruct> unvisitedArray = new ArrayList<DORCStruct>();
        for(DORCStruct dorc : pointArray){
            if(dorc.p.state == Point.NOISE){
                dorc.c = 0;
                noiseArray.add(dorc);
                unvisitedArray.add(dorc);
//                System.out.println(dorc.p.id);
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
            while(dorc.c < eta){
                //找到dorc在noise中距离最近的点，
                DORCStruct min = null;
                double minValue = Double.MAX_VALUE;
                for(DORCStruct d : noiseArray){
                    double dist = Point.getDistance(d.p, dorc.p);
                    if(minValue > dist){
                        minValue = dist;
                        min = d;
                    }
                }

                if(min == null){
                    //把 tmpcombine里面的元素分配给最近的点。
                    for(DORCStruct d : tmpCombine){
                        DORCStruct tmpMin = null;
                        double tmpMinValue = Double.MAX_VALUE;
                        for(DORCStruct tmpd : pointArray){
                            if(tmpd.p.state == Point.CORE || tmpd.p.state == Point.EDGE){
                                double dist = Point.getDistance(tmpd.p, d.p);
                                if(tmpMinValue > dist){
                                    tmpMin = tmpd;
                                    tmpMinValue = dist;
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

        //把所有的pointArray按照p.id进行排序
        //对于所有的DORCStruct进行输出
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

    public void newrun(){
        ArrayList<DORCStruct> noiseArray = new ArrayList<DORCStruct>();
        ArrayList<DORCStruct> unvisitedArray = new ArrayList<DORCStruct>();

        // update noise array and unvisited array
        for (DORCStruct dorc : pointArray){
            if (dorc.p.state == Point.NOISE){
                noiseArray.add(dorc);
                unvisitedArray.add(dorc);
            }
            if (dorc.p.state == Point.EDGE) unvisitedArray.add(dorc);
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
                if(Point.getDistance(dorc.p, noise.p) <= this.eps - tau){
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
                            if(dorc.p.id == d.p.id){
                                continue;
                            }
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
                // change the neighbors of dorc to core or edge as well
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
