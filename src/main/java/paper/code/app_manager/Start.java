package paper.code.app_manager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import paper.code.ql3.*;
import paper.code.send_req.*;
import paper.code.warmup.warmup;
public class Start {
    public static void main(String[]args){
        int iter = 5;
        double sim_time = 150; // 10000 second
        double check_time = 10;
        double st_time = 6e10;
        ExecutorService es = Executors.newCachedThreadPool();
        restart r = new restart();
        // cachedThreadPool.execute(new mu_test(sim_time));
        for(int j = 0;j<iter;j++){
            r.res();
            es.execute(new get_server(sim_time));
            es.execute(new mu_test(sim_time));
            es.execute(new start_ql3("app_mn1",sim_time));
            es.execute(new start_ql3("app_mn2",sim_time));
            es.execute(new start_ql3("app_mnae1",sim_time));
            es.execute(new start_ql3("app_mnae2",sim_time));
            // es.execute(new warmup("app_mn1", sim_time));
            // es.execute(new warmup("app_mn2", sim_time));
            // es.execute(new warmup("app_mnae1", sim_time));
            // es.execute(new warmup("app_mnae2", sim_time));
            
            try {
                es.awaitTermination(60, TimeUnit.SECONDS);//wait until thread terminate
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                Thread.sleep((long) sim_time * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("exit");
            
        }
        System.out.println("process end");
    }


}
