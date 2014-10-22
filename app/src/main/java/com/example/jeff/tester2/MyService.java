package com.example.jeff.tester2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyService extends Service {
    BluetoothAdapter bta;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    boolean apprunning = false;
    static int hour = 180;
    int hourcounter = 180;
    int check = 0;


    private BluetoothAdapter.LeScanCallback mLeScanCallback =  new BluetoothAdapter.LeScanCallback() {
        @Override

        public void onLeScan(final BluetoothDevice device, final int Irssi,
                             final byte[] scanRecord) {
            int startByte = 2;
            int major = 0;
            int minor = 0;
            int tx = 0;
            boolean patternFound = false;

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
            }
            // logica for beacons detected comes here
            // checks if you in the proximity of a KONTAKT Beacon and if it has been an hour ago
            if(tx > - 70 && major == 31690)
            {
                startMain();
                hourcounter --;
            }

            if (check >=20)
            {
                // after every 20 results we  reset the counter and stop the scanning ( this is make sure we arent scanning non stop)
                check = 0;
                startMain();
                apprunning = true;
                bta.stopLeScan(mLeScanCallback);
            }
            check ++;


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

    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bta = bluetoothManager.getAdapter();


    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // String s = "henk";
        //   Toast.makeText(this,s,Toast.LENGTH_SHORT).show();

        // checks is bluetooth is on and the app isnt running ( app active returns false is app isnt running)
        // the check should be executed with major and minor values in the bluetoogh scan

        if (bta.isEnabled() && !AppActive("com.example.jeff.tester2.MenuActivity") && !AppActive("com.example.jeff.tester2.LoginActivity") && hourcounter == hour) {

            bta.startLeScan(mLeScanCallback);
            hourcounter --;
        }

        if(hourcounter < hour)
        {
            hourcounter--;
        }

        if(hourcounter < 1)
        {
            hourcounter = hour;
        }

        return Service.START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void startMain() {

        Intent start = new Intent(this, MenuActivity.class);
        start.setAction(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_LAUNCHER);
        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(start);
    }

    public boolean AppActive(String myclass)
    {
        // checks if the app is running, so we wont refresh it if its active
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(Integer.MAX_VALUE);


        for(ActivityManager.RunningTaskInfo task : tasks)
        {
            if(task.baseActivity.getClassName().equals(myclass))
            {
                return true;
            }
        }
        return false;

    }
}

