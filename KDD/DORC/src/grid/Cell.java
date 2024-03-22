package grid;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    public int key;
    private List<Point> list;
    private List<Point> noiseList;
    private List<Point> nonNoiseList;
    private int clusterNum;
    private boolean isCore;
    public int type = 0; // 0 undetermine, 1 all core, 2 has core, 3 all noise, 4 has noise


    public Cell() {
        clusterNum = -1;
        list = new ArrayList<>();
        noiseList = new ArrayList<>();
        nonNoiseList = new ArrayList<>();
        isCore = false;
    }
    public Cell(int key){
        this.key = key;
        clusterNum = -1;
        list = new ArrayList<>();
        noiseList = new ArrayList<>();
        nonNoiseList = new ArrayList<>();
        isCore = false;
    }

    /**
     * Given a point of the cell it returns the nearest point in its neighborhood.
     * @param point the point instance to examine its distance from current point.
     * @return the nearest point
     */
    public Point getNearestCorePoint(Point point) {
        Point nearestCorePoint = null;
        double nearestDistance = Double.MAX_VALUE; // Initialize with the maximum value
        for (Point p : list) {
            if (p.isCore()) {
                double distance = point.getDistanceFrom(p); // Calculate once per core point
                if (nearestCorePoint == null || distance < nearestDistance) {
                    nearestCorePoint = p;
                    nearestDistance = distance; // Update the nearest distance
                }
            }
        }
        return nearestCorePoint;
    }

    /**
     * Sets the given cluster number to the relevant cell attribute but also to all points of the cell.
     * @param clusterNum the cluster number to be assigned to the Cell and its points
     */
    public void setClusterNum(int clusterNum) {
        //isCore = true;
        this.clusterNum = clusterNum;
        for (Point p : list) {
            p.setCluster(clusterNum);
        }
    }

    public void removeAllPoints(){
        list.clear();
    }

    public List<Point> getList() {
        return list;
    }

    public void setList(List<Point> list) {
        this.list = list;
    }

    public void setNoiseList(List<Point> list) {
        this.noiseList = list;
    }

    public int getClusterNum() {
        return clusterNum;
    }

    public boolean isCore() {
        return isCore;
    }

    public void setCore(boolean core) {
        isCore = core;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int getNumberOfPoints() {
        return list.size();
    }

    public Point getPoint(int position) {
        return list.get(position);
    }

    public boolean getNoiseList() {
        if(this.isEmpty())
        {
            return false;
        }
        else
        {
            Point np=null;
            for(Point p:this.getList())
            {
                if(p.isNoise())
                {
                    np=p;
                    break;
                }
            }
            if (np!=null)
            {
                return true;
            }
            else
                return false;
        }
    }

    public boolean getNonNoiseList() {
//   	 List<Point> nonNoiseList = new ArrayList<>();
        if(this.isEmpty())
        {
            return false;
        }
        else
        {
            Point np=null;
//       	 List<Point> noiseList = new ArrayList<>();
            for(Point p:this.getList())
            {
                if(!p.isNoise())
                {
                    np=p;
                    break;

                }

            }
            if (np!=null)
            {
                return true;
            }
            else
                return false;
        }
    }

    public Point getNoisePoint(int position) {
        return list.get(position);
    }

    public void addPoint(Point newPoint) {
        list.add(newPoint);
    }

    public void setPoint(int position, Point newPoint) {
        list.set(position, newPoint);
    }

    public void outputListElements() {
        for (Point p : list) {
            System.out.println(p);
        }
    }

}