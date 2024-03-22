package grid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Scan {

    protected static List<Point> points; //the list of all points of our input file  //static
    protected List<Point> truth; //the list of all points of our input file
    protected List<Point> ilpdata; //the list of all points of our input file
    protected List<Point> Noise; //the list of noise points of our input file
    protected List<Point> Border; //the list of border points of our input file
    protected List<Point> BorderNoise;
    protected List<Cluster> clusters; //the list of all clusters uniformed by our algorithm
    protected static int minPoints;
    protected static double eps;
    protected int clusterCounter; //used to initialize different clusters

    abstract void scan();

    /**
     * Reads the input 2-dimensional data from the specified path.
     * Data should have a header and be of format:
     * id   \t  x   \t  y
     * 1    \t 94.3 \t  -1.394
     * @param path
     */
    public void oldreadData(String path) {
        String line = "";
        int id = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] attr = null;
//            line = br.readLine(); //remove header
            while ((line = br.readLine()) != null) {
                attr = line.split("\t");
                points.add(new Point(id++, Double.parseDouble(attr[1]), Double.parseDouble(attr[2])));
            }
        } catch (IOException ex) {
            System.out.println("Please enter a valid filepath.");
            System.exit(0);
        }
    }
    public void readData(String path) {
        int id = 0;
        List<Point> tempPoints = new ArrayList<>(); // Temporary list to avoid resizing
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//        br.readLine(); // Uncomment to skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] attr = line.split("\t");
                double x = Double.parseDouble(attr[1]);
                double y = Double.parseDouble(attr[2]);
                tempPoints.add(new Point(id++, x, y));
            }
            points.addAll(tempPoints); // Add all at once to the main list
        } catch (IOException ex) {
            System.out.println("Error reading the file: " + ex.getMessage());
            System.exit(1);
        }
    }

    public void readDataTruth(String path) {
        String line = "";
        int id = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] attr = null;
//            line = br.readLine(); //remove header
            while ((line = br.readLine()) != null) {
                attr = line.split("\t");
                truth.add(new Point(id++, Double.parseDouble(attr[1]), Double.parseDouble(attr[2])));
            }
        } catch (IOException ex) {
            System.out.println("Please enter a valid filepath.");
            System.exit(0);
        }
    }

    protected void readDataILP(String path) {
        String line = "";
        int id = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] attr = null;
//            line = br.readLine(); //remove header
            while ((line = br.readLine()) != null) {
                attr = line.split("\t");
                ilpdata.add(new Point(id++, Double.parseDouble(attr[1]), Double.parseDouble(attr[2])));
            }
        } catch (IOException ex) {
            System.out.println("Please enter a valid filepath.");
            System.exit(0);
        }
    }

    /**
     * Prints the results of the algorithm including:
     * Execute Time, number of noise points, total clusters
     * and for each cluster print the number of points it contains.
     */
    protected void print() {
        System.out.println(" using DBSCAN with EPS " + eps + " and MinPoints " + minPoints);
        printHeader();
//        System.out.println("Total clusters: " + clusters.size());
//        System.out.println("-----------------------------");
        for (Cluster cluster : clusters) {
//            System.out.println("Cluster_" + cluster.getId() + ": " + cluster.getPoints().size() + " points");
        }
//        System.out.println("-----------------------------");
    }

    protected void printHeader(){
//        System.out.println("-----------------------------");

//        System.out.println("Total points: " + points.size());
        int cntNoise=0;
        for(Point p: points){
            if(p.isNoise()){ cntNoise++; }
        }
//        System.out.println("Noise: " + cntNoise + " points");
    }

}
