package paper.code.send_req;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import paper.code.ql3.start;

public class ClientThread implements Runnable {
    private String clientName;
    private channel channel;
    private double send_time = 1000;
    ArrayList<Double> freq = new ArrayList<Double>();
    private int inteval = 300000;
    private double startTime = 0.0;
    private double changeTime = 0.0;
    stop s = new stop();
    
    public ClientThread(String clientName, channel channel) {
        this.clientName = clientName;
        this.channel = channel;
        add_freq();
    }

    public String rand_RFID() {
        int val = (int) ((Math.random() * 899999) + 100000);
        String RFID = String.valueOf(val);
        return RFID;
    }

    /**
     * read lambda
     */
    public void add_freq() {
        read_input r = new read_input();
        try{
            freq = r.read_use();
        }
        catch(FileNotFoundException e){
            
        }
    }

    public void stop(){
        String filename = "src/main/java/paper/code/send_req/time.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            int line = 0;
            try {
                line = Integer.parseInt(r.readLine());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * calculate 1/lambda
     */
    public long cal_send_time() {
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
    void write() {
        try {
            String filename = "src/main/java/paper/code/signal.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(0 + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    void Wait(long time) {

        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        double elapsed = 0.0;
        startTime = System.nanoTime();
        changeTime = System.nanoTime();
        while (true) {
            String RFID = rand_RFID();
            long send_time = cal_send_time();
            Request request = new Request(RFID);
            this.channel.put(request);
            /**
             * using busy waiting to control
             */
            long end = System.nanoTime() + send_time;
            while (System.nanoTime() < end) {
            }
            if((System.nanoTime() - changeTime) > 1e10){
                System.out.println("10s");
                int out = s.read();
                if(out == 1){
                    System.out.println("req stop");
                    Wait(80000);
                    
                    // write();
                }
            }
            changeTime = System.nanoTime();
        }
    }
}