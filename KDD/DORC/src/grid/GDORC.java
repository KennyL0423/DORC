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
//        startTime = System.currentTimeMillis();
        constructGrid();    // here
//        endTime = System.currentTimeMillis();
//        System.out.println((endTime-startTime)/1000.0);
        determineCorePoints();
        DetermineBorderPoint();
        DetermineNoisePoint();
        initialization(); // algorithm 1
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
            Cell currentCell = grid.getCell(i, j);
            int cellPoints = currentCell.getList().size(); // 获取单元格内点的数量

            // 如果单元格内的点数大于等于minPoints，则设置所有点为核心点
            if (cellPoints >= minPoints) {
                for (Point p : currentCell.getList()) {
                    p.setLabelCore();
                    p.setYLP(1);
                }
                currentCell.setCore(true);
            } else if (cellPoints != 0) { // 单元格内有点，但不足minPoints
                List<Cell> neighboringCells = grid.calculateNeighboringCells(i, j); // 获取邻近单元格列表
                for (Point p : currentCell.getList()) {
                    Set<Point> numPoints = new HashSet<>(); // 存储邻近点
                    for (Cell neighbor : neighboringCells) { // 遍历每个邻近单元格
                        if (neighbor.isEmpty()) continue;
                        for (Point q : neighbor.getList()) { // 遍历邻近单元格内的所有点
                            if (p.getDistanceFrom(q) <= eps) { // 计算距离，判断是否为邻近点
                                numPoints.add(q);
                                if (numPoints.size() >= minPoints) { // 若达到核心点条件，则标记为核心点并跳出循环
                                    p.setLabelCore();
                                    p.setYLP(1);
                                    currentCell.setCore(true);
                                    break;
                                }
                            }
                        }
                        if (numPoints.size() >= minPoints) break; // 已找到足够的邻近点，跳出循环
                    }
                }
            }
        }
    }

    private Point findNearestCorePoint(Point currentPoint, List<Cell> neighboringCells) {
        return neighboringCells.stream()
                .filter(neighborCell -> !neighborCell.isEmpty())
                .flatMap(neighborCell -> neighborCell.getList().stream())
                .filter(Point::isCore)
                .min(Comparator.comparingDouble(corePoint -> corePoint.getDistanceFrom(currentPoint)))
                .orElse(null);
    }

    private int calculateYLPC(Point currentPoint, List<Cell> neighboringCells) {
        return (int) neighboringCells.stream()
                .flatMap(neighborCell -> neighborCell.getList().stream())
                .filter(borderPoint -> borderPoint.getDistanceFrom(currentPoint) <= eps)
                .count();
    }

    private void DetermineBorderPoint() {
        for (int key : grid.grid.keySet()) {
            int i = key / grid.ncols; // 注意之前的+1问题，在这里调整为正确的行列计算方式
            int j = key % grid.ncols;
            grid.getCell(i, j).getList().stream() // 使用Stream API处理当前单元格的所有点
            .filter(point -> !point.isCore()) // 过滤出非核心点
            .forEach(currentPoint -> {
                List<Cell> neighboringCells = grid.calculateNeighboringCells(i, j);
                Point nearestCorePoint = findNearestCorePoint(currentPoint, neighboringCells);
                if (nearestCorePoint != null) {
                    currentPoint.setCluster(nearestCorePoint.getCluster());
                    currentPoint.setLabelBorder();
                    Border.add(currentPoint); // 假设Border是已定义的集合
                } else {
                    currentPoint.setLabelNoise();
                    Noise.add(currentPoint); // 假设Noise是已定义的集合
                }
                int ylpc = calculateYLPC(currentPoint, neighboringCells);
                double ylpcd = (double) ylpc / minPoints;
                currentPoint.setYLP(ylpcd);
            });
//            for (Point currentPoint : grid.getCell(i, j).getList()) { //for every point in current cell
//                if (!currentPoint.isCore()) {//当前点不是core
//                    int ylpc=0;
//                    Point q = null; //q
//                    List<Cell> nCells = grid.calculateNeighboringCells(i, j);
//                    for (Cell neighborCell : nCells) { //for every neighbor cell
//                        if (!neighborCell.isEmpty()) {//周边cell非空
//                            Point nearCorePoint =null;//周边点
//                            Point temp = (neighborCell.getNearestCorePoint(currentPoint));//周边cell中与当前点最近的core点
//                            if(temp!=null && temp.getDistanceFrom(currentPoint)<=eps){
//                                nearCorePoint = temp;
//
//                            }
//                            if(nearCorePoint == null){
//                                continue;
//                            }
//                            if (q == null) {
//                                q = nearCorePoint;
//                            } else if (currentPoint.getDistanceFrom(nearCorePoint) <= currentPoint.getDistanceFrom(q)) {
//                                q = nearCorePoint;
//                            }
//
//                            for(Point borderq : neighborCell.getList())
//                            {
//                                if(borderq.getDistanceFrom(currentPoint)<=eps){
//                                    ylpc++;
//                                }
//                            }
//                            // set y for this point
//                            double ylpcd= (double) ylpc/minPoints;
//                            currentPoint.setYLP(ylpcd);
//                        }
//                    }
//                    if (q != null) {
//                        currentPoint.setCluster(q.getCluster());    // not used
//                        currentPoint.setLabelBorder();
//                        Border.add(currentPoint);
//                    } else {
//                        currentPoint.setLabelNoise();
//                        Noise.add(currentPoint);
//                    }
//                }
//            }
        }
    }
    private void DetermineNoisePoint() {
        for (Point currentPoint : points) { // 遍历所有点
            if (currentPoint.isUndefined()) { // 标记未定义的点为噪声
                currentPoint.setLabelNoise();
                Noise.add(currentPoint);
            }
            if (currentPoint.isNoise()) {
                // 计算当前点所在的单元格坐标
                int[] uj_ij = currentPoint.getGrid(minX, minY);
                Cell uj = grid.getCell(uj_ij[0], uj_ij[1]);
                // 添加单元格到噪声单元格集合
                noise_cell.add(uj); // 假设 noise_cell 是 Set 类型，自动处理重复元素
                // 计算键值并将噪声点添加到对应的列表
                int key = uj_ij[0] * grid.getNcols() + uj_ij[1]; // 调整key的计算，如果已经修改了网格定义
                cell_noise_points.computeIfAbsent(key, k -> new ArrayList<>()).add(currentPoint);
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
                    for (Point pk : uk.getList()) {
                        // 直接检查是否为噪声点且距离小于 eps
                        if (pk.isNoise() && pj.getDistanceFrom(pk) <= eps) {
                            pt_n_neighbors.add(pk);
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
        int key = pi_ij[0] * (grid.getNcols() + 1) + pi_ij[1]; // 计算对应的键值
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

    public int[] calculateNearestNoiseCell(int i, int j){
        int nc[] = new int [2];
        int distance = grid.getNcols() + grid.getNrows();
        for (Cell nCell : noise_cell){
            int nCell_index = grid.cell_index.get(nCell);
            int nCell_i = nCell_index/(grid.getNcols()+1); // rownum
            int nCell_j = nCell_index%(grid.getNcols()+1); // colnum
            // calculate the relative distance
            int cur_distance = Math.abs(nCell_i - i) + Math.abs(nCell_j - j);
            if (cur_distance < distance){
                distance = cur_distance;
                nc[0] = nCell_i;
                nc[1] = nCell_j;
            }
        }
        return nc;
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
        ArrayList<Point> nonCore = new ArrayList<>(Noise);
        nonCore.addAll(Border);
        Collections.sort(nonCore, new Comparator<Point>(){
            @Override
            public int compare(Point o1, Point o2) {
                if(o1.getYLP() < o2.getYLP())
                    return -1;
                if(o1.getYLP() > o2.getYLP())
                    return 1;
                return 0;
            }
        });
        // gdorc algorithm here: algorithm 2 in paper
        // repair while noise points exist
        double nearestNoise = 0.0;
        double nearestNonNoise = 0.0;
        while (Noise.size()!=0)
        {
            Point pj = nonCore.get(nonCore.size()-1);
            int [] uj_ij = pj.getGrid(minX, minY);
//            Cell uj = grid.getCell(uj_ij[0], uj_ij[1]);
            ArrayList<Point> pj_noise_neighbors = noise_neighbors.get(pj.getId());
            if((Noise.size()-pj_noise_neighbors.size()) >= (1-pj.getYLP())*minPoints) {
//                int repeatTimes = (int) ((1 - pj.getYLP()) * minPoints + 0.1) + pj_noise_neighbors.size();
                int repeatTimes = (int)((1-pj.getYLP())*minPoints + 0.1);
                // enough to make it a core, find the nearest noise cell ui to uj, find the noise point pi in ui
                while (repeatTimes > 0) {
                    // find the nearest noise cell
//                    startTime = System.currentTimeMillis();
                    int[] ui_ij = grid.calculateNearestNoiseCell(uj_ij[0], uj_ij[1]);
//                    int[] ui_ij = calculateNearestNoiseCell(uj_ij[0], uj_ij[1]);
//                    endTime = System.currentTimeMillis();
//                    nearestNoise += ((endTime-startTime)/1000.0);
                    if (grid.hasCell(ui_ij[0], ui_ij[1])) {
//                        System.out.println("GDORC Noise size: " + Noise.size());
//                        startTime = System.currentTimeMillis();
                        Cell ui = grid.getCell(ui_ij[0], ui_ij[1]);
//                        List<Point> pis = ui.getList();
                        List<Point> pis = ui.getList();
                        for(Point pi : pis) {
                            if (pi.isNoise()) {
                                if (pj.getDistanceFrom(pi) > eps) {
                                    ArrayList<Point> pi_neighbors = NeighborPoints(pi);
                                    for (Point pl : pi_neighbors) {
                                        if (pj.getDistanceFrom(pl) > eps) {
                                            pl.setYLP(pl.getYLP() - 1 / minPoints);
                                            ArrayList<Point> tmp = noise_neighbors.get(pl.getId());
                                            tmp.remove(pi);
                                            noise_neighbors.put(pl.getId(), tmp);
                                        }
                                    }
                                    // repair pi to pj
                                    pi.setCluster(pj.getCluster());
                                    pi.setLabelCore();
                                    pi.setX(pj.getX());
                                    pi.setY(pj.getY());
                                }
                                RemoveNoise(pi);
                                repeatTimes--;
                                ArrayList<Point> pj_neighbors = NeighborPoints(pj);
                                for (Point pk : pj_neighbors) {
                                    if (pi.getDistanceFrom(pk) > eps) {
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
                                    }
                                }
                                if (repeatTimes <= 0) break;
                            }
                        }
                    }
                }
                // change pj status
                if(pj.getYLP()!=1){
                    ArrayList<Point> pj_neighbors = NeighborPoints(pj);
                    for (Point pk : pj_neighbors) {
                        if (pk.isNoise()) {
                            RemoveNoise(pk);
                            ArrayList<Point> pk_neighbors = NeighborPoints(pk);
                            for (Point ph : pk_neighbors) {
                                ArrayList<Point> tmp = noise_neighbors.get(ph.getId());
                                tmp.remove(pk);
                                noise_neighbors.put(ph.getId(), tmp);
                            }
                        }
                    }
                    pj.setLabelCore();
                    pj.setYLP(1);
                }
                if (pj.isNoise()) {
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
