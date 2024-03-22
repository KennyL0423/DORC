package grid;

import java.util.*;

import utils.DORCStruct;
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
    public ArrayList<Cell> core_cell;
    public HashMap<Integer, ArrayList<Point>> NonCore_NonCneighbors;

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
        BorderNoise = new ArrayList<>();
        truth = new ArrayList<>();
        ilpdata = new ArrayList<>();
        clusters = new ArrayList<>();
        noise_cell = new ArrayList<>();
        noise_neighbors = new HashMap<>();
        cell_noise_points = new HashMap<>();
        NonCore_NonCneighbors = new HashMap<>();
        core_cell = new ArrayList<>();
        // preparations
        readData(filename);
//        System.out.println("finish read");
//        startTime = System.currentTimeMillis();
        scan(); // initialization of the gdorc algorithm
//        endTime = System.currentTimeMillis();
//        System.out.println("finish scan: " + (endTime-startTime)/1000.0);
//        startTime = System.currentTimeMillis();
        qdorc();    // gdorc is built upon qdorc
//        endTime = System.currentTimeMillis();
//        System.out.println("finishi dorc: " + (endTime-startTime)/1000.0);
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
        startTime = System.currentTimeMillis();
        constructGrid();    // here
        determineCorePoints();
        DetermineBorderNoisePoint();
        initialization(); // algorithm 1
        endTime = System.currentTimeMillis();
        System.out.println((endTime-startTime)/1000.0);
    }

    private void constructGrid() {
        cellWidth = eps / Math.sqrt(2); // 2D data points
        calculateMinMaxDimensions();
        nRows = (int) Math.ceil((maxX - minX) / cellWidth);
        nCols = (int) Math.ceil((maxY - minY) / cellWidth);

        grid = new Grid(nRows, nCols, eps); // init grid
        for (Point p : points) {
            int tempx, tempy;
            tempx = (int) ((p.getX() - minX) / cellWidth);
            tempy = (int) ((p.getY() - minY) / cellWidth);
            grid.setPointInCell(tempx, tempy, p);
        }
        for (HashMap.Entry<Integer, Cell> entry : grid.grid.entrySet()) {
            if (!entry.getValue().isEmpty())
            grid.cell_index.put(entry.getValue(), entry.getKey());
        }
    }

    private void determineCorePoints() {
        for (int key : grid.grid.keySet()) { // 遍历所有单元格
            int i = key / grid.ncols; // 计算所在行号
            int j = key % grid.ncols; // 计算所在列号
            Cell currentCell = grid.grid.get(key);
            int cellPoints = currentCell.getList().size(); // 获取单元格内点的数量

            // 如果单元格内的点数大于等于minPoints，则设置所有点为核心点
            if (cellPoints >= minPoints) {
                for (Point p : currentCell.getList()) {
                    p.setLabelCore();
                    p.setYLP(1);
                }
                currentCell.type = 1;   // all core
            } else if (cellPoints != 0) { // 单元格内有点，但不足minPoints
                List<Cell> neighboringCells = grid.calculateNeighboringCells(i, j); // 获取邻近单元格列表
                for (Point p : currentCell.getList()) {
                    int numCnt = currentCell.getList().size(); // 自己grid里的都包括
                    for (Cell neighbor : neighboringCells) { // 遍历每个邻近单元格
                        if (neighbor.isEmpty()) continue;
                        for (Point q : neighbor.getList()) { // 遍历邻近单元格内的所有点
                            if (p.getDistanceFrom(q) <= eps) { // 计算距离，判断是否为邻近点
                                numCnt += 1;
                                if (numCnt >= minPoints) { // 若达到核心点条件，则标记为核心点并跳出循环
                                    p.setLabelCore();
                                    p.setYLP(1);
                                    currentCell.type = 1;   // all core
                                    if(!core_cell.contains(currentCell)) core_cell.add(currentCell);
                                    break;
                                }
                            }
                        }
                        if (numCnt >= minPoints) break; // 已找到足够的邻近点，跳出循环
                    }
                    if(numCnt < minPoints){
                        BorderNoise.add(p);
                    }
                }
            }
        }
    }

    private Point findNeighborCorePoint(Point currentPoint, List<Cell> neighboringCells) {
        return neighboringCells.stream()
                .flatMap(neighborCell -> neighborCell.getList().stream())
                .filter(point -> point.isCore() && point.getDistanceFrom(currentPoint) <= eps)
                .findFirst()
                .orElse(null);
    }

    private int calculateYLPC(Point currentPoint, List<Cell> neighboringCells) {
        return (int) neighboringCells.stream()
                .flatMap(neighborCell -> neighborCell.getList().stream())
                .filter(borderPoint -> borderPoint.getDistanceFrom(currentPoint) <= eps)
                .count();
    }

    private void DetermineBorderNoisePoint() {   //计算noise的ylp值
//        System.out.println("nonCore: " + BorderNoise.size());
        for (Point pb : BorderNoise){
            int[] ij = pb.getGrid(minX,minY);
            Cell uj = grid.getCell(ij[0], ij[1]);
            List<Cell> neighboringCells = grid.calculateNeighboringCells(ij[0], ij[1]);
            int ylpc = calculateYLPC(pb, neighboringCells);
            ylpc += uj.getList().size();
            double ylpcd = (double) ylpc / minPoints;
            pb.setYLP(ylpcd);
            // maintain nonCoreNonCneighbor (neighbors)
            for(Cell nCell : neighboringCells){
                for(Point pC: nCell.getList()){
                    if(!pC.isCore()){
                        if(pb.getDistanceFrom(pC)<=eps){
                            NonCore_NonCneighbors.computeIfAbsent(pb.getId(), k -> new ArrayList<>()).add(pC);
                        }
                    }
                }
            }
            for(Point pC : uj.getList()){
                if(!pC.isCore()){
                    NonCore_NonCneighbors.computeIfAbsent(pb.getId(), k -> new ArrayList<>()).add(pC);
                }
            }
            neighboringCells.add(uj);
            Point corePt = findNeighborCorePoint(pb,neighboringCells);
            if (corePt != null) {
                pb.setLabelBorder();
                Border.add(pb); // 假设Border是已定义的集合
                if(uj.type==0){
                    uj.type = 2;
                    if(!core_cell.contains(uj)) core_cell.add(uj);
                }
            } else {
                pb.setLabelNoise();
                Noise.add(pb); // 假设Noise是已定义的集合
                uj.type = 4;
                if(!noise_cell.contains(uj)){
                    noise_cell.add(uj);
                }
                int key = ij[0] * grid.getNcols() + ij[1];
                cell_noise_points.computeIfAbsent(key, k -> new ArrayList<>()).add(pb);
            }
        }
    }

    /**
     * partial algorithm 1: initialize U(N), N(u), N(p)
     */
    public void initialization() {
        // 初始化噪声邻居映射
        for (Point pj : points) {
            // 使用HashSet优化查找
            Set<Point> pt_n_neighbors = new HashSet<>();
            if (pj.getYLP() < 1) {
                // uj_ij 表示当前点 pj 所在的单元格
                int[] uj_ij = pj.getGrid(minX, minY);
                // 查找邻近单元格（可能包含噪声邻居）
                List<Cell> uks = grid.calculateNeighboringCells(uj_ij[0], uj_ij[1]);
                for (Cell uk : uks) {
                    if(uk.type == 4){
                        for (Point pk : cell_noise_points.get(grid.cell_index.get(uk))) {
                            // 直接检查是否为噪声点且距离小于 eps
                            if (pj.getDistanceFrom(pk) <= eps) {
                                pt_n_neighbors.add(pk);
                            }
                        }
                    }
                }
            }
            // 使用HashSet后不需要检查是否已经包含该点
            noise_neighbors.put(pj.getId(), new ArrayList<>(pt_n_neighbors));
        }
    }

    // algorithm 3
    /**
     * algorithm 3: remove noise point after repairing
     * @param pi
     */
    public void RemoveNoise(Point pi){
        Noise.remove(pi); // 假设Noise是一个集合，直接移除即可
        int[] pi_ij = pi.getGrid(minX, minY); // 获取点pi所在的网格位置
        Cell ui = grid.getCell(pi_ij[0], pi_ij[1]); // 获取pi所在的单元格
        int key = pi_ij[0] * grid.getNcols() + pi_ij[1]; // 计算对应的键值
        // 检查该键值是否存在于cell_noise_points中
        if(cell_noise_points.containsKey(key)){
            ArrayList<Point> Nui = cell_noise_points.get(key); // 获取该单元格中的噪声点列表
            boolean isRemoved = Nui.remove(pi); // 尝试移除点pi，返回值表明是否成功移除
            if(isRemoved && Nui.isEmpty()){ // 如果pi被成功移除且现在列表为空
                noise_cell.remove(ui); // 从noise_cell集合中移除单元格ui
                cell_noise_points.remove(key); // 从cell_noise_points中移除对应的条目
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
        // 获取pi所在的单元格位置
        int[] pi_ij = pi.getGrid(minX, minY);
        // 计算邻近单元格
        int key = pi_ij[0] * grid.getNcols() + pi_ij[1];
        for (Point pt : grid.grid.get(key).getList()){
            neighbors.add(pt);
        }
        List<Cell> nCells = grid.calculateNeighboringCells(pi_ij[0], pi_ij[1]);
        // 遍历每个邻近单元格
        for (Cell neighborCell : nCells) {
            // 周边cell非空的情况下，添加满足条件的邻近点
            neighborCell.getList().stream()
                    .filter(pt -> pt.getDistanceFrom(pi) <= eps)
                    .forEach(neighbors::add);
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

    public int[] calculateNearestNoiseCell(int i, int j){
        int res[] = new int[2];
        double minv = Double.MAX_VALUE;
        for(Cell nCell : noise_cell){
            int nCell_i = nCell.key / grid.ncols; // 计算所在行号
            int nCell_j = nCell.key % grid.ncols; // 计算所在列号
            if(nCell_j==j && nCell_i==i) continue;  // 不算自己
            double dist = Math.sqrt(Math.pow(i - nCell_i, 2) + Math.pow(j - nCell_j, 2));
            if(dist < minv){
                minv = dist;
                res[0] = nCell_i;
                res[1] = nCell_j;
            }
        }
        return res;
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
    // noise_cell list
    // noise list
    // noise_neighbor list
    public void qdorc(){
        ArrayList<Point> nonCore = new ArrayList<>(Noise);
        nonCore.addAll(Border);
        nonCore.sort(Comparator.comparingDouble(Point::getYLP));
//        System.out.println("GDORC Point size: "+ points.size());
//        System.out.println("GDORC Noise size: "+ Noise.size());
//        System.out.println("GDORC cell size: " + grid.cell_index.size());
//        System.out.println(noise_cell.size());
        while (!Noise.isEmpty())
        {
//            System.out.println(Noise.size());
            Point pj = nonCore.get(nonCore.size()-1);
//            System.out.println("pj: " + pj.getYLP());
//            System.out.println("Noise size: " + Noise.size());
//            System.out.println("NonCore size: " + nonCore.size());
//            if(pj.getYLP()==1){
//                nonCore.remove(pj);
//                continue;
//            }
            int [] uj_ij = pj.getGrid(minX, minY);
            ArrayList<Point> pj_noise_neighbors = noise_neighbors.get(pj.getId());
            int self_noise = 0;
            if(Noise.contains(pj)){
                self_noise = 1;
            }
            if((Noise.size()-self_noise-pj_noise_neighbors.size()) >= (1-pj.getYLP())*minPoints) {
                int repeatTimes = (int)((1-pj.getYLP())*minPoints + 0.1)+pj_noise_neighbors.size();
                while (repeatTimes > 0) {
                    int[] ui_ij = calculateNearestNoiseCell(uj_ij[0], uj_ij[1]);
                    int u_key = ui_ij[0] * grid.getNcols() + ui_ij[1];
//                    Cell ui = grid.getCell(ui_ij[0], ui_ij[1]);
                    ArrayList<Point> cnp = new ArrayList<>(cell_noise_points.get(u_key));
                    int size = cnp.size();
                    for(int i=0; i<size; i++) {
                        Point pi = cnp.get(i);
                        if (pj.getDistanceFrom(pi) > eps) {
                            if(NonCore_NonCneighbors.containsKey(pi.getId())){
                                ArrayList<Point> tmp = new ArrayList<>(NonCore_NonCneighbors.get(pi.getId()));
                                for(Point pl: tmp){
                                    pl.setYLP(pl.getYLP() - 1 / minPoints);
                                    if(noise_neighbors.containsKey(pl))  noise_neighbors.get(pl.getId()).remove(pi);
                                    if(NonCore_NonCneighbors.containsKey(pl)) NonCore_NonCneighbors.get(pl.getId()).remove(pi);
                                }
                            }
//                            ArrayList<Point> pi_neighbors = NeighborPoints(pi);
//                            for (Point pl : pi_neighbors) {
//                                if (pj.getDistanceFrom(pl) > eps) {
//                                    pl.setYLP(pl.getYLP() - 1 / minPoints);
//                                    ArrayList<Point> tmp = noise_neighbors.get(pl.getId());
//                                    tmp.remove(pi);
//                                    noise_neighbors.put(pl.getId(), tmp);
//                                }
//                            }
                            pi.setLabelCore();
                            RemoveNoise(pi);
                            pi.setX(pj.getX());
                            pi.setY(pj.getY());
                            pi.setYLP(1);
                            nonCore.remove(pi);
                        }
                        repeatTimes--;
                        if(NonCore_NonCneighbors.containsKey(pj.getId())){
                            ArrayList<Point> tmp = NonCore_NonCneighbors.get(pj.getId());
                            for(Point pk: tmp){
                                if(pi.getDistanceFrom(pk) > eps){
                                    double kylp = pk.getYLP() + 1 / minPoints;
                                double new_kylp;
                                if (kylp > 1) {
                                    new_kylp = 1;
                                } else {
                                    new_kylp = kylp;
                                }
                                pk.setYLP(new_kylp);
                                }
                                if(pk.getYLP()>=1){
                                    nonCore.remove(pk);
                                    RemoveNoise(pk);
                                    if(NonCore_NonCneighbors.containsKey(pk)){
                                        ArrayList<Point> tmp1 = new ArrayList<>(NonCore_NonCneighbors.get(pk.getId()));
                                        for(Point pnc : tmp1){
                                            if(NonCore_NonCneighbors.containsKey(pnc.getId())){
                                                NonCore_NonCneighbors.get(pnc.getId()).remove(pk);
                                            }
                                            if(noise_neighbors.containsKey(pnc.getId())){
                                                noise_neighbors.get(pnc.getId()).remove(pk);
                                            }
                                        }
                                        NonCore_NonCneighbors.remove(pk.getId());
                                    }
                                }
                            }
                        }
//                        ArrayList<Point> pj_neighbors = NeighborPoints(pj);
//                        for (Point pk : pj_neighbors) {
//                            if (pi.getDistanceFrom(pk) > eps) {
//                                double kylp = pk.getYLP() + 1 / minPoints;
//                                double new_kylp;
//                                if (kylp > 1) {
//                                    new_kylp = 1;
//                                } else {
//                                    new_kylp = kylp;
//                                }
//                                pk.setYLP(new_kylp);
//                            }
//                            if(pk.getYLP()>=1){
//                                nonCore.remove(pk);
//                            }
//                        }
                        if (repeatTimes <= 0) break;
                    }
                }
                // change pj status
                nonCore.remove(pj);
                RemoveNoise(pj);
                if(pj.getYLP()!=1){
                    if(NonCore_NonCneighbors.containsKey(pj.getId())){
                        ArrayList<Point> tmp = new ArrayList<>(NonCore_NonCneighbors.get(pj.getId()));
                        for(Point pl: tmp){
                            if(noise_neighbors.containsKey(pl.getId()))  noise_neighbors.get(pl.getId()).remove(pj);
                            if(NonCore_NonCneighbors.containsKey(pl.getId())) NonCore_NonCneighbors.get(pl.getId()).remove(pj);
                        }
                        NonCore_NonCneighbors.remove(pj.getId());
                    }
//                    ArrayList<Point> pj_neighbors = NeighborPoints(pj);
//                    for (Point pk : pj_neighbors) {
//                        if (pk.isNoise()) {
//                            RemoveNoise(pk);
//                            ArrayList<Point> pk_neighbors = NeighborPoints(pk);
//                            for (Point ph : pk_neighbors) {
//                                ArrayList<Point> tmp = noise_neighbors.get(ph.getId());
//                                tmp.remove(pk);
//                                noise_neighbors.put(ph.getId(), tmp);
//                            }
//                        }
//                    }
                    pj.setLabelCore();
                    pj.setYLP(1);
                }
//                Noise.remove(pj);
            }
            else // no sufficient noises retain
            {
                while(Noise.size()!=0){
                    Point pi=Noise.get(0);
                    int [] ui_ij = pi.getGrid(minX, minY);
                    int [] uk_ij;
                    uk_ij=calculateNearestNonNoiseCell(ui_ij[0], ui_ij[1]);
                    Cell uk = grid.getCell(uk_ij[0], uk_ij[1]);
                    RemoveNoise(pi);
                    for(Point pk : uk.getList())
                    {
                        if(!pk.isNoise())
                        {
                            pi.setLabelCore();
                            pi.setX(pk.getX());
                            pi.setY(pk.getY());
                            break;
                        }
                    }

//                    Noise.remove(pi);
                }
            }
        }
    }

    private int[] calculateNearestNonNoiseCell(int i, int j) {
        int res[] = new int[2];
        double minv = Double.MAX_VALUE;
        for(Cell cCell: core_cell){
            int nCell_i = cCell.key / grid.ncols; // 计算所在行号
            int nCell_j = cCell.key % grid.ncols; // 计算所在列号
            if(nCell_j==j && nCell_i==i) continue;  // 不算自己
            double dist = Math.sqrt(Math.pow(i - nCell_i, 2) + Math.pow(j - nCell_j, 2));
            if(dist < minv){
                minv = dist;
                res[0] = nCell_i;
                res[1] = nCell_j;
            }
        }
        return res;
    }

    public void log(String path){
        LogWriter repairLog = new LogWriter(path);
        repairLog.open();
        for (Point p: points){
            repairLog.log(p.getId()+"\t"+p.getX()+"\t"+p.getY()+"\n");
        }
    }
}
