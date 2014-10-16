package com.example.jeff.tester2;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class LocationActivity extends Activity {

    private Smoothener smoothener;
    private DistanceCalc distanceCalc;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public Context context;

    private ArrayList<String> beaconList;
    private ArrayList<String> detected;

    private int distance = 0;

    private EditText queueSizeInput;

    private ListView listView1;
    private BluetoothManager btm;
    private BluetoothAdapter bta;
    private ArrayAdapter arrayAdapter2;
    private CSVwriter CSV;
    private long starttime;
    private boolean message1 = false;
    private boolean message2 = false;
    private boolean message0 = false;
    NotificationMaker notificationMaker;
    int distance1;
    int distance2;
    int distance3;


    DrawView drawView;
    //Trilateration trilateration;

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


                            Arssi = smoothener.smoothen(rssi,major,minor);
                            //meter = distanceCalc.distance(Arssi,tx);


                        if (patternFound) {
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

                            for (DrawView.Beacon b : drawView.beaconList) {
                                if (major == b.major && minor == b.minor) {
                                    if ( Arssi >= -49){distance1=20;}
                                    else if (Arssi <= -50 && Arssi >= -58){distance1=50;}
                                    else if (Arssi <= -59 && Arssi >= -63 ){distance1=100;}
                                    else if (Arssi <= -64 && Arssi >= -68 ){distance1=150;}
                                    else if (Arssi <= -69 && Arssi >= -73 ){distance1=200;}
                                    else if (Arssi <= -74 && Arssi >= -78 ){distance1=250;}
                                    else if (Arssi <= -79 && Arssi >= -82 ){distance1=300;}
                                    else if (Arssi <= -83 && Arssi >= -86){distance1=350;}
                                    else if (Arssi <= -87 ){distance1=400;}
                                    distance1*=2.5;
                                    b.distance=distance1;
                                    drawView.updateScreen();
                                }
                            }



                               /* Point location = trilateration.getTrilateration(distance1,distance2,distance3);
                                location.x+=100;
                                location.y+=100;
                                */

                                Listcheck(s, device.toString());
                                log(device.getName(), Integer.toString(major), Integer.toString(minor), Long.toString((System.currentTimeMillis() - starttime)),  Integer.toString(rssi),  Integer.toString(Arssi), Integer.toString(distance), Integer.toString(tx));






                        }
                    }
                }

            };


    public LocationActivity() {
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
        // Set full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        drawView = new DrawView(this);
        setContentView(drawView);
        drawView.requestFocus();

        drawView.addBeacon(100,100, 31690, 2);
        drawView.addBeacon(955,580, 31655, 10);
        drawView.addBeacon(215,1830, 31690, 8);

        drawView.addBeacon(955,1830, 31690, 3);
        drawView.addBeacon(100,1340, 31690, 1);
        drawView.addBeacon(955,100, 31690, 7);

        queueSizeInput = (EditText) findViewById(R.id.Quesize);
        listView1 = (ListView) findViewById(R.id.list_Data);

        beaconList = new ArrayList<String>();
        detected = new ArrayList<String>();
        smoothener = new Smoothener(15);
        notificationMaker = new NotificationMaker(this);
        //distanceCalc =  new DistanceCalc(15);

        btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bta = btm.getAdapter();

        arrayAdapter2 = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                beaconList);

        if (bta == null || !bta.isEnabled()) {
            // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bta.enable();
        }

        CSV = new CSVwriter();
        smoothener = new Smoothener(Integer.parseInt("15"));
        log("Name", "major", "minor", "Tijd(ms)", "RSSI", "Adj. RSSI", " input dist.(m)", "Tx");
        if (bta == null || !bta.isEnabled()) {
            bta.enable();
        }

        starttime = System.currentTimeMillis();
        beaconList.clear();
        detected.clear();
        //queuesize.setEnabled(false);


        scanLeDevice();
        // this is a minor change

        getEmail();
     //   ConnectToDatabase();
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
    public void ConnectToDatabase(){
        try {

            // SET CONNECTIONSTRING
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            String username = "a7702411_m4m";
            String password = "move4mobile";
            Connection DbConn = DriverManager.getConnection("jdbc:jtds:sqlserver://mysql9.000webhost.com;user=" + username + ";password=" + password);

            Log.w("Connection", "open");
            Statement stmt = DbConn.createStatement();
            ResultSet reset = stmt.executeQuery(" select * from users ");



            DbConn.close();

        } catch (Exception e)
        {
            Log.w("Error connection","" + e.getMessage());
        }
    }

    public void getEmail() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = manager.getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {

                Context context = getApplicationContext();
                CharSequence text = account.name;
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();

            }
        }
    }


}
