package utils;

import java.util.ArrayList;

public class Leader {
    public ArrayList<Point> followers;
    public Point leader;
    public int count;

    public Leader(){
        this.leader = null;
        this.followers = new ArrayList<Point>();
        this.count = 0;
    }
}
