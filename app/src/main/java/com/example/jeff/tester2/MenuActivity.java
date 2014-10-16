package com.example.jeff.tester2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Sander on 16-10-2014.
 */
public class MenuActivity extends Activity {
    Button btnButton1;
    Button btnButton2;
    Button btnButton3;
    Button btnButton4;
    private ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Set full screen view

        setContentView(R.layout.fancy_screen);

        btnButton1 =(Button) findViewById(R.id.btnButton1);
        btnButton2 =(Button) findViewById(R.id.btnButton2);
        btnButton3 =(Button) findViewById(R.id.btnButton3);
        btnButton4 =(Button) findViewById(R.id.btnButton4);




        btnButton1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to mainactivity screen
                Intent i = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(i);
            }
        });

        btnButton4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to mainactivity screen
                nDialog = new ProgressDialog(MenuActivity.this);
                nDialog.setMessage("Loading..");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();
                Intent i = new Intent(getApplicationContext(), TestLogActivity.class);
                startActivity(i);
                nDialog.dismiss();
            }
        });

    }
}
