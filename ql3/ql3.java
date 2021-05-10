package paper.code.ql3;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.lang.model.util.ElementScanner6;

public class ql3 {

    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private int actions = 5; // number of actions

    private int container = 4; // max number of container
    private int u = 10; // we divide cpu utilization into 10 degrees
    private int c = 10; // we divide cpu shares into 10 degrees
    private int states = container * u * c; // total states
    private int tmax_vio = 0;
    State s = new State();

    private int Rmax = 50; // maximum response time

    private int cperf = 100; // performance penalty
    private int cres = 100; // resource cost

    public String con_name;

    private final int reward = 100;
    private final int penalty = -10;

    private char[][] maze; // Maze read from file
    private int[][] R; // Reward lookup
    private double[][] Q; // Q learning
    ArrayList<String> machine_id = new ArrayList<String>(); // docker machine id
    private String docker_cert_path = "C:\\Users\\USER\\.docker\\machine\\machines\\default";

    // need to initial reward matrix
    /***
     * set state as (numbers of container,cpu utilization,cpu shares) thus,there
     * will be 400 states q table design order: number of container -> cpu
     * utilization -> cpu shares [i][j][k] 9 actions A = {-1,0,1} x {-r,0,r}
     * add/remove containers x add/remove cpu shares 5 actions A = {0,-1,+1,-r,+r}
     * add/remove containers or add/remove cpu shares or do nothing here , we let r
     * be 1 . let cpu shares go up/down 1 degree.(1 degree means 102(max cpu shares
     * is 1024)) so , there will be 9/5 actions for every states q table will be 400
     * * 9 or 400 * 5 initial state will be 0 . it means 1 replicas , 0 cpu
     * utilization , 0 cpu shares
     * 
     * 
     * c(s,a,s') = W_{adp}(vertical scaling)+W_{perf}(response time>R_{max}) +
     * W_{res}((k+a1)(c+a2))/K_{max}
     * 
     * how to collect response time?
     * 
     * 
     */


    public ql3(String con_name) {
        this.con_name = con_name;
    }

    void init_state() {
        s.cons = 0;
        s.use = 0;
        s.cpus = 3;
    }

    /**
     * print out q table
     */
    void print() {
        FileWriter fw;
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_qtable.txt";
            fw = new FileWriter(filename);
            for (int i = 0; i < states; i++) {
                for (int j = 0; j < actions; j++) {
                    fw.write(Q[i][j] + " ");
                }
                fw.write("\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void print_state_action(String action) {
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_state_action.txt";
            FileWriter fw = new FileWriter(filename, true);
            // System.out.println(filename);
            fw.write(cla_state(s) + " " + action + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * start learning
     */
    void learn() {
        calculateQ();

    }

    /**
     * initialize q table which is 5 actions -1 means impossible states
     */
    void init_5() {
        Q = new double[states][actions];// Reward lookup
        for (int i = 0; i < states; i++) {
            for (int j = 0; j < actions; j++) {
                Q[i][j] = 0;
            }
        }

    }

    void read_qtable(){
        String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_qtable.txt";
        try {
            FileReader fr = new FileReader(filename);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }


    /**
     * get 1.replicas 2.cpu utilization 3.cpu shares form docker swarm and return
     * cpu usage level
     */
    void get_state1() {
        try{
            ArrayList<String> cpu = new ArrayList(); // store all replicas' container id
            int replicas = 0; // replicas of target container
            double use = 0.0;// calculate average cpu utilization
            int i = 0;
            /**
             * // * get cpu utilization,replicas
             */
            Runtime run = Runtime.getRuntime();
            Process pr;
            for (i = 0; i < machine_id.size(); i++) {
                String cmd = "sudo docker-machine ssh " + machine_id.get(i) + " docker stats --no-stream";
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
                        if (line.indexOf(con_name) < 0) {
                            continue;
                        }
                        // System.out.println(machine_id.get(i));
                        String[] sp = line.split("%");
                        String[] sp2 = sp[0].split(" ");
                        String ii = sp2[sp2.length - 1];
                        // System.out.println("ii" + ii);
                        cpu.add(ii);
                        replicas++;
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            double avg = 0.0;
            for (i = 0; i < cpu.size(); i++) {
                double tmp = Double.parseDouble(cpu.get(i));
                avg += tmp;
            }
            avg /= replicas;
            int cpus = s.cpus + 1;
            cpus *= 10;
            avg /= cpus;
            print_use(avg * 100);
            double lev = 0.1;
            for (i = 0; i < 9; i++) {
                if (avg < lev)
                    break;
                lev += 0.1;
            }
            s.cons = replicas-1;
            s.use = i;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }

    int get_state2() {
        FileReader fr;
        String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_stats_use.txt";
        double avg = 0.0;
        try {
            fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    avg = Double.parseDouble(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int cpus = s.cpus + 1;
        cpus *= 10;

        // System.out.println("cpus " + cpus);

        avg /= cpus;
        // if(avg > 1)
        //     avg = 1;
        print_use(avg * 100);
        // System.out.println("avg " + avg);
        double lev = 0.1;
        int i;
        for (i = 0; i < 9; i++) {
            if (avg < lev)
                break;
            lev += 0.1;
        }

        return i;
    }

    
    /***
     * s: State state: index of State in Q table actionFromCurrentState: possible
     * action from current state next: the next state{State} action: the action that
     * state take q: q value of the action ci: cost of this state nextstate: index
     * of next state in Q table actionsFromCurrentState2: next state possible
     * actions action2: next state maximum value action minQ: next state maximum
     * action value
     */
    void calculateQ() {
        try{
            // epison greedy parameter
        int epison = 1;
        int r = 0;
        r = (int) (Math.random() * 100);
        if (r < 10)
            epison = 0;
        get_state1();
        int state = cla_state(s);
        System.out.println("state " + state);
        State[] actionsFromCurrentState = possibleActionsFromState(s); // get all possible states. Return type <State>
        int nextState = 0;
        State next = new State(); // next state s'
        next = s;
        State nexenext = new State();
        int action = 0;
        // choose action normally
        if (epison == 1) {
            double max = -1e9;
            ArrayList<Double> ran = new ArrayList<Double>();
            ArrayList<Integer> ind = new ArrayList<Integer>();
            double[] sss = new double[actions];
            for (int i = 0; i < actions; i++) {
                sss[i] = 1e9;
            }
            /**
             * get next state
             */
            for (int i = 0; i < actionsFromCurrentState.length; i++) {
                if (actionsFromCurrentState[i] == null)
                    continue;
                State tmp = actionsFromCurrentState[i];

                double q = Q[state][i];
                ran.add(q);
                sss[i] = q;
                if (q > max) {
                    action = i;
                    next = tmp;
                    max = q;
                }

            }
            for (int i = 0; i < actions; i++) {
                if (sss[i] == max)
                    ind.add(i);
            }
            r = (int) (Math.random() * ind.size());
            action = ind.get(r);
            next = actionsFromCurrentState[action];

        } else {// choose action randomly
            r = (int) (Math.random() * 5);
            while (actionsFromCurrentState[r] == null) {
                r = (int) (Math.random() * 5);
            }
            State tmp = actionsFromCurrentState[r];
            action = r;
            next = tmp;
        }
        /**
         * calculate cost
         */
        double ci = cost(s, next);
        /**
         * update q table
         */
        double q = Q[state][action]; // state before update
        nextState = cla_state(next);
        // calculate minQ
        State[] actionsFromCurrentState2 = possibleActionsFromState(next); // get all possible states. Return type
        double max = -1e9;
        int action2 = 0;
        /**
         * get next state from next state
         */
        for (int i = 0; i < actionsFromCurrentState2.length; i++) {
            if (actionsFromCurrentState2[i] == null)
                continue;
            State tmp = actionsFromCurrentState2[i];
            double qq = Q[nextState][i];
            if (qq > max) {
                action2 = i;
                max = qq;
            }
        }
        double minQ = Q[nextState][action2]; // next state action
        double value = (1 - alpha) * q + alpha * (-ci + gamma * minQ); // calculate value
        Q[state][action] = value; // update q table
        print_qvalue(value);
        if (state == nextState) {
            print_state_action("same");
            Wait();
        } else
            container_update(s, next);
        print_con(s);
        print_share(s);
        // state update
        s = next;
        state = nextState;

        }
        catch(Exception e){
            e.printStackTrace();
            
        }
        
    }

    /**
     * return all possible actions
     */
    State[] possibleActionsFromState(State st) {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<State> res = new ArrayList<>();
        State[] res1 = new State[5];
        State tmp = new State();
        State tmp1 = new State();
        State tmp2 = new State();
        State tmp3 = new State();

        int ans = cla_state(st);
        res.add(st);
        result.add(ans); // do nothing
        res1[0] = st;

        tmp.cons = st.cons - 1;
        tmp.use = st.use;
        tmp.cpus = st.cpus; // -1
        if (tmp.cons < 0)
            ; // min number of container;
        else {
            res.add(tmp);
            res1[1] = tmp;
        }

        tmp1.cons = st.cons + 1;
        tmp1.use = st.use;
        tmp1.cpus = st.cpus;
        if (tmp1.cons == container)
            ; // min number of container;
        else {
            res1[2] = tmp1;
            res.add(tmp1); // +1

        }

        tmp2.cons = st.cons;
        tmp2.use = st.use;
        tmp2.cpus = st.cpus - 1;
        if (tmp2.cpus < 3)// <40%
            ;
        else {
            res.add(tmp2); // -r
            res1[3] = tmp2;
        }

        tmp3.cons = st.cons;
        tmp3.use = st.use;
        tmp3.cpus = st.cpus + 1;
        if (tmp3.cpus > 9)
            ;
        else {
            res.add(tmp3); // +r
            res1[4] = tmp3;
        }

        return res1;
        // return res.toArray(new State[res.size()]);
    }

    /**
     * calculate state index
     */
    int cla_state(State st) {
        if (st.cons < 0)
            st.cons = 0;
        if (st.cons == container)
            st.cons = container - 1;
        if(st.cons > 3)
            st.cons =3;
        if (st.cpus < 3)//<40%
            st.cpus = 3;
        if (st.cpus > 9)
            st.cpus = 9;
        if (st.use < 0)
            st.use = 0;
        if (st.use > 9)
            st.use = 9;

        int ans = st.cons * 100 + st.use * 10 + st.cpus;
        return ans;
    }

    


    /**
     * calculate cost
     */
    double cost(State old, State ne) {
        double sum = 0;
        // weight of cost
        double wres = 0.09;
        double wadp = 0.01;
        double wperf = 0.90;
        double resp_time = response_time();
        // cost
        double cadp = 10;
        double cperf_ = cperf;

        if (ne.cpus != old.cpus) // it make adaption with it
            cadp = 100;

        // if (ne.use < 8) // performance simulation
        //     cperf_ = 0;

        double tmax = 50;
        // if(con_name != "app_mn1")
        //     tmax = 10;
        if (resp_time < tmax)// if response time > Rmax then get performance cost
            cperf_ = 0;
        else
            tmax_vio++;

        if(resp_time>100)
            resp_time = 100;
        
        double tmp = resp_time / tmax;
        double pow = Math.pow(1.4,tmp);
        cperf_ *= pow;

        int cons = ne.cons + 1;
        double total_use = Double.valueOf(cons) * Double.valueOf(ne.cpus + 1) / 10;
        // total_use /= 4; //max container numbers
        total_use *= cres;
        // sum = wres * (cons * cres) + wadp * (cadp) + wperf * (cperf_);
        sum = wres * total_use + wadp * (cadp) + wperf * (cperf_);
        return sum;
    }

    /**
     * update containers by new state
     */
    void container_update(State old, State ne) {
        int cons = 0;
        int flag = 0;
        // update replicas
        if (ne.cons != old.cons) {
            flag = 1;
            if (ne.cons > old.cons) {
                cons = ne.cons + 1;
                System.out.println("+1");
                print_state_action("+1");
            } else {
                cons = ne.cons + 1;
                System.out.println("-1");
                print_state_action("-1");
            }
            // System.out.println(cons);
            String cmd = "sudo docker-machine ssh default docker service update " + con_name + " --replicas " + cons;
            Runtime run = Runtime.getRuntime();
            Process pr;
            try {
                pr = run.exec(cmd);
            } catch (IOException e) {
                System.out.println(e);
            }
            Wait();
        }
        /**
         * update cpu shares you need to get replicated containers' container id to
         * change their cpu shares
         */

        int tmp = 0;
        double tmp1 = 0.0;
        double d = new Double(ne.cpus + 1);
        if (ne.cpus > old.cpus) {
            System.out.println("+r");
            print_state_action("+r");
        } else if (ne.cpus < old.cpus) {
            System.out.println("-r");
            print_state_action("-r");
        }
        tmp1 = d / 10;// cpus
        // tmp1 /= cons; // where improve cpus
        tmp = (ne.cpus + 1) * 100;// cpu shares

        docker_api_update(tmp1);// update cpus
        if(flag==0)
            Wait();
        // Wait();
    }

    /**
     * sleep 10s until container stable
     */
    void Wait() {

        try {
            Thread.sleep(20000);
        } catch (Exception e) {

        }
    }

    public void send_request() {
        try {
            int val = (int) ((Math.random() * 899999) + 100000);
            String con = "";
            if ((val % 2) == 1)
                con = "false";
            else
                con = "true";
            String path = "";
            switch (con_name) {
            case "app_mn1":
                path = "http://192.168.99.114:666/~/mn-cse/mn-name/AE1/RFID_Container_for_stage0";
                break;
            case "app_mn2":
                path = "http://192.168.99.114:777/~/mn-cse/mn-name/AE2/Control_Command_Container";
                break;
            case "app_mnae1":
                path = "http://192.168.99.114:1111/test";
                break;
            case "app_mnae2":
                path = "http://192.168.99.114:2222/test";
            }

            URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setReadTimeout(2000);
            http.setDoOutput(true);
            // http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("X-M2M-Origin", "admin:admin");
            http.setRequestProperty("Content-Type", "application/json;ty=4");
            try {
                http.setRequestMethod("POST");
                http.connect();
                DataOutputStream out = new DataOutputStream(http.getOutputStream());

                String request = "{\"m2m:cin\": {\"con\": \" default \", \"cnf\": \"application/xml\",\"lbl\":\"req\",\"rn\":\""
                        + val + "\"}}";
                // '{"m2m:cin": {"con": "EXAMPLE_VALUE", "cnf": "text/plain:0"}}'
                out.write(request.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
                int satus = http.getResponseCode();// error place
                if(satus == 503){
                    stop();
                    recovery();
                }
                    
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                System.out.println(con_name + " connection timeout");
                stop();
                recovery();
                
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    public void stop(){
        try {
            String filename = "src/main/java/paper/code/signal.txt";;
            FileWriter fw1 = new FileWriter(filename);
            fw1.write(1 + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void recovery(){
        //docker service update --force --update-parallelism 1 app_mn1
        // String cmd = "sudo docker-machine ssh default docker service update --force --update-parallelism 1 " + con_name;
        String cmd = "sudo docker-machine ssh default docker service update --force  " + con_name;
        Runtime run = Runtime.getRuntime();
        Process pr;
        System.out.println(cmd);
        try {
            pr = run.exec(cmd);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        Wait();
    }

    /**
     * get response time(ms)
     */
    double response_time() {
        double elapsed = 0.0;
        double startTime = System.nanoTime();
        double timeout = System.nanoTime();
        send_request();
        elapsed = System.nanoTime() - startTime;
        elapsed /= 1e6;
        // System.out.println("elapsed " + elapsed);
        print_res_time(elapsed);
        return elapsed;
    }

    void print_res_time(double elapsed) {
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_response_time.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(elapsed + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * print cons and shares
     * 
     * @param st
     */
    void print_con(State st) {
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_con1.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(st.cons + 1 + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void print_share(State st) {
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_cpus.txt";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(st.cpus + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void print_use(double avg) {
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_use.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(avg + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void print_tmax_vio() {
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_tmax_vio.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(tmax_vio + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void print_qvalue(double qvalue){
        try {
            String filename = "src/main/java/paper/code/ql3/" + con_name + "/" + con_name + "_qvalue.txt";
            FileWriter fw1 = new FileWriter(filename, true);
            fw1.write(qvalue + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * get docker machine id
     */
    void get_machine_id() {
        String cmd = "sudo docker-machine ls";
        Runtime run = Runtime.getRuntime();
        Process pr;
        // System.out.println(cmd);
        try {
            pr = run.exec(cmd);
            BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            String result = "";

            while (true) {
                line = r.readLine();
                // System.out.println(line);
                if (line == null) {
                    break;
                }
                if (line.indexOf("tcp") < 0) {
                    continue;
                }
                String[] sp = line.split(" ");
                machine_id.add(sp[0]);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * use docker engine api to update container on other docker machines
     * 
     * @param con_name
     */
    void docker_api_update(double cpus) {

        for (int i = 0; i < machine_id.size(); i++) {
            String cmd = "sudo docker-machine ssh " + machine_id.get(i) + " docker ps";
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
                    if (line.indexOf(con_name) < 0) {
                        continue;
                    }
                    String[] sp = line.split(" ");
                    String name = sp[0];
                    String cmd1 = "sudo docker-machine ssh " + machine_id.get(i) + " docker update --cpus=" + cpus + " "
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

    void update_cpus(ArrayList<String> con_id) {

    }

}

/**
 * state formate
 */

class State {
int cons;
int use;
int cpus;
}
