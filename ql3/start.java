package paper.code.ql3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class start {
    static ArrayList<String> id = new ArrayList(); // store all replicas' container id
    static ArrayList<String> machine_id = new ArrayList<String>(); // docker machine id
    static int iter = 900;

    
    public static void service_discover() {
        int i = 0;
        /**
         * // * get cpu utilization,replicas
         */
        String cmd = "sudo docker-machine ssh default docker service ls";
        Runtime run = Runtime.getRuntime();
        Process pr;
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

                if (line.indexOf("happysorry") < 0) {
                    continue;
                }
                String[] sp = line.split(" ");
                String ii = sp[8];
                id.add(ii);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        service_discover();
        ExecutorService ex = Executors.newFixedThreadPool(10);
        for (int i = 0; i < id.size(); i++) {
            String con_name = id.get(i);
            // mkdir(con_name);
            try {
                Runnable tmp = new start_ql3(con_name,iter);
                ex.execute(tmp);
            } catch (Throwable e) {
                System.out.println(e);
            }
        }
    }

    public start(){
        service_discover();
        ExecutorService ex = Executors.newFixedThreadPool(10);
        for (int i = 0; i < id.size(); i++) {
            String con_name = id.get(i);
            // mkdir(con_name);
            try {
                Runnable tmp = new start_ql3(con_name,iter);
                ex.execute(tmp);
            } catch (Throwable e) {
                System.out.println(e);
            }
        }
    }

}
