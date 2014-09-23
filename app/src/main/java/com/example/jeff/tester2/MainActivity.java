package com.example.jeff.tester2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private Smoothener smoothener;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public Context context;
    private CheckBox logbox;
    private CheckBox algcheck;
    private CheckBox smoothCheck;
    private ArrayList<String> beaconList;
    private ArrayList<Integer> flatList;
    private ArrayList<String> detected;
    private EditText distanceInput;
    private EditText queuesize;
    private int distance = 0;
    private double baseline = 0;
    double magicnr = 1.0;
    private EditText majorInput;
    private EditText minorInput;
    private Button button1;
    private Button button2;
    private ListView listView1;
    private BluetoothManager btm;
    private BluetoothAdapter bta;
    private ArrayAdapter arrayAdapter2;
    private CSVwriter CSV;
    private long starttime;

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override

                public void onLeScan(final BluetoothDevice device, final int Irssi,
                                     final byte[] scanRecord) {
                    int rssi = Irssi;
                    int Arssi = 0;
                    int startByte = 2;
                    int major = 0;
                    int minor = 0;
                    int tx = 0;
                    boolean patternFound = false;


                    // scant de bytearray for de standaard IBeacon waardes
                    while (startByte <= 5) {
                        if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                                ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                            patternFound = true;
                            break;
                        }
                        startByte++;
                    }
                    // als Het IBEACON patroon gevonden is kan er worden gezocht naar de MInor en MAjor
                    if (patternFound) {
                        //Convert to hex String
                        byte[] uuidBytes = new byte[16];
                        System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                        String hexString = bytesToHex(uuidBytes);

                        //Here is your UUID
                        String uuid = hexString.substring(0, 8) + "-" +
                                hexString.substring(8, 12) + "-" +
                                hexString.substring(12, 16) + "-" +
                                hexString.substring(16, 20) + "-" +
                                hexString.substring(20, 32);

                        //Here is your Major value
                        major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
                        //Here is your Minor value
                        minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
                        tx = (scanRecord[startByte + 24]);

                        // make baseline
                        //makeBaseline(rssi);

                        // adjust Rssi
                        /*
                       if(smoothCheck.isChecked() && baseline != 0.0)
                       {
                           rssi = Flatline(rssi);
                       }
                       */
                        if(smoothCheck.isChecked())
                        {
                             Arssi = smoothener.smoothen(rssi);
                        }


                        if (patternFound || !algcheck.isChecked()) {
                            String s = "";
                            try {
                                s += "Name:" + device.getName() + " | ";
                                s += "Unique: " + device.toString() + " | ";
                                s += "Rssi: " + rssi + " | ";
                                s += "ARssi: " + Arssi + " | ";
                                s += "Major: " + major + " | ";
                                s += "Minor: " + minor + " | ";
                                s += "TX: " + tx + " | ";


                            } catch (Exception e) {
                                s += "error";
                            }

                            if (majorInput.getText().length() == 0) {
                                Listcheck(s, device.toString());
                                log(device.getName(), Integer.toString(major), Integer.toString(minor), Long.toString((System.currentTimeMillis() - starttime)),  Integer.toString(rssi),  Integer.toString(Arssi), Integer.toString(distance), Integer.toString(tx));

                            }

                            if (majorInput.getText().length() > 0 && minorInput.getText().length() == 0) {
                                if (Integer.parseInt(majorInput.getText().toString()) == major) {
                                    Listcheck(s, device.toString());
                                    log(device.getName(), Integer.toString(major), Integer.toString(minor), Long.toString((System.currentTimeMillis() - starttime)), Integer.toString(rssi),  Integer.toString(Arssi), Integer.toString(distance), Integer.toString(tx));

                                }
                            }

                            if (majorInput.getText().length() > 0 && minorInput.getText().length() > 0) {

                                if (Integer.parseInt(majorInput.getText().toString()) == major && Integer.parseInt(minorInput.getText().toString()) == minor) {
                                    Listcheck(s, device.toString());
                                    log(device.getName(), Integer.toString(major), Integer.toString(minor), Long.toString((System.currentTimeMillis() - starttime)), Integer.toString(rssi), Integer.toString(Arssi), Integer.toString(distance), Integer.toString(tx));

                                }

                            }
                        }
                    }
                }

            };


    public MainActivity() {
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        majorInput = (EditText) findViewById(R.id.editText);
        minorInput = (EditText) findViewById(R.id.editText2);
        distanceInput = (EditText) findViewById(R.id.editText3);
     //   queuesize = (EditText) findViewById(R.id.editText4);
        listView1 = (ListView) findViewById(R.id.listView);
        logbox = (CheckBox) findViewById(R.id.checkBox);
        algcheck = (CheckBox) findViewById(R.id.checkBox2);
        smoothCheck = (CheckBox)findViewById(R.id.checkBox3);
        beaconList = new ArrayList<String>();
        flatList = new ArrayList<Integer>();
        detected = new ArrayList<String>();
        smoothener = new Smoothener();

        btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bta = btm.getAdapter();

        arrayAdapter2 = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                beaconList);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CSV = new CSVwriter();
                if (bta == null || !bta.isEnabled()) {
                    bta.enable();
                }
                if (distanceInput.getText().length() > 0) {
                    distance = Integer.parseInt(distanceInput.getText().toString());
                }
                starttime = System.currentTimeMillis();
                beaconList.clear();
                detected.clear();
                //queuesize.setEnabled(false);
                majorInput.setEnabled(false);
                minorInput.setEnabled(false);
                logbox.setEnabled(false);
                distanceInput.setEnabled(false);
                listView1.setAdapter(arrayAdapter2);
                log("Name","major", "minor", "Tijd(ms)", "RSSI", "Adj. RSSI", "dist.(m)", "Tx");
                scanLeDevice();
                // this is a minor change
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopscanLeDevice();
                majorInput.setEnabled(true);
               // queuesize.setEnabled(true);
                minorInput.setEnabled(true);
                logbox.setEnabled(true);
                distanceInput.setEnabled(true);
                logStop();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanLeDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bta.enable();

                bta.startLeScan(mLeScanCallback);
            }
        }).start();
    }

    private void StopscanLeDevice() {

        bta.stopLeScan(mLeScanCallback);
        bta.disable();


    }

    private void log(final String input1, final String input2, final String input3, final String input4, final String input5, final String input6, final String input7, final String input8) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (logbox.isChecked()) {
                    ArrayList<String> loglist = new ArrayList<String>();
                    loglist.add(input1); // brand
                    loglist.add(input2); // major
                    loglist.add(input3); // minor
                    loglist.add(input4); // time
                    loglist.add(input5); // RSSI
                    loglist.add(input6);//Adjusted rssi
                    loglist.add(input7); // distance
                    loglist.add(input8); // TX power

                    try {
                        CSV.writeCsvRow(loglist);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void makeBaseline(int input) {
        flatList.add(input);

        if (flatList.size() == 3) {
            double avg = 0;
            double newbaseline = baseline;

            for (Integer i : flatList) {
                avg += i;
            }
            avg = Math.abs((avg / flatList.size()));
            double difference =  avg - baseline;
            // positive difference means the baseline should be adjusted upwards
            // negative difference means the baseline shoudl be adjusted downwards

            if (avg > baseline) {

                // als de nieuwe  avg weinig verschilt van de baseline
                // maken we een kleine aanpassing op de baseline
                // numbers are determined from meetings
                if ( (avg * 0.93)  < baseline) {
                    newbaseline = (baseline + (difference * (magicnr * 0.5)));

                }
                // als de nieuwe avg redelijk verschilt van de nieuwe baseline
                // maken we redelijke aanpassing op de baseline
                else if ((avg * 0.87) <= baseline) {
                    newbaseline = (baseline + (difference * (magicnr)));
                }
                // de nieuwe avg weikt enorm af van de nieuwe basline, de avg is nu de baseline
                else {
                    newbaseline = avg;
                }
            }

            if (avg < baseline) {

                // als de nieuwe  avg weinig verschilt van de baseline
                // maken we een kleine aanpassing op de baseline
                if ((avg * 1.07) > baseline) {
                    newbaseline = (baseline + (difference * (magicnr * 0.5)));

                }
                // als de nieuwe avg redelijk verschilt van de nieuwe baseline
                // maken we redelijke aanpassing op de baseline
                else if ((avg * 1.13) >= baseline) {
                    newbaseline = (baseline + (difference * (magicnr)));
                }
                // de nieuwe avg weikt enorm af van de nieuwe basline, de avg is nu de baseline
                else {
                    newbaseline = avg;
                }

            }
            flatList.clear();
            //System.out.println("baselined");
            baseline = newbaseline;
        }
    }


    public int Flatline(int Irssi) {
        double output = Math.abs(Irssi);

        // positive difference means the rssi is smaller than the baseline
        // negative difference means the rssi is bigger than the baseline
        double difference = baseline - output;

        // signal needs to be adjusted upwards
        if (difference > 0) {
            //double test = output * 0.1;
            // the difference is smaller than 7% of the rssi , minor adjustment
            if (difference < ((output * 0.1) * 7)) {

                output = output + (difference * (magicnr * 0.6));
            }

            // the difference is smaller than 13 % of the rssi , major adjustment
            else if (difference <= ((output * 0.1)*13)) {
                output = output + (difference * (magicnr));
            }
            // the difference is very big
            else {

            }

        }
        // signal needs to be adjusted downwards
        if (difference < 0) {
            // if difference is bigger than
            // the difference is smaller than 7% of the rssi , minor adjustment
            if (difference > ((output * 0.1) * -7)) {
                output = output + (difference * (magicnr * 0.6));
            }

            // the difference is smaller than 13 % of the rssi , major adjustment
            else if (difference >= ((output * 0.1) * -13)) {
                output = output + (difference * (magicnr));
            }
            // the difference is very big
            else {

            }

        }
           // System.out.println("smoothed");

        return(int)  (output * -1);
    }


    private void logStop() {
        try {
            CSV.saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void Listcheck(final String input, final String CompareString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (detected.contains(CompareString)) {
                    int index = detected.indexOf(CompareString);
                    beaconList.set(index, input);
                }

                if (!detected.contains(CompareString)) {
                    detected.add(CompareString);
                    beaconList.add(input);
                }

                arrayAdapter2.notifyDataSetChanged();
            }
        });

    }


}
