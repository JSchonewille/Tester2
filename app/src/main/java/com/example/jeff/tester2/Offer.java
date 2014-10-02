package com.example.jeff.tester2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;


public class Offer extends Activity {
    ImageView img;
    TextView textView1;
    Qrmaker qmaker;
    String Qrlink;
    String Qrtext;


    public void setup()
    {
        Intent myIntent = getIntent();
        Qrlink = myIntent.getStringExtra("input1");
        Qrtext = myIntent.getStringExtra("input2");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        setup();
        img = (ImageView) findViewById(R.id.imageView);
        textView1 = (TextView) findViewById(R.id.OfferText);
        qmaker = new Qrmaker();
        Drawable ob = null;
        try {
            ob = new BitmapDrawable(getResources(),qmaker.encode(Qrlink));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        img.setBackground(ob);
        textView1.setText(Qrtext);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.offer, menu);
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
}
