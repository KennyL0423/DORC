package method;

import utils.*;

import java.util.*;

public class LDORC {
    public ArrayList<Leader> leaders;
    ArrayList<DORCStruct> pointArray;
    public int eta;
    public double eps;
    public double tau;
    int n;
    ArrayList<Point> allpoints;
    HashMap<Leader, ArrayList<Leader>> leader_neighbors;

    public LDORC(int eta, double eps){
        n = Dataset.dataset.size();
        this.leaders = new ArrayList<Leader>();
        pointArray = new ArrayList<DORCStruct>();
        allpoints = new ArrayList<>();
        leader_neighbors = new HashMap<>();
        this.eta = eta;
        this.eps = eps;
        this.tau = eps/5;
        initialize();
//        newrun();
        lrun();
    }

    private void initialize(){
        for (int i=0; i<n; i++){
            DORCStruct dorc = new DORCStruct(n, Dataset.dataset.get(i));
            pointArray.add(dorc);
        }
        leaderCluster();
//        getEpsilonNeighbor();
        newGetEpsilonNeighbor();
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
                newLeader.id = dorc.p.id;
                newLeader.count = 1;
                leaders.add(newLeader);
                ArrayList<Leader> tmp = new ArrayList<>();
                leader_neighbors.put(newLeader, tmp);
            }else{
                l.followers.add(dorc.p);
                l.count++;
            }
        }
    }

    /*
        LDORC should only consider the aforesaid leader pairs
        when calling qdorc, therefore needs to use leaders
        as representatives of the followers
     */
    private void newGetEpsilonNeighbor(){
        for (Leader pk: leaders){
            ArrayList<Leader> neighbors = new ArrayList<>();
            for (Leader pl: leaders){
                if(pl.id<=pk.id) continue;
                if (Point.getDistance(pk.leader, pl.leader) <= this.eps - 2*this.tau){
                    int tmp  = pk.count;
                    pk.count += pl.count;
                    pl.count += tmp;
                    neighbors.add(pl);
                    ArrayList<Leader> pklist = leader_neighbors.get(pk);
                    pklist.add(pl);
                    leader_neighbors.put(pk, pklist);
                    ArrayList<Leader> pllist = leader_neighbors.get(pl);
                    pllist.add(pk);
                    leader_neighbors.put(pl, pllist);
                }
            }
            if(pk.count >= this.eta){
                pk.leader.state = Point.CORE;
                for (Point follower : pk.followers){
                    follower.state = Point.CORE;
                }
                for(Leader leader : neighbors){
                    for(Point followers : leader.followers){
                        if(followers.state == Point.NOISE) {
                            followers.state = Point.EDGE;
                        }
                    }
                }
            }
        }
        // identify noise leader
        for (Leader l: leaders){
            if(l.leader.state != Point.CORE && l.leader.state != Point.EDGE){
                l.leader.state = Point.NOISE;
                for (Point follower : l.followers){
                    follower.state = Point.NOISE;
                }
            }
            if (l.leader.state==Point.CORE) l.state=Point.VISITED;
            else l.state=Point.NOTVISITED;
            if (l.count >= this.eta) l.y = 1.0;
            else l.y = (double)l.count / eta;
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

    /*
    this is the ldorc's version of qdorc
    which consider aforesaid leaders
     */
    public void lrun(){
        ArrayList<Leader> noiseArray = new ArrayList<>();
        ArrayList<Leader> unvisitedArray = new ArrayList<>();
        //update noise array and unvisited array (but for leaders only)
        for (Leader l: leaders){
            l.counter = l.followers.size();
            if(l.leader.state == Point.NOISE){
                noiseArray.add(l);
                unvisitedArray.add(l);
            } else if (l.leader.state == Point.EDGE){
                unvisitedArray.add(l);
            }
        }
        // sort the unvisited array by y (compare between leaders)
        Collections.sort(unvisitedArray, new Comparator<Leader>() {
            @Override
            public int compare(Leader o1, Leader o2) {
                if (o1.y< o2.y) return -1;
                if (o1.y > o2.y) return 1;
                return 0;
            }
        });
        // QDORC begins here
        while (noiseArray.size()>0){
            // get the point p_j with the maximum y
            Leader max_y = unvisitedArray.get(unvisitedArray.size()-1);
            max_y.state = DORCStruct.VISITED;
            int self_noise = 0;
            if(noiseArray.contains(max_y)){
                self_noise = max_y.followers.size();
            }
            // 需要排除目前已经是neighbor的noise
            int noise_neighbor_cnt = 0;
            int noise_point_cnt = 0;
            for( Leader noise: noiseArray){
                noise_point_cnt += noise.followers.size();
                if(max_y.id == noise.id){
                    continue;
                }
                if(Point.getDistance(max_y.leader, noise.leader) <= this.eps - this.tau){
                    noise_neighbor_cnt += noise.followers.size();
                }
            }
            if (noise_point_cnt - self_noise - noise_neighbor_cnt>=(1-max_y.y)*eta) {
                for (int k=0; k<(1-max_y.y)*eta;) {
                    // find point p_i in noise set with the minimum distance
                    double minv = Double.MAX_VALUE;
                    Leader minNoiseLeader = null;
                    for (Leader d : noiseArray) {
                        double dist = Point.getDistance(max_y.leader, d.leader);
                        if (dist < minv) {
                            if(max_y.id == d.id){
                                continue;
                            }
                            minv = Point.getDistance(max_y.leader, d.leader);
                            minNoiseLeader = d;
                        }
                    }
                    int amount = Math.min((int)((1-max_y.y)*eta), minNoiseLeader.counter);
                    int cnt = 0;
                    for(Point follower: minNoiseLeader.followers){
                        if(follower.state == Point.NOISE){
                            follower.redirectPoint = max_y.leader;
                            follower.state = Point.VISITED;
                            minNoiseLeader.counter--;
                            cnt++;
                            k++;
                        }
                        if (cnt == amount){
                            if(minNoiseLeader.counter <= 0){
                                noiseArray.remove(minNoiseLeader);
                            }
                            break;
                        }
                    }
                }
                // update status of p_j
                max_y.y = 1.0;
                max_y.leader.state = Point.CORE;
                unvisitedArray.remove(unvisitedArray.size()-1);
                noiseArray.remove(max_y);

                ArrayList<Leader> ylist = leader_neighbors.get(max_y);
                for (Leader ld: ylist){
                    noiseArray.remove(ld);
                }
            }
            else{
                Iterator<Leader> iterator = noiseArray.iterator();
                while (iterator.hasNext()){
                    Leader targetNoise = iterator.next();
                    // find the non-noise leader p_k with the minimum distance
                    double minv = Double.MAX_VALUE;
                    Leader minLeader = null;
                    for (Leader d2: leaders){
                        if (d2.leader.state==Point.CORE || d2.leader.state==Point.EDGE) {
                            double dist = Point.getDistance(targetNoise.leader, d2.leader);
                            if (dist < minv) {
                                minv = dist;
                                minLeader = d2;
                            }
                        }
                    }
                    // move d1 to minLeader
                    for(Point follower: targetNoise.followers){
                        follower.redirectPoint = minLeader.leader;
                        follower.state = minLeader.state;
                        targetNoise.counter--;
                    }
                    iterator.remove();
                    unvisitedArray.remove(targetNoise);
                }
            }
        }
        // put all the points in an arraylist
        for (Leader leader: leaders){
            for (Point follower: leader.followers){
                allpoints.add(follower);
            }
        }
        // sort by point id
        Collections.sort(allpoints, new Comparator<Point>(){
            @Override
            public int compare(Point o1, Point o2) {
                if(o1.id < o2.id)
                    return -1;
                if(o2.id > o2.id)
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

    public void oldlog(String path){
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

    public void log(String path){
        LogWriter repairLog = new LogWriter(path);
        repairLog.open();
        for(Point pt : allpoints){
            if(pt.redirectPoint == null){
                repairLog.log(pt.id+"\t"+pt.x+"\t"+pt.y+"\n");
            }else{
                repairLog.log(pt.id+"\t"+pt.redirectPoint.x+"\t"+pt.redirectPoint.y+"\n");
            }
        }
    }
}
