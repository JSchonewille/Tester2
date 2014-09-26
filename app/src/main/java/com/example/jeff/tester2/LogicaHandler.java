package com.example.jeff.tester2;

import android.content.Context;

/**
 * Created by Jeff on 26-9-2014.
 */
public class LogicaHandler {

    private int major;
    private int minor;
    private int rssi;
    private Context mainContext;

   private boolean message0 = false;
   private boolean message1 = false;
   private boolean message2 = false;

    NotificationHandler notificationHandler;

    public LogicaHandler( Context c)
    {
        mainContext = c;
        notificationHandler = new NotificationHandler(mainContext);
    }


    public void handle(int majorInput, int minorInput, int rssiInput) {
        major = majorInput;
        minor = minorInput;
        rssi = rssiInput;
        notification();

    }

    private void notification() {

        if (major == 41494 && minor == 50573 && rssi > -74 && !message1) {
            message1=true;
            notificationHandler.showNotification("Welkom", "Welkom bij move4mobile", mainContext, 0);
        }
        if (major == 41494 && minor == 50573 && rssi > -89 && !message0) {
            message0=true;
            notificationHandler.showNotification("Move4Mobile Gramsbergen", "Move4Mobile zit op de eerste verdieping.", mainContext, 0);
        }

        if (major == 52607 && minor == 63819 && rssi > -68 && !message2) {
            message2=true;
            notificationHandler.showNotification("Kantine", "Dit is de kantine van Move4Mobile.", mainContext, 0);
        }

    }
}


