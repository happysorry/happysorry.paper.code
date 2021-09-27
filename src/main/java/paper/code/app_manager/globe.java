package main.java.paper.code.app_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
// import org.apache.commons.io.input.ReversedLinesFileReader;

public class globe implements Runnable{
    public  double sim_time = 0;
    public  double Tmax = 20;
    public  double total_max = 75; // mn1:20 mn2:20 mnae1:5 mnae2:5
    public  ArrayList<String> cons = new ArrayList<>();
    public int max_iter = 0;
    public globe(int max_iter){
        this.max_iter = max_iter;
    }

    public void run(){
        add_cons();
        double startTime = System.nanoTime();
        double checkTime = System.nanoTime();
        int iter = 0;
        while(true){
            iter = read_iter();
            if(iter >= max_iter)
                break;
            if((System.nanoTime()-checkTime) / 1e9 > 30){
                int signal = read();
                if(signal != 0){
                    Wait(140000);
                }
                check();
                checkTime = System.nanoTime();
            }
                
        }
    }
    public static void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public int read(){
        String filename = "signal.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            int line = 0;
            try {
                line = Integer.parseInt(r.readLine());
            if(line==1){
                return 1;
            }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    public int read_iter(){
        FileReader fr;
        String filename = "iter.txt";
        double avg = 0.0;
        int iter = 0;
        try {
            fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    iter = Integer.parseInt(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            return 0;
        }
        return iter;
    }

    // public static void main(String[]args){
    //     add_cons();
    //     // System.out.println("asdasda");
    //     double startTime = System.nanoTime();
    //     double checkTime = System.nanoTime();
        
    //         update("app_mn1");
    //         // check();
    //         // read("app_mn1");
    //         // if((System.nanoTime()-startTime) / 1e9 > sim_time)
    //         //     break;
    //         // if((System.nanoTime()-checkTime) / 1e9 > 30)
    //         //     check();
        
    // }

    public  void add_cons(){
        cons.add("app_mn1");
        cons.add("app_mn2");
        cons.add("app_mnae1");
        cons.add("app_mnae2");
    }
    public  void check(){
        double total_time = 0;
        double[]res = new double[cons.size()];
        for(int i = 0;i<cons.size();i++){
            String con_name = cons.get(i);
            double res_time = read(con_name);
            total_time += res_time;
            res[i] = res_time;
            // if(res_time > Tmax){
            //     update(con_name);
            // }
        }
        total_time += res[0]; // add mn1 twice
        if(total_time > total_max){ // over total response time
            // int ind = 0;
            // double m = 0;
            // for(int i = 0;i < cons.size();i++){
            //     if(res[i] > m){
            //         m = res[i];
            //         ind = i;
            //     }
            for(int i = 0;i<cons.size();i++){
                if(i == 0){
                    if(res[i] > 25){
                        String con_name = cons.get(i);
                        update(con_name);
                    }
                }
                else if(i == 1){
                    if(res[i] > 15){
                        String con_name = cons.get(i);
                        update(con_name);
                    }
                }
                else{
                    if(res[i] > 5){
                        String con_name = cons.get(i);
                        update(con_name);
                    }
                }
            }
        }
    }

    public double read(String con_name){
        String filename = "ql3/" + con_name + "/" + con_name + "_response_time.txt";
        // System.out.println(filename);
        int numRead = 3;//read last 3 values
        int count = 0;
        double avg = 0;
        try{
            RandomAccessFile r = new RandomAccessFile(filename, "r");
            long length;
            try {
                length = r.length();
                if(length == 0L)
                    return 0;
                long pos = length - 1;
                while(pos > 0){
                    pos --;
                    r.seek(pos);
                    // System.out.println(r.readByte() == '\n');
                    if(r.readByte() == '\n'){// readline when \n shows up
                        String line = new String(r.readLine());
                        double tmp = Double.parseDouble(line);
                        // System.out.println(tmp);
                        avg += tmp;
                        count ++;
                        // System.out.println(numRead);
                        if(count == numRead)
                            break;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        avg /= numRead;
        return avg;
    }

    public int get_cons(String con_name){
        String filename = "ql3/" + con_name + "/" + con_name + "_con2.txt";
        int replicas = 0;
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    replicas = (int) Double.parseDouble(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return replicas;
    }

    public  void update(String con_name){
        int replicas = get_cons(con_name);
        if(replicas == 4)
            return;
        replicas ++;
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker service scale " + con_name + "=" + replicas;
        System.out.println(cmd);
        print_con2(con_name, replicas);
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }

    public void print_con2(String con_name,int replicas) {
        try {
            String filename = "ql3/" + con_name + "/" + con_name + "_con2.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(replicas + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
