package utils;

public class DORCStruct {
//    public int[] h;
//    public double[] w;
    public int original_c;
    public int c = 0;
    public double y;
    public Point p;
    public boolean hasVisitedNeighbor = false;
    public int state = NOTVISITED;
    public Point redirectPoint;

    public static int NOTVISITED = 0;
    public static int VISITED = 1;

    public DORCStruct(int n, Point p){
//        h = new int[n];
//        w = new double[n];
        c = 0;
        y = 0;
        this.p = p;
        this.redirectPoint = null;
    }
}
