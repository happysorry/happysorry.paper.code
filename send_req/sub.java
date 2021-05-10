package paper.code.send_req;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * cause old subscription will disappear no reasonally.
 * So , we make this file to create new subscription.
 */
public class sub {
    public static void main(String args[]){
        String path = "http://192.168.99.110:666/~/mn-cse/mn-name/AE1/RFID_Container_for_stage0";
        del_sub(path);
    }


    public static void del_sub(String path){
        URL url;
        path = path + "/sub";
        try{
            url = new URL(path);
            try{
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setDoOutput(true);
                // http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("X-M2M-Origin", "admin:admin");
                http.setRequestProperty("Content-Type", "application/xml;ty=23");
                http.setRequestMethod("DELETE");
                http.connect();
                int status = http.getResponseCode();
                System.out.println("del " + status);
            }catch(IOException e){

            }
        }catch(Exception e){

        }
    }

    public static void sub_ae(String path){
        // String path = "http://192.168.99.110:666/~/mn-cse/mn-name/AE1/RFID_Container_for_stage0";
        URL url;
        // path = path + "/sub";
            try {
                url = new URL(path);
                HttpURLConnection http;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setDoOutput(true);
                // http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("X-M2M-Origin", "admin:admin");
                http.setRequestProperty("Content-Type", "application/xml;ty=23");
                http.setRequestMethod("POST");
                http.connect();
                DataOutputStream out = new DataOutputStream(http.getOutputStream());
                // String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><m2m:sgn xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"><nev><rep rn=\"" + val +"\"><con>hello world</con></rep><rss>1</rss></nev><sud>false</sud><sur>/in-cse/in-name/MY_SENSOR/DATA/SUB_MY_SENSOR</sur></m2m:sgn>";
                String request = "<m2m:sub xmlns:m2m=\"http://www.onem2m.org/xml/protocols\" rn=\"sub\"><nu>http://192.168.99.110:1111/test</nu><nct>2</nct></m2m:sub>";
                // '{"m2m:cin": {"con": "EXAMPLE_VALUE", "cnf": "text/plain:0"}}'
                out.write(request.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
                int satus = http.getResponseCode();
                System.out.println("sub " + satus);
                // System.out.println(val);
                // System.out.println(status);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
    }

}
