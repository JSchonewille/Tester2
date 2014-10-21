package library;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Jeff on 26-9-2014.
 */
public class LogicaHandler {

    HashMap<String, Integer> map = new HashMap<String, Integer>();
    private int major;
    private int minor;
    private int rssi;
    private Context mainContext;

    private boolean message0 = false;
    private boolean message1 = false;
    private boolean message2 = false;


    NotificationMaker notificationHandler;

    public LogicaHandler(Context c) {
        mainContext = c;
        notificationHandler = new NotificationMaker(mainContext);
    }


    public void handle(int majorInput, int minorInput, int rssiInput) {
        major = majorInput;
        minor = minorInput;
        rssi = rssiInput;
        lingering();
        notification();

    }

    private void notification() {

        if (major == 41494 && minor == 50573 && rssi > -74 && !message1) {
            message1 = true;
            notificationHandler.showNotification("Welkom", "Welkom bij move4mobile", mainContext, 0);
        }
        if (major == 41494 && minor == 50573 && rssi > -89 && !message0) {
            message0 = true;
            notificationHandler.showNotification("Move4Mobile Gramsbergen", "Move4Mobile zit op de eerste verdieping.", mainContext, 0);
        }

        if (major == 52607 && minor == 63819 && rssi > -68 && !message2) {
            message2 = true;
            notificationHandler.showNotification("Kantine", "Dit is de kantine van Move4Mobile.", mainContext, 0);
        }

    }

    private void lingering() {
        String key = Integer.toString(major + minor);
        // counter ++
        if (map.get(key) != null && rssi > -80) {
            int i = map.get(key);
            i++;
            map.put(key, (i));

            // checks if certain counter is reached
            if (map.get(key) == 30) {
                notificationHandler.CouponNotification2("Korting!", " Ik zie dat u twijfeld", mainContext, 1,key,"u krijgt 10 % korting voor beacon " + key);
            }
        }


        if (map.get(key) == null && rssi > -80) {
            map.put(key, 1);
        }
    }
}


