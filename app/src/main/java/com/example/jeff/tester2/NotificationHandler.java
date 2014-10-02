package com.example.jeff.tester2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

/**
 * Created by Sander on 26-9-2014.
 */
public class NotificationHandler {



   NotificationManager nMN;

    public NotificationHandler(Context mContext) {
        nMN = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }


    public void showNotification(String Title, String eventtext, Context ctx, int mssgID) {

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.move4mobile,
                eventtext, System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, mssgID,
                new Intent(ctx, MainActivity.class), mssgID);

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // notification.bigContentView = new RemoteViews(context.getPackageName(),R.layout.activity_main_godlike );

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(ctx, Title, eventtext,
                contentIntent);
        // Send the notification.

        nMN.notify("Title", mssgID, notification);

    }

    public void CouponNotification2(String Title, String eventtext, Context ctx, int mssgID,String link ,String text) {

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.move4mobile,
                eventtext, System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this
        // notification
        Intent it = new Intent(ctx,Offer.class);
        it.putExtra("input1",link);
        it.putExtra("input2",text);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, mssgID,it, mssgID);

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // notification.bigContentView = new RemoteViews(context.getPackageName(),R.layout.activity_main_godlike );

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(ctx, Title, eventtext,
                contentIntent);
        // Send the notification.

        nMN.notify("Title", mssgID, notification);

    }









}
