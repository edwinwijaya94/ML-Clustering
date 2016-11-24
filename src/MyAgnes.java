/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import weka.clusterers.AbstractClusterer;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Elvan and Vicko
 */

class Point implements Cloneable {
    private ArrayList<Double> coordinates = new ArrayList<>();

    Point(ArrayList<Double> coordinates){
        this.coordinates = coordinates;
    }

    ArrayList<Double> getCoordinates(){
        return coordinates;
    }

    public String toString(){
        String points = "";

        for (Double coordinate: coordinates){
            if (points.equalsIgnoreCase("")) points += coordinate;
            else points += "•" + coordinate;
        }

        return points;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class ProximityMatrix {
    private Double[][] proximityMatrix;
    private ArrayList<ArrayList<Point>> clusters;

    ProximityMatrix(ArrayList<ArrayList<Point>> clusters){
        this.clusters = clusters;
        proximityMatrix = new Double[clusters.size()][clusters.size()];
    }

    private float getEuclideanDistance(Point a, Point b){
        int sum = 0;

        for(int i=0; i<a.getCoordinates().size(); i++){
            sum += Math.pow((b.getCoordinates().get(i) - a.getCoordinates().get(i)), 2);
        }

        return (float) Math.sqrt(sum);
    }

    ProximityMatrix calculateSingleLinkage(){
        for (int i=0;i<clusters.size();i++) {
            for (int j=i;j<clusters.size();j++){
                if (i == j) {
                    proximityMatrix[i][j] = Double.valueOf(0);
                    proximityMatrix[j][i] = Double.valueOf(0);
                } else {
                    ArrayList<Point> clusterA = clusters.get(i);
                    ArrayList<Point> clusterB = clusters.get(j);
                    Double minimumDistanceBetweenClusterAB = Double.MAX_VALUE;

                    for (Point pointA: clusterA) {
                        for (Point pointB: clusterB) {
                            minimumDistanceBetweenClusterAB = Math.min(minimumDistanceBetweenClusterAB, getEuclideanDistance(pointA, pointB));
                        }
                    }

                    proximityMatrix[i][j] = minimumDistanceBetweenClusterAB;
                    proximityMatrix[j][i] = minimumDistanceBetweenClusterAB;
                }
            }
        }

        return this;
    }
    
    ProximityMatrix calculateCompleteLinkage(){
        for (int i=0;i<clusters.size();i++) {
            for (int j=i;j<clusters.size();j++){
                if (i == j) {
                    proximityMatrix[i][j] = Double.valueOf(0);
                    proximityMatrix[j][i] = Double.valueOf(0);
                } else {
                    ArrayList<Point> clusterA = clusters.get(i);
                    ArrayList<Point> clusterB = clusters.get(j);
                    Double maximumDistanceBetweenClusterAB = Double.MIN_VALUE;

                    for (Point pointA: clusterA) {
                        for (Point pointB: clusterB) {
                            maximumDistanceBetweenClusterAB = Math.max(maximumDistanceBetweenClusterAB, getEuclideanDistance(pointA, pointB));
                        }
                    }

                    proximityMatrix[i][j] = maximumDistanceBetweenClusterAB;
                    proximityMatrix[j][i] = maximumDistanceBetweenClusterAB;
                }
            }
        }

        return this;
    }

    int[] getNearestCluster(){
        Double minimumDistanceBetweenClusterAB = Double.MAX_VALUE;
        int clusterA = -1, clusterB = -1;

        for (int i=0;i<proximityMatrix.length;i++){
            for (int j=i+1;j<proximityMatrix[i].length;j++){
                if (minimumDistanceBetweenClusterAB > proximityMatrix[i][j]) {
                    clusterA = i;
                    clusterB = j;
                    minimumDistanceBetweenClusterAB = proximityMatrix[i][j];
                }
            }
        }

        return new int[]{clusterA, clusterB};
    }

    public void printProximityMatrix() {
        System.out.println("****************************");
        for (Double[] aProximityMatrix : proximityMatrix) {
            for (Double anAProximityMatrix : aProximityMatrix) {
                System.out.print(anAProximityMatrix + " ");
            }
            System.out.println();
        }
        System.out.println("****************************");
    }

    double getProximity(int row, int col){
        return proximityMatrix[row][col];
    }
}

public class MyAgnes extends AbstractClusterer {
    private ArrayList<ArrayList<ArrayList<Point>>> allLevelClusters = new ArrayList<>();
    private String linkage;
    
    @Override
    public int numberOfClusters() throws Exception {
        return 1;
    }
    
    void setLinkage(String newLinkage) {
        linkage = newLinkage;
    }

    void printCluster(){
        System.out.println(allLevelClusters);
    }

    public void printCluster(int level){
        System.out.println(allLevelClusters.get(level));
    }

    public ArrayList<ArrayList<Point>> cloneClusters(ArrayList<ArrayList<Point>> clusters){
        ArrayList<ArrayList<Point>> clonedClusters = new ArrayList<>();

        for(ArrayList<Point> cluster : clusters) {
            clonedClusters.add((ArrayList<Point>) cluster.clone());
        }

        return clonedClusters;
    }

    @Override
    public void buildClusterer(Instances data) throws Exception {
        ArrayList<ArrayList<Point>> clusters = new ArrayList<>();

        // Create clusters with each cluster containing one data point
        for (Instance aData : data) {
            ArrayList<Point> cluster = new ArrayList<>();
            double[] dArray = aData.toDoubleArray();
            ArrayList<Double> af = new ArrayList<Double>();
            for (double d : dArray) {
                af.add(d);
            }
            Point p = new Point(af);
            cluster.add(p);
            clusters.add(cluster);
        }

        allLevelClusters.add(cloneClusters(clusters));
        System.out.println(clusters.size() + " Clusters : " + clusters);

        // Start clustering until number of clusters = 1
        for (;clusters.size() != 1;) {
            ProximityMatrix proximityMatrix = new ProximityMatrix(clusters);
            
            if (linkage.equalsIgnoreCase("single")) {
                proximityMatrix.calculateSingleLinkage();
            } else if (linkage.equalsIgnoreCase("complete")) {
                proximityMatrix.calculateCompleteLinkage();
            }

            proximityMatrix.printProximityMatrix();

            int[] nearestCluster = proximityMatrix.getNearestCluster();
            int clusterAIdx = nearestCluster[0];
            int clusterBIdx = nearestCluster[1];

            // Merge both clusters
            clusters.get(clusterAIdx).addAll(clusters.get(clusterBIdx));
            clusters.remove(clusterBIdx);

            System.out.println();
            System.out.println(clusters.size() + " Clusters : " + clusters.toString().substring(1, clusters.toString().length()-1).replace("],", "]"));
            allLevelClusters.add(cloneClusters(clusters));
        }
    }
}
