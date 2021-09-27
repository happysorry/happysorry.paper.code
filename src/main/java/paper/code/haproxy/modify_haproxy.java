package paper.code.haproxy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class modify_haproxy {
    int version = 1;
    public String con_name = "";
    ArrayList<String> server = new ArrayList<>();
    ArrayList<String> now_server = new ArrayList<>();
    public modify_haproxy(String con_name){
        this.con_name = con_name;
        add_server();
    }

    public void add_server(){
        server.add("default");
    }

    public void read_ver(){
        System.out.println("haproxy");
        String filename = "src/main/java/paper/code/warmup/ver.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            
            try {
                while ((line = r.readLine()) != null) {
                    version = Integer.parseInt(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            
        }
    }

    public void write_ver(){
        try {
            
            FileWriter fw = new FileWriter("src/main/java/paper/code/warmup/ver.txt");
            fw.write(version + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    

    public void get_cons(){
        read_ver();
        ArrayList<String> new_server = new ArrayList<>();
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker service ps " + con_name;
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
                if(line.indexOf(con_name)<0)
                    continue;
                String[] sp = line.split(" ");
                String tmp = sp[22];
                new_server.add(tmp);
                System.out.println("server name " + tmp);
            }
            check_server(new_server);
            } catch (IOException e) {
                System.out.println(e);
        }
        write_ver();  
    }


    public void check_server(ArrayList<String> new_server){
        System.out.println("new " + new_server.size() + " server " + server.size());
        int flag = 0;
        if(new_server.size()>server.size()){
            for(int i=0;i<new_server.size();i++){
                flag = 0;
                for(int j=0;j<server.size();j++){
                    if(new_server.get(i)==server.get(j)){
                        flag = 1;
                        break;
                    }    
                }
                if(flag == 0){
                    add_server(new_server.get(i));
                }     
            }
        }else if(new_server.size() < server.size()){
            for(int i=0;i<server.size();i++){
                flag = 0;
                for(int j=0;j<new_server.size();j++){
                    if(server.get(i)==new_server.get(j)){
                        flag = 1;
                        break;
                    }
                    if(flag == 0) 
                        del_server(server.get(i));
                }
            }
        }
        
        

        
        //update server
        server = new_server;
    }

    public void add_server(String server){
        System.out.println("add server " + server);
        String ip = get_ip(server);
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "curl -X POST --user user:1 -H \"Content-Type: application/json\"  -d '{\"address\": \"" + ip + "\",\"check\": \"enabled\",\"name\": \"" + server + "\",\"port\": 666}'  \"http://127.0.0.1:5555/v2/services/haproxy/configuration/servers?backend=back&version=" + version + "\"";
        System.out.println(cmd);
        version ++;
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
                System.out.println(line);
            }
        }catch(IOException e){

        }
    }

    public void del_server(String server){
        System.out.println("del server " + server);
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "curl -X DELETE --user user:1 -H \"Content-Type: application/json\"   \"http://localhost:5555/v2/services/haproxy/configuration/servers/" + server + "?backend=back&version=" + version + "\"";
        System.out.println(cmd);
        version ++;
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
                System.out.println(line);
            }
        }catch(IOException e){

        }
    }

    public String get_ip(String server){
        switch(server){
            case "default":
                return "192.168.99.116";
            case "worker":
                return "192.168.99.111";
            case "worker1":
                return "192.168.99.112";
            case "worker2":
                return "192.168.99.113";
        }
        return "0";
    }

}
