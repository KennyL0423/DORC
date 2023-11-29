package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Dataset {
    public static ArrayList<Point> dataset = new ArrayList<Point>();
    public void importDataset(String path){
        File file = new File(path);
        try {
            BufferedReader br =new BufferedReader(new FileReader(file));
            String str = "";
            String line = br.readLine(); //remove header
            int id = 0;
            while((str = br.readLine()) != null){
                String[] tmp = str.split("\t");
                Point p = new Point();
                p.id = id;
                p.x = Double.parseDouble(tmp[1]);
                p.y = Double.parseDouble(tmp[2]);
                dataset.add(p);
                id++;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void clearAll(){
        dataset.clear();
    }
}
