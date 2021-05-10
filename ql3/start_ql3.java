package paper.code.ql3;

import java.io.FileWriter;
import java.io.IOException;

public class start_ql3 implements Runnable{
    String con_name;
    int iter = 0;
    public start_ql3(String con_name,int iter){
        this.con_name = con_name;
        this.iter = iter;
    }

    public void run(){
        ql3 ql = new ql3(con_name);
        stop s = new stop();
        ql.get_machine_id();
        ql.init_5();
        ql.init_state();
        for(int i = 0;i < iter ; i++){
            int out = s.read();
            if(out==1){
                Wait(80000);
                // write();
            }
            System.out.println("iteration " + i);
            ql.print_state_action("iteration " + i);
            try {
                ql.learn();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ql.print();
        ql.print_tmax_vio();
    }
    void Wait(long time) {

        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
    void write() {
        try {
            String filename = "src/main/java/paper/code/signal.txt";
            FileWriter fw1 = new FileWriter(filename);
            fw1.write("ok" + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
