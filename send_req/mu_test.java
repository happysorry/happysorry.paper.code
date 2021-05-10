package paper.code.send_req;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;



public class mu_test implements Runnable {

    static long val = 0;
    static ArrayList<String> stage = new ArrayList<String>();// store every stages
    static int num = 0;
    static int cnt = 0;
    static ArrayList<Double> freq = new ArrayList<Double>();
    static stop s = new stop();
    static double sim_time = 0.0;
    static double startTime = 0.0;
    static double changeTime = 0.0;

    public mu_test(double sim_time){
        this.sim_time = sim_time;
    }
    public static void main(String[] args) {
        add_stage();
        send_req(sim_time);

    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        add_freq();
        add_stage();
        changeTime = System.nanoTime();  
        send_req(sim_time);
    }

    public void add_freq() {
        read_input r = new read_input();
        try{
            freq = r.read_use();
        }
        catch(FileNotFoundException e){
            
        }
    }
    /**
     * calculate 1/lambda
     */
    public static long cal_send_time(double lambda) {
        // double send_time = Math.log(1 - new Random().nextDouble()) / (-lambda); //exponential distribution
        double send_time = 1 / lambda;//constant interval
        send_time *= 1e9;// change to nanosecond
        long s = (long) send_time;
        // System.out.println(s);
        return s;
    }

    public static long cal_send_time() {
        double elapsed = System.nanoTime() - startTime;
        elapsed /= 1e9;
        int ind = (int) elapsed;
        double lambda = freq.get(ind);
        double send_time = Math.log(1 - new Random().nextDouble()) / (-lambda);
        send_time *= 1e9;// change to nanosecond
        long s = (long) send_time;
        // System.out.println(s);
        return s;

    }

    
    public static void add_stage() {
        stage.add("RFID_Container_for_stage0");
        stage.add("RFID_Container_for_stage1");
        stage.add("Liquid_Level_Container");
        stage.add("RFID_Container_for_stage2");
        stage.add("Color_Container");
        stage.add("RFID_Container_for_stage3");
        stage.add("Contrast_Data_Container");
        stage.add("RFID_Container_for_stage4");
      }
    

    public static void stage1(String RFID,int cnt) {
        try {
            val ++;
            num ++;
            String con = "";
            con = "false";
            if ((val % 2) == 1)
                con = "false";
            else
                con = "true";
            String path = "http://192.168.99.114:666/~/mn-cse/mn-name/AE1/" + stage.get(cnt);
            if(cnt == stage.size())
                cnt = 0;
            URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            // http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("X-M2M-Origin", "admin:admin");
            http.setRequestProperty("Content-Type", "application/json;ty=4");
            try {
                http.setRequestMethod("POST");
                http.connect();
                DataOutputStream out = new DataOutputStream(http.getOutputStream());
                String request = "{\"m2m:cin\": {\"con\": \"" + con
                + "\", \"cnf\": \"application/xml\",\"lbl\":\"req\",\"rn\":\"" + num + "\"}}";
                out.write(request.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
                int satus = http.getResponseCode();
                // System.out.println(satus);
                // print_response_code(satus);
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
    static void print_val() {
        try {
            String filename = "val.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(val + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    static void read_val(){
        String filename = "val.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
                try {
                    while ((line = r.readLine()) != null) {
                        val = Long.parseLong(line);
                    }
                    // System.out.println(val);
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String rand_RFID() {
        int val = (int) ((Math.random() * 899999) + 100000);
        String RFID = String.valueOf(val);
        return RFID;
    }


    static void send_req(double simtime) {
        double endtime = System.nanoTime();
        String RFID = rand_RFID();
        while (true) {
            long send_time = cal_send_time();
            double tmp = System.nanoTime();
            stage1(RFID,cnt);
            cnt ++;
            if(cnt == stage.size()){
                cnt = 0;
                RFID = rand_RFID();
            }
            long end = System.nanoTime() + send_time;
            if((System.nanoTime() - changeTime) > 1e10){
                System.out.println("10s");
                int out = s.read();
                if(out == 1){
                    System.out.println("req stop");
                    Wait(80000);
                }
                changeTime = System.nanoTime();
            }
            
            endtime = System.nanoTime();
            tmp = (endtime - startTime) / 1e9;
            if (tmp > simtime)
                break;
            while (System.nanoTime() < end) {
            }
        }
    }


    static void Wait(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
}

