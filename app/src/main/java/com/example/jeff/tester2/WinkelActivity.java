package com.example.jeff.tester2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import library.Smoothener;
import library.ibeacon;


public class WinkelActivity extends Activity {

    ibeacon b1;
    ibeacon b2;
    ibeacon b3;
    ibeacon b4;

    TextView t1;
    TextView t2;
    TextView t3;
    TextView t4;
    TextView t5;

    ImageView v1;


    private Smoothener smoothener;
    private BluetoothManager btm;
    private BluetoothAdapter bta;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winkel);

        b1 = new ibeacon(52607, 63819);
        b2 = new ibeacon(41494, 50573);
        b3 = new ibeacon(31655,10);
        b4 = new ibeacon(31690, 2);

        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.textView5);

        v1 = (ImageView) findViewById(R.id.imageView);

        btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bta = btm.getAdapter();
        smoothener = new Smoothener(15);
        bta.enable();

        if (bta == null || !bta.isEnabled()) {
            // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bta.enable();
        }

        scanLeDevice();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.winkel, menu);
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


                        Arssi = smoothener.smoothen(rssi, major, minor);
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
                            logic(major, minor, Arssi);
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


    @Override
    public void onBackPressed() {
        StopscanLeDevice();
        super.onBackPressed();
        finish();
    }

    private void logic(int major, int minor, int rssi) {

        if (b1.isBeacon(major, minor)) {
            updatebeacon(b1, rssi);
        }

        if (b2.isBeacon(major, minor)) {
            updatebeacon(b2, rssi);
        }

        if (b3.isBeacon(major, minor)) {
            updatebeacon(b3, rssi);
        }

        if (b4.isBeacon(major, minor)) {
            updatebeacon(b4, rssi);
        }

        if (b4.isBeacon(major, minor)) {
              updatebeacon(b4, rssi);
         }


    }

    private void updatebeacon(ibeacon b, int rssi) {
        if (rssi > -60) {
            b.setKleur(Color.GREEN);
            b.Counterup();
            b.Counterup();
            updategui();
            return;
        }

        if (rssi > -75) {
            b.setKleur(Color.YELLOW);
             updategui();
            b.Counterup();
            return;
        }

        if (rssi > -90) {
            b.setKleur(Color.RED);
            updategui();
            return;
        }
    }


    public void updategui() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t1.setText(Integer.toString(b1.getSeconds()));
                t2.setText(Integer.toString(b2.getSeconds()));
                t3.setText(Integer.toString(b3.getSeconds()));
                t4.setText(Integer.toString(b4.getSeconds()));

                t1.setBackgroundColor(b1.getKleur());
                t2.setBackgroundColor(b2.getKleur());
                t3.setBackgroundColor(b3.getKleur());
                t4.setBackgroundColor(b4.getKleur());

                if(b1.getSeconds() >=30 && !b1.getShown())
                {

                    v1.setImageBitmap(b1.getQr());
                    t5.setText("u krijgt een gratis biertje");
                    b1.setShown();
                }

                if(b2.getSeconds() >=30 && !b2.getShown())
                {
                    v1.setImageBitmap(b2.getQr());
                    t5.setText("dit brood kunt u met korting meenemen");
                    b2.setShown();
                }

                if(b3.getSeconds() >=30 && !b3.getShown())
                {
                    v1.setImageBitmap(b3.getQr());
                    t5.setText("de negerzoenen zijn nu in de aanbieding");
                    b3.setShown();
                }

                if(b4.getSeconds() >=30 && !b4.getShown())
                {
                    v1.setImageBitmap(b4.getQr());
                    t5.setText("verf nu uw haar zwart, met andrelon zwarte haarverf");
                    b4.setShown();
                }



            }
        });
    }

}
