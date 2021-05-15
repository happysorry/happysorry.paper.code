package paper.code.app_manager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import paper.code.ql3.*;
import paper.code.send_req.*;
public class Start {
    public static void main(String[]args){
        int iter = 400;
        double sim_time = 10; // 10000 second
        double check_time = 10;
        double st_time = 6e10;
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        // cachedThreadPool.execute(new mu_test(sim_time));
        for(int j = 0;j<3;j++){
            Thread s1 = new Thread(new mu_test(sim_time));
            s1.start();
            
            Thread t2 = new Thread(new start_ql3("app_mn1",sim_time));
            Thread t3 = new Thread(new start_ql3("app_mn2",sim_time));
            Thread t4 = new Thread(new start_ql3("app_mnae1",sim_time));
            Thread t5 = new Thread(new start_ql3("app_mnae2",sim_time));
            t2.start();
            t3.start();
            t4.start();
            t5.start();


            // cachedThreadPool.execute(new mu_test(sim_time));
            // cachedThreadPool.execute(new start_ql3("app_mn1",sim_time));
            // cachedThreadPool.execute(new start_ql3("app_mn2",sim_time));
            // cachedThreadPool.execute(new start_ql3("app_mnae1",sim_time));
            // cachedThreadPool.execute(new start_ql3("app_mnae2",sim_time));

            // Thread g6 = new Thread(new globe_restime());
            // g6.start();
            // globe_restime gl = new globe_restime();
            // gl.monitor();
            double startTime = System.nanoTime();
            double stop_time = System.nanoTime();
            while(true){
                double t = System.nanoTime() - startTime;
                double tt = System.nanoTime() - stop_time;
                t/=1e9;
                if(t > sim_time)//simulate time
                    break;
                tt/=1e9;
                // System.out.println("tt " +  tt);
                if(tt > check_time){
                    stop s = new stop();
                    int num = s.read();
                    System.out.println("num " + num);
                    if(num == 1){
                        System.out.println("thread stop");
                        try {
                            Thread.sleep(80000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block

                            e.printStackTrace();
                        }
                        s.write();
                    }
                    stop_time = System.nanoTime();
                }
            }
            System.out.println("exit");
        }
    }


    public static void send_req(){
        Test t = new Test();
    }

    public static void ql3(){
        start s = new start();
    }
}
