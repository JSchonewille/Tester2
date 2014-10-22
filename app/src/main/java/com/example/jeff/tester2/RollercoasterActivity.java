package com.example.jeff.tester2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

import library.NotificationMaker;
import library.Smoothener;

/**
 * Created by Sander on 17-10-2014.
 */
public class RollercoasterActivity extends Activity {
    private Smoothener smoothener;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public Context context;

    private ArrayList<String> beaconList;
    private ArrayList<String> detected;

    private ImageView rollercoasterTrain;

    private BluetoothManager btm;
    private BluetoothAdapter bta;

    NotificationMaker notificationMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Set full screen view
        setContentView(R.layout.rollercoaster_test);

        rollercoasterTrain = (ImageView) findViewById(R.id.rollercoasterTrain);

        beaconList = new ArrayList<String>();
        detected = new ArrayList<String>();
        smoothener = new Smoothener(Integer.parseInt("15"));
        notificationMaker = new NotificationMaker(this);
        //distanceCalc =  new DistanceCalc(15);

        btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bta = btm.getAdapter();


        if (bta == null || !bta.isEnabled()) {
            // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bta.enable();
        }
        beaconList.clear();
        detected.clear();

        scanLeDevice();


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
    }

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


                            if (major == 31690){
                                if (minor == 3) {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            rollercoasterTrain.setImageResource(R.drawable.rollercoaster_car2);
                                            updateScreen();
                                        }});
                                }

                                else if (minor == 4)
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rollercoasterTrain.setImageResource(R.drawable.rollercoaster_car3);
                                            updateScreen();
                                        }
                                    });
                                }
                                else if (minor == 6)
                                {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            rollercoasterTrain.setImageResource(R.drawable.rollercoaster_car1);
                                            updateScreen();
                                        }});
                                }
                                else if (minor == 7)
                                {
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            rollercoasterTrain.setImageResource(R.drawable.rollercoaster_car4);
                                            updateScreen();
                                        }});
                                }
                            }






                        }
                    }
                }

            };


    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private void updateScreen()
    {
       rollercoasterTrain.postInvalidate();
    }

    @Override
    public void onBackPressed(){
        StopscanLeDevice();
        super.onBackPressed();
        finish();
    }

}
