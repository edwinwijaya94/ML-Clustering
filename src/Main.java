
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

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
    
    private static Instances loadData(String path) throws Exception {
        Instances data = null;
        if (path.endsWith(".arff")) {
            DataSource source = new DataSource("dataset/"+path);
            data = source.getDataSet();
        } else if (path.endsWith(".csv")) {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File("dataset/"+path));
            
            //if there is no header row in csv file
            String[] options = new String[1];
            options[0] = "-H";
            loader.setOptions(options);
            
            data = loader.getDataSet();
            
            //save ARFF
            String arffFileName = "dataset/"+path.substring(0, path.indexOf('.')).concat(".arff");
            File file = new File(arffFileName);
            if (!file.exists()) {
                ArffSaver saver = new ArffSaver();
                saver.setInstances(data);
                saver.setFile(file);
                saver.writeBatch();
            }
            
            DataSource source = new DataSource(arffFileName);
            data = source.getDataSet();
        }
        return data;
  }
    
    public static void KMeansHandler(String file, int numCluster, boolean excludeClass) throws FileNotFoundException, IOException, Exception{
        MyKMeans K = new MyKMeans();
        K.setNumCluster(numCluster);
        Instances instances = loadData(file);
        if (excludeClass) {
            Remove remove = new Remove();
            remove.setAttributeIndices("last");
            remove.setInputFormat(instances);
            Instances newInstances = Filter.useFilter(instances, remove);
            System.out.println(newInstances);
            K.buildClusterer(newInstances);
            K.printCluster();
        } else {
            K.buildClusterer(instances);
            K.printCluster();
        }
    }
    
    public static void AggSingleHandler(String file) throws FileNotFoundException, IOException, Exception{
        MyAgnes A = new MyAgnes();
        A.setLinkage("single");
        A.buildClusterer(loadData(file));
        A.printCluster();
    }
    
    public static void AggCompleteHandler(String file) throws FileNotFoundException, IOException, Exception{
        MyAgnes A = new MyAgnes();
        A.setLinkage("complete");
        A.buildClusterer(loadData(file));
        A.printCluster();
    }
    
    public static void main(String[] args) throws IOException, Exception{
        
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Input command : <algorithm> <filename> <optional params>");
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
