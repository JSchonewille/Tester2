package com.example.jeff.tester2;

/**
 * Created by Sander on 7-10-2014.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import library.DatabaseHandler;
import library.UserFunctions;

public class RegisterActivity extends Activity {
    /**
     *  JSON Response node names.
     **/
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";
    /**
     * Defining layout items.
     **/
    EditText inputFirstName;
    EditText inputLastName;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        /**
         * Defining all layout items
         **/
        inputFirstName = (EditText) findViewById(R.id.fname);
        inputLastName = (EditText) findViewById(R.id.lname);
        inputUsername = (EditText) findViewById(R.id.uname);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.pword);
        btnRegister = (Button) findViewById(R.id.btnRegister);

/**
 * Button which Switches back to the login screen on clicked
 **/
        TextView registerScreen = (TextView) findViewById(R.id.link_to_login);


        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to login screen
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        /**
         * Register Button click event.
         * A Toast is set to alert when the fields are empty.
         * Another toast is set to alert Username must be 5 characters.
         **/
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputFirstName.getText().toString().equals("")) && ( !inputLastName.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals("")) )
                {
                    if ( inputUsername.getText().toString().length() > 2 ){
                        NetAsync(view);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Username should be minimum 3 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * Async Task to check whether internet connection is working
     **/
    private class NetCheck extends AsyncTask
    {
        private ProgressDialog nDialog;
        private Boolean th;
        Context context = getApplicationContext();

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(RegisterActivity.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        @Override
        protected Object doInBackground(Object[] objects)  {
/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        th = true;

                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            th = false;

            return false;
        }

        @Override
        protected void onPostExecute(Object result){
            if(th == true){
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
               Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
            }
        }



        private class ProcessRegister extends AsyncTask {
            String email,password,fname,lname,uname;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                inputUsername = (EditText) findViewById(R.id.uname);
                inputPassword = (EditText) findViewById(R.id.pword);
                fname = inputFirstName.getText().toString();
                lname = inputLastName.getText().toString();
                email = inputEmail.getText().toString();
                uname= inputUsername.getText().toString();
                password = inputPassword.getText().toString();

                nDialog.setTitle("Creating User");

            }
            @Override
            protected JSONObject doInBackground(Object[] objects) {
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.registerUser(fname, lname, email, uname, password);

                return json;
            }

            @Override
            protected void onPostExecute(Object son) {
                /**
                 * Checks for success message.
                 **/
                try {
                    JSONObject json = (JSONObject) son;

                    if (json!=null) {
                        if (json.getString(KEY_SUCCESS) != null) {
                            String res = json.getString(KEY_SUCCESS);
                            String red = json.getString(KEY_ERROR);

                            if (Integer.parseInt(res) == 1) {
                                nDialog.setTitle("Getting Data");

                                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                JSONObject json_user = json.getJSONObject("user");
                                /**
                                 * Removes all the previous data in the SQlite database
                                 **/
                                UserFunctions logout = new UserFunctions();
                                logout.logoutUser(getApplicationContext());
                                db.addUser(json_user.getString(KEY_FIRSTNAME), json_user.getString(KEY_LASTNAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_USERNAME), json_user.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));
                                /**
                                 * Stores registered data in SQlite Database
                                 * Launch Registered screen
                                 **/
                                Intent registered = new Intent(getApplicationContext(), MenuActivity.class);
                                /**
                                 * Close all views before launching Registered screen
                                 **/
                                registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(registered);
                                finish();
                            } else if (Integer.parseInt(red) == 2) {
                                nDialog.dismiss();
                                Toast.makeText(context, "Email already registered", Toast.LENGTH_SHORT).show();

                            } else if (Integer.parseInt(red) == 3) {
                                nDialog.dismiss();
                                Toast.makeText(context, "Invalid email",  Toast.LENGTH_SHORT).show();
                            }
                        } else {
                           Toast.makeText(context, "Error occured in registration", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    }
    }
    public void NetAsync(View view){
        new NetCheck().execute();
    }
}