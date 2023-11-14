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
    // added assistant ararays
    // N(p) for point p
    public HashMap<Integer, ArrayList<Point>> noise_neighbors;
    public ArrayList<Cell> noise_cell;
    public HashMap<Integer, ArrayList<Point>> cell_noise_points;

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
        noise_cell = new ArrayList<>();
        noise_neighbors = new HashMap<>();
        cell_noise_points = new HashMap<>();
        // preparations
        readData(filename);
        scan(); // initialization of the gdorc algorithm
        qdorc();    // gdorc is built upon qdorc

    }

//    public void scan() {
//        // algorithm initialization
//        constructGrid();    // here
//        determineCorePoints();
//        DetermineBorderPoint();
//        DetermineNoisePoint();
//    }
    // ly version of scan with initialization
    public void scan() {
        // algorithm initialization
        constructGrid();    // here
        determineCorePoints();
        DetermineBorderPoint();
        DetermineNoisePoint();
        initialization(); // algorithm 1
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
            if (currentPoint.isNoise()){
                // get cell where current point is in
                int [] uj_ij=currentPoint.getGrid(minX, minY);;
                // get the i and j label for cell uj
                Cell uj = grid.getCell(uj_ij[0], uj_ij[1]);
                // init noise_cell U(N);
                noise_cell.add(uj);
                // init cell_noise_pt
                int key = uj_ij[0] * (grid.getNcols() + 1) + uj_ij[1];
                if(!cell_noise_points.containsKey(key)){
                    ArrayList<Point> nps = new ArrayList<>();
                    nps.add(currentPoint);
                    cell_noise_points.put(key, nps);
                }else{
                    ArrayList<Point> nps =cell_noise_points.get(key);
                    nps.add(currentPoint);
                    cell_noise_points.put(key, nps);
                }
            }
        }
    }

    /**
     * partial algorithm 1: initialize U(N), N(u), N(p)
     */
    public void initialization(){
        // init noise_neighbors N(p);
        for (Point pj : points){
            ArrayList<Point> pt_n_neighbors = new ArrayList<>();
            if(pj.getYLP() < 1){
                // uj_ij indicates the current cell pj is in
                int [] uj_ij = pj.getGrid(minX, minY);
                // finds the neighboring cells (contains possible noise neighbors)
                List<Cell> uks = grid.calculateNeighboringCells(uj_ij[0], uj_ij[1]);
                for (Cell uk: uks){
                    for (Point pk: uk.getList()){
                        if (pk.isNoise()){
                            if (pj.getDistanceFrom(pk) <= eps) {  // cal for distance
                                if (!pt_n_neighbors.contains(pk)) { //found new neighbor point
                                    pt_n_neighbors.add(pk);
                                }
                            }
                        }
                    }
                }
            }
            noise_neighbors.put(pj.getId(), pt_n_neighbors);
        }
    }

    // algorithm 3
    /**
     * algorithm 3: remove noise point after repairing
     * @param pi
     */
    public void RemoveNoise(Point pi){
        Noise.remove(pi);
        int [] pi_ij = pi.getGrid(minX, minY);
        Cell ui = grid.getCell(pi_ij[0], pi_ij[1]);
        int key = pi_ij[0] * (grid.getNcols() + 1) + pi_ij[1];
        if(cell_noise_points.containsKey(key)){
            ArrayList<Point> Nui = cell_noise_points.get(key);
            Nui.remove(pi);
            cell_noise_points.put(key, Nui);
            if(Nui.size() == 0){
                noise_cell.remove(ui);
            }
        }
    }

    // algorithm 4
    /**
     * algorithm 4: Calculates the neighbors of point pi
     * @param pi
     * @return a set S of pi's neighbors
     */
    public ArrayList<Point> NeighborPoints(Point pi){
        ArrayList<Point> neighbors = new ArrayList<>();
        // get i and j for ui (cell that pi is in)
        int [] pi_ij = pi.getGrid(minX, minY);
        List<Cell> nCells = grid.calculateNeighboringCells(pi_ij[0], pi_ij[1]);
        for (Cell neighborCell : nCells) { //for every neighbor cell
            if (!neighborCell.isEmpty()) {//周边cell非空
                for(Point pt: neighborCell.getList()){
                    if(pt.getDistanceFrom(pi)<=eps){
                        neighbors.add(pt);
                    }
                }
            }
        }
        return neighbors;
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

    public void oldqdorc(){
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
        if(Border.size()!=0){
            Border.sort(null);
        }else if(Noise.size()!=0){
            Noise.sort(null);
        }
        while (Noise.size()!=0)
        {
            Point p=Noise.get(Noise.size()-1);
            int [] pC;
            // get current grid row and col
            pC=p.getGrid(minX, minY);

            if(Noise.size()>=(1-p.getYLP())*minPoints)
            {
                int repeatTimes=(int) ((1-p.getYLP())*minPoints);
                p.setLabelCore();
                p.setYLP(1);
                Noise.remove(p);

                while(repeatTimes>0)
                {
                    int [] nc=new int[2];
                    // input i and j, calc for nearest noise cell (minimum distance)
                    // finds the nearest cell with noise point
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
                                    npi.setXLP(p.getXLP()); // not used
                                    npi.setX(p.getX());
                                    npi.setY(p.getY());
                                }
                                break;
                            }
                        }
                        Noise.remove(npi);
                        repeatTimes--;
                    }
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

    // new qdorc (based on pseudocode)
    public void qdorc(){
        // sort the pt lists
        if(Noise.size() != 0){
            Noise.sort(null);
        }
        if(Border.size() != 0){
            Border.sort(null);
        }
        // combined noise and border as noncore
        ArrayList<Point> nonCore = new ArrayList<>(Noise);
        nonCore.addAll(Border);
        if(nonCore.size() != 0){
            nonCore.sort(null);
        }
        // gdorc algorithm here: algorithm 2 in paper
        // repair while noise points exist
        // debug: System.out.println("Noise size:" + Noise.size());
        while (Noise.size()!=0)
        {
            // since sorted, can get point pj in uj with maximum yj and yj < 1
            Point pj = nonCore.get(nonCore.size()-1);
            int [] uj_ij = pj.getGrid(minX, minY);
            Cell uj = grid.getCell(uj_ij[0], uj_ij[1]);
            ArrayList<Point> pj_noise_neighbors = noise_neighbors.get(pj.getId());
            // debug: System.out.println("pj noise neighbors:" + pj_noise_neighbors.size());
            if((Noise.size()-pj_noise_neighbors.size()) >= (1-pj.getYLP())*minPoints)
            {
                // pseudocode
                int repeatTimes=(int) (((1-pj.getYLP())*minPoints) + pj_noise_neighbors.size());
                // debug: System.out.println("repeat times: " + repeatTimes);
                // int repeatTimes=(int) ((1-pj.getYLP())*minPoints);
                // enough to make it a core
                // find the nearest noise cell ui to uj
                // find the noise point pi in ui
                while(repeatTimes>0)
                {
                    // currently here
                    // find the nearest noise cell
                    int [] ui_ij = grid.calculateNearestNoiseCell(uj_ij[0], uj_ij[1]);
                    if(grid.hasCell(ui_ij[0], ui_ij[1]))
                    {
                        Cell ui=grid.getCell(ui_ij[0], ui_ij[1]);
//                        int ikey = ui_ij[0] * (grid.getNcols() + 1) + ui_ij[1];
//                        // gets all the noise points in the noise cell
//                        ArrayList<Point> pis = cell_noise_points.get(ikey);
                        List<Point> pis = ui.getList();
                        for(Point pi : pis)
                        {
                            if (pi.isNoise()){
                                if (pj.getDistanceFrom(pi) > eps){
                                    // repair pi to pj
                                    pi.setCluster(pj.getCluster());
                                    pi.setLabelCore();
                                    pi.setX(pj.getX());
                                    pi.setY(pj.getY());
                                    ArrayList<Point> pj_neighbors = NeighborPoints(pj);
                                    ArrayList<Point> pi_neighbors = NeighborPoints(pi);
                                    for (Point pk : pj_neighbors){
                                        if (pi.getDistanceFrom(pk) > eps){
                                            double kylp = pk.getYLP() + 1/minPoints;
                                            double new_kylp;
                                            if (kylp > 1){
                                                new_kylp = 1;
                                            }else{
                                                new_kylp = kylp;
                                            }
                                            pk.setYLP(new_kylp);
                                            if(pk.isNoise()){
                                                RemoveNoise(pk);
                                                ArrayList<Point> pk_neighbors = NeighborPoints(pk);
                                                for (Point ph : pk_neighbors){
                                                    ArrayList<Point> tmp = noise_neighbors.get(ph.getId());
                                                    tmp.remove(pk);
                                                    noise_neighbors.put(ph.getId(), tmp);
                                                }
                                            }
                                        }
                                    }
                                    for (Point pl : pi_neighbors) {
                                        if (pi.getDistanceFrom(pl) > eps){
                                            pl.setYLP(pl.getYLP() - 1/minPoints);
                                            ArrayList<Point> tmp = noise_neighbors.get(pl.getId());
                                            tmp.remove(pi);
                                            noise_neighbors.put(pl.getId(), tmp);
                                        }
                                    }
                                    RemoveNoise(pi);
                                    repeatTimes--;
                                }
                            }
                        }
                    }
                }
                // change pj status
                pj.setLabelCore();
                pj.setYLP(1);
                if(pj.isNoise()) {
                    RemoveNoise(pj);
                }
                nonCore.remove(pj);
            }
            else // no sufficient noises retain
            {
                // for each noise point pi in cell ui
                // make them part of the already core cell
                // find uk with minimum distance between ui and uk
                // fix the noise point to k
                // get rid of the noise point
                while(Noise.size()!=0){
                    Point pi=Noise.get(0);
                    int [] ui_ij;
                    ui_ij=pi.getGrid(minX, minY);
                    int [] uk_ij;
                    uk_ij=grid.calculateNearestNonNoiseCell(ui_ij[0], ui_ij[1]);
                    Cell uk = grid.getCell(uk_ij[0], uk_ij[1]);
                    for(Point pk : uk.getList())
                    {
                        if(!pk.isNoise())
                        {
                            pi.setLabelBorder();
                            pi.setX(pk.getX());
                            pi.setY(pk.getY());
                            pi.setCluster(pk.getCluster());
                            break;
                        }
                    }
                    RemoveNoise(pi);
                }
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
