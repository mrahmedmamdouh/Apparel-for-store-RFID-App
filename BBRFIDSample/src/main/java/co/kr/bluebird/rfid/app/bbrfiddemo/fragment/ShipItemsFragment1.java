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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import co.kr.bluebird.rfid.app.bbrfiddemo.BuisnessLogic;
import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.Globals;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.fileutil.FileSelectorDialog;
import co.kr.bluebird.rfid.app.bbrfiddemo.permission.PermissionHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.transaction_listView;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.MODE_PRIVATE;

//import java.io.Reader;


public class ShipItemsFragment1 extends Fragment {
    ArrayList<ShipItemsFragment1.itemModel> data= new ArrayList<>();

    private static final String TAG = ShipItemsFragment1.class.getSimpleName();
    private static final String mLogFileName = "/DATA-";
    private static final String mExtentionName = ".txt";
    private static final boolean D = Constants.ACC_D;
    public static String x;
    private static final String FILE_DIR_PRE = "//DIR//";

    private static final String FILE_EXT = ".bin";

    private static final int PROGRESS_DIALOG = 1;

    private static final String FILE_NAME = "example.csv";

    private ListView mRfidList;

    private Reader mReader;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    Globals g;
    BuisnessLogic BL;
    public String _item,_NewIserial;
    private Context mContext;
    private boolean BCread = false;
    private ProgressDialog mDialog;
    String date;
    private ArrayAdapter<CharSequence> mItemChar;
    String timeStamp;
    private Handler mOptionHandler;
    private ProgressBar mTagLocateProgress;
    private int mLocateValue;
    private Fragment mFragment;
    private SoundPool mSoundPool;
    private int mSoundId;
    private boolean mLocate;
    private boolean mSoundFileLoadState;
    private float mSoundVolume;
    private FileSelectorDialog mFileSelecter;
    private boolean mSoundPlay = true;
    private boolean brmode = false;
    private boolean rfmode = false;
    Integer tidread = 0;
    Integer successepc = 0;
    Integer successtid = 0;
    Integer successwrite = 0;
    Integer taskdone = 0;
    String EPCdata = "";
    String TIDdata = "";
    String TIDFull = "";
    String currentdate ="";
    private AccessHandler mAccessHandler = new AccessHandler(this);

    String old = "";
    String oldtwo = "";
    EditText barcode, donetags, donetagstwo, boxnum, epc, tid;
    Button save,clear;
    String mText;
    String mText2;
    TextView modetext,counttext;
    String url = "http://41.65.223.218:8888/api/RFIDTransDetails";
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    public static ShipItemsFragment1 newInstance() {
        return new ShipItemsFragment1();
    }
    private ProgressBar mBatteryProgress;
    public  String NewIserial,SPName;



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.barcode_transaction, container,false);
        mContext = inflater.getContext();

        mFragment = this;

        dbhelper = new WDxDBHelper(getActivity());
        db =dbhelper.getWritableDatabase();


        mBatteryProgress = (ProgressBar)v.findViewById(R.id.batt_progress);
        modetext = (TextView) v.findViewById(R.id.modetext);
        counttext = (TextView) v.findViewById(R.id.counter);
        mRfidList = (ListView)v.findViewById(R.id.access_list);
        barcode = (EditText) v.findViewById(R.id.ed_barcode);
        barcode.requestFocus();
        barcode.setInputType(InputType.TYPE_NULL); barcode.setTextIsSelectable(true);
        boxnum = (EditText) v.findViewById(R.id.ed_boxnum);
        epc = (EditText) v.findViewById(R.id.ed_epc);
        tid = (EditText) v.findViewById(R.id.ed_tid);
        mRfidList.setOnItemClickListener(listListener);
        donetags = (EditText) v.findViewById(R.id.ed_donetags);
        //donetagstwo = (EditText) v.findViewById(R.id.ed_donetagstwo);
        save = (Button) v.findViewById(R.id.bt_save);
        clear = (Button) v.findViewById(R.id.bt_clear);
               mItemChar = ArrayAdapter.createFromResource(mContext, R.array.items_array,
                android.R.layout.simple_list_item_1);
        mRfidList.setAdapter(mItemChar);

        barcode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0)
                return;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(System.currentTimeMillis());




                Log.d(TAG, "clicked");
                db.beginTransaction();
                try {

                        SharedPreferences pref = getActivity().getSharedPreferences("NewIserial", MODE_PRIVATE);
                        _NewIserial =  pref.getString("NewIserial", "DEFAULT");
                        _item = barcode.getText().toString();
                        db.execSQL("insert into TransactionDetails  (EBC, tblRFidTransHeader) values ('" + _item + "','"+_NewIserial+"')");


                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }



            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), transaction_listView.class);
                startActivity(intent1);//Edited here
                Cursor c = db.rawQuery("select count(*) as COUNT from TransactionDetails",null);
                while (c.moveToNext())
                {
                    x = c.getString(c.getColumnIndex("COUNT"));

                    Toast.makeText(getActivity(),c.getString(c.getColumnIndex("COUNT")),Toast.LENGTH_SHORT).show();
                }
                Gson g = new Gson();
                StringEntity entity1 = null;
                data.clear();
                c = db.rawQuery("select distinct EBC,tblRFidTransHeader from TransactionDetails where tblRFidTransHeader = " + _NewIserial,null);
                if(c!=null && c.getCount()>0)
                {
                    while(c.moveToNext())
                    {
                        data.add(new ShipItemsFragment1.itemModel(c.getString(c.getColumnIndex("EBC")), (c.getInt(c.getColumnIndex("tblRFidTransHeader")))));
                    }
                }
                c.close();
                String userData = g.toJson(data);
                try {
                    entity1= new StringEntity(userData,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String serviceURL = "http://41.65.223.218:8888/api/RFIDTransDetails";
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
                            Log.d(TAG, "onSuccess: " + id);
                            if(id.equals("true")){
                                db.delete("TransactionDetails","tblRFidTransHeader= ?", new String[]{_NewIserial});
                            Toast.makeText(mContext, "DATA HAS BEEN SENT SUCCESSFULLY ", Toast.LENGTH_LONG).show();
                                SharedPreferences pref = getActivity().getSharedPreferences("NewIserial", MODE_PRIVATE);
                                NewIserial = pref.getString("NewIserial", "DEFAULT");

                                SharedPreferences pref1 = getActivity().getSharedPreferences("SPName", MODE_PRIVATE);
                                SPName = pref1.getString("SPName", "DEFAULT");

                                AsyncHttpClient client = new AsyncHttpClient();
                                RequestParams params = new RequestParams();
                                params.put("NewIserial", NewIserial);
                                params.put("SPName", SPName);
                                client.post("http://41.65.223.218:8888/API/RFIDTransSP?TransId=41&SPName=SP1", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(String numberOfRowsBarCode) {
                                        Log.d(TAG, "onSuccess: " + numberOfRowsBarCode);
                                        SharedPreferences pref = getActivity().getSharedPreferences("numberOfRowsBarCode", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("numberOfRowsBarCode", numberOfRowsBarCode);  // Saving string
                                        editor.apply();
                                        super.onSuccess(numberOfRowsBarCode); }
                                });

                            }
                            else if (id.equals("false")){ Toast.makeText(mContext, "DATA HAS NOT BEEN SENT .... PLEASE TRY AGAIN LATER ", Toast.LENGTH_LONG).show();}

                            super.onSuccess(id);
                        }
                        @Override
                        public void onFailure(Throwable error) {
                            Toast.makeText(mContext, "Data has not been sent", Toast.LENGTH_SHORT).show();

                            super.onFailure(error);
                        }
                        @Override
                        public void onFinish() {
                            data.clear();
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


    private void SavetoWeb(String TID, String Barcode){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                donetags.setText("something went wrong");
                error.printStackTrace();
                requestQueue.stop();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("TID",TID);
                params.put("Barcode",Barcode);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    private String formatdataasjson() throws JSONException {
        final JSONObject root = new JSONObject();

        root.put("EPCDATA:","EPC");
        root.put("TIDDATA:","TID");

        return root.toString(1);
    }
    @SuppressLint("StaticFieldLeak")
    private void sendDatatoServer() throws JSONException {
        String json = formatdataasjson();

        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                return getServerResponse(json);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();

    }
    private String getServerResponse(String json) {

        return null;
    }






    private OnItemClickListener listListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            int pos = arg2;
            int result = -1000;

            String sResult = null;
            StringBuilder message = new StringBuilder();
            switch (pos) {
                case 0:
                    //read bank type tid
                    //result = mReader.RF_READ(SDConsts.RFMemType.TID, 1, 7, "00000000", false);

                    //read bank type reserved (0, 2) 8byte
                    // Address : 0,1 kill password, Address :2,3 access password
                    //result = mReader.RF_READ(SDConsts.RFMemType.RESERVED, 0, 2, "00000000", false);
                    //result = mReader.RF_READ(SDConsts.RFMemType.RESERVED, 2, 2, "00000000", false);

                    //read bank type user
                    //result = mReader.RF_READ(SDConsts.RFMemType.USER, 0, 4, "00000000", false);
                    //result = mReader.RF_READ(SDConsts.RFMemType.USER, 7, 1, "00000000", false);

                    //read bank type epc
                    //1: startposition , 7 : word length(2*(7 - 1)) * 8 = 96bit
                    String str = barcode.getText().toString();
                    StringBuilder sb = new StringBuilder();

                    for (int toPrepend=24-str.length(); toPrepend>0; toPrepend--) {
                        sb.append('0');
                    }

                    sb.append(str);
                    String res = sb.toString();

                    //result = mReader.RF_WriteTagID(2, res, "00000000", false);
                    result = mReader.RF_WRITE(SDConsts.RFMemType.EPC, 2, res, "00000000",  false);
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_WRITE");
                    else
                        message.append("RF_WRITE failed " + result);
                    break;

                case 1:
                    //result = mReader.RF_WRITE(SDConsts.RFMemType.USER, 0, "0000000000000000", "00000000", false);
                    //result = mReader.RF_WRITE(SDConsts.RFMemType.USER, 0, "1111222233334444", "00000000", false);
                    //result = mReader.RF_WRITE(SDConsts.RFMemType.EPC, 2, "111122223333444455556666", "00000000",  false);
                    //  result = mReader.RF_WRITE(SDConsts.RFMemType.EPC, 2, "303556022843A3C00000000D", "00000000",  false);

                    // if (result == SDConsts.RFResult.SUCCESS)
                    //    message.append("RF_WRITE");
                    // else
                    //    message.append("RF_WRITE failed " + result);
                    // break;

                    // epc write
                    //result = mReader.RF_WriteTagID(1, "3000666655554444333322223333", "00000000", true);

                    result = mReader.RF_READ(SDConsts.RFMemType.TID, 0, 6, "00000000", false);

                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_READ");
                    else
                        message.append("RF_READ failed " + result);
                    break;

                case 2:
                    // epc write
                    //result = mReader.RF_WriteTagID(1, "3000666655554444333322223333", "00000000", true);

//                String str = barcode.getText().toString();
//                StringBuilder sb = new StringBuilder();
//
//                for (int toPrepend=28-str.length(); toPrepend>0; toPrepend--) {
//                    sb.append('0');
//                }
//
//                sb.append(str);
//                String res = sb.toString();
//
//                result = mReader.RF_WriteTagID(1, res, "00000000", false);
//                if (result == SDConsts.RFResult.SUCCESS)
//                    message.append("RF_WriteTagID");
//                else
//                    message.append("RF_WriteTagID failed " + result);
//                break;
                    result = mReader.RF_READ(SDConsts.RFMemType.EPC, 2, 6, "00000000", false);

                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_READ");
                    else
                        message.append("RF_READ failed " + result);
                    break;

                case 3:
                    // new password , old password
                    result = mReader.RF_WRITE(SDConsts.RFMemType.EPC, 2, "0000000000000000000000000000", "00000000",  false);
                    //result = mReader.RF_WriteTagID(2, res, "00000000", false);
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_WriteTagID");
                    else
                        message.append("RF_WriteTagID failed " + result);
                    break;
                case 4:
                    result = mReader.RF_WriteKillPassword("00000000", "00000000", false);
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_WriteKillPassword");
                    else
                        message.append("RF_WriteKillPassword failed " + result);
                    break;
                case 5:
                    //1. EPC lock by password
                    result = mReader.RF_LOCK("0030", "0020", "00000000", false); // epc lock(0020)
                    //2. EPC unlock by password
                    //result = mReader.RF_LOCK("0030", "0000", "00000000", false); // epc unlock(0000)
                    //3. EPC permallock
                    //result = mReader.RF_LOCK("0030", "0030", "00000000", false); // epc permal lock(0030)
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_LOCK");
                    else
                        message.append("RF_LOCK failed " + result);
                    break;
                case 6:
                    //WARNING !! This command kill your tag.
                    //Temporary blocked. If you want to use this api, remove the comment block
                    //result = mReader.RF_KILL("12345678", "00000000", false);
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_KILL");
                    else
                        message.append("RF_KILL is blocked");
                    break;
                case 7:
                    result = mReader.RF_BlockWrite(SDConsts.RFMemType.USER, 7, "1234", "00000000");
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_BlockWrite");
                    else
                        message.append("RF_BlockWrite failed " + result);
                    break;
                case 8:
                    //result = mReader.RF_BlockPermalock(0, 1, 0x4100, "00000000");
                    //result = mReader.RF_BlockPermalock(0, 1, 0x0100, "00000000");
                    //first block 0x8000, second 0x4000, 3rd 0x2000, 4th 0x1000
                    result = mReader.RF_BlockPermalock(0, 1, 0x8000, "00000000");

                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_BlockPermalock");
                    else
                        message.append("RF_BlockPermalock failed " + result);
                    break;
                case 9:
                    //result = mReader.RF_BlockErase(SDConsts.RFMemType.USER, 0, 2, "00000000");
                    result = mReader.RF_BlockErase(SDConsts.RFMemType.USER, 7, 1, "00000000");
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_BlockErase");
                    else
                        message.append("RF_BlockErase failed " + result);
                    break;
                case 10:
                    sResult = mReader.RF_GetLibVersion();
                    message.append("RF_GetLibVersion = " + sResult);
                    break;
                case 11:
                    result = mReader.RF_ResetConfigToFactoryDefaults();
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_ResetConfigToFactoryDefaults");
                    else
                        message.append("RF_ResetConfigToFactoryDefaults failed " + result);
                    break;
                case 12:
                    result = mReader.RF_ModuleReboot();
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_ModuleReboot");
                    else
                        message.append("RF_ModuleReboot failed " + result);
                    break;
                case 13:
                    boolean b = PermissionHelper.checkPermission(mContext, PermissionHelper.mStoragePerms[0]);
                    if (b) {
                        result = 0;
                    }
                    else
                        PermissionHelper.requestPermission(getActivity(), PermissionHelper.mStoragePerms);
                    if (result == SDConsts.RFResult.SUCCESS)
                        message.append("RF_UpdateRFIDFirmware");
                    break;
                case 14:
                    sResult = mReader.RF_GetRFIDVersion();
                    message.append("RF_GetRFIDVersion = " + sResult);
                    break;

            }
            if (sResult == null)
                Log.d(TAG, "Result = " + result);

            else
                Log.d(TAG, "Result = " + sResult);
            if (message.length() > 0)
                Toast.makeText(mContext, message.toString(), Toast.LENGTH_SHORT).show();
        }

    };



    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mReader = Reader.getReader(mContext, mAccessHandler);

        changemode();
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        //successwrite = 0;
        mReader.RF_SetRadioPowerState(10);
        super.onStart();
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onResume");
        changemode();
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onPause");
        changemoderfid();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (D) Log.d(TAG, "onStop");
        changemoderfid();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (D) Log.d(TAG, "onRequestPermissionsResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case PermissionHelper.REQ_PERMISSION_CODE:
                    if (permissions != null) {
                        boolean hasResult = false;
                        for (String p : permissions) {
                            if (p.equals(PermissionHelper.mStoragePerms[0])) {
                                hasResult = true;
                                break;
                            }
                        }

                    }
                    break;
            }
        }
    }

    private static class AccessHandler extends Handler {
        private final WeakReference<ShipItemsFragment1> mExecutor;
        public AccessHandler(ShipItemsFragment1 f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            ShipItemsFragment1 executor = mExecutor.get();
            if (executor != null) {
                try {
                    executor.handleMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readepc() {
        //if (barcode.getText().toString().trim().length() <= 0){

        //return;
        //}
        //else if(barcode.getText().toString().trim().length() > 0) {
        //if (successtid == 1 && successepc == 0) {
        int result1;
        StringBuilder message = new StringBuilder();
        result1 = mReader.RF_READ(SDConsts.RFMemType.EPC, 2, 6, "00000000", false);

        if (result1 == SDConsts.RFResult.SUCCESS) {
            //successepc = 1;
            // successtid = 0;

            message.append("RF_READ");
            donetags.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bat_start));
            //successtid = 0;
        }
        else
            message.append("RF_READ failed " + result1);
        // }

        // }
        //}
        // else if(successtid == 0 && successepc == 1) {
        // Toast.makeText(mContext, "برجاء اعادة العملية السابقه", Toast.LENGTH_SHORT).show();
        return;
        // }
    }

   public void changemode(){

        int ret = -100;
        int mode;
        ret = mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
        final MediaPlayer beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        beepsound.start();
        beepsound.setOnCompletionListener(MediaPlayer::release);

    }

    public void changemoderfid(){

        int ret = -100;
        int mode;
        ret = mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
        final MediaPlayer beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        beepsound.start();
        beepsound.setOnCompletionListener(MediaPlayer::release);

    }
    private boolean createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(5).build();
        if (mSoundPool != null)
            return true;
        return false;
    }
    private boolean createOldSoundPool(){
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (mSoundPool != null)
            return true;
        return false;
    }
    private void createSoundPool() {
        boolean b = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            b = createNewSoundPool();
        else
            b = createOldSoundPool();
        if (b) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
            float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mSoundVolume = actVolume / maxVolume;
            SoundLoadListener();
        }
    }
    private void SoundLoadListener() {
        if (mSoundPool != null) {
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    // TODO Auto-generated method stub
                    mSoundFileLoadState = true;
                }
            });
            mSoundId = mSoundPool.load(mContext, R.raw.beep, 1);
        }
    }
    private class SoundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (mLocate)
                mTagLocateProgress.setProgress(mLocateValue);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (mSoundPlay) {
                try {
                    if (mSoundFileLoadState) {
                        if (mSoundPool != null) {
                            mSoundPool.play(mSoundId, mSoundVolume, mSoundVolume, 0, 0, (48000.0f / 44100.0f));
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (java.lang.NullPointerException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    };


    public void handleMessage(Message m) throws IOException {
        if (D) Log.d(TAG, "mAccessHandler m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        int result = m.arg2;
        String data = "";
        if (m.obj != null  && m.obj instanceof String)
            data = (String)m.obj;
        String messageStr = null;


        switch (m.what) {
            case SDConsts.Msg.RFMsg:
                switch (m.arg1) {
                    //RF_Read callback message
                    case SDConsts.RFCmdMsg.READ:
                        //messageStr = "READ result = " + result + "\n" + data;
                        //old = donetags.getText().toString();
                        //oldtwo = donetagstwo.getText().toString();
                        //if(data == ""){
                        //Toast.makeText(mContext, "برجاء وضع التاج امام الجهاز", Toast.LENGTH_SHORT).show();
                        //successtid = 1;
                        //successepc = 0;
                        //return;
                        //}
                        //else {
                        //if (tidread == 0) {
                        donetags.setText(data + "\n" + old);

                        //int mynum = Integer.parseInt(data.toString());
                        //saveToTxtFileEPC(barcode.getText().toString().trim());
                        //String newdata = Integer.toString(mynum);
                        //donetagstwo.setText(barcode.getText().toString().trim()+","+"1"+"\n"+oldtwo);
                        //savetoCSVEPC(barcode.getText().toString().trim());
                        final MediaPlayer beepsound = MediaPlayer.create(getActivity(), R.raw.beeptwo);
                        beepsound.start();
                        beepsound.release();
                        if (data != "" && successtid == 1){
                            //tidread = 1;
                            TIDFull = data;
                            TIDdata = data.substring(12);
                            if(successepc == 1)
                            {successtid = 0;
                                successepc = 0;
                                readepc();

                            }
                        }
                        else{
                            changemode();
}
                        //} else if (tidread == 1) {
                        //donetags.setText(" " + data + old);
                        //final MediaPlayer beepsound = MediaPlayer.create(getActivity(), R.raw.beeptwo);
                        //beepsound.start();
                        //tidread = 0;
                        //EPCdata = data;
                        //if(EPCdata !="" && TIDdata != ""){
                        //savetoCSV(EPCdata,TIDdata);
                        //writeToFile(EPCdata,TIDdata);
                        //saveToTxtFile(EPCdata,TIDdata);


                        //int count = Integer.parseInt(counttext.getText().toString());
                        //count = count+1;
                        //}
                        //}

                        // }

                        break;
                    case SDConsts.RFCmdMsg.WRITE:
                        //messageStr = "WRITE result = " + result + " " + data;
                        readepc();
                        final MediaPlayer beepsound1 = MediaPlayer.create(getActivity(), R.raw.beepthree);
                        beepsound1.start();
                        beepsound1.release();
                        //SavetoWeb(TIDFull,barcode.getText().toString().trim());
                        break;
                    case SDConsts.RFCmdMsg.WRITE_TAG_ID:
                        messageStr = "WRITE_TAG_ID result = " + result + " " + data;

                        break;
                    case SDConsts.RFCmdMsg.WRITE_ACCESS_PASSWORD:
                        messageStr = "WRITE_ACCESS_PASSWORD result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.WRITE_KILL_PASSWORD:
                        messageStr = "WRITE_KILL_PASSWORD result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.LOCK:
                        messageStr = "LOCK result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.KILL:
                        messageStr = "KILL result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_WRITE:
                        messageStr = "BLOCK_WRITE result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_PERMALOCK:
                        messageStr = "BLOCK_PERMALOCK result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_ERASE:
                        messageStr = "BLOCK_ERASE result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW_START:
                        messageStr = "UPDATE_RF_FW_START " + result + " " + data;
                        if (result == SDConsts.RFResult.SUCCESS) {
                            Activity activity = getActivity();
                        }
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW:
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW_END:
                        messageStr = "UPDATE_RF_FW_END " + result + " " + data;
                        break;
                }
                break;
            case SDConsts.Msg.SDMsg:
                if (m.arg1 == SDConsts.SDCmdMsg.TRIGGER_PRESSED) {
                    //writeepc();
                    if (barcode.getText().toString().trim().length() <= 0){
                        Toast.makeText(mContext, "برجاء قراءة باركود اولاً", Toast.LENGTH_SHORT).show();
                        changemode();
                        return;
                    }

                }

                if (m.arg1 == SDConsts.SDCmdMsg.SLED_MODE_CHANGED){
                    int mode;
                    mode = mReader.SD_GetTriggerMode();
                    if (mode == SDConsts.SDTriggerMode.BARCODE){
                        modetext.setText("قراءه");
                    }
                    else if (mode == SDConsts.SDTriggerMode.RFID){
                        modetext.setText("كتابه");
                    }
                }



                if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                    if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                    else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(mFragment).attach(mFragment).commit();
                }
                break;

            case SDConsts.Msg.BCMsg:
                StringBuilder readData = new StringBuilder();
                if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_TRIGGER_PRESSED){
                    barcode.setText("");
                }

                else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_TRIGGER_RELEASED){

                }

                else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_READ) {
                    if (D) Log.d(TAG, "BC_MSG_BARCODE_READ");
                    if (m.arg2 == SDConsts.BCResult.SUCCESS)
                        readData.append(" " + "BC_MSG_BARCODE_READ");
                    else if (m.arg2 == SDConsts.BCResult.ACCESS_TIMEOUT)
                        readData.append(" " + "BC_MSG_BARCODE_ACCESS_TIMEOUT");
                    if (m.obj != null && m.obj instanceof String) {
                        readData.append("\n" + (String)m.obj);

                    }

                }
                else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_ERROR) {
                    if (D) Log.d(TAG, "BARCODE_ERROR");
                    if (m.arg2 == SDConsts.BCResult.LOW_BATTERY)
                        readData.append(" " + "BARCODE_ERROR Low battery");
                    else
                        readData.append(" " + "BARCODE_ERROR ");
                    readData.append("barcode pasue");
                }
                if (D) Log.d(TAG, "data = " + readData.toString());
                break;

            //case SDConsts.Msg.BTMsg:
            //if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
            // if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);
            //if (mOptionHandler != null)
            // mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_DISCONNECTED).sendToTarget();
            // }
            //  break;
        }
        if (messageStr != null)
            Toast.makeText(mContext, messageStr, Toast.LENGTH_SHORT).show();
    }
    private static class BatteryHandler extends Handler {
        private final WeakReference<RFAccessFragment> mExecutor;
        public BatteryHandler(RFAccessFragment f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            RFAccessFragment executor = mExecutor.get();
            if (executor != null) {
                try {
                    executor.handleMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }












}
    private class itemModel
    {
        private String EBC;
        private int tblRFidTransHeader;




        public int gettblRFidTransHeader() {
            return tblRFidTransHeader;
        }

        public void settblRFidTransHeader(int tblRFidTransHeader) {
            this.tblRFidTransHeader = tblRFidTransHeader;
        }

        public itemModel(String EBC, int tblRFidTransHeader)
        {
            this.EBC=EBC;
            this.tblRFidTransHeader= tblRFidTransHeader;
        }
        public String getEBC() {
            return EBC;
        }

        public void setEBC (String EBC) {
            this.EBC = EBC;
        }
    }
}