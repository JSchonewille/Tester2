package com.example.jeff.tester2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Sander on 26-9-2014.
 */
public class NotificationMaker {



   NotificationManager nMN;

    public NotificationMaker(Context mContext) {
        nMN = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }


    public void showNotification(String Title, String eventtext, Context ctx, int mssgID) {

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.move4mobile,
                eventtext, System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, mssgID,
                new Intent(ctx, TestLogActivity.class), mssgID);

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

    public void showImageNotification(String Title, String eventtext, Context ctx, int mssgID ){

        Bitmap remote_picture = null;


       NotificationCompat.BigPictureStyle notiStyle = new
               NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(Title);
        notiStyle.setSummaryText(eventtext);

            remote_picture = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.eersteverdieping);


        notiStyle.bigPicture(remote_picture);
        Notification myNotification = new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.move4mobile)
                        .setContentTitle(Title)
                        .setLargeIcon(remote_picture)
                        .setContentText(eventtext)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setStyle(notiStyle).build();


        nMN.notify("Title", mssgID, myNotification);

    }











}
