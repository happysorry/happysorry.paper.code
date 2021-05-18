package paper.code.warmup;

public class warmup implements Runnable{
    public String con_name = "";
    public double sim_time = 0;

    public warmup(String con_name,double sim_time){
        this.con_name = con_name;
        this.sim_time = sim_time;
    }
    

    @Override
    public void run() {
        // TODO Auto-generated method stub
        double startTime = System.nanoTime();
        get_state g = new get_state(con_name);
        while(true){
            double endTime = (System.nanoTime() - startTime) / 1e9;
            if(endTime > sim_time)
                break;
            //get cpu utilization every second  
            g.get_state1();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
    
}
