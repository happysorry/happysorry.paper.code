package paper.code.send_req;
import java.util.ArrayList;

public class sub_ctrl implements Runnable{
    private channel channel;
    private String p = "http://192.168.99.110:666/~/mn-cse/mn-name/AE1/";
    public int ind = 0;
    ArrayList<String> stage = new ArrayList<String>();
    public int service_time = 0;
    public String sub_def = p + "Defective_Product_Container";
  
    public sub_ctrl(){
        add_stage();
    }
  
    public void add_stage() {
      stage.add("RFID_Container_for_stage0");
    //   stage.add("RFID_Container_for_stage1");
      stage.add("Liquid_Level_Container");
      stage.add("RFID_Container_for_stage2");
      stage.add("Color_Container");
      stage.add("RFID_Container_for_stage3");
      stage.add("Contrast_Data_Container");
      stage.add("RFID_Container_for_stage4");
      stage.add("Defective_Product_Container");
    }
  
    @Override
    public void run() {
        int ind = 0;
        sub s = new sub();
        sub_def ss = new sub_def();
        while(true){
            while (true) {
                String path = p + stage.get(ind);
                if(ind == stage.size()-1){//sub_def
                    ss.del_sub(path);
                    ss.sub_ae(path);
                }
                else{//sub
                    s.del_sub(path);
                    System.out.println(ind);
                    System.out.println(path);
                    s.sub_ae(path);
                }

                ind ++;
                if(ind == stage.size()){
                    ind = 0;
                    break;
                }    
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
            
    }
    
}
