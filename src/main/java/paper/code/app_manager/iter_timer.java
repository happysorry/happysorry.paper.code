package main.java.paper.code.app_manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class iter_timer implements Runnable {
    public int max_iter = 0;
    public iter_timer(int max_iter){
        this.max_iter = max_iter;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        it();
    }

    public void it(){
        int iter = 0;
        int flag = 0;
        while(iter <= max_iter){
            flag = read();
            if(flag != 0){
                iter --;
                write_iter(iter);
                Wait(140000);
            }
            else{
                write_iter(iter);
            }
            iter ++;
            Wait(30000);
        }
    }
    public void write_iter(int iter) {
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

    public int read(){
        String filename = "signal.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            int line = 0;
            try {
                line = Integer.parseInt(r.readLine());
            if(line==1){
                return 1;
            }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    public static void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
