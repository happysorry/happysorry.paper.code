package paper.code.app_manager;
import paper.code.ql3.*;
import paper.code.send_req.*;
public class Start {
    public static void main(String[]args){
        System.out.println("Start");
        send_req();
        ql3();
    }


    public static void send_req(){
        Test t = new Test();
    }

    public static void ql3(){
        start s = new start();
    }
}
