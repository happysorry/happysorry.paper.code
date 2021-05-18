package paper.code.app_manager;

import java.io.IOException;

public class restart {
    public restart(){
        
    }

    public void res(){
        System.out.println("restart");
        rm();
        Wait(20000);
        deploy();
        Wait(60000);
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

    public void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
