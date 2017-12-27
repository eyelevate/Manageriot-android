package com.manageriot.manageriot.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manageriot.manageriot.Models.Passport;
import com.manageriot.manageriot.R;
import com.q42.qlassified.Qlassified;
import com.q42.qlassified.Storage.QlassifiedSharedPreferencesService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail;
    EditText mPassword;
    Button mLoginButton;
    TextView mErrors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("primed","test");
//        checkAuthentication();
        mLoginButton = (Button)findViewById(R.id.loginButton);
        mErrors = (TextView)findViewById(R.id.errorLabel);
        mErrors.setText("");

        // start keystore
        // Initialize the Qlassified service
        Qlassified.Service.start(this);

        Qlassified.Service.setStorageService(new QlassifiedSharedPreferencesService(this, getString(R.string.keystore_name)));
//        Qlassified.Service.put(getString(R.string.keystore_tokens), "");
//        String keychain = Qlassified.Service.getString("onedough84@gmail.com");
//        Log.d("user keychain ",keychain);
    }

    // check if authenticated
    public void checkAuthentication(View view) {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;
        Passport passport = new Passport();
        final String checker_token = passport.makeCheckerToken();
        final String url = passport.makeUrl("check-validated-by-manager");
        mEmail = (EditText)findViewById(R.id.emailText);
        mPassword = (EditText)findViewById(R.id.passwordText);

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("msg",response);
                try {
                    Log.d("ok","here we go");
                    JSONObject obj = new JSONObject(response);
                    Boolean validated = obj.getBoolean("validated");
                    Boolean status = obj.getBoolean("status");
                    if (status) {
                        if (validated) { // user has been validated on the server continue on

                            login();

                        } else { // display error message
                            mErrors.setText("This user has not been validated by a manager. Please contact your boss or system administrator");
                        }
                    } else { // display error message
                        mErrors.setText("Could not connect to server. There was an issue with the connection. Please try again.");
                    }
                } catch (JSONException e) {
                    mErrors.setText("Server error: Could not access the live server, please try again.");
                    Log.d("errors",e.toString());
                    e.printStackTrace();
                }
                Log.d("response",response);


            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error
//                CharSequence text = String.valueOf(error);
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
                mErrors.setText("Email/Password combination did not authenticate. Please try again.");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", checker_token);

                return params;
            }
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("email", mEmail.getText().toString()); //Add the data you'd like to send to the server.
                MyData.put("password", mPassword.getText().toString()); //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);
        // remove the virtual keyboard
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void login() {
        // register the current user in keystore
        Qlassified.Service.put(getString(R.string.keystore_current), mEmail.getText().toString());

        // check if the user has a token in keychain
        try {

            String token_list = Qlassified.Service.getString(mEmail.getText().toString());
            if (token_list != null) { //
                JSONObject main = new JSONObject(token_list);
                if (main.has(mEmail.getText().toString())) {
                    // check if the token authenticates
                    check_token();
                } else {
                    obtain_token();
                }

            } else { // empty so this is first time. please send to server for new token save then login
                obtain_token();
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }




        // if so then use the token and verify if token works

        // if token works login

        // if token does not work create a new token

        // else show error message system failure and bump out

    }

    private void obtain_token() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        final Passport passport = new Passport();
        final String url = passport.makeOauthUrl("oauth/token");
        mEmail = (EditText)findViewById(R.id.emailText);
        mPassword = (EditText)findViewById(R.id.passwordText);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("msg",response);
                try {
                    Log.d("ok","oauth is made token for is");
                    JSONObject obj = new JSONObject(response);

                    if (obj.has("errors")) { // errors on send set message to let user know
                        mErrors.setText(obj.getString("errors"));
                    } else {  // made the token set it in the keystore and redirect to home
                        // get the keystore and set it as a dictionary
                        String keychain = Qlassified.Service.getString(mEmail.getText().toString());
                        if (keychain != null || keychain != "") {
                            // create map to store
//                            Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
//                            Map<String, String> tkn = new HashMap<String, String>();
//                            tkn.put("access_token",obj.getString("access_token"));
//                            tkn.put("refresh_token",obj.getString("refresh_token"));
//                            map.put(mEmail.getText().toString(),tkn);
//                            Gson gson = new GsonBuilder().create();
//                            String jsonString = gson.toJson(map);
//                            Log.d("String token",jsonString);
                            // update the dictionary with the correct value
                            Qlassified.Service.put(mEmail.getText().toString(), obj.getString("access_token"));
                            String kc = Qlassified.Service.getString(mEmail.getText().toString());
                            Log.d("keychain",kc);
                        }
                        // check token
                        check_token();


                    }


                } catch (JSONException e) {
                    mErrors.setText("Server error: Could not access the live server, please try again.");
                    Log.d("errors",e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error
//                CharSequence text = String.valueOf(error);
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
                mErrors.setText("Email/Password combination did not authenticate. Please try again.");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/x-www-form-urlencoded");

                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("grant_type", "password");
                params.put("client_id", getString(R.string.client_id));
                params.put("client_secret", getString(R.string.client_secret));
                params.put("username", mEmail.getText().toString());
                params.put("password", mPassword.getText().toString());
                params.put("scope", "");

                return params;
            }
//
        };
        MyRequestQueue.add(MyStringRequest);
    }


    private void check_token() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        final Passport passport = new Passport();
        final String url = passport.makeUrl("check/token");

        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.d("ok","oauth is made token for is");
                    JSONObject obj = new JSONObject(response);

                    if (obj.has("message")) {
                        mErrors.setText("Your account is not active. Please contact your manager for approval.");
                    } else if (obj.has("status")) {
                        if (obj.getBoolean("status")) {
                            // redirect to home you have successfully logged in
                        } else { // user is not authenticated
                            mErrors.setText(obj.getString("reason"));

                        }
                    }


                } catch (JSONException e) {
                    mErrors.setText("Server error: Could not access the live server, please try again.");
                    Log.d("errors",e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                mErrors.setText("Authentication did not approve your token.");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", passport.getAuthorization());

                return params;
            }

        };
        MyRequestQueue.add(MyStringRequest);
    }

    private void launchHome() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
