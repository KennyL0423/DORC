package grid;

import utils.LogWriter;
import utils.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class DBSCAN extends Scan {

    public DBSCAN(double eps, int minPoints, String filename) {
        this.eps = eps;
        this.minPoints = minPoints;
        clusterCounter = 0;
        points = new ArrayList<>();
        clusters = new ArrayList<>();
        readData(filename);
        scan();
    }

    public DBSCAN(double eps, int minPoints, ArrayList<Point> data) {
        this.eps = eps;
        this.minPoints = minPoints;
        clusterCounter = 0;
        points = data;
        clusters = new ArrayList<>();
        scanWithNotRemove();
    }
    /**
     * Implements the algorithmic logic of original DBSCAN.
     * For every point in our dataset we search for its neighbours (using regionQuery function)
     * and if its size is sufficient, point becomes a core and we call expandCluster function
     * to further expand this newly-created cluster with its points' neighbours.
     * If the number of neighbour points are not sufficient (less than minPoints) then
     * the point is marked as a Noise.
     */
    public void scan() {
//        for (Point p : points) {
//            if (p.isUndefined()) { //point is unclassified
//                HashSet<Point> neighbours = regionQuery(p);
//                if ((neighbours.size() + 1) < minPoints) { //+1 because we are ignoring the current point
//                    p.setLabelNoise();
//                } else {
//                    Cluster cluster = new Cluster(clusterCounter++);
//                    p.setLabelCore();
//                    p.setCluster(cluster.getId());
//                    cluster.addPoint(p);
//                    clusters.add(expandCluster(p, neighbours, cluster));
//                }
//            }
//        }
//        Iterator<Point> it = points.iterator();
//        while(it.hasNext()){
//            Point p = it.next();
//            if (p.isNoise()) it.remove();
//        }
        for (Point p:points){
            HashSet<Point> neighbours = regionQuery(p);
            if ((neighbours.size() + 1) < minPoints)
                p.setLabelNoise();
        }
        points.removeIf(Point::isNoise);
    }

    public void scanWithNotRemove() {
        for (Point p : points) {
            if (p.isUndefined()) { //point is unclassified
                HashSet<Point> neighbours = regionQuery(p);
                if ((neighbours.size() + 1) < minPoints) { //+1 because we are ignoring the current point
                    p.setLabelNoise();
                } else {
                    Cluster cluster = new Cluster(clusterCounter++);
                    p.setLabelCore();
                    p.setCluster(cluster.getId());
                    cluster.addPoint(p);
                    clusters.add(expandCluster(p, neighbours, cluster));
                }
            }
        }

    }

    /**
     * This function iterates over the neighbours of the examining point that were found.
     * For each point of that neighborhood:
     *  a) It marks it as a core if that points’ neighborhood also meets the minimum value (minPoints)
     *  b) It marks it as border if that points’ neighborhood does not meet the minimum value (minPoints)
     * In any case it then merges the point into the current cluster.
     *
     * @param p current point that we examine
     * @param neighbours list of neighbor points for the currently examined point
     * @param cluster the current function that we build based on examining point and its neighbors
     * @return the completed cluster
     */
    protected Cluster expandCluster(Point p, HashSet<Point> neighbours, Cluster cluster) {
        List<Point> newPointsToAdd = new ArrayList<>();
        newPointsToAdd.addAll(neighbours);
        for (int i = 0; i < newPointsToAdd.size(); i++) {
            Point k = newPointsToAdd.get(i);
            if (k.isUndefined() || k.isNoise()) { //point is unclassified
                HashSet<Point> neighboursPts = regionQuery(k);
                if ((neighboursPts.size() + 1) >= minPoints) { //join into neighbours. +1 because we ignored current point
                    k.setLabelCore();
                    for (Point k2 : neighboursPts) {
                        if (!newPointsToAdd.contains(k2) ) {
                            if (k2.isUndefined() || k2.isNoise()) {
                                newPointsToAdd.add(k2);
                            }
                        }
                    }
                } else {
                    k.setLabelBorder();
                }
            }
            if (!Cluster.isInACluster(clusters, k)) { //checks if k belongs in a cluster.
                k.setCluster(cluster.getId());
                cluster.addPoint(k);
            }
        }
        neighbours.addAll(newPointsToAdd);
        return cluster;
    }

    /**
     * Given a point "origin" returns all its neighbours according to the eps and minPoints thresholds.
     *
     * @param origin the current point we examine
     * @return a set of neighbour points of given "origin" point
     */
    protected HashSet<Point> regionQuery(Point origin) {
        HashSet<Point> neighbours = new HashSet<>();
        for (Point p : points) {
            if (!p.equals(origin) && origin.getDistanceFrom(p) <= eps) {
                neighbours.add(p);
            }
        }
        return neighbours;
    }

    public void log(String path){
        LogWriter repairLog = new LogWriter(path);
        repairLog.open();
        for (Point p: points){
            repairLog.log(p.getId()+"\t"+p.getX()+"\t"+p.getY()+"\n");
        }
    }
}


