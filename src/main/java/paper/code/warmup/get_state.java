package paper.code.warmup;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class get_state {
    ArrayList<String> machine_id = new ArrayList<>();
    public String con_name = "";
    public double[]arr = new double[5];
    public int ind = 0;

    public get_state(String con_name){
        this.con_name = con_name;
        get_machine_id();
        add_arr();
    }

    void get_machine_id() {
        String cmd = "sudo docker-machine ls";
        Runtime run = Runtime.getRuntime();
        Process pr;
        // System.out.println(cmd);
        try {
            pr = run.exec(cmd);
            BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            String result = "";

            while (true) {
                line = r.readLine();
                // System.out.println(line);
                if (line == null) {
                    break;
                }
                if (line.indexOf("tcp") < 0) {
                    continue;
                }
                String[] sp = line.split(" ");
                machine_id.add(sp[0]);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public void get_state1() {
        try{
            ArrayList<String> cpu = new ArrayList(); // store all replicas' container id
            int replicas = 0; // replicas of target container
            double use = 0.0;// calculate average cpu utilization
            int i = 0;
            /**
             * // * get cpu utilization,replicas
             */
            Runtime run = Runtime.getRuntime();
            Process pr;
            for (i = 0; i < machine_id.size(); i++) {
                String cmd = "sudo docker-machine ssh " + machine_id.get(i) + " docker stats --no-stream";
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
                        if (line.indexOf(con_name) < 0) {
                            continue;
                        }
                        // System.out.println(machine_id.get(i));
                        String[] sp = line.split("%");
                        String[] sp2 = sp[0].split(" ");
                        String ii = sp2[sp2.length - 1];
                        // System.out.println("ii" + ii);
                        cpu.add(ii);
                        replicas++;
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            double avg = 0.0;
            for (i = 0; i < cpu.size(); i++) {
                double tmp = Double.parseDouble(cpu.get(i));
                avg += tmp;
            }
            avg /= replicas;
            arr[ind] = avg;
            ind ++;
            if(ind == arr.length)
                ind = 0;
            avg_cpu();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    public void add_arr(){
        for(int i=0;i<5;i++)
            arr[i]=-1;
    }

    public void avg_cpu(){
        double avg = 0;
        int cnt = 0;
        for(int i=0;i<5;i++){
            if(arr[i]<0)
                continue;
            cnt ++;
            avg += arr[i];
        }
        avg /= cnt;
        write(avg);
    }


    public void write(double avg){
        try {
            String filename = con_name + "warmup.txt";
            FileWriter fw = new FileWriter("src/main/java/paper/code/warmup/" + filename);
            fw.write(avg + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
