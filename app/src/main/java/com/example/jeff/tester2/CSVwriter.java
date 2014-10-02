package com.example.jeff.tester2;


import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jeff on 16-9-2014.
 */
public class CSVwriter {
    boolean success = true;
    public boolean writing = false;
    FileWriter writer;
    File gpxfile;


    public CSVwriter() {

        try {
            writer = new FileWriter(getOutputfile());
        } catch (Exception e) {
            System.out.println("henk");
        }
    }

    private File getOutputfile() {
        File output = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + "/com.example.jeff.tester2");

        // create storage folder if it does not exist
        if (!output.exists()) {
            if (!output.mkdirs()) {
                return null;
            }
        }

        String filename = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        filename = "iBeaconTest" + filename;
        filename = filename + ".csv";
        File outfile;
        outfile = new File((output.getPath() + File.separator + filename));
        return outfile;
    }


    public void writeCsvRow(ArrayList<String> arrayList) throws IOException {
        writing = true;
        String test = "";
        for (String item : arrayList) {
            test += item;
            test += " ,";
        }
        test += "\n";

        writer.write(test);
    }

    public void saveFile() throws IOException {
        if (writing) {
            writer.flush();
            writer.close();
            writing = false;
        }
    }
}
