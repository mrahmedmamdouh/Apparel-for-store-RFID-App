package co.kr.bluebird.rfid.app.bbrfiddemo.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.MainActivity;
import co.kr.bluebird.rfid.app.bbrfiddemo.PutAwayActivity;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.control.ListItem;
import co.kr.bluebird.rfid.app.bbrfiddemo.control.TagListAdapter;
import co.kr.bluebird.rfid.app.bbrfiddemo.fileutil.FileManager;
import co.kr.bluebird.rfid.app.bbrfiddemo.permission.PermissionHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.stopwatch.StopwatchService;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import co.kr.bluebird.sled.SelectionCriterias;

public class PutAwayToStore extends Fragment {





        private static final String TAG = PutAwayToStore.class.getSimpleName();

        private static final boolean D = Constants.INV_D;

        private StopwatchService mStopwatchSvc;

        private TagListAdapter mAdapter;

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

        private int mSoundId;

        private float mSoundVolume;

        private boolean mSoundFileLoadState;

        private PutAwayToStore.SoundTask mSoundTask;

        private double mOldTotalCount = 0;

        private double mOldSec = 0;

        private FileManager mFileManager;

        private String mLocateTag;

        private String mLocateEPC;

        private int mLocateStartPos;

        private LinearLayout mLocateLayout;

        private LinearLayout mListLayout;

        private ProgressBar mTagLocateProgress;

        private int mLocateValue;

        private ImageButton mBackButton;
        private  Switch startStop;

        private TextView mLocateTv;

        private boolean mLocate;

        private TimerTask mLocateTimerTask;

        private Timer mClearLocateTimer;

        private Spinner mSessionSpin;
        private ArrayAdapter<CharSequence> mSessionChar;

        private Spinner mSelFlagSpin;
        public static String x,y;
        private ArrayAdapter<CharSequence> mSelFlagChar;

        WDxDBHelper dbhelper;
        SQLiteDatabase db;
        //private int mCurrentPower;

        ArrayList<PutAwayToStore.itemModel> data= new ArrayList<>();
        public  String store_id, urlNumber, portNumber;
        private int mTickCount = 0;

        private PutAwayToStore.UpdateStopwatchHandler mUpdateStopwatchHandler = new PutAwayToStore.UpdateStopwatchHandler(this);

        private PutAwayToStore.InventoryHandler mInventoryHandler = new PutAwayToStore.InventoryHandler(this);

        public static PutAwayToStore newInstance() {
            return new PutAwayToStore();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (D) Log.d(TAG, "onCreateView");
            View v = inflater.inflate(R.layout.putawaytostore_frag, container, false);
            mContext = inflater.getContext();

            dbhelper = new WDxDBHelper(getActivity());
            db =dbhelper.getWritableDatabase();

            mOptionHandler = ((PutAwayActivity)getActivity()).mUpdateConnectHandler;

            mRfidList = (ListView)v.findViewById(R.id.rfid_list);

            startStop = (Switch) v.findViewById(R.id.startStop);
            SharedPreferences pref = getActivity().getSharedPreferences("storeNumber", Context.MODE_PRIVATE);
            store_id = pref.getString("storeNumber", "DEFAULT");

            SharedPreferences pref1 = getActivity().getSharedPreferences("urlNumber", Context.MODE_PRIVATE);
            urlNumber = pref1.getString("urlNumber", "DEFAULT");

            SharedPreferences pref2 = getActivity().getSharedPreferences("portNumber", Context.MODE_PRIVATE);
            portNumber = pref2.getString("portNumber", "DEFAULT");



            startStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // do something, the isChecked will be
                    // true if the switch is in the On position
                    if (isChecked) {
                        StartProcess();

                        // do something when check is selected
                    } else {
                        StopProcess();
                        //do something when unchecked
                    }
                }
            });
            boxnum = (EditText) v.findViewById(R.id.ed_boxnum);
            plus = (Button) v.findViewById(R.id.bt_plus);
            minus = (Button) v.findViewById(R.id.bt_minus);
            newfile = (Button) v.findViewById(R.id.bt_newfile);
            donebt = (Button) v.findViewById(R.id.done);
            mLocateLayout = (LinearLayout)v.findViewById(R.id.tag_locate_container);

            mListLayout = (LinearLayout)v.findViewById(R.id.tag_list_container);

            mLocateTv = (TextView)v.findViewById(R.id.tag_locate_text);

            mTagLocateProgress = (ProgressBar)v.findViewById(R.id.tag_locate_progress);

            mBackButton = (ImageButton)v.findViewById(R.id.back_button);
            mBackButton.setOnClickListener(sledListener);

            mTimerText = (TextView)v.findViewById(R.id.timer_text);

            mCountText = (TextView)v.findViewById(R.id.count_text_1);

            mSpeedCountText = (TextView)v.findViewById(R.id.speed_count_text);

            mAvrSpeedCountTest = (TextView)v.findViewById(R.id.speed_avr_count_text);

            Activity activity = getActivity();

            if (activity != null) {
                String speedCountStr = activity.getString(R.string.speed_count_str) + activity.getString(R.string.speed_postfix_str);
                mSpeedCountText.setText(speedCountStr);
                mAvrSpeedCountTest.setText(speedCountStr);
            }

            mBatteryText = (TextView)v.findViewById(R.id.battery_text);

            mTurboSwitch = (Switch)v.findViewById(R.id.turbo_switch);

            mRssiSwitch = (Switch)v.findViewById(R.id.rssi_switch);

            mFilterSwitch = (Switch)v.findViewById(R.id.filter_switch);

            mSoundSwitch = (Switch)v.findViewById(R.id.sound_switch);

            mMaskSwitch = (Switch)v.findViewById(R.id.mask_switch);

            mToggleSwitch = (Switch)v.findViewById(R.id.toggle_switch);

            mPCSwitch = (Switch)v.findViewById(R.id.pc_switch);

            mFileSwitch = (Switch)v.findViewById(R.id.file_switch);

            mClearButton = (Button)v.findViewById(R.id.clear_button);
            mClearButton.setOnClickListener(clearButtonListener);

            mInvenButton = (Button)v.findViewById(R.id.inven_button);
            mInvenButton.setOnClickListener(sledListener);

            mStopInvenButton = (Button)v.findViewById(R.id.stop_inven_button);
            mStopInvenButton.setOnClickListener(sledListener);

            mProgressBar = (ProgressBar)v.findViewById(R.id.timer_progress);
            mProgressBar.setVisibility(View.INVISIBLE);

            mSessionSpin = (Spinner)v.findViewById(R.id.session_spin);
            mSessionChar = ArrayAdapter.createFromResource(mContext, R.array.session_array,
                    android.R.layout.simple_spinner_dropdown_item);
            mSessionSpin.setAdapter(mSessionChar);


            mSelFlagSpin = (Spinner)v.findViewById(R.id.sel_flag_spin);
            mSelFlagChar = ArrayAdapter.createFromResource(mContext, R.array.sel_flag_array,
                    android.R.layout.simple_spinner_dropdown_item);
            mSelFlagSpin.setAdapter(mSelFlagChar);

            mAdapter = new TagListAdapter(mContext);
            mRfidList.setAdapter(mAdapter);

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
            donebt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent1);//Edited here
                    Log.d(TAG, "clicked");
                    db.beginTransaction();
                    try {
                        db.execSQL("delete from PutAwayCount");
                        for (int i = 0; i < mAdapter.getCount(); i++) {
                            ListItem item = (ListItem) mAdapter.getItem(i);

                            String _item = item.mUt;
                            db.execSQL("insert into PutAwayCount (items) values ('" + _item + "')");
                        }

                        db.setTransactionSuccessful();
                    }
                    finally {
                        db.endTransaction();
                    }

                    Cursor c = db.rawQuery("select count(*) as COUNT from PutAwayCount",null);
                    while (c.moveToNext())
                    {
                        x = c.getString(c.getColumnIndex("COUNT"));


                        Toast.makeText(getActivity(),c.getString(c.getColumnIndex("COUNT")),Toast.LENGTH_SHORT).show();
                    }
                    Gson g = new Gson();
                    StringEntity entity1 = null;
                    data.clear();
                    c = db.rawQuery("select distinct items from PutAwayCount",null);
                    if(c!=null && c.getCount()>0)
                    {
                        while(c.moveToNext())
                        {
                            data.add(new PutAwayToStore.itemModel(c.getString(c.getColumnIndex("items")),store_id));
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
                            public void onSuccess(String content) {
                                Toast.makeText(mContext, "Data has been sent", Toast.LENGTH_SHORT).show();

                                super.onSuccess(content);
                            }
                            @Override
                            public void onFailure(Throwable error) {
                                Toast.makeText(mContext, "Data has not been sent", Toast.LENGTH_SHORT).show();

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



            newfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFileManager.closeFile();
                }
            });

            bindStopwatchSvc();

            return v;
        }

        private AdapterView.OnItemSelectedListener sessionListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    Toast.makeText(mContext, "If you want to use session 1 ~ 3 value, toggle off", Toast.LENGTH_SHORT).show();
                mReader.RF_SetSession(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        private AdapterView.OnItemSelectedListener selFlagListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mReader.RF_SetSelectionFlag(position + 1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };


        public  void StopProcess(){
            int ret;
            ret = mReader.RF_StopInventory();
            if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                mInventory = false;
                enableControl(!mInventory);
                pauseStopwatch();
            } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                Toast.makeText(mContext, "Stop Inventory failed", Toast.LENGTH_SHORT).show();


        }
        public  void StartProcess(){
            int ret;
            if (!mInventory) {
                openFile();
                //openFileEPC();
                if (mLocate) {
                    //mCurrentPower = mReader.RF_GetRadioPowerState();
                    ret = mReader.RF_PerformInventoryForLocating(mLocateEPC);
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

        }
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
            mIsTurbo = true;
            mFile = true;
            mMask =false;
            mIgnorePC = true;
            mSoundPlay = true;
            mRssi = true;
            mToggle =true;
            mTagFilter = true;



            createSoundPool();

            mOldTotalCount = 0;

            mOldSec = 0;

            mReader = Reader.getReader(mContext, mInventoryHandler);
            if (mReader != null && mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {
                enableControl(true);
                updateButtonState();
            }
            else
                enableControl(false);

            mLocate = false;

            mInventory = false;
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(System.currentTimeMillis());

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
            mReader.RF_StopInventory();
            pauseStopwatch();
            mInventory = false;
            if (mSoundPool != null)
                mSoundPool.release();
            mSoundFileLoadState = false;
            if (mFileManager != null) {
                mFileManager.closeFile();
                mFileManager = null;
            }
            stopStopwatch();
            unbindStopwatchSvc();
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
                mAdapter.removeAllItem();

                updateCountText();

                stopStopwatch();

                mOldTotalCount = 0;

                mOldSec = 0;

                updateSpeedCountText();

                updateAvrSpeedCountText();

                Activity activity = getActivity();
                if (activity != null)
                    mSpeedCountText.setText("0" + activity.getString(R.string.speed_postfix_str));
            }
        }


        private View.OnClickListener sledListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (D) Log.d(TAG, "stopwatchListener");

                int id = v.getId();
                int ret;
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
                            openFile();
                            //openFileEPC();
                            if (mLocate) {
                                //mCurrentPower = mReader.RF_GetRadioPowerState();
                                ret = mReader.RF_PerformInventoryForLocating(mLocateEPC);
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
                        Intent intent1 = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent1);//Edited here
                        Log.d(TAG, "clicked");
                        db.beginTransaction();
                        try {
                            db.execSQL("delete from PutAwayCount");
                            for (int i = 0; i < mAdapter.getCount(); i++) {
                                ListItem item = (ListItem) mAdapter.getItem(i);

                                String _item = item.mUt;
                                db.execSQL("insert into PutAwayCount (items) values ('" + _item + "')");
                            }

                            db.setTransactionSuccessful();
                        }
                        finally {
                            db.endTransaction();
                        }

                        Cursor c = db.rawQuery("select count(*) as COUNT from PutAwayCount",null);
                        while (c.moveToNext())
                        {
                            x = c.getString(c.getColumnIndex("COUNT"));


                            Toast.makeText(getActivity(),c.getString(c.getColumnIndex("COUNT")),Toast.LENGTH_SHORT).show();
                        }
                        Gson g = new Gson();
                        StringEntity entity1 = null;
                        data.clear();
                        c = db.rawQuery("select distinct items from PutAwayCount",null);
                        if(c!=null && c.getCount()>0)
                        {
                            while(c.moveToNext())
                            {
                                data.add(new PutAwayToStore.itemModel(c.getString(c.getColumnIndex("items")),store_id));
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
                                public void onSuccess(String content) {
                                    Toast.makeText(mContext, "Data has been sent", Toast.LENGTH_SHORT).show();

                                    super.onSuccess(content);
                                }
                                @Override
                                public void onFailure(Throwable error) {
                                    Toast.makeText(mContext, "Data has not been sent", Toast.LENGTH_SHORT).show();

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

                break;
                }
            }
        };

        private void openFile() {
            if ( !mInventory && !mLocate) {
                mFile = true;

                if (mFileManager == null)
                    mFileManager = new FileManager(mContext);
                mFileManager.openFile(boxnum.getText().toString().trim());
            }
        }
        private void openFileEPC() {
            //if (mFile && !mInventory && !mLocate) {
            if ( !mInventory && !mLocate) {
                mFile = true;

                if (mFileManager == null)
                    mFileManager = new FileManager(mContext);
                mFileManager.openFileInv(boxnum.getText().toString().trim());
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



        private void startStopwatch() {
            if (D) Log.d(TAG, "startStopwatch");

            if (mStopwatchSvc != null && !mStopwatchSvc.isRunning())
                mStopwatchSvc.start();

            mProgressBar.setVisibility(View.VISIBLE);
        }

        private void pauseStopwatch() {
            if (D) Log.d(TAG, "pauseStopwatch");

            if (mStopwatchSvc != null && mStopwatchSvc.isRunning())
                mStopwatchSvc.pause();

            updateCountText();

            updateTimerText();

            updateSpeedCountText();

            updateAvrSpeedCountText();

            mProgressBar.setVisibility(View.INVISIBLE);
        }

        private void stopStopwatch() {
            if (D) Log.d(TAG, "stopStopwatch");

            if (mStopwatchSvc != null && mStopwatchSvc.isRunning())
                mStopwatchSvc.pause();

            if (mStopwatchSvc != null)
                mStopwatchSvc.reset();

            updateTimerText();

            updateAvrSpeedCountText();

            mProgressBar.setVisibility(View.INVISIBLE);
        }

        private void updateCountText() {
            if (D) Log.d(TAG, "updateCountText");
            String text = Integer.toString(mAdapter.getCount());
            mCountText.setText(text);
        }

        private void updateTimerText() {
            if (D) Log.d(TAG, "updateTimerText");
            if (mStopwatchSvc != null)
                mTimerText.setText(mStopwatchSvc.getFormattedElapsedTime());
        }

        private void updateSpeedCountText() {
            if (D) Log.d(TAG, "updateSpeedCountText");
            String speedStr = "";
            double value = 0;
            double totalCount = 0;
            double sec = 0;
            if (mStopwatchSvc != null) {
                sec = ((double)((int)(mStopwatchSvc.getElapsedTime() / 100))) / 10;

                if (!mTagFilter)
                    totalCount = mAdapter.getTotalCount();
                else {
                    totalCount = mAdapter.getTotalCount();
                    for (int i = 0 ; i < mAdapter.getCount(); i++)
                        totalCount += mAdapter.getItemDupCount(i);
                }
                if (totalCount > 0 && sec - mOldSec >= 1) {
                    value = (double)((int)(((totalCount - mOldTotalCount) / (sec - mOldSec)) * 10)) / 10;

                    mOldTotalCount = totalCount;

                    mOldSec = sec;
                    Activity activity = getActivity();
                    if (activity != null)
                        speedStr = Double.toString(value) + activity.getString(R.string.speed_postfix_str);
                    mSpeedCountText.setText(speedStr);
                }
            }
        }

        private void updateAvrSpeedCountText() {
            if (D) Log.d(TAG, "updateAvrSpeedCountText");
            String speedStr = "";
            double value = 0;
            int totalCount = 0;
            double sec = 0;
            if (mStopwatchSvc != null) {
                sec = ((double)((int)(mStopwatchSvc.getElapsedTime() / 100))) / 10;

                if (!mTagFilter)
                    totalCount = mAdapter.getTotalCount();
                else {
                    totalCount = mAdapter.getTotalCount();
                    for (int i = 0 ; i < mAdapter.getCount(); i++)
                        totalCount += mAdapter.getItemDupCount(i);
                }
                if (totalCount > 0 && sec >= 1)
                    value = (double)((int)(((double)totalCount / sec) * 10)) / 10;

                Activity activity = getActivity();
                if (activity != null)
                    speedStr = Double.toString(value) + activity.getString(R.string.speed_postfix_str);
                mAvrSpeedCountTest.setText(speedStr);
            }
        }

        private void enableControl(boolean b) {

                mRfidList.setOnItemClickListener(null);
            mTurboSwitch.setEnabled(b);
            mRssiSwitch.setEnabled(b);
            mFilterSwitch.setEnabled(b);
            mSoundSwitch.setEnabled(b);
            mMaskSwitch.setEnabled(b);
            mToggleSwitch.setEnabled(b);
            mPCSwitch.setEnabled(b);
            mFileSwitch.setEnabled(b);
            mSessionSpin.setEnabled(b);
            mSelFlagSpin.setEnabled(b);
            mBackButton.setVisibility(b ? View.VISIBLE :View.INVISIBLE);

        }

        private ServiceConnection mStopwatchSvcConnection= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName arg0, IBinder arg1) {
                // TODO Auto-generated method stub
                if (D) Log.d(TAG, "onServiceConnected");

                mStopwatchSvc = ((StopwatchService.LocalBinder)arg1).getService(mUpdateStopwatchHandler);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                // TODO Auto-generated method stub
                if (D) Log.d(TAG, "onServiceDisconnected");

                mStopwatchSvc = null;
            }
        };



        private void bindStopwatchSvc() {
            if (D) Log.d(TAG, "bindStopwatchSvc");
            mContext.bindService(new Intent(mContext, StopwatchService.class), mStopwatchSvcConnection, Context.BIND_AUTO_CREATE);
        }

        private void unbindStopwatchSvc() {
            if (D) Log.d(TAG, "unbindStopwatchSvc");
            try {
                if (mStopwatchSvc != null)
                    mContext.unbindService(mStopwatchSvcConnection);
            }
            catch (IllegalArgumentException iae) {
                return;
            }
        }

        private static class UpdateStopwatchHandler extends Handler {
            private final WeakReference<PutAwayToStore> mExecutor;
            public UpdateStopwatchHandler(PutAwayToStore f) {
                mExecutor = new WeakReference<>(f);
            }

            @Override
            public void handleMessage(Message msg) {
                PutAwayToStore executor = mExecutor.get();
                if (executor != null) {
                    executor.handleUpdateStopwatchHandler(msg);
                }
            }
        }

        public void handleUpdateStopwatchHandler(Message m) {
            if (D) Log.d(TAG, "mUpdateStopwatchHandler");
            if (m.what == StopwatchService.TICK_WHAT) {
                if (D) Log.d(TAG, "received stopwatch message");

                mTickCount++;

                updateCountText();

                updateSpeedCountText();

                if (mTickCount == 10) {
                    updateAvrSpeedCountText();
                    mTickCount = 0;
                }
                updateTimerText();

                mStopwatchSvc.update();

                mRfidList.setSelection(mRfidList.getAdapter().getCount() - 1);
            }
        }

        private static class InventoryHandler extends Handler {
            private final WeakReference<PutAwayToStore> mExecutor;
            public InventoryHandler(PutAwayToStore f) {
                mExecutor = new WeakReference<>(f);
            }

            @Override
            public void handleMessage(Message msg) {
                PutAwayToStore executor = mExecutor.get();
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

                            mAdapter.addItem(-1, "Inventory Stopped reason : " + m.arg2,  Integer.toString(m.arg2), !mIgnorePC, mTagFilter);
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

                        case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                            //Toast.makeText(mContext, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                            if (D) Log.d(TAG, "Battery state = " + m.arg2);
                            mBatteryText.setText("" + m.arg2 + "%");
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
            //mTagLocateProgress.setProgress(data);
            if (mSoundTask == null) {
                mSoundTask = new PutAwayToStore.SoundTask();
                mSoundTask.execute();
            }
            else {
                if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                    mSoundTask.cancel(true);
                    mSoundTask = null;
                    mSoundTask = new PutAwayToStore.SoundTask();
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

            mAdapter.addItem(-1, data, info, !mIgnorePC, mTagFilter);
            if (mFileManager != null && mFile)
                mFileManager.writeToFile(data);

            mRfidList.setSelection(mRfidList.getAdapter().getCount() - 1);
            if (!mInventory) {
                updateCountText();
                updateSpeedCountText();
                updateAvrSpeedCountText();
            }

            if (mSoundTask == null) {
                mSoundTask = new PutAwayToStore.SoundTask();
                mSoundTask.execute();
            }
            else {
                if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                    mSoundTask.cancel(true);
                    mSoundTask = null;
                    mSoundTask = new PutAwayToStore.SoundTask();
                    mSoundTask.execute();
                }
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

        private void updateButtonState() {
            mPCSwitch.setChecked(mIgnorePC);

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

                int session = 2;
                if (session != mSessionSpin.getSelectedItemPosition())
                    mSessionSpin.setSelection(session);

                int flag = 1;
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
        private class itemModel
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

