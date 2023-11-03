package utils;

public class Point {
    public double x;
    public double y;
    public int poi = -1;
    public int state = NOTVISITED;
    public int id = 0;
    public int noiseBelong = -1;
    public boolean isNoiseInRawData = false;

    public final static int NOTVISITED = 0;
    public final static int NOISE = 1;
    public final static int EDGE = 2;
    public final static int CORE = 3;
    public final static int VISITED = 4;

    public static double getDistance(Point p1, Point p2){
        double tmp = Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2);
        return Math.sqrt(tmp);
    }
    public static double getDistance(double x1,double y1, double x2, double y2){
        double tmp = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        return Math.sqrt(tmp);
    }

    public Point(){

    }
    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }
    public Point(int id, double x, double y){
        this.id = id;
        this.x = x;
        this.y = y;
    }
    public Point(String id, String x, String y){
        this.id = Integer.parseInt(id);
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
    }
}
