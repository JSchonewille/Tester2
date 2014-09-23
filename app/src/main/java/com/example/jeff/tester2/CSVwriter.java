package com.example.jeff.tester2;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import android.os.Environment;
import android.text.format.DateFormat;

/**
 * Created by Jeff on 16-9-2014.
 */
public class CSVwriter {


    File folder = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.example.jeff.tester2");
    boolean success = true;
    public boolean writing =  false;
    FileWriter writer;
    File gpxfile;


    public CSVwriter(){
        try {

            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                String CSVfilename = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                CSVfilename = CSVfilename.replace(" ","-");
                CSVfilename = "iBeaconTest" + CSVfilename;
                String name = CSVfilename + ".csv";
                gpxfile = new File(folder, name);
                writer = new FileWriter(gpxfile);

            } else {
                System.out.println("Error making writer");
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void writeCsvRow(ArrayList<String> arrayList) throws IOException {
        writing = true;
        String test = "";
        for (String item : arrayList) {
            test += item;
            test +=" ,";
        }
        test +="\n";

        writer.write(test);
    }

    public void saveFile() throws IOException {
        if (writing)
        {
            writer.flush();
            writer.close();
            writing = false;
        }
    }

}
