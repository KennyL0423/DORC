package grid;

import java.util.*;
import utils.LogWriter;

public class GDORC extends Scan {

    private double cellWidth;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private int nRows;
    private int nCols;
    private Grid grid;
    private double weight;
    public static double rme;
    public static double clAccuracy;
    public static double ratio;
    private static long startTime;
    private static long endTime;
    public  static long Time;

    public GDORC() {
        eps = 10;   // eps for distance threshold
        minPoints = 5; //eta for core_pts density threshold
        clusterCounter = 0;
        points = new ArrayList<>();
        clusters = new ArrayList<>();
        readData("input.txt");
        scan();
    }

    public GDORC(double eps, int minPoints, String filename) {
        this.eps = eps; // epsilon
        this.minPoints = minPoints; // eta
        clusterCounter = 0;
        Noise = new ArrayList<>();  // noise list
        Border = new ArrayList<>(); // border means itself is not core but stays with a core
        points = new ArrayList<>(); // all points
        truth = new ArrayList<>();
        ilpdata = new ArrayList<>();
        clusters = new ArrayList<>();
        // preparations
        readData(filename);
        scan(); // initialization of the gdorc algorithm
        qdorc();    // gdorc is built upon qdorc

    }

    public void scan() {
        // algorithm initialization
        constructGrid();    // here
        determineCorePoints();
        DetermineBorderPoint();
        DetermineNoisePoint();
    }

    private void constructGrid() {
        cellWidth = eps / Math.sqrt(2); // 2D data points
        calculateMinMaxDimensions();
        nRows = (int) ((maxX - minX) / cellWidth + 1);  // row num
        nCols = (int) ((maxY - minY) / cellWidth + 1);  // col num
        grid = new Grid(nRows, nCols, eps); // init grid
        for (Point p : points) {
            int tempx;
            int tempy;
            tempx = (int) ((p.getX() - minX) / cellWidth);
            tempy = (int) ((p.getY() - minY) / cellWidth);
            grid.setPointInCell(tempx, tempy, p);   // init points in cell
        }
    }

    private void determineCorePoints() {
        for (int key:grid.grid.keySet()){   // traversing all cells
            int i = key/(grid.ncols+1); // rownum
            int j = key%(grid.ncols+1); // colnum
            int cellPoints = grid.getCell(i, j).getList().size();   // get cell points
            if (cellPoints >= minPoints) { //set all points of cell as Core
                for (Point p : grid.getCell(i, j).getList()) {
                    p.setLabelCore();
                    p.setYLP(1);
                }
                grid.getCell(i,j).setCore(true);
            } else if (cellPoints != 0) {   // exists points in cell
                for (Point p : grid.getCell(i, j).getList()) { //of every point of the current cell
                    // for every point p
                    Set<Point> numPoints = new HashSet<>(); //calculates number of neighbours (points with distance less than eps)
                    // for every point, calculate for neighboring cells
                    // a little bit time-consuming since there can be more than one point in the same cell that share the info
                    // consider maintain a neighboring cell list for each cell
                    // TODO: if we can get rid of redundancy
                    List<Cell> nCells = grid.calculateNeighboringCells(i, j); //compute the cells within eps distance that can provide possible neighbor points
                    if (nCells.isEmpty()) {
                        continue;
                    }
                    // exist neighboring cells (as it should)
                    for (Cell nc : nCells) { //for every such neighbor cell (with potential neighbor points)
                        if (nc.isEmpty()) {
                            continue;
                        }
                        for (Point q : nc.getList()) { //we examine all points of a neighbor
                            if (p.getDistanceFrom(q) <= eps) {  // cal for distance
                                if (!numPoints.contains(q)) { //found new neighbor point
                                    numPoints.add(q);
                                }
                                if (numPoints.size() >= minPoints) {    // this is a core point
                                    p.setLabelCore();
                                    p.setYLP(1);
                                    grid.getCell(i,j).setCore(true);
                                    break;
                                }
                            }
                        }
                        if (numPoints.size() >= minPoints) {    // this is a core point
                            break; //continues the break to the outer loop: next cell to examine.
                        }
                    }
                }
            }
        }
    }

    private void DetermineBorderPoint() {
//    	int b=0;
        for (int key:grid.grid.keySet()){
            int i = key/(grid.ncols+1);
            int j = key%(grid.ncols+1);

            for (Point currentPoint : grid.getCell(i, j).getList()) { //for every point in current cell
                if (!currentPoint.isCore()) {//当前点不是core
                    int ylpc=0;
                    Point q = null; //q
                    // TODO: same as determineCorePoints
                    List<Cell> nCells = grid.calculateNeighboringCells(i, j);
                    for (Cell neighborCell : nCells) { //for every neighbor cell
                        if (!neighborCell.isEmpty()) {//周边cell非空
                            Point nearCorePoint =null;//周边点
                            Point temp = (neighborCell.getNearestCorePoint(currentPoint));//周边cell中与当前点最近的core点
                            if(temp!=null && temp.getDistanceFrom(currentPoint)<=eps){
                                nearCorePoint = temp;

                            }
                            if(nearCorePoint == null){
                                continue;
                            }
                            if (q == null) {
                                q = nearCorePoint;
                            } else if (currentPoint.getDistanceFrom(nearCorePoint) <= currentPoint.getDistanceFrom(q)) {
                                q = nearCorePoint;
                            }

                            for(Point borderq : neighborCell.getList())
                            {
                                if(borderq.getDistanceFrom(currentPoint)<=eps){
                                    ylpc++;

                                }
                            }
                            // set y for this point
                            double ylpcd= (double) ylpc/minPoints;
                            currentPoint.setYLP(ylpcd);

                        }
                    }
                    if (q != null) {
                        currentPoint.setCluster(q.getCluster());    // not used
                        currentPoint.setLabelBorder();
                        Border.add(currentPoint);
                    } else {
                        currentPoint.setLabelNoise();
                        Noise.add(currentPoint);
                    }
                }
            }

        }
    }
    private void DetermineNoisePoint() {
        for (Point currentPoint : points) { //for every point in current cell
            if (currentPoint.isUndefined()) {   // those are neither core nor border
                currentPoint.setLabelNoise();
                Noise.add(currentPoint);
            }
        }
    }

    /**
     * calculates the min and max X and Y dimensions among all data points.
     */
    private void calculateMinMaxDimensions() {
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        maxY = Double.MIN_VALUE;

        for (Point p : points) {
            if (p.getX() > maxX) {
                maxX = p.getX();
            }
            if (p.getX() < minX) {
                minX = p.getX();
            }
            if (p.getY() > maxY) {
                maxY = p.getY();
            }
            if (p.getY() < minY) {
                minY = p.getY();
            }
        }
    }

    public void qdorc(){
        // cal for min x and min y
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        for (Point p : points) {
            if (p.getX() < minX) {
                minX = p.getX();
            }
            if (p.getY() < minY) {
                minY = p.getY();
            }
        }
        // sort the pt lists
        if(Border.size()!=0){
            Border.sort(null);
        }else if(Noise.size()!=0){
            Noise.sort(null);
        }
        // gdorc algorithm here: algorithm 2 in paper
        while (Noise.size()!=0)
        {
            // point with maximum yj and yj < 1
            Point p=Noise.get(Noise.size()-1);
            int [] pC;
            // get current grid row and col
            pC=p.getGrid(minX, minY);
            // TODO: noise neighboring point, fix according to pseudo-code
            if(Noise.size()>=(1-p.getYLP())*minPoints)
            {
                int repeatTimes=(int) ((1-p.getYLP())*minPoints);
                // TODO: move to the end
                p.setLabelCore();
                p.setYLP(1);
                Noise.remove(p);

                while(repeatTimes>0)
                {
                    int [] nc=new int[2];
                    // input i and j, calc for nearest noise cell (minimum distance)
                    nc=grid.calculateNearestNoiseCell(pC[0], pC[1]);
                    if(grid.hasCell(nc[0], nc[1]))
                    {
                        Cell nCell=grid.getCell(nc[0], nc[1]);
                        Point npi=null;
                        for(Point np:nCell.getList())
                        {
                            if(np.isNoise())
                            {
                                npi=np;
                                npi.setCluster(p.getCluster());
                                npi.setLabelCore();
                                if (nc[0]!=pC[0] || nc[1]!=pC[1])
                                {
                                    npi.setXLP(p.getXLP());
                                    npi.setX(p.getX());
                                    npi.setY(p.getY());
                                }
                                break;
                            }
                        }
                        Noise.remove(npi);
                        repeatTimes--;
                    }
                    // TODO: always break?
                    break;
                }
            }
            else
            {
                while(Noise.size()!=0){
                    Point pn=Noise.get(0);
                    int [] pnC;
                    pnC=pn.getGrid(minX, minY);
                    int [] nc;
                    nc=grid.calculateNearestNonNoiseCell(pC[0], pC[1]);
                    Cell nCell=grid.getCell(nc[0], nc[1]);
                    Point pp=null;
                    for(Point np:nCell.getList())
                    {
                        if(!np.isNoise())
                        {
                            pp=np;
                            pn.setLabelCore();
                            pn.setYLP(1);
                            Noise.remove(pn);
                            pn.setXLP(pp.getXLP());
                            pn.setX(pp.getX());
                            pn.setY(pp.getY());
                            pn.setCluster(np.getCluster());
                        }
                    }
                }
                break;
            }
        }
    }

    public void log(String path){
        LogWriter repairLog = new LogWriter(path);
        repairLog.open();
        for (Point p: points){
            repairLog.log(p.getId()+"\t"+p.getX()+"\t"+p.getY()+"\n");
        }
    }
}
