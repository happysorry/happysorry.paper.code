package paper.code.app_manager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import paper.code.ql3.*;
import paper.code.send_req.*;
public class Start {
    public static void main(String[]args){
        channel channel = new channel(1);
        // ExecutorService ex = Executors.newFixedThreadPool(10);
        channel.startWorkerThread();
        int iter = 200;
        double sim_time = 1e13; // 10000 second
        double check_time = 1e10;
        double st_time = 6e10;

        Thread s1 = new Thread(new mu_test(sim_time));
        s1.start();
        
        Thread t2 = new Thread(new start_ql3("app_mn1",iter));
        Thread t3 = new Thread(new start_ql3("app_mn2",iter));
        Thread t4 = new Thread(new start_ql3("app_mnae1",iter));
        Thread t5 = new Thread(new start_ql3("app_mnae2",iter));
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        Thread g6 = new Thread(new globe_restime());
        g6.start();
        // globe_restime gl = new globe_restime();
        // gl.monitor();
        double startTime = System.nanoTime();
        double stop_time = System.nanoTime();
        while(true){
            double t = System.nanoTime();
            if(t-startTime > sim_time)//simulate time
                break;
            
            if((t - stop_time) > check_time){
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
        s1.interrupt();
        t2.interrupt();
        t3.interrupt();
        t4.interrupt();
        t5.interrupt();
    }


    public static void send_req(){
        Test t = new Test();
    }

    public static void ql3(){
        start s = new start();
    }
}
