package utils;

import java.util.ArrayList;

public class Leader {
    public ArrayList<Point> followers;
    public Point leader;
    public int count;
    public int id;
    public int state = DORCStruct.NOTVISITED;
    public double y;
    public int counter;
    public Leader(){
        this.leader = null;
        this.followers = new ArrayList<Point>();
        this.count = 0;
    }
}
