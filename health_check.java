package main.java.code;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class health_check implements Runnable{
    public static double sim_time = 0;
    public static int flag = 0;
    static ArrayList<String> machine = new ArrayList<>();
    // public int check_time = 30;
    public health_check(double sim_time){
        this.sim_time = sim_time;
    }

    public static void main(String[]args){
        sim_time = 90;
        // check();
        // add_machine();
        // get_use1();
        
        restart();
        Wait(10000);
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        check();
    }

    public static void add_machine(){
        machine.add("worker");
        machine.add("worker1");
        machine.add("worker2");
        machine.add("worker3");
    }
    public static void check(){
        double t = System.nanoTime();
        
        add_machine();
        while(true){
            if((System.nanoTime() - t) / 1e9 > sim_time)
                break;
            int code = send();
            //send timeout
            
            if(flag != 0){
                restart();
                flag = 0;
                // write(1);
                Wait(180000);
                // write(0);
            }

            Wait(10000);
            
             
        }
    }

    public static void get_use1() {
        int replicas = 0; // replicas of target container
        double use = 0.0;// calculate average cpu utilization
        int i = 0;
        /**
         * // * get cpu utilization,replicas
         */
        Runtime run = Runtime.getRuntime();
        Process pr;
        for (i = 0; i < machine.size(); i++) {
            String cmd = "sudo docker-machine ssh " + machine.get(i) + " docker ps";
            // System.out.println(cmd);
            try {
                pr = run.exec(cmd);
                BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                String result = "";

                while (true) {
                    line = r.readLine();

                    if (line == null) {
                        break;
                    }
                    if (line.indexOf("app_mn1") < 0) {
                        continue;
                    }
                    String[] sp = line.split(" ");
                    String name = sp[0];
                    // System.out.println(name);
                    String cmd1 = "sudo docker-machine ssh " + machine.get(i) + " docker stop " + name;
                    delete(cmd1);
                }
            }catch(IOException e){

            }
        }
             
    }

    public static void delete(String cmd){
        Runtime run = Runtime.getRuntime();
        // System.out.println(cmd);
        Process pr;
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }


    
    public static int send() {
        int status = 0;
        try{
            try {
                int val = (int) ((Math.random() * 899999) + 1);
                String con = "";
                con = "false";
                if ((val % 2) == 1)
                    con = "false";
                else
                    con = "true";
                String path = "http://192.168.99.125:666/~/mn-cse/mn-name/AE1/RFID_Container_for_stage0";
                // String path = "http://192.168.99.123:666/~/mn-cse/mn-name/AE1/" + stage;
                URL url = new URL(path);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setDoOutput(true);
                // http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("X-M2M-Origin", "admin:admin");
                http.setRequestProperty("Content-Type", "application/json;ty=4");
                try {
                    try{
                    http.setRequestMethod("POST");
                    http.setConnectTimeout(1000);
                    http.setReadTimeout(1000);
                    http.connect();
                    DataOutputStream out = new DataOutputStream(http.getOutputStream());
                    String request = "{\"m2m:cin\": {\"con\": \"" + con
                    + "\", \"cnf\": \"application/xml\",\"lbl\":\"req\",\"rn\":\"" + val + "\"}}";
                    out.write(request.toString().getBytes("UTF-8"));
                    out.flush();
                    out.close();
                    status = http.getResponseCode();
                    // System.out.println(status);
                    }catch(SocketTimeoutException e){
                        System.out.println("send timeout");
                        flag ++;
                        // timeout t = new timeout();
                        // t.restart();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
            }
        }
        catch(IndexOutOfBoundsException e){
        }
        return status;
    }



    public static void restart(){
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker service update --force app_mn1";
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }

    public static void write(int val) {
        try {
            String filename = "signal.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(val + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void Wait(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
   
}