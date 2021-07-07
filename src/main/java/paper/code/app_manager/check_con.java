package main.java.paper.code.app_manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class check_con implements Runnable{
    public static ArrayList<String> cons = new ArrayList<>();
    public static double sim_time = 0;

    public check_con(double sim_time){
        this.sim_time = sim_time;
    }
    public static void main(String[]args){
        add_cons();
        double startTine = System.nanoTime();
        while(true){
            for(int i=0;i<cons.size();i++){
                String con_name = cons.get(i);
                get_cons(con_name);
            }
            while((System.nanoTime() - startTine)/1e9 < 10);
            startTine = System.nanoTime();
            
        }
    }
    public void run(){
        add_cons();
        double startTine = System.nanoTime();
        double st = System.nanoTime();
        while(true){
            for(int i=0;i<cons.size();i++){
                String con_name = cons.get(i);
                get_cons(con_name);
            }
            while((System.nanoTime() - startTine)/1e9 < 10);
            startTine = System.nanoTime();
            if((System.nanoTime()-st)/1e9 > sim_time)
                break;
        }
    }
    public static void write(String filename,int replicas) {
        try {
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(replicas + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void get_cons(String con_name){
        String filename = "ql3/" + con_name + "/" + con_name + "_con2.txt";
        int replicas = 0;
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    replicas = (int) Double.parseDouble(line);
                    if(replicas < 1){
                        replicas = 1;
                        write(filename, replicas);
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
    public static void add_cons(){
        cons.add("app_mn1");
        cons.add("app_mn2");
        cons.add("app_mnae1");
        cons.add("app_mnae2");
    }
}
