
import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Edwin
 */
public class Main {
    
    public static void KMeansHandler(String csvFile, int numCluster, boolean excludeClass) throws FileNotFoundException, IOException{
        //convert to float
        ArrayList<ArrayList<Float>> trainingData = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader("dataset/"+csvFile));
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
           ArrayList<Float> instance = new ArrayList<>();
           for(int i=0; i<nextLine.length; i++){
                if(!excludeClass || (excludeClass && i<nextLine.length-1)){ //assume class located in last attr
                    instance.add(Float.parseFloat(nextLine[i]));
                }
           }
           trainingData.add(instance);
        }
        
        MyKMeans K = new MyKMeans();
        K.cluster(trainingData, numCluster);
        K.printCluster();
    }
    
    public static void AggSingleHandler(String csvFile) throws FileNotFoundException, IOException{
        System.out.println();
        ArrayList<Point> trainingPointData = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader("dataset/"+csvFile));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            ArrayList<Float> instance = new ArrayList<>();
            for (String aNextLine : nextLine) {
                instance.add(Float.parseFloat(aNextLine));
            }
            trainingPointData.add(new Point(instance));
        }

        MyAgnes A = new MyAgnes();
        A.setLinkage("single");
        A.buildClassifier(trainingPointData);
        A.printCluster();
    }
    
    public static void AggCompleteHandler(String csvFile) throws FileNotFoundException, IOException{
        System.out.println();
        ArrayList<Point> trainingPointData = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader("dataset/"+csvFile));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            ArrayList<Float> instance = new ArrayList<>();
            for (String aNextLine : nextLine) {
                instance.add(Float.parseFloat(aNextLine));
            }
            trainingPointData.add(new Point(instance));
        }

        MyAgnes A = new MyAgnes();
        A.setLinkage("complete");
        A.buildClassifier(trainingPointData);
        A.printCluster();
    }
    
    public static void main(String[] args) throws IOException{
        
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Input command : <algorithm> <csv file> <optional params>");
            String input = sc.nextLine();
            if(input.startsWith("kmeans")){
                String[] command = input.split(" ");
                //<kmeans> <csv file> <number of cluster> <is exclude class>
                KMeansHandler(command[1], Integer.parseInt(command[2]), Boolean.parseBoolean(command[3]));
            }
            else if(input.startsWith("aggsingle")){
                String[] command = input.split(" ");
                AggSingleHandler(command[1]);
            }
            else if(input.startsWith("aggcomplete")){
                String[] command = input.split(" ");
                AggCompleteHandler(command[1]);
            }
        }
        
    }
}
