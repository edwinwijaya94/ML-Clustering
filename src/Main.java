
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    public static void main(String[] args) throws IOException{
        
        //convert to float
        ArrayList<ArrayList<Float>> trainingData = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader("dataset/point.csv"));
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
           ArrayList<Float> instance = new ArrayList<>();
           for(int i=0; i<nextLine.length; i++){
               instance.add(Float.parseFloat(nextLine[i]));
           }
           trainingData.add(instance);
        }
        
        MyKMeans K = new MyKMeans();
        K.cluster(trainingData, 3);
        K.printCluster();


        System.out.println();
        ArrayList<Point> trainingPointData = new ArrayList<>();
        reader = new CSVReader(new FileReader("dataset/point.csv"));
        while ((nextLine = reader.readNext()) != null) {
            ArrayList<Float> instance = new ArrayList<>();
            for (String aNextLine : nextLine) {
                instance.add(Float.parseFloat(aNextLine));
            }
            trainingPointData.add(new Point(instance));
        }

        MyAgnes A = new MyAgnes();
        A.buildClassifier(trainingPointData);
        A.printCluster();
    }
}
