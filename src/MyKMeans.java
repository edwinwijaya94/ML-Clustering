
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Edwin
 */
public class MyKMeans {
    
    public class Distance{
        int cluster; //cluster index
        float distance; //distance to centroid
        
        public int getCluster(){
            return this.cluster;
        }
        
        public float getDistance(){
            return this.distance;
        }
        
        public void setCluster(int cluster){
            this.cluster = cluster;
        }
        
        public void setDistance(float distance){
            this.distance = distance;
        }
        
    }
    
    ArrayList<ArrayList<Float>> centroids;
    ArrayList<ArrayList<ArrayList<Float>>> clusters;
    
    public float euclideanDist(ArrayList<Float> vec1, ArrayList<Float> vec2){
        float res = 0;
        int sum = 0;
        for(int i=0; i<vec1.size(); i++){
            sum +=Math.pow((vec2.get(i)-vec1.get(i)), 2);
        }
        res = (float) Math.sqrt(sum);
        return res;
    }
    
    public ArrayList<Float> getCentroid(ArrayList<ArrayList<Float>> data){
        ArrayList<Float> res = new ArrayList<>();
        int size = data.size();
        int row = data.size();
        int col = data.get(0).size();
        
        for(int j=0; j<col; j++){
            float sum = 0;
            for(int i=0; i<row; i++){
                sum += data.get(i).get(j);
            }
            res.add(sum/size);
        }
        
        return res;
    }
    
    public void cluster(ArrayList<ArrayList<Float>> trainingData, int numCluster){
        
        centroids = new ArrayList<>();
        //set initial centroids
        int diff = trainingData.size()/numCluster;
        for(int i=0; i<numCluster; i+=diff){
            centroids.add(trainingData.get(i));
        }
//        centroids.add(trainingData.get(0));
//        centroids.add(trainingData.get(3));
//        centroids.add(trainingData.get(6));
        
        ArrayList<ArrayList<ArrayList<Float>>> prevClusters = new ArrayList<>();
        Distance minDist = new Distance(); // cluster-idx, distance
        
        //iterate
        while(true){
            initCluster(numCluster);
            
            //do clustering
            for(int i=0; i<trainingData.size(); i++){
                
                //calculate min distance
                minDist.setCluster(0);
                minDist.setDistance(euclideanDist(trainingData.get(i), centroids.get(0)));
                for(int j=1; j<centroids.size(); j++){
                    float dist = euclideanDist(trainingData.get(i), centroids.get(j));
                    if(dist<minDist.getDistance()){
                        minDist.setCluster(j);
                        minDist.setDistance(dist);
                    }
                }

                //put to cluster
                clusters.get(minDist.getCluster()).add(trainingData.get(i));
            }
            
            //check if clusters change
            if(clusters.equals(prevClusters)){
                break;
            } else {
                prevClusters = (ArrayList<ArrayList<ArrayList<Float>>>) clusters.clone();
            }

            //calculate new centroid
            for(int j=0; j<centroids.size(); j++){
                if(clusters.get(j).size() > 0){ // check for empty cluster
                    centroids.set(j, getCentroid(clusters.get(j)));
                }
            }
        }
    }
    
    public void initCluster(int numCluster){
        clusters = new ArrayList<>();
        for(int i=0; i<numCluster; i++){
            clusters.add(new ArrayList<>());
        }
    }
    
    public void printCluster(){
        for(int i=0; i<clusters.size(); i++){
            System.out.printf("cluster-%d (%d):",i+1,clusters.get(i).size());
            for(int j=0; j<clusters.get(i).size(); j++){
                System.out.print(clusters.get(i).get(j)+" ");
            }
            System.out.println("");
        }
    }
}
