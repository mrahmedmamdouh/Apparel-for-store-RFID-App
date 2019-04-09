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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.MainActivity;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.control.TagListAdapter;
import co.kr.bluebird.rfid.app.bbrfiddemo.fetchData;
import co.kr.bluebird.rfid.app.bbrfiddemo.fileutil.FileManager;
import co.kr.bluebird.rfid.app.bbrfiddemo.listAdapter;
import co.kr.bluebird.rfid.app.bbrfiddemo.permission.PermissionHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.repl_elements;
import co.kr.bluebird.rfid.app.bbrfiddemo.stopwatch.StopwatchService;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import co.kr.bluebird.sled.SelectionCriterias;

public class BRReplFragment extends Fragment implements fetchData.download_complete {

    private static final String TAG = BRReplFragment.class.getSimpleName();

    private static final boolean D = Constants.INV_D;

    private StopwatchService mStopwatchSvc;

    private TagListAdapter mAdapter;
    public String transactionId;

    int x;
    private ListView mRfidList;
    String date;
    private TextView mBatteryText;
    String timeStamp;
    private TextView mTimerText;
    public EditText boxnum;
    private TextView mCountText;

    private TextView mSpeedCountText;

    private TextView mAvrSpeedCountTest;

    private Button mClearButton, donebt;

    private Button mInvenButton;

    private Button mStopInvenButton;
    Button plus,minus, newfile;
    private Switch mTurboSwitch;

    private Switch mRssiSwitch;

    private Switch mFilterSwitch;

    private Switch mSoundSwitch;

    private Switch mMaskSwitch;

    private Switch mToggleSwitch;

    private Switch mPCSwitch;

    private Switch mFileSwitch;

    private ProgressBar mProgressBar;

    private Reader mReader;

    private Context mContext;

    private boolean mTagFilter = false;

    private boolean mSoundPlay = true;

    private boolean mMask = false;

    private boolean mInventory = false;

    private boolean mIsTurbo = true;

    private boolean mToggle = false;

    private boolean mIgnorePC = false;

    private boolean mRssi = false;

    private boolean mFile = false;

    private Handler mOptionHandler;

    private SoundPool mSoundPool;
    private RFAccessFragment mRFAccessFragment;
    private Fragment mCurrentFragment;
    private FragmentManager mFragmentManager;


    public String  listitem;


    private int mSoundId;

    private float mSoundVolume;

    private boolean mSoundFileLoadState;

    private SoundTask mSoundTask;

    private double mOldTotalCount = 0;

    private double mOldSec = 0;

    private FileManager mFileManager;

    public String mLocateTag;

    private String mLocateEPC;

    public int mLocateStartPos;

    private LinearLayout mLocateLayout;

    private LinearLayout mListLayout;

    private ProgressBar mTagLocateProgress;

    private int mLocateValue;

    private ImageButton mBackButton;

    private TextView mLocateTv;

    private boolean mLocate;

    private TimerTask mLocateTimerTask;

    private Timer mClearLocateTimer;

    private Spinner mSessionSpin;
public String idi;

    private Spinner mSelFlagSpin;
    public static ListView  list;
    public ArrayList<repl_elements> elements = new ArrayList<>();
    public List <String> itemTags = new ArrayList <String>();
    public ArrayList<String> jr= new ArrayList <>();
    public listAdapter adapter;

    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    //private int mCurrentPower;

    private int mTickCount = 0;
    private String urlNumber,portNumber;
    private ProgressBar mBatteryProgress;



    private InventoryHandler mInventoryHandler = new InventoryHandler(this);

    public static BRReplFragment newInstance() {
        return new BRReplFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_repl, container, false);
        mContext = inflater.getContext();
        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;
        list = (ListView ) v.findViewById(R.id.tableLayout);
        mBatteryProgress = (ProgressBar) v.findViewById(R.id.batt_progress);

        adapter = new listAdapter(this, mContext);
        SharedPreferences pref1 = getActivity().getSharedPreferences("urlNumber", Context.MODE_PRIVATE);
        urlNumber = pref1.getString("urlNumber", "DEFAULT");

        SharedPreferences pref2 = getActivity().getSharedPreferences("portNumber", Context.MODE_PRIVATE);
        portNumber = pref2.getString("portNumber", "DEFAULT");
        mFragmentManager = getFragmentManager();


        fetchData download_data = new fetchData(this);
        download_data.download_data_from_link("http://"+ urlNumber+":"+ portNumber+"/api/Data/Replan");

        Log.d(TAG, "onCreateView: " + urlNumber + portNumber);
        mLocateLayout = (LinearLayout)v.findViewById(R.id.tag_locate_container);

        mListLayout = (LinearLayout)v.findViewById(R.id.locate_container);

        mLocateTv = (TextView)v.findViewById(R.id.tag_locate_text);

        mTagLocateProgress = (ProgressBar)v.findViewById(R.id.tag_locate_progress);

        mBackButton = (ImageButton)v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(sledListener);


        mClearButton = (Button)v.findViewById(R.id.clear_button);
        mClearButton.setOnClickListener(clearButtonListener);

        mInvenButton = (Button)v.findViewById(R.id.inven_button);
        mInvenButton.setOnClickListener(sledListener);

        mStopInvenButton = (Button)v.findViewById(R.id.stop_inven_button);
        mStopInvenButton.setOnClickListener(sledListener);
        list.setAdapter(adapter);
        list.setOnItemClickListener(listItemClickListener);

        return v;
    }



    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {


            repl_elements i = (repl_elements) list.getItemAtPosition(position);

            mLocateTag= i.items;

            SharedPreferences pref = getActivity().getSharedPreferences("transactionId", Context.MODE_PRIVATE);
            transactionId = "1";
            AsyncHttpClient client = new AsyncHttpClient();
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("id", transactionId);
            param.put("sub_items", i.it_id);
            Log.d(TAG, "onItemClick: " + param);
            RequestParams params = new RequestParams(param);


            client.post("http://" + urlNumber + ":" + portNumber+ "/api/Data/AllTag", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String data) {
                    fetchData download_data1 = new fetchData(BRReplFragment.this);
                    download_data1.download_data_from_link("http://" + urlNumber + ":" + portNumber + "/api/Data/AllTag");
                    try {
                        JSONArray data_array = new JSONArray(data);

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject obj = data_array.getJSONObject(i);
                            jr.add(obj.getString("items"));
                            Log.d(TAG, "itemtags: " + jr);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } }
            });

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle(getString(R.string.locating_str));
            alert.setMessage(getString(R.string.want_tracking_str));
            alert.setPositiveButton(getString(R.string.yes_str), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    SelectionCriterias s = new SelectionCriterias();
                    s.makeCriteria(SelectionCriterias.SCMemType.EPC, mLocateTag,
                            mLocateStartPos, mLocateTag.length() * 4,
                            SelectionCriterias.SCActionType.ASLINVA_DSLINVB);
                    mReader.RF_SetSelection(s);
                    switchLayout(false);
                    mLocateTv.setText(mLocateTag);
                    //enableControl(false);

                    }

            });
            alert.setNegativeButton(getString(R.string.no_str) ,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }
    };

    private void switchLayout(boolean showList) {
        mLocate = !showList;
//        if (mLocate)
//            mReader.RF_SetRssiTrackingState(SDConsts.RFRssi.ON);
        if (showList) {
            mListLayout.setVisibility(View.VISIBLE);
            mLocateLayout.setVisibility(View.GONE);
            mInvenButton.setText(R.string.inven_str);
            mStopInvenButton.setText(R.string.stop_inven_str);
        }
        else {
            mTagLocateProgress.setProgress(0);
            mListLayout.setVisibility(View.GONE);
            mLocateLayout.setVisibility(View.VISIBLE);
            mInvenButton.setText(R.string.track_str);
            mStopInvenButton.setText(R.string.stop_track_str);
        }
    }

    private void createSoundPool() {
        boolean b = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            b = createNewSoundPool();
        else
            b = createOldSoundPool();
        if (b) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mSoundVolume = actVolume / maxVolume;
            SoundLoadListener();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(5).build();
        if (mSoundPool != null)
            return true;
        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean createOldSoundPool(){
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (mSoundPool != null)
            return true;
        return false;
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



    @Override
    public void get_data(String data) {

        try {
            JSONArray data_array = new JSONArray(data);

            for (int i = 0; i < data_array.length(); i++) {
                JSONObject obj = data_array.getJSONObject(i);
                repl_elements add = new repl_elements();
                add.it_id = obj.getString("it_id");
                add.style = obj.getString("style");
                add.color = obj.getString("color");
                add.size = obj.getString("size");
                add._id = obj.getString("_id");
                add.items = obj.getString("items");


                elements.add(add);
                Log.d(TAG, "get_data: " + elements);

            }

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public Object getSystemService(Object layoutInflaterService) {
        return null;
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
                } catch (NullPointerException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    };

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mSoundFileLoadState = false;

        createSoundPool();

        mOldTotalCount = 0;

        mOldSec = 0;

        mReader = Reader.getReader(mContext, mInventoryHandler);
        if (mReader != null && mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {
            enableControl(true);

        }
        else
            enableControl(false);

        mLocate = false;

        mInventory = false;
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        addCheckListener();

        super.onStart();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onResume");
        elements = new ArrayList<>();
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onPause");
        elements = new ArrayList<>();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (D) Log.d(TAG, "onStop");
        mReader.RF_StopInventory();
        mInventory = false;
        if (mSoundPool != null)
            mSoundPool.release();
        mSoundFileLoadState = false;
        if (mFileManager != null) {
            mFileManager.closeFile();
            mFileManager = null;
        }


        super.onStop();
    }

    private View.OnClickListener clearButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (D) Log.d(TAG, "clearButtonListener");
            clearAll();
        }
    };

    private void clearAll() {
        if (!mInventory) {
            adapter.removeAllItem();



            stopStopwatch();

            mOldTotalCount = 0;

            mOldSec = 0;



            Activity activity = getActivity();
        }
    }


    public View.OnClickListener sledListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (D) Log.d(TAG, "stopwatchListener");

            int id = v.getId();
            int ret = 0;
            switch (id) {
                case R.id.back_button:
                    ret = mReader.RF_StopInventory();
                    if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                        mInventory = false;
                        enableControl(!mInventory);
                        pauseStopwatch();
                    } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                        Toast.makeText(mContext, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

                    switchLayout(true);
                    mLocateTv.setText("");
                    mLocateTag = null;
                    clearAll();
                    break;

                case R.id.inven_button:
                    if (!mInventory) {
                        clearAll();
                        openFile();
                        //openFileEPC();
                        if (mLocate) {
                            //mCurrentPower = mReader.RF_GetRadioPowerState();
                            Iterator<String> iterator = jr.iterator();
                            while (iterator.hasNext()) {
                                ret = mReader.RF_PerformInventoryForLocating(iterator.next());
                              int data = 0;
                                if (mLocateValue == data){

                                    iterator.next();
                                }

                            }
                        } else
                            ret = mReader.RF_PerformInventoryWithLocating(mIsTurbo, mMask, mIgnorePC);

                        if (ret == SDConsts.RFResult.SUCCESS) {
                            startStopwatch();
                            mInventory = true;
                            enableControl(!mInventory);
                        } else if (ret == SDConsts.RFResult.MODE_ERROR)
                            Toast.makeText(mContext, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                        else if (ret == SDConsts.RFResult.LOW_BATTERY)
                            Toast.makeText(mContext, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                        else
                        if (D) Log.d(TAG, "Start Inventory failed");
                    }
                    break;

                case R.id.stop_inven_button:
                    ret = mReader.RF_StopInventory();
                    if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                        mInventory = false;
                        enableControl(!mInventory);
                        pauseStopwatch();
                    } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                        Toast.makeText(mContext, "Stop Inventory failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void openFile() {
        if ( !mInventory && !mLocate) {
            mFile = true;

            if (mFileManager == null)
                mFileManager = new FileManager(mContext);
        }
    }
    private void openFileEPC() {
        //if (mFile && !mInventory && !mLocate) {
        if ( !mInventory && !mLocate) {
            mFile = true;

            if (mFileManager == null)
                mFileManager = new FileManager(mContext);
        }
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
                                mFile = true;
                            else
                                mFile = false;
                        }
                    }
                    break;
            }
            mFileSwitch.setChecked(mFile);
        }
    }

    private CompoundButton.OnCheckedChangeListener sledcheckListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            int id = buttonView.getId();
            switch (id) {
                case R.id.turbo_switch:
                    if (isChecked)
                        mIsTurbo = true;
                    else
                        mIsTurbo = false;
                    break;

                case R.id.file_switch:
                    if (isChecked) {
                        boolean b = PermissionHelper.checkPermission(mContext,PermissionHelper.mStoragePerms[0]);
                        if (!b)
                            PermissionHelper.requestPermission(getActivity(), PermissionHelper.mStoragePerms);
                        else
                            mFile = true;
                    }
                    else {
                        mFile = false;
                        if (mFileManager != null) {
                            mFileManager.closeFile();
                            mFileManager = null;
                        }
                    }
                    break;

                case R.id.rssi_switch:
                    if (isChecked) {
                        if (mReader.RF_SetRssiTrackingState(SDConsts.RFRssi.ON) == SDConsts.RFResult.SUCCESS)
                            mRssi = true;
                    }
                    else {
                        if (mReader.RF_SetRssiTrackingState(SDConsts.RFRssi.OFF) == SDConsts.RFResult.SUCCESS)
                            mRssi = false;
                    }
                    break;

                case R.id.filter_switch:
                    clearAll();
                    if (isChecked)
                        mTagFilter = true;
                    else
                        mTagFilter = false;
                    break;

                case R.id.sound_switch:
                    if (isChecked)
                        mSoundPlay = true;
                    else
                        mSoundPlay = false;
                    break;

                case R.id.mask_switch:
                    if (isChecked)
                        mMask = true;
                    else
                        mMask = false;
                    break;

                case R.id.toggle_switch:
                    if (isChecked) {
                        if (mReader.RF_SetToggle(SDConsts.RFToggle.ON) == SDConsts.RFResult.SUCCESS)
                            mToggle = true;
                    }
                    else {
                        if (mReader.RF_SetToggle(SDConsts.RFToggle.OFF) == SDConsts.RFResult.SUCCESS)
                            mToggle = false;
                    }
                    break;

                case R.id.pc_switch:
                    if (isChecked)
                        mIgnorePC = true;
                    else
                        mIgnorePC = false;
                    break;
            }
        }
    };

    private void startStopwatch() {
        if (D) Log.d(TAG, "startStopwatch");

        if (mStopwatchSvc != null && !mStopwatchSvc.isRunning())
            mStopwatchSvc.start();

    }

    private void pauseStopwatch() {
        if (D) Log.d(TAG, "pauseStopwatch");

        if (mStopwatchSvc != null && mStopwatchSvc.isRunning())
            mStopwatchSvc.pause();



    }

    private void stopStopwatch() {
        if (D) Log.d(TAG, "stopStopwatch");

        if (mStopwatchSvc != null && mStopwatchSvc.isRunning())
            mStopwatchSvc.pause();

        if (mStopwatchSvc != null)
            mStopwatchSvc.reset();




    }




    private void enableControl(boolean b) {
        if (b)
            list.setOnItemClickListener(listItemClickListener);
        else
            list.setOnItemClickListener(listItemClickListener);

    }




    private static class InventoryHandler extends Handler {
        private final WeakReference<BRReplFragment> mExecutor;
        public InventoryHandler(BRReplFragment f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            BRReplFragment executor = mExecutor.get();
            if (executor != null) {
                executor.handleInventoryHandler(msg);
            }
        }
    }

    public void handleInventoryHandler(Message m) {
        if (D) Log.d(TAG, "mInventoryHandler");
        if (D) Log.d(TAG, "m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        switch (m.what) {
            case SDConsts.Msg.SDMsg:
                switch(m.arg1) {
                    case SDConsts.SDCmdMsg.TRIGGER_PRESSED:
                        if (!mInventory) {
                            clearAll();
                            openFile();
                            //openFileEPC();
                            //+++NTNS
                            int ret;
                            if (mLocate) {
                                //mCurrentPower = mReader.RF_GetRadioPowerState();
                                ret = mReader.RF_PerformInventoryForLocating(mLocateEPC);
                            } else
                                ret = mReader.RF_PerformInventory(mIsTurbo, mMask, mIgnorePC);
//                    int ret = mReader.RF_PerformInventoryCustom(SDConsts.RFMemType.USER, 0, 10, "00000000", false);
                            //---NTNS
                            if (ret == SDConsts.RFResult.SUCCESS) {
                                startStopwatch();
                                mInventory = true;
                                enableControl(!mInventory);
                            } else if (ret == SDConsts.RFResult.MODE_ERROR)
                                Toast.makeText(mContext, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                            else if (ret == SDConsts.RFResult.LOW_BATTERY)
                                Toast.makeText(mContext, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                            else
                            if (D) Log.d(TAG, "Start Inventory failed");
                        }
                        break;

                    case SDConsts.SDCmdMsg.SLED_INVENTORY_STATE_CHANGED:
                        mInventory = false;
                        enableControl(!mInventory);
                        pauseStopwatch();
                        // In case of low battery on inventory, reason value is LOW_BATTERY
                        Toast.makeText(mContext, "Inventory Stopped reason : " + m.arg2, Toast.LENGTH_SHORT).show();

                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:
                        if (mReader.RF_StopInventory() == SDConsts.SDResult.SUCCESS) {
                            mInventory = false;
                            enableControl(!mInventory);
                        }
                        pauseStopwatch();
                        break;

                    case SDConsts.SDCmdMsg.SLED_UNKNOWN_DISCONNECTED:
                        //This message contain DETACHED event.
                        if (mInventory) {
                            pauseStopwatch();
                            mInventory = false;
                        }
                        enableControl(false);
                        if (mOptionHandler != null)
                            mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_DISCONNECTED).sendToTarget();
                        break;


                }
                break;

            case SDConsts.Msg.RFMsg:
                switch(m.arg1) {
                    //+++NTNS
//                    case SDConsts.RFCmdMsg.INVENTORY_CUSTOM_READ:
//                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
//                            String data = (String)m.obj;
//                            if (data != null)
//                                processReadDataCustom(data);                            
//                        }
//                        break;
                    //---NTNS
                    case SDConsts.RFCmdMsg.INVENTORY:
                    case SDConsts.RFCmdMsg.READ:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            if (m.obj != null  && m.obj instanceof String) {
                                String data = (String) m.obj;
                                if (data != null)
                                    processReadData(data);
                            }
                        }
                        break;
                    case SDConsts.RFCmdMsg.LOCATE:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            if (m.obj != null && m.obj instanceof Integer) {
                                processLocateData((int) m.obj);
                            }
                        }
                        break;
                }
                break;
        }
    }

    private void processLocateData(int data) {
        startLocateTimer();
        mLocateValue = data;
        mTagLocateProgress.setProgress(data);
        if (mSoundTask == null) {
            mSoundTask = new SoundTask();
            mSoundTask.execute();
        }
        else {
            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                mSoundTask.cancel(true);
                mSoundTask = null;
                mSoundTask = new SoundTask();
                mSoundTask.execute();
            }
        }
    }

    private void processReadData(String data) {
        //updateCountText();
        StringBuilder tagSb = new StringBuilder();
        tagSb.setLength(0);
        String info = "";
        String originalData = data;
        if (data.contains(";")) {
            if (D) Log.d(TAG, "full tag = " + data);
            //full tag example(with optional value)
            //1) RF_PerformInventory => "3000123456783333444455556666;rssi:-54.8"
            //2) RF_PerformInventoryWithLocating => "3000123456783333444455556666;loc:64"
            int infoTagPoint = data.indexOf(';');
            info = data.substring(infoTagPoint, data.length());
            int infoPoint = info.indexOf(':') + 1;
            info = info.substring(infoPoint, info.length());
            if (D) Log.d(TAG, "info tag = " + info);
            data = data.substring(0, infoTagPoint);
            if (D) Log.d(TAG, "data tag = " + data);
        }

        if (info != "") {
            Activity activity = getActivity();
            String prefix = "";
            if (originalData.contains("rssi")) {
                if (activity != null)
                    prefix = activity.getString(R.string.rssi_str);
            }
            else if (originalData.contains("loc")){
                if (activity != null)
                    prefix = activity.getString(R.string.loc_str);
            }
            if (activity != null)
                info = prefix + info;
        }


    }


    //+++NTNS
//    private void processReadDataCustom(String data) {
//        updateCountText();
//        StringBuilder tagSb = new StringBuilder(); 
//        tagSb.setLength(0);
//        String rssi = ""; 
//        String customData = "";
//        if (data.contains(";")) {
//            if (D) Log.d(TAG, "full tag = " + data);
//            //full tag example = "3000123456783333444455556666;rssi:-54.8^custom=2348920384"
//            int customdDataPoint = data.indexOf('^');
//            customData = data.substring(customdDataPoint, data.length());
//            int customPoint = customData.indexOf('=') + 1;
//            customData = customData.substring(customPoint, customData.length());
//            if (D) Log.d(TAG, "custom data = " + customData);
//            data = data.substring(0, customdDataPoint);
//            
//            int rssiTagPoint = data.indexOf(';');
//            rssi = data.substring(rssiTagPoint, data.length());
//            int rssiPoint = rssi.indexOf(':') + 1;
//            rssi = rssi.substring(rssiPoint, rssi.length());
//            if (D) Log.d(TAG, "rssi tag = " + rssi);
//            data = data.substring(0, rssiTagPoint);
//            
//            if (D) Log.d(TAG, "data tag = " + data);
//            data = data + "\n" + customData;
//        }
//        if (rssi != "") {
//            Activity activity = getActivity();
//            if (activity != null)
//                rssi = activity.getString(R.string.rssi_str) + rssi;
//        }
//        mAdapter.addItem(-1, data, rssi, mTagFilter);
//        if (mSoundPlay) {
//            if (mSoundTask == null) {
//                mSoundTask = new SoundTask();
//                mSoundTask.execute();
//            }
//            else {
//                if (mSoundTask.getStatus() == Status.FINISHED) {
//                    mSoundTask.cancel(true);
//                    mSoundTask = null;
//                    mSoundTask = new SoundTask();
//                    mSoundTask.execute();                    
//                }
//            }
//        }
//        mRfidList.setSelection(mRfidList.getAdapter().getCount() - 1);
//        if (!mInventory) {
//            updateCountText();
//            updateSpeedCountText();
//            updateAvrSpeedCountText();
//        }
//    }
    //---NTNS

    private void addCheckListener() {
        if (mTurboSwitch != null)
            mTurboSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mRssiSwitch != null)
            mRssiSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mFilterSwitch != null)
            mFilterSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mSoundSwitch != null)
            mSoundSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mMaskSwitch != null)
            mMaskSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mToggleSwitch != null)
            mToggleSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mPCSwitch != null)
            mPCSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mFileSwitch != null)
            mFileSwitch.setOnCheckedChangeListener(sledcheckListener);


    }

    private void updateButtonState() {

        mFileSwitch.setChecked(mFile);

        mFilterSwitch.setChecked(mTagFilter);

        mSoundSwitch.setChecked(mSoundPlay);

        mMaskSwitch.setChecked(mMask);

        mTurboSwitch.setChecked(mIsTurbo);


        if (mReader != null) {
            int toggle = mReader.RF_GetToggle();
            if (toggle == SDConsts.RFToggle.ON)
                mToggle = true;
            else
                mToggle = false;
            mToggleSwitch.setChecked(mToggle);

            int session = mReader.RF_GetSession();
            if (session != mSessionSpin.getSelectedItemPosition())
                mSessionSpin.setSelection(session);

            int flag = mReader.RF_GetSelectionFlag();
            if (flag != mSelFlagSpin.getSelectedItemPosition() + 1)
                mSelFlagSpin.setSelection(flag - 1);

            int rssi = mReader.RF_GetRssiTrackingState();
            if (rssi == SDConsts.RFRssi.ON)
                mRssi = true;
            else
                mRssi = false;
            mRssiSwitch.setChecked(mRssi);

            int battery = mReader.SD_GetBatteryStatus();
            if (!(battery < 0 || battery > 100))
                mBatteryText.setText("" + battery + "%");

        }
    }

    private void startLocateTimer() {
        stopLocateTimer();

        mLocateTimerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                locateTimeout();
            }
        };
        mClearLocateTimer = new Timer();
        mClearLocateTimer.schedule(mLocateTimerTask, 500);
    }

    private void stopLocateTimer() {
        if (mClearLocateTimer != null ) {
            mClearLocateTimer.cancel();
            mClearLocateTimer = null;
        }
    }

    private void locateTimeout() {
        mTagLocateProgress.setProgress(0);
    }
}