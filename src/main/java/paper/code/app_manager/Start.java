package paper.code.app_manager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.java.paper.code.app_manager.globe;
import main.java.paper.code.app_manager.health_check;
import main.java.paper.code.app_manager.iter_timer;
import main.java.paper.code.app_manager.read_qvalue;
import paper.code.ql3.*;
import paper.code.send_req.*;

public class Start {
    public static void main(String[]args){
        int iter = 1;
        double sim_time = 3600; // 10000 second
        double check_time = 10;
        double st_time = 6e10;
        int max_iter = 121;
        String input_file = "send_req/input/exp_60.dat";
        ExecutorService es = Executors.newCachedThreadPool();
        restart r = new restart();
        stop s = new stop();
        double last_qvalue = 0;
        int it = 0;
        // cachedThreadPool.execute(new mu_test(sim_time));
        for(int j = 0;j<iter;j++){
            r.res();
            Wait(30000);
            write_iter(0);
            es.execute(new iter_timer(max_iter));
            es.execute(new health_check(max_iter));
            es.execute(new get_all_use(max_iter));
            es.execute(new new_send_request(input_file,max_iter));
            // es.execute(new check_con(sim_time));
            // es.execute(new mu_test(sim_time));
            Wait(10000);
            
            es.execute(new start_ql3("app_mn1",max_iter));
            es.execute(new start_ql3("app_mn2",max_iter));
            es.execute(new start_ql3("app_mnae1",max_iter));
            es.execute(new start_ql3("app_mnae2",max_iter));
            es.execute(new globe(max_iter));
            // es.execute(new warmup("app_mn1", sim_time));
            // es.execute(new warmup("app_mn2", sim_time));
            // es.execute(new warmup("app_mnae1", sim_time));
            // es.execute(new warmup("app_mnae2", sim_time));
            while(true){
                it = read_iter();
                if(it < max_iter)
                    Wait(30000);
                else
                    break;
            }
            
            read_qvalue rr = new read_qvalue();
            double qvalue = rr.read_last_qvalue();
            if(last_qvalue !=0){
                if(last_qvalue - qvalue<0.0001)
                    break;
            }
            last_qvalue = qvalue;


            System.out.println("exit");
            
            
        }
        System.out.println("process end");
    }
    public static void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void write_iter(int iter) {
        try {
            String filename = "iter.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(iter + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static int read_iter(){
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

}
