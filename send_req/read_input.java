package paper.code.send_req;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class read_input {
    ArrayList<Double> freq = new ArrayList<Double>();
    int d = 10;
    public read_input(){

    }

    public ArrayList<Double> read_use() throws FileNotFoundException{
        String filename = "src/main/java/paper/code/send_req/profile.dat";
        FileReader fr = new FileReader(filename);
        BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    double tmp = Double.parseDouble(line);
                    tmp /= d;
                    freq.add(tmp);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return freq;
    }
}
