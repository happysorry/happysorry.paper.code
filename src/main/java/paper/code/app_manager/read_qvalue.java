package main.java.paper.code.app_manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class read_qvalue {
    public double read_last_qvalue(){
        String con_name = "app_mn1";
        String filename = "ql3/" + con_name + "/" + con_name + "_qvalue2.txt";
        // System.out.println(filename);
        int numRead = 1;//read last 1 values
        int count = 0;
        double avg = 0;
        try{
            RandomAccessFile r = new RandomAccessFile(filename, "r");
            long length;
            try {
                length = r.length();
                if(length == 0L)
                    return 0;
                long pos = length - 1;
                while(pos > 0){
                    pos --;
                    r.seek(pos);
                    // System.out.println(r.readByte() == '\n');
                    if(r.readByte() == '\n'){// readline when \n shows up
                        String line = new String(r.readLine());
                        double tmp = Double.parseDouble(line);
                        // System.out.println(tmp);
                        avg += tmp;
                        count ++;
                        // System.out.println(numRead);
                        if(count == numRead)
                            break;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        avg /= numRead;
        return avg;
    }
}
