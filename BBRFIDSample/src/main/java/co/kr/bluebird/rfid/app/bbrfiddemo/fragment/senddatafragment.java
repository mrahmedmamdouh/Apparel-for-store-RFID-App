/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 *
 * http://www.bluebirdcorp.com/
 *
 * Author : Bogon Jun
 *
 * Date : 2016.01.18
 */

package co.kr.bluebird.rfid.app.bbrfiddemo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;

public class senddatafragment extends Fragment {

    private static final String TAG = senddatafragment.class.getSimpleName();
    private static final boolean D = Constants.INFO_D;

    private Button senddata;
    WDxDBHelper dbhelper;

    SQLiteDatabase db;
    ArrayList<itemModel> data= new ArrayList<>();
    ArrayList<itemModel> data2=new ArrayList<>();
    public static String store_id, urlNumber, portNumber;
    private ProgressBar mBatteryProgress;

    public static senddatafragment newInstance() {
        return new senddatafragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.send_data, container, false);
        dbhelper = new WDxDBHelper(getActivity());

        db =dbhelper.getWritableDatabase();
        senddata = (Button) v.findViewById(R.id.senddata);
        Button senddatabr = (Button) v.findViewById(R.id.senddatabr);

        SharedPreferences pref = getActivity().getSharedPreferences("storeNumber", Context.MODE_PRIVATE);
        store_id = pref.getString("storeNumber", "DEFAULT");

        SharedPreferences pref1 = getActivity().getSharedPreferences("urlNumber", Context.MODE_PRIVATE);
        urlNumber = pref1.getString("urlNumber", "DEFAULT");

        SharedPreferences pref2 = getActivity().getSharedPreferences("portNumber", Context.MODE_PRIVATE);
        portNumber = pref2.getString("portNumber", "DEFAULT");

        senddata.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Gson  g = new Gson();
                StringEntity entity = null;



                data2.clear();
                Cursor c1 = db.rawQuery("select distinct items from storecount",null);
                if (c1!=null && c1.getCount()>0)
                {
                    while ((c1.moveToNext()))
                    {
                        data2.add(new itemModel(c1.getString(c1.getColumnIndex("items")),store_id));
                    }
                }
                c1.close();
                String userData1 = g.toJson(data2);
                try {
                    entity= new StringEntity(userData1,"UTF-8");
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                String serviceURL1 = "http://" + urlNumber + ":" + portNumber+ "/api/Data/AddStItems";
                Log.d(getTag(),serviceURL1);
                AsyncHttpClient client1 = new AsyncHttpClient();
                try {
                    client1.post(getActivity(), serviceURL1 , entity,"application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                        }
                        @Override
                        public void onSuccess(String id) {
                            Toast.makeText(getActivity(), "Data has been sent by transaction id : " +  id, Toast.LENGTH_SHORT).show();
                            db.delete("storecount",null,null);

                            super.onSuccess(id);
                        }
                        @Override
                        public void onFailure(Throwable error) {

                            Toast.makeText(getActivity(), "Data has not been sent", Toast.LENGTH_SHORT).show();
                            super.onFailure(error);
                        }
                        @Override
                        public void onFinish() {
                            super.onFinish();
                        }
                    });
                }
                catch (IllegalArgumentException exception)
                {
                }




            }
        });
        senddatabr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson  g = new Gson();
                StringEntity entity1 = null;
                data.clear();
                Cursor c = db.rawQuery("select distinct items from BrCount",null);
                if(c!=null && c.getCount()>0)
                {
                    while(c.moveToNext())
                    {
                        data.add(new itemModel(c.getString(c.getColumnIndex("items")),store_id));
                    }
                }
                c.close();
                String userData = g.toJson(data);
                try {
                    entity1= new StringEntity(userData,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String serviceURL = "http://" + urlNumber + ":" + portNumber+ "/api/Data/AddBrItems";
                Log.d(getTag(),serviceURL);
                AsyncHttpClient client = new AsyncHttpClient();
                try {
                    client.post(getActivity(), serviceURL  , entity1,"application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                        }
                        @Override
                        public void onSuccess(String id) {
                            Toast.makeText(getActivity(), "Data has been sent by transaction id : " +  id, Toast.LENGTH_SHORT).show();
                            db.delete("BrCount",null,null);
                            super.onSuccess(id);
                        }                        @Override
                        public void onFailure(Throwable error) {
                            Toast.makeText(getActivity(), "Data has not been sent", Toast.LENGTH_SHORT).show();
                            super.onFailure(error);
                        }
                        @Override
                        public void onFinish() {
                            super.onFinish();
                        }
                    });
                }
                catch (IllegalArgumentException exception)
                {
                }



            }
        });
        return v;
    }

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");

        super.onStart();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (D) Log.d(TAG, "onStop");
        super.onStop();
    }


    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mInfoHandler");

    }

    public static class itemModel
    {
        private String items;
        private String store_id;


        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public itemModel(String items, String store_id)
        {
            this.items=items;
            this.store_id= store_id;
        }
        public String getItem() {
            return items;
        }

        public void setItem(String items) {
            this.items = items;
        }
    }
}