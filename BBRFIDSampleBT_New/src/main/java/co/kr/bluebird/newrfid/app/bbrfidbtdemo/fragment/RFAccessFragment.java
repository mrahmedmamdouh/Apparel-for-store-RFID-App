/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 *
 * http://www.bluebirdcorp.com/
 *
 * Author : Bogon Jun
 *
 * Date : 2016.01.18
 */

package co.kr.bluebird.newrfid.app.bbrfidbtdemo.fragment;

import co.kr.bluebird.newrfid.app.bbrfidbtdemo.Constants;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.fileutil.FileSelectorDialog;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.MainActivity;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.permission.PermissionHelper;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.R;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.kr.bluebird.newrfid.app.bbrfidbtdemo.WDxDBHelper;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.Globals;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.BuisnessLogic;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.*;


public class RFAccessFragment extends Fragment {

    private static final String TAG = RFAccessFragment.class.getSimpleName();
    private static final String mLogFileName = "/DATA-";
    private static final String mExtentionName = ".txt";
    private static final boolean D = Constants.ACC_D;

    private static final String FILE_DIR_PRE = "//DIR//";

    private static final String FILE_EXT = ".bin";

    private static final int PROGRESS_DIALOG = 1;

    private static final String FILE_NAME = "example.csv";

    private ListView mRfidList;

    private String mCurrentStatus;
    private boolean mIsRegisterReceiver;

    private static final String STATUS_CLOSE = "STATUS_CLOSE";
    private static final String STATUS_OPEN = "STATUS_OPEN";
    private static final String STATUS_TRIGGER_ON = "STATUS_TRIGGER_ON";

    private static final int SEQ_BARCODE_OPEN = 100;
    private static final int SEQ_BARCODE_CLOSE = 200;

    private BTReader mReader;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    Globals g;
    BuisnessLogic BL;
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
    Integer taskdone = 0;
    String EPCdata = "";
    String TIDdata = "";
    String currentdate ="";
    private AccessHandler mAccessHandler = new AccessHandler(this);
    String old = "";
    String oldtwo = "";
    EditText barcode, donetags, donetagstwo, boxnum, epc, tid;
    Button save,clear,changebc, changerf,plus,minus;
    String mText;
    String mText2;
    TextView modetext,counttext;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    public static RFAccessFragment newInstance() {
        return new RFAccessFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.rf_access_frag, container,false);
        mContext = inflater.getContext();

        mFragment = this;

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        String url = "http://41.33.161.148:366/api/tid";

        modetext = (TextView) v.findViewById(R.id.modetext);
        counttext = (TextView) v.findViewById(R.id.counter);
        mRfidList = (ListView)v.findViewById(R.id.access_list);
        barcode = (EditText) v.findViewById(R.id.ed_barcode);
        barcode.requestFocus();
        boxnum = (EditText) v.findViewById(R.id.ed_boxnum);
        epc = (EditText) v.findViewById(R.id.ed_epc);
        tid = (EditText) v.findViewById(R.id.ed_tid);
        mRfidList.setOnItemClickListener(listListener);
        donetags = (EditText) v.findViewById(R.id.ed_donetags);
        //donetagstwo = (EditText) v.findViewById(R.id.ed_donetagstwo);
        save = (Button) v.findViewById(R.id.bt_save);
        clear = (Button) v.findViewById(R.id.bt_clear);
        plus = (Button) v.findViewById(R.id.bt_plus);
        minus = (Button) v.findViewById(R.id.bt_minus);
        // changebc = (Button) v.findViewById(R.id.bt_changebc);
        //  changerf = (Button) v.findViewById(R.id.bt_changerf);
        mItemChar = ArrayAdapter.createFromResource(mContext, R.array.items_array,
                android.R.layout.simple_list_item_1);
        mRfidList.setAdapter(mItemChar);

        barcode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0)
                changemoderfid();
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
                //mText = donetags.getText().toString();
                //mText2 = donetagstwo.getText().toString();
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //== PackageManager.PERMISSION_GRANTED) {
                //String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                //}
                //else {
                date = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(System.currentTimeMillis());
                counttext.setText("0");
                //}
                //}
                //else {


                //}


            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(boxnum.getText().toString().trim());
                int plusnum = num + 1;
                boxnum.setText(Integer.toString(plusnum));

            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(boxnum.getText().toString().trim());
                int plusnum = num - 1;
                boxnum.setText(Integer.toString(plusnum));

            }
        });
        //changebc.setOnClickListener(new View.OnClickListener() {
        //                              @Override
        //                            public void onClick(View v) {

        //                              changemode();
        //                        }
        //                  }
        //);
        //changerf.setOnClickListener(new View.OnClickListener() {
        //                              @Override
        //                            public void onClick(View v) {
        //                              int ret = -100;
        //                            int mode;
        //                          ret = mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
        //                        final MediaPlayer beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        //                      beepsound.start();
        //                }
        //          }
        //);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String str = donetags.getText().toString();
                //String substr = str.substring(53);
                //old = donetags.getText().toString();
                //donetags.setText("");
                //counttext.setText("0");

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
                        params.put("TID","3123");
                        params.put("Barcode","66666666");
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        return v;
    }


    private void writeToFile(String EPC, String TID){

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("DataFile.txt",mContext.MODE_APPEND));
            outputStreamWriter.write(EPC +","+ TID);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void savetoCSV(String EPC, String TID) throws IOException {

        String storage = Environment.getExternalStorageDirectory().getPath();

        String csv = storage + "/MyCSV.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv, true));

        String [] record = (EPC +","+TID+"\n").split(",");

        writer.writeNext(record,false);
        //List<String[]> data = new ArrayList<String[]>();
        //data.add(new String[]{EPC,TID});
        //writer.writeAll(data);
        writer.close();
    }
    private void savetoCSVEPC(String EPC) throws IOException {

        String storage = Environment.getExternalStorageDirectory().getPath();

        String csv = storage + "/EPCMyCSV.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv, true));

        String [] record = (EPC +","+"1"+"\r\n").split(",");

        writer.writeNext(record,false);
        //List<String[]> data = new ArrayList<String[]>();
        //data.add(new String[]{EPC,TID});
        //writer.writeAll(data);
        writer.close();
    }

    private void saveToTxtFile(String EPC, String TID) {

        try {
            boolean b = PermissionHelper.checkPermission(mContext, PermissionHelper.mStoragePerms[0]);
            if (!b) {
                PermissionHelper.requestPermission(getActivity(), PermissionHelper.mStoragePerms);
            } else {
                String storage = Environment.getExternalStorageDirectory().getPath();
                File dir = new File(storage + "/My Files/");
                dir.mkdirs();
                //dir.setExecutable(true, false);
                //dir.setReadable(true, false);
                //dir.setWritable(true, false);

                date = timeStamp;
                String fileName = "TID_" + date;
                String box = boxnum.getText().toString().trim();
                File file = new File(dir,fileName +"_"+ box +"_"+ mExtentionName);

                //file.setExecutable(true, false);
                //file.setReadable(true, false);
                //file.setWritable(true, false);
                FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(EPC + "," + TID + "\r\n");

                bw.close();
                Toast.makeText(mContext, "saved!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void saveToTxtFileEPC(String EPC) {

        try{
            boolean b = PermissionHelper.checkPermission(mContext, PermissionHelper.mStoragePerms[0]);
            if (!b) {
                PermissionHelper.requestPermission(getActivity(), PermissionHelper.mStoragePerms);
            } else {

                String storage = Environment.getExternalStorageDirectory().getPath();
                File dir = new File(storage + "/My Files/");

                dir.mkdirs();


                //dir.setExecutable(true, false);
                //dir.setReadable(true, false);
                //dir.setWritable(true, false);

                date = timeStamp;
                String fileName = "BARCODE_" + date;

                File file = new File(dir,fileName +"_"+ boxnum.getText().toString().trim() +"_"+ mExtentionName);
                FileWriter fw = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(EPC + "," + "1" + "\r\n");

                bw.close();
                Toast.makeText(mContext, "saved!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                        createAlertDialog(getString(R.string.warning_str), getString(R.string.update_warning_rfid_str));
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
        mReader = BTReader.getReader(mContext, mAccessHandler);
        changemode();
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());

        mReader.RF_SetRadioPowerState(10);
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
        closeDialog();

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
                        if (hasResult) {
                            if (grantResults != null && grantResults.length != 0 &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                                createAlertDialog(getString(R.string.warning_str), getString(R.string.update_warning_rfid_str));
                        }
                    }
                    break;
            }
        }
    }

    private static class AccessHandler extends Handler {
        private final WeakReference<RFAccessFragment> mExecutor;
        public AccessHandler(RFAccessFragment f) {
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

    public void writeepc(){

        if (barcode.getText().toString().trim().length() <= 0){
            Toast.makeText(mContext, "برجاء قراءة باركود اولاً", Toast.LENGTH_SHORT).show();
            changemode();
            return;
        }
        else if(barcode.getText().toString().trim().length() > 0){
            int result0;
            String str = barcode.getText().toString();
            StringBuilder sb = new StringBuilder();
            StringBuilder message = new StringBuilder();
            for (int toPrepend=12-str.length(); toPrepend>0; toPrepend--) {
                sb.append('0');
            }

            sb.append(str);
            //String res = sb.toString();
            String res = TIDdata+sb;
            //result = mReader.RF_WriteTagID(2, res, "00000000", false);
            result0 = mReader.RF_WRITE(SDConsts.RFMemType.EPC, 2, res, "00000000",  false);
            if (result0 == SDConsts.RFResult.SUCCESS){
                message.append("RF_WRITE");
                //successtid = 0;
                successepc = 1;
                }

            else{
                message.append("RF_WRITE failed " + result0);
            Toast.makeText(mContext, "برجاء وضع التاج امام الجهاز", Toast.LENGTH_SHORT).show();
                //successtid = 1;
            return;}
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

        // if (result1 == SDConsts.RFResult.SUCCESS) {
        //successepc = 1;
        // successtid = 0;
        //
        message.append("RF_READ");
        donetags.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bat_start));
        //successepc = 1;

        // } else {
        message.append("RF_READ failed " + result1);
        // }

           // }
        //}
       // else if(successtid == 0 && successepc == 1) {
           // Toast.makeText(mContext, "برجاء اعادة العملية السابقه", Toast.LENGTH_SHORT).show();
          //  return;
       // }
    }


    public void readtid() {
        //if (barcode.getText().toString().trim().length() <= 0) {

          //  return;
        //} else if (barcode.getText().toString().trim().length() > 0) {

            int result2;
            StringBuilder message = new StringBuilder();
           // if (successtid == 0 && successepc == 1) {
                result2 = mReader.RF_READ(SDConsts.RFMemType.TID, 0, 6, "00000000", false);
                if (result2 == SDConsts.RFResult.SUCCESS) {
                  //  successepc = 0;
                   // successtid = 1;
                   // donetags.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bat_start));
                    message.append("RF_READ");
                    donetags.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bat_end));
                    successtid = 1;
                } else {
                    message.append("RF_READ failed " + result2);
                    //successtid = 0;
                }
            //} else if(successepc == 0 && successtid == 1) {
              //  successtid = 1;
             //   successepc = 0;
             //   readepc();
            //}
       // }
    }
    public void changemode(){

        int ret = -100;
        int mode;
        ret = mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
        final MediaPlayer beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        beepsound.start();


    }
    public void changemoderfid(){

        int ret = -100;
        int mode;
        ret = mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
        final MediaPlayer beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        beepsound.start();


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
                                if (data != "" && successtid == 1){
                                    //tidread = 1;
                                    TIDdata = data.substring(12);

                                    if(successepc == 1)
                                    {successtid = 0;
                                    successepc = 0;
                                        readepc();

                                    }
                                    //successepc = 0;}
                                }
                                else{
                                    changemode();
                                    int cnt = Integer.parseInt(counttext.getText().toString().trim());
                                    int pluscnt = cnt + 1;
                                    counttext.setText(Integer.toString(pluscnt));
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
                            if (activity != null)
                                createDialog(PROGRESS_DIALOG, activity.getString(R.string.update_rf_firm_str));
                        }
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW:
                        setProgressState(result);
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW_END:
                        closeDialog();
                        messageStr = "UPDATE_RF_FW_END " + result + " " + data;
                        break;
                }
                break;
            case SDConsts.Msg.SDMsg:
                if (m.arg1 == SDConsts.SDCmdMsg.TRIGGER_PRESSED) {
                    //writeepc();
                    //if (successtid == 0){
                        readtid();
                        //;}
                    //if (successtid == 1){writeepc();}

                }
                if (m.arg1 == SDConsts.SDCmdMsg.TRIGGER_RELEASED) {
                    if(successtid == 1){writeepc();}

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

    private void createDialog(int type, String message) {
        if (mDialog != null) {
            if (mDialog.isShowing())
                mDialog.cancel();
            mDialog = null;
        }
        mDialog = new ProgressDialog(mContext);
        mDialog.setCancelable(false);

        mDialog.setTitle(message);
        if (type == PROGRESS_DIALOG){
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        mDialog.show();
    }

    private void setProgressState(int percent) {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.setProgress(percent);
            }
        }
    }

    private void closeDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing())
                mDialog.cancel();
            mDialog = null;
        }
    }

    private void createAlertDialog(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                selectFile();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_str), null);
        builder.show();
    }

    private void selectFile() {
        File path = new File(Environment.getExternalStorageDirectory() + FILE_DIR_PRE);
        mFileSelecter = new FileSelectorDialog(mContext, path, FILE_EXT);
        mFileSelecter.addFileListener(new FileSelectorDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                boolean b = PermissionHelper.checkPermission(mContext, PermissionHelper.mStoragePerms[0]);
                if (b) {

                    int ret = mReader.RF_UpdateRFIDFirmware(file.toString());
                    Toast.makeText(mContext, getString(R.string.update_rf_firm_str) + "result = " +
                            ret, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, getString(R.string.update_firm_str) +
                            " failed because of permission",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mFileSelecter.showDialog();
    }
}