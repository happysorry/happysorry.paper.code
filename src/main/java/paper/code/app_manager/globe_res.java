package main.java.paper.code.app_manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class globe_res {
    public static ArrayList<Double> mn1 = new ArrayList<>();
    public static ArrayList<Double> mn2 = new ArrayList<>();
    public static ArrayList<Double> mnae1 = new ArrayList<>();
    public static ArrayList<Double> mnae2 = new ArrayList<>();


    public static void main(String[] args) {
        read("app_mn1");
        read("app_mn2");
        read("app_mnae1");
        read("app_mnae2");

        System.out.println(mn2.size());
        for(int i=0;i<mn1.size();i++){
            double avg = 0;
            avg = 2 * mn1.get(i) + mn2.get(i) + mnae1.get(i) +mnae2.get(i);
            // System.out.println(avg);
            write(avg);
        }

    }

    public static void read(String con_name){
        String filename = "ql3/" + con_name + "/" + con_name + "_response_time.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            double val = 0;
            try {
                while ((line = r.readLine()) != null) {
                    val = Double.parseDouble(line);
                    switch(con_name){
                        case "app_mn1":
                            mn1.add(val);
                            break;
                        case "app_mn2":
                            mn2.add(val);
                            break;
                        case "app_mnae1":
                            mnae1.add(val);
                            break;
                        case "app_mnae2":
                            mnae2.add(val);
                            break;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public  static void write(double avg) {
        try {
            String filename = "globe_res.txt";
            FileWriter fw1 = new FileWriter(filename,true);
            fw1.write(avg + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
