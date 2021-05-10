package paper.code.send_req;
public class Test {


    public Test(){
        channel channel = new channel(1);
        channel.startWorkerThread();

        Thread clientThread0 = new Thread(new ClientThread("client2", channel));
        clientThread0.start();
        // Thread clientThread1 = new Thread(new stop());
        // clientThread1.start();
    }

    public static void main(String args[]) {

        channel channel = new channel(1);
        channel.startWorkerThread();

        Thread clientThread0 = new Thread(new ClientThread("client2", channel));
        clientThread0.start();
        Thread clientThread1 = new Thread(new stop());
        clientThread1.start();
    }
}
