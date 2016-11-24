
import java.util.ArrayList;
import weka.clusterers.AbstractClusterer;
import weka.core.Instance;
import weka.core.Instances;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Edwin
 */
public class MyKMeans extends AbstractClusterer {
   
    @Override
    public int numberOfClusters() throws Exception {
        return numCluster;
    }
    
    public class Distance{
        int cluster; //cluster index
        double distance; //distance to centroid
        
        public int getCluster(){
            return this.cluster;
        }
        
        public double getDistance(){
            return this.distance;
        }
        
        public void setCluster(int cluster){
            this.cluster = cluster;
        }
        
        public void setDistance(double distance){
            this.distance = distance;
        }
        
    }
    
    ArrayList<ArrayList<Double>> centroids;
    ArrayList<ArrayList<ArrayList<Double>>> clusters;
    int iterations;
    int numCluster;
    
    public double euclideanDist(ArrayList<Double> vec1, ArrayList<Double> vec2){
        double res = 0;
        int sum = 0;
        for(int i=0; i<vec1.size(); i++){
            sum +=Math.pow((vec2.get(i)-vec1.get(i)), 2);
        }
        res = (double) Math.sqrt(sum);
        return res;
    }
    
    public ArrayList<Double> getCentroid(ArrayList<ArrayList<Double>> data){
        ArrayList<Double> res = new ArrayList<>();
        int size = data.size();
        int row = data.size();
        int col = data.get(0).size();
        
        for(int j=0; j<col; j++){
            double sum = 0;
            for(int i=0; i<row; i++){
                sum += data.get(i).get(j);
            }
            res.add(sum/size);
        }
        
        return res;
    }
    
    public void setNumCluster(int numCluster) {
        this.numCluster = numCluster;
    }
    
    public void initCluster() {
        clusters = new ArrayList<>();
        for(int i=0; i<numCluster; i++){
            clusters.add(new ArrayList<>());
        }
    }
    
    public void printCluster(){
        System.out.println("Iterations: " + this.iterations);
        for(int i=0; i<clusters.size(); i++){
            System.out.printf("cluster-%d (%d):",i+1,clusters.get(i).size());
            for(int j=0; j<clusters.get(i).size(); j++){
                System.out.print(clusters.get(i).get(j)+" ");
            }
            System.out.println("");
        }
    }
    
     @Override
    public void buildClusterer(Instances data) throws Exception {
        this.iterations = 0;
        centroids = new ArrayList<>();
        //set initial centroids
        int diff = data.numInstances()/numCluster;
        for(int i=0, j=0; j<numCluster; i+=diff,j++){
            Instance instance = data.instance(i);
            double[] dArray = instance.toDoubleArray();
            ArrayList<Double> ad = new ArrayList<Double>();
            for (double d : dArray) {
                ad.add(d);
            }
            centroids.add(ad);
        }
//        centroids.add(trainingData.get(0));
//        centroids.add(trainingData.get(3));
//        centroids.add(trainingData.get(6));
        
        ArrayList<ArrayList<ArrayList<Double>>> prevClusters = new ArrayList<>();
        Distance minDist = new Distance(); // cluster-idx, distance
        
        //iterate
        while(true){
            
            initCluster();
            
            //do clustering
            for(int i=0; i<data.numInstances(); i++){
                
                //calculate min distance
                minDist.setCluster(0);
                Instance instance = data.instance(i);
                double[] dArray = instance.toDoubleArray();
                ArrayList<Double> ad = new ArrayList<Double>();
                for (double d : dArray) {
                    ad.add(d);
                }
                minDist.setDistance(euclideanDist(ad, centroids.get(0)));
                for(int j=1; j<centroids.size(); j++){
                    double dist = euclideanDist(ad, centroids.get(j));
                    if(dist<minDist.getDistance()){
                        minDist.setCluster(j);
                        minDist.setDistance(dist);
                    }
                }

                //put to cluster
                clusters.get(minDist.getCluster()).add(ad);
            }
            
            //check if clusters change
            if(clusters.equals(prevClusters)){
                break;
            } else {
                prevClusters = (ArrayList<ArrayList<ArrayList<Double>>>) clusters.clone();
                this.iterations++;
            }

            //calculate new centroid
            for(int j=0; j<centroids.size(); j++){
                if(clusters.get(j).size() > 0){ // check for empty cluster
                    centroids.set(j, getCentroid(clusters.get(j)));
                }
            }
        }
    }
}
