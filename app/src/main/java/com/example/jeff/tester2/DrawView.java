package com.example.jeff.tester2;

/**
 * Created by Sander on 2-10-2014.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View  {
    private static final String TAG = "DrawView";
    boolean initiate = false;

    public List<Beacon> beaconList = new ArrayList<Beacon>();

    List<Point> points = new ArrayList<Point>();
    Integer[] beaconDistance = new Integer[4];
    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        //this.setOnTouchListener(this);

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {



            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            canvas.drawRect(100,100,975,1850, paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect(105,105,970,1845, paint);
            paint.setColor(Color.BLACK);

            for (Beacon beacon : beaconList)
            {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(beacon.x, beacon.y, beacon.x+20, beacon.y+20, paint);
                paint.setStyle(Paint.Style.STROKE);
                if (beacon.distance>0) {canvas.drawCircle(beacon.x,beacon.y,beacon.distance, paint);}

            }

    }
/*
    public boolean onTouch(View view, MotionEvent event) {
        // if(event.getAction() != MotionEvent.ACTION_DOWN)
        // return super.onTouchEvent(event);
        Point point = new Point();
        point.x = event.getX();
        point.y = event.getY();
        points.clear();
        points.add(point);
        invalidate();
        Log.d(TAG, "point: " + point);
        return true;
    }
    */

    public void drawLocation(int beaconNr, int radius){
        //points.clear();


        invalidate();
    }

    public void drawRadius(){}

    public void updateScreen(){
        postInvalidate();
    }

    public void editDistance(int beaconNr,int distance)
    {
        beaconDistance[beaconNr]=distance;
       // invalidate();
        postInvalidate();
    }

    public void addBeacon(int x, int y, int major, int minor)
    {
        Beacon p = new Beacon();
        p.x =x;
        p.y=y;
        p.major =major;
        p.minor = minor;
        beaconList.add(p);
    }




    public class Beacon{
    public int x, y, distance, major, minor;
    }

}



