/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 * @author Edwin
 */

class Point implements Cloneable {
    private ArrayList<Float> coordinates = new ArrayList<>();

    Point(ArrayList<Float> coordinates){
        this.coordinates = coordinates;
    }

    ArrayList<Float> getCoordinates(){
        return coordinates;
    }

    public String toString(){
        String points = "";

        for (Float coordinate: coordinates){
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
    private Float[][] proximityMatrix;
    private ArrayList<ArrayList<Point>> clusters;

    ProximityMatrix(ArrayList<ArrayList<Point>> clusters){
        this.clusters = clusters;
        proximityMatrix = new Float[clusters.size()][clusters.size()];
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
                    proximityMatrix[i][j] = Float.valueOf(0);
                    proximityMatrix[j][i] = Float.valueOf(0);
                } else {
                    ArrayList<Point> clusterA = clusters.get(i);
                    ArrayList<Point> clusterB = clusters.get(j);
                    Float minimumDistanceBetweenClusterAB = Float.MAX_VALUE;

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

    int[] getNearestCluster(){
        Float minimumDistanceBetweenClusterAB = Float.MAX_VALUE;
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
        for (Float[] aProximityMatrix : proximityMatrix) {
            for (Float anAProximityMatrix : aProximityMatrix) {
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

public class MyAgnes {
    private ArrayList<ArrayList<ArrayList<Point>>> allLevelClusters = new ArrayList<>();

    void buildClassifier(ArrayList<Point> data){
        ArrayList<ArrayList<Point>> clusters = new ArrayList<>();

        // Create clusters with each cluster containing one data point
        for (Point aData : data) {
            ArrayList<Point> cluster = new ArrayList<>();
            cluster.add(aData);
            clusters.add(cluster);
        }

        allLevelClusters.add(cloneClusters(clusters));
        System.out.println(clusters.size() + " Clusters : " + clusters);

        // Start clustering until number of clusters = 1
        for (;clusters.size() != 1;) {
            ProximityMatrix proximityMatrix = new ProximityMatrix(clusters);
            proximityMatrix.calculateSingleLinkage();

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
}
