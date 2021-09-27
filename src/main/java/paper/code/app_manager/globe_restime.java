package paper.code.app_manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class globe_restime implements Runnable{
    ArrayList<String> file = new ArrayList<>();
    double res[] = new double[4];
    double limit_time = 200;//limit time 200ms 
    
    public globe_restime(){
        add_file();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        monitor();
    }
    
    public void add_file(){
        file.add("src/main/java/paper/code/ql3/app_mn1/app_mn1_response_time2.txt");
        file.add("src/main/java/paper/code/ql3/app_mn2/app_mn2_response_time2.txt");
        file.add("src/main/java/paper/code/ql3/app_mnae1/app_mnae1_response_time2.txt");
        file.add("src/main/java/paper/code/ql3/app_mnae2/app_mnae2_response_time2.txt");
    }
    
    public void monitor(){
        while(true){
            System.out.println("total");
            stop s = new stop();
            int num = s.read();
            if(num == 1){
                try {
                    System.out.println("globe sleep");
                    Thread.sleep(80000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            read();
            double res_time = cal();
            update(res_time);
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void update(double res_time){
        if(res_time < limit_time){
            return;
        }
        System.out.println("globe update");
        int neck = 0;
        double tmp = 0;
        // find out bottleneck
        for(int i = 0 ;i<4;i++){
            if(res[i] > tmp){
                tmp = res[i];
                neck = i;
            }
        }
        String con_name = "";
        switch(neck){
            case 0:
                con_name = "app_mn1";
                break;
            case 1:
                con_name = "app_mn2";
                break;
            case 2:
                con_name = "app_mnae1";
                break;
            case 3:
                con_name = "app_mnae2";
                break;
        };
        int cons = read_cons(con_name);
        cons ++;
        String cmd = "sudo docker-machine ssh default docker service update " + con_name + " --replicas " + cons;
        Runtime run = Runtime.getRuntime();
        Process pr;
        try {
            pr = run.exec(cmd);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public int read_cons(String con_name){
        String filename ="src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_con2.txt";
        FileReader fr;
        int cons = 0;
        try {
            fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    ;
                }
            cons = Integer.parseInt(line);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            // e1.printStackTrace();
        }
        return cons;
    }

    public void read(){
        for(int i = 0;i<file.size();i++){
            String filename = file.get(i);
            FileReader fr;
            try {
                fr = new FileReader(filename);
                BufferedReader r = new BufferedReader(fr);
                String line = "";
                try {
                    while ((line = r.readLine()) != null) {
                            res[i] = Double.parseDouble(line);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }
                
        }
    }

    public void write(double total) {
        try {
            String filename = "src/main/java/paper/code/app_manager/total_res_time.txt";
            FileWriter fw1 = new FileWriter(filename,true);
            fw1.write(total + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * calculate total response time
     */
    public double cal(){
        double total = 0.0;
        for(int i = 0;i<4;i++){
            if(res[i] > 1000)
                res[i]=1000;
            total += res[i];
        }
        write(total);
        return total;
    }

    

}
