package paper.code.app_manager;

import java.io.IOException;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
public class restart {
    public ArrayList<String> machine = new ArrayList<>();
    public ArrayList<String> id = new ArrayList<>();
    public restart(){
        add_machine();
        add_id();
    }

    public void res(){
        System.out.println("restart");
        rm();
        Wait(20000);
        deploy();
        Wait(60000);
        // update();
        // Wait(60000);
    }


    public void rm(){
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker stack rm app";
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }

    public void deploy(){
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker stack deploy --compose-file docker-compose.yml app";
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }

    public void add_machine(){
        machine.add("worker");
        machine.add("worker1");
        machine.add("worker2");
        machine.add("worker3");
    }

    public void add_id(){
        id.add("app_mn1");
        id.add("app_mn2");
        id.add("app_mnae1");
        id.add("app_mnae2");
    }
    public void update() {

        for (int i = 0; i < machine.size(); i++) {
            int flag = 0;
            String cmd = "sudo docker-machine ssh " + machine.get(i) + " docker ps";
            Runtime run = Runtime.getRuntime();
            // System.out.println(cmd);
            try {
                Process pr = run.exec(cmd);
                BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                String result = "";

                while (true) {
                    line = r.readLine();

                    if (line == null) {
                        break;
                    }
                    for(int j=0;j<id.size();j++)
                        if (line.indexOf(id.get(j)) >= 0) {
                            flag = 1;
                            break;
                        }
                    String[] sp = line.split(" ");
                    String name = sp[0];
                    String cmd1 = "sudo docker-machine ssh " + machine.get(i) + " docker update --cpus=0.5 "
                            + name;
                    try {
                        Process pr2 = run.exec(cmd1);
                    } catch (IOException e) {

                    }
                }
            } catch (IOException e) {

            }
        }

    }

    
    public void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
