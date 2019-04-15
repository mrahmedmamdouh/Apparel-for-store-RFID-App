package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;


public class Login extends Activity implements fetchData.download_complete {
    Button b1,b2;
    EditText ed1,ed2;
    public static ArrayList<Transaction_objects> transElements = new ArrayList<>();
    public String StoreCode, winUser, winPassword, accessToken, bearer;
    public ArrayList<Transaction_objects> jr = new ArrayList<Transaction_objects>();
    TextView tx1;
    int counter = 3;
    public int UserId, StoreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        SharedPreferences pref = getApplication().getSharedPreferences("storeNumber", Context.MODE_PRIVATE);
        StoreCode = pref.getString("storeNumber", "DEFAULT");
        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);

        b2 = (Button)findViewById(R.id.button2);
        tx1 = (TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);


        AsyncHttpClient client = new AsyncHttpClient();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(ed1.getText().toString().equals("admin") &&
                        ed2.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, SettingLoginScreen.class);
                    startActivity(intent);


                } else if (ed1.getText().toString().length() != 0 &&
                        ed2.getText().toString().length() != 0) {

                    winPassword = ed2.getText().toString();
                    winUser = ed1.getText().toString();
                    HashMap<String, String> param1 = new HashMap<String, String>();
                    String username = "wdx.user";
                    String password = "123456";
                    String grant_type = "password";
                    param1.put("username", winUser);
                    param1.put("password", winPassword);
                    param1.put("grant_type", grant_type);
                    Log.d(TAG, "onItemClick: " + param1);
                    RequestParams params1 = new RequestParams(param1);


                    client.post("http://41.65.223.218:8889/API", params1, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String data) {

                            Log.d(TAG, "onSuccess: " + data);
                            try {
                                JSONObject object = new JSONObject(data);
                                Transaction_objects add = new Transaction_objects();
                                accessToken = object.getString("access_token");
                                bearer = object.getString("token_type");


                                SharedPreferences pref = getApplication().getSharedPreferences("access_token", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("access_token", accessToken);  // Saving string
                                editor.apply();

                                SharedPreferences pref1 = getApplication().getSharedPreferences("bearer", MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = pref1.edit();
                                editor1.putString("bearer", bearer);  // Saving string
                                editor1.apply();

                                Log.d(TAG, "onSuccess: " + accessToken );

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

                        }
                    });



                    AsyncHttpClient client = new AsyncHttpClient();


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            HashMap<String, String> param = new HashMap<String, String>();

                            param.put("Authorization", bearer + " " +accessToken);
                            Log.d(TAG, "onItemClick: " + param);
                            RequestParams params = new RequestParams(param);

                    client.post("http://41.65.223.218:8888/api/RFIDAuthUser?winUser=wdx.user&winPassword=123456&StoreCode=101", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String data) {

                            Log.d(TAG, "onSuccess: " + data);
                            try {
                                JSONObject object = new JSONObject(data);
                                Transaction_objects add = new Transaction_objects();
                                add.UserId = object.getInt("UserId");
                                add.StoreId = object.getInt("StoreId");
                                UserId = object.getInt("UserId");
                                SharedPreferences pref = getApplication().getSharedPreferences("UserId", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putInt("UserId", add.UserId);  // Saving string
                                editor.apply();
                                StoreId = object.getInt("StoreId");
                                SharedPreferences pref1 = getApplication().getSharedPreferences("StoreId", MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = pref1.edit();
                                editor1.putInt("StoreId", add.StoreId);  // Saving string
                                editor1.apply();
                                Log.d(TAG, "onSuccess: " + UserId + "," + StoreId + "," + add);
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

                        }
                    });}
                    }, 2000);
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();

                }

                else{
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
                    tx1.setVisibility(View.VISIBLE);
                    tx1.setBackgroundColor(Color.RED);
                    counter--;
                    tx1.setText(Integer.toString(counter));

                    if (counter == 0) {
                        b1.setEnabled(false);
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void get_data(String data) {


    }
}
