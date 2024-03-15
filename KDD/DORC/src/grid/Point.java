package grid;

import java.util.Objects;

public class Point implements Comparable<Object> {

    private int id;
    private double x;
    private double y;
    private double xr;
    private double yr;
    private double ylp;
    //    private double eps;
    private int xlp;
    private int cluster;
    private int[] grid =new int[2];
    private PointLabel label;

    public Point(int id, double x, double y) {//, int[] grid
        this.id = id;
        this.x = x;
        this.y = y;
        this.xr = x;
        this.yr = y;
        setLabelUndefined();
        this.cluster = -1;
        this.ylp=0;
        this.xlp=id;
//        this.grid=grid;
    }

    public Point(int id, int cluster) {//, int[] grid
        this.id = id;

        this.cluster = cluster;
//        this.ylp=0;
//        this.xlp=id;
//        this.grid=grid;
    }

    public Point(int id, double x, double y, int cluster) {//, int[] grid
        this.id = id;
        this.x = x;
        this.y = y;
        this.xr = x;
        this.yr = y;
        setLabelUndefined();
        this.cluster = cluster;
        this.ylp=0;
        this.xlp=id;
//        this.grid=grid;
    }

    /**
     * Computes the euclidean distance of current point with the point given in parameter.
     * @param otherPoint
     * @return the distance between the 2 points
     */
    public double getDistanceFrom(Point otherPoint) {
        double dist = Math.sqrt(Math.pow(this.x - otherPoint.x, 2) + Math.pow(this.y - otherPoint.y, 2));
        return dist;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", cluster=" + cluster +
                ", label=" + label +
                ", ylp=" + ylp +
                ", xlp=" + xlp +
//                ", grid=" + grid[0] +grid[1]+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0;// &&
//                Integer.compare(point.xlp, xlp)==0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y,xlp);
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getXR() {
        return xr;
    }

    public double getYR() {
        return yr;
    }

    public double getYLP() {
        return ylp;
    }

    public int getXLP() {
        return xlp;
    }

    public int[] getGrid(double minX, double minY) {
        double cellWidth = GDORC.eps / Math.sqrt(2);
        int gridX = (int) ((x - minX) / cellWidth);
        int gridY = (int) ((y - minY) / cellWidth);
        return new int[] {gridX, gridY};
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public void setYLP(double ylp) {
        this.ylp = ylp;
    }

    public void setXR(double xr) {
        this.xr = xr;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setYR(double yr) {
        this.yr = yr;
    }

    public void setXLP(int xlp) {
        this.xlp = xlp;
    }

    public void setGrid(int [] grid) {
        this.grid = grid;
    }

    public void setLabelBorder() {
        label = PointLabel.BORDER;
    }

    public void setLabelCore() {
        label = PointLabel.CORE;
    }

    public void setLabelNoise() {
        label = PointLabel.NOISE;
    }

    public void setLabelUndefined() {
        label = PointLabel.UNDEFINED;
    }

    public boolean isCore() {
        return label.equals(PointLabel.CORE);
    }

    public boolean isUndefined() {
        return label.equals(PointLabel.UNDEFINED);
    }

    public boolean isNoise() {
        return label.equals(PointLabel.NOISE);
    }

    public boolean isBorder() {
        return label.equals(PointLabel.BORDER);
    }

    public PointLabel getLabel() {
        return label;
    }

    @Override
    public int compareTo(Object o) {
        Point p = (Point) o;
        if (this.getX() < p.getX()) return -1;
        if (this.getX() > p.getX()) return 1;
        return 0;
    }

    public double wij(Object o) {
        Point p = (Point) o;
        double weight=Math.sqrt(p.getX()*p.getX()+p.getY()*p.getY());

        return weight;
    }
//        Point p = (Point) o;
//        if (this.getX() < p.getX()) return -1;
//        if (this.getX() > p.getX()) return 1;
//        return 0;
//    }

}