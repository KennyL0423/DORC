package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Cluster {
    public int count = 0;

    public ArrayList<Point> dataArray;
    public int eta; //minPts
    public double eps; //radius

    public Cluster(ArrayList<Point> dataArray, int eta, double eps){
        this.dataArray = dataArray;
        this.eta = eta;
        this.eps = eps;
        run();
    }

    private void run(){
        for (Point p: dataArray){
            if (p.state==Point.NOTVISITED){
                ArrayList<Point> neighbors = getNearbyPts(p);
                if (neighbors.size() +1 >= this.eta){
                    p.poi = count;
                    expandCluster(p, neighbors);
                } else{
                    p.state = Point.NOISE;
                }
            }
        }
    }

    private void expandCluster(Point startPts, ArrayList<Point> neighbors){
        startPts.state = Point.CORE;
        ArrayList<Point> newPointsToAdd = new ArrayList<Point>();
        newPointsToAdd.addAll(neighbors);

        for(int j = 0; j<newPointsToAdd.size();j++){
            Point k = newPointsToAdd.get(j);

            if(k.state == Point.NOTVISITED || k.state == Point.NOISE){
                k.state = Point.VISITED;
                ArrayList<Point> neighborPts = getNearbyPts(k);//对于每一个加入的点再找其周围的点
                if(neighborPts.size() + 1 >= this.eta) {
                    k.state = Point.CORE;
                    for (Point k2:neighborPts) {
                        if (!newPointsToAdd.contains(k2)) {
                            newPointsToAdd.add(k2);
                        }
                    }
                } else{
                    k.state = Point.EDGE;
                }
            }
            if(k.poi == -1)
                k.poi = count;
        }
        count++;
    }

    public Point getUnvisitedPts(){
        Point result = null;
        for(Point point: dataArray){
            if(point.state == Point.NOTVISITED){
                result = point;
                break;
            }
        }
        return result;
    }

    public ArrayList<Point> getNearbyPts(Point point){
        ArrayList<Point> result = new ArrayList<Point>();
        for(Point p : dataArray){
            if(p.id!=point.id && Point.getDistance(p, point) <= this.eps)
                result.add(p);
        }
        return result;
    }

    public void log(String path){
        LogWriter log = new LogWriter(path);
        log.open();
        for(Point p:dataArray){
            log.log(p.id+"\t"+p.x+"\t"+p.y+"\t"+p.poi+"\n");
        }
    }
}
