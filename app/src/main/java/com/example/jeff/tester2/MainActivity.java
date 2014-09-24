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
    private DistanceCalc distanceCalc;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public Context context;
    private CheckBox logbox;
    private CheckBox algcheck;
    private CheckBox smoothCheck;
    private ArrayList<String> beaconList;
    private ArrayList<String> detected;
    private EditText distanceInput;
    private int distance = 0;
    private EditText majorInput;
    private EditText minorInput;
    private EditText queueSizeInput;
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
                    int meter = 0;
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

                        if(smoothCheck.isChecked())
                        {
                             Arssi = smoothener.smoothen(rssi,major,minor);
                             //meter = distanceCalc.distance(Arssi,tx);
                        }


                        if (patternFound || !algcheck.isChecked()) {
                            String s = "";
                            try {
                                s += "Name:" + device.getName() + " | ";
                                s += "Unique: " + device.toString() + " | ";
                                s += "Rssi: " + rssi + " | ";
                                s += "ARssi: " + Arssi + " | ";
                                //s += "Distance" + meter + " meter |";
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
        setContentView(R.layout.activity_main_godlike);
        context = getApplicationContext();
        button1 = (Button) findViewById(R.id.button_Start);
        button2 = (Button) findViewById(R.id.button_Stop);
        majorInput = (EditText) findViewById(R.id.input_Major);
        minorInput = (EditText) findViewById(R.id.input_Minor);
        distanceInput = (EditText) findViewById(R.id.input_Meter);
        queueSizeInput = (EditText) findViewById(R.id.Quesize);
        listView1 = (ListView) findViewById(R.id.list_Data);
        logbox = (CheckBox) findViewById(R.id.check_Log);
        algcheck = (CheckBox) findViewById(R.id.check_Algorithm);
        smoothCheck = (CheckBox)findViewById(R.id.check_Smoothing);
        beaconList = new ArrayList<String>();
        detected = new ArrayList<String>();
        //distanceCalc =  new DistanceCalc(15);

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
                smoothener = new Smoothener(Integer.parseInt(queueSizeInput.getText().toString()));
                log("Name","major", "minor", "Tijd(ms)", "RSSI", "Adj. RSSI", " input dist.(m)", "Tx");
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
                queueSizeInput.setEnabled(false);
                logbox.setEnabled(false);
                distanceInput.setEnabled(false);
                algcheck.setEnabled(false);
                smoothCheck.setEnabled(false);
                listView1.setAdapter(arrayAdapter2);

                scanLeDevice();
                // this is a minor change
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopscanLeDevice();
                smoothener.clearqueue();
                //distanceCalc.emptyQueue();
                majorInput.setEnabled(true);
               // queuesize.setEnabled(true);
                minorInput.setEnabled(true);
                queueSizeInput.setEnabled(true);
                logbox.setEnabled(true);
                distanceInput.setEnabled(true);
                algcheck.setEnabled(true);
                smoothCheck.setEnabled(true);
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
        //bta.disable();


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
                    loglist.add(input6);//Adjusted RSSI
                    loglist.add(input7); // distance input by user
                    loglist.add(input8); // TX power
                    //loglist.add(input9 + " meter"); // Distance output by program

                    try {
                        CSV.writeCsvRow(loglist);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
