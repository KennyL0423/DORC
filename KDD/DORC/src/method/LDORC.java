package method;

import utils.*;

import java.util.*;

public class LDORC {
    ArrayList<DORCStruct> pointArray;
    public int eta;
    private static long startTime;
    private static long endTime;
    public double eps;
    public double tau;
    int n;
    HashMap<DORCStruct,ArrayList<DORCStruct>> leaders_followers;
    HashMap<DORCStruct, ArrayList<DORCStruct>> neighbor_leaders;
    ArrayList<DORCStruct> leaders;


    public LDORC(int eta, double eps){
        n = Dataset.dataset.size();
        this.leaders = new ArrayList<>();
        this.leaders_followers = new HashMap<>();
        this.neighbor_leaders = new HashMap<>();
        pointArray = new ArrayList<DORCStruct>();
        this.eta = eta;
        this.eps = eps;
        this.tau = eps/5;
//        this.tau = 0;
        startTime = System.currentTimeMillis();
        initialize();
        endTime = System.currentTimeMillis();
        System.out.println((endTime-startTime)/1000.0);
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

    private void nleaderCluster() {
        // Consider using a spatial data structure for leaders if the search is expensive
        for (DORCStruct dorc : pointArray) {
            DORCStruct nearestLeader = null;
            double minDistance = Double.MAX_VALUE;

            for (DORCStruct leader : leaders) {
                double distance = Point.getDistance(dorc.p, leader.p);
                if (distance <= tau && distance < minDistance) {
                    nearestLeader = leader;
                    minDistance = distance;  // Update the nearest distance found
                }
            }

            if (nearestLeader == null) {
                leaders.add(dorc);
                leaders_followers.put(dorc, new ArrayList<>());
                neighbor_leaders.put(dorc, new ArrayList<>());
                dorc.original_c = 1;
            } else {
                // Get or initialize the follower list for the nearest leader
                leaders_followers.computeIfAbsent(nearestLeader, k -> new ArrayList<>()).add(dorc);
                nearestLeader.original_c++;
            }
        }
    }
    private void leaderCluster(){
        for(DORCStruct dorc : pointArray){
            DORCStruct l = null;
            for(DORCStruct leader : leaders){
                if(Point.getDistance(dorc.p, leader.p) <= tau){
                    l = leader;
                    break;
                }
            }
            if(l == null){
                leaders.add(dorc);
                ArrayList<DORCStruct> tmp = new ArrayList<>();
                leaders_followers.put(dorc, tmp);
                ArrayList<DORCStruct> tmp2 = new ArrayList<>();
                neighbor_leaders.put(dorc, tmp2);
                dorc.original_c = 1;
            }else{
                ArrayList<DORCStruct> followerList = leaders_followers.get(l);
                followerList.add(dorc);
                l.original_c++;
            }
        }
    }

    /*
        LDORC should only consider the aforesaid leader pairs
        when calling qdorc, therefore needs to use leaders
        as representatives of the followers
     */
    private void getEpsilonNeighbor(){
        System.out.println(leaders.size());
        for(DORCStruct l1 : leaders) {
            l1.c += l1.original_c;
            for (DORCStruct l2 : leaders) {
                if(l1.p.id==l2.p.id) continue;
                if ( Point.getDistance(l1.p, l2.p) <= this.eps - 2*tau) {
                    l1.c += l2.original_c;
                    ArrayList<DORCStruct> l1neighbor = neighbor_leaders.get(l1);
//                    ArrayList<DORCStruct> l2neighbor = neighbor_leaders.get(l2);
                    if(!l1neighbor.contains(l2)) l1neighbor.add(l2);
//                    if(!l2neighbor.contains(l1)) l2neighbor.add(l1);
                    neighbor_leaders.put(l1, l1neighbor);
//                    neighbor_leaders.put(l2, l2neighbor);
                }
            }
            if (l1.c >= this.eta)
                l1.p.state = Point.CORE;
            if(l1.p.state == Point.CORE){
                ArrayList<DORCStruct> neighborLeaders = neighbor_leaders.get(l1);
                for (DORCStruct neighbor: neighborLeaders){
                    if(neighbor.p.state != Point.CORE){
                        neighbor.p.state = Point.EDGE;
                    }
                }
            }else{
                if(l1.p.state != Point.EDGE){
                    l1.p.state = Point.NOISE;
                }
            }
        }
        int noise_cnt = 0;
        int noise_pt_cnt = 0;
        int border_cnt = 0;
        int border_pt_cnt = 0;
        int core_cnt = 0;
        int core_pt_cnt = 0;
        for(DORCStruct leader:leaders){
            if(leader.p.state == Point.CORE){
                core_cnt+=1;
                core_pt_cnt+=leader.original_c;
            } else if (leader.p.state==Point.EDGE) {
                border_cnt+=1;
                border_pt_cnt+=leader.original_c;
            } else if (leader.p.state==Point.NOISE) {
                noise_cnt+=1;
                noise_pt_cnt+=leader.original_c;
            }
        }
//        System.out.println("noise leader : " + noise_cnt + ", " + noise_pt_cnt);
//        System.out.println("border leader: " + border_cnt + ", " + border_pt_cnt);
//        System.out.println("core leader: " + core_cnt + ", " + core_pt_cnt);
        // calculate y for leaders
        for (DORCStruct leader: leaders){
            if (leader.p.state==Point.CORE) leader.state=DORCStruct.VISITED;
            else leader.state=DORCStruct.NOTVISITED;
            if (leader.c >= eta) leader.y = 1.0;
            else leader.y = (double)leader.c / eta;
        }
        // followers' y and c = leader's y and c
        // followers' state = leader's state
        for (DORCStruct leader: leaders_followers.keySet()){
            ArrayList<DORCStruct> followers = leaders_followers.get(leader);
            for(DORCStruct follower: followers){
                follower.c = leader.c;
                follower.y = leader.y;
                follower.p.state = leader.p.state;
            }
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

    /*
    qdorc part stays the same for ldorc
     */
    public void newrun(){
        ArrayList<DORCStruct> noiseArray = new ArrayList<DORCStruct>();
        ArrayList<DORCStruct> unvisitedArray = new ArrayList<DORCStruct>();
        ArrayList<DORCStruct> coreArray = new ArrayList<>();
        // update noise array and unvisited array
//        System.out.println("point array size: " + pointArray.size());
        for (DORCStruct dorc : pointArray){
            if (dorc.p.state == Point.NOISE){
                noiseArray.add(dorc);
                unvisitedArray.add(dorc);
            }
            if (dorc.p.state == Point.EDGE) unvisitedArray.add(dorc);
            if(dorc.p.state == Point.CORE) coreArray.add(dorc);
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
//        System.out.println("LDORC Noise size: " + noiseArray.size());
//        System.out.println("LDORC Border size: " + (unvisitedArray.size()-noiseArray.size()));
//        System.out.println("LDORC Core size: " + coreArray.size());
//        System.out.println("LDORC Leader size:"+ leaders.size());
        // QDORC begins
        while (noiseArray.size()>0){
//            System.out.println(noiseArray.size());
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
                if(Point.getDistance(dorc.p, noise.p) <= this.eps){
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
