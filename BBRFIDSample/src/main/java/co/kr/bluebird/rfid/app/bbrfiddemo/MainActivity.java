/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 * 
 * http://www.bluebirdcorp.com/
 * 
 * Author : Bogon Jun
 *
 * Date : 2016.01.18
 */

package co.kr.bluebird.rfid.app.bbrfiddemo;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.icu.text.IDNA;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BRInventoryFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BRReplFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BatteryFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.ConnectivityFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.InfoFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.InventoryFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.PutAwayToStore;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.RFAccessFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.RFConfigFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.RFIDWriteFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.RFSelectionFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.RapidFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.ReceivingToStore;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.SDFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.senddatafragment;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    
    private static final String TAG = MainActivity.class.getSimpleName();
    
    private static final boolean D = Constants.MAIN_D;

    public static final int MSG_OPTION_DISCONNECTED = 0;
    
    public static final int MSG_OPTION_CONNECTED = 1;

    public static final int MSG_BACK_PRESSED = 2;
    
    private String[] mFunctionsString;
    
    private DrawerLayout mDrawerLayout;
    
    private ListView mDrawerList;
    
    private ActionBarDrawerToggle mDrawerToggle;

    private Reader mReader;
    
    private Context mContext;
    
    private FragmentManager mFragmentManager;
    
    private boolean mIsConnected;
    
    private ConnectivityFragment mConnectivityFragment;
    private SDFragment mSDFragment;
    private RFAccessFragment mRFAccessFragment;
    private RFConfigFragment mRFConfigFragment;
    private RFSelectionFragment mRFSelectionFragment;
    private RapidFragment mRapidFragment;
    private InventoryFragment mInventoryFragment;
    private cycleCount mcycleCount1;

    private BRInventoryFragment mInventoryFragmentbr;
    private senddatafragment msenddatafragment;
    private BarcodeFragment mBarcodeFragment;
    private InfoFragment mInfoFragment;
    private BatteryFragment mBatteryFragment;
    private BRReplFragment mReplenishmentFragment;
    private RFIDWriteFragment mRFIDWriteFragment;
    private ReceivingToStore mReceivingToStore;
    private PutAwayToStore mPutAwayToStoreFragment;
    private Button enablesettings, disablesettings;
    private EditText pass;
    private LinearLayout mUILayout;

    private Fragment mCurrentFragment;
    private settings mSettingFragment;

    private ImageButton mConnectButton;
    private ImageButton mSDFunctionButton;
    private ImageButton mRFConfigButton;
    private TextView mRFConfigButtonText;
    private ImageButton mRFAccessButton;
    private ImageButton mRFSelectButton;
    private ImageButton mRapidButton;
    private ImageButton mPutAway;
    private ImageButton mReceiving;
    private ImageButton RFIDWrtie;
    private ImageButton mBarcodeButton;
    private ImageButton mBatteryButton;
    private ImageButton mInformationButton;
    private ImageButton mRepl;
    private ImageButton mSettingsButton;
    private ImageView mCILogoImage;
    private ImageView mTransProcess;
    private ImageButton mCycleCount;
    private ProgressBar mBatteryProgress;
    private ScrollView mScrollView;

    private final MainActivity.BatteryHandler mBatteryHandler = new MainActivity.BatteryHandler(this);

    private final MainHandler mMainHandler = new MainHandler(this);
    
    public final UpdateConnectHandler mUpdateConnectHandler = new UpdateConnectHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mBatteryProgress = (ProgressBar) findViewById(R.id.batt_progress);


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mUILayout = (LinearLayout)findViewById(R.id.ui_layout);

        mCurrentFragment = null;

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int buttonHeight = size.x / 3;

        mPutAway = (ImageButton) findViewById(R.id.putAway1) ;
        mPutAway.setMinimumHeight(buttonHeight);

        mReceiving = (ImageButton) findViewById(R.id.Receiving1);
        mReceiving.setMinimumHeight(buttonHeight);

        mConnectButton = (ImageButton)findViewById(R.id.connect_bt);
        mConnectButton.setMinimumHeight(buttonHeight);

        mTransProcess = (ImageButton) findViewById(R.id.transaction1);
        mTransProcess.setMinimumHeight(buttonHeight);
        mTransProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this,transaction_listView.class);
                startActivity(intent3);
            }
        });





        mRFAccessButton = (ImageButton)findViewById(R.id.rfacc_bt);
        mRFAccessButton.setMinimumHeight(buttonHeight);
        mReader = Reader.getReader(mContext, mMainHandler);

        if (mReader.SD_GetConnectState() != SDConsts.SDConnectState.CONNECTED)
            Toast.makeText(MainActivity.this, "NOT CONNECTED WITH GUN SLED",
                    Toast.LENGTH_LONG).show();




        mRepl = (ImageButton)findViewById(R.id.Repl);
        mRepl.setMinimumHeight(buttonHeight);

        mCycleCount = (ImageButton) findViewById(R.id.rfCycleCount);
        mCycleCount.setMinimumHeight(buttonHeight);

        mRFConfigButton = (ImageButton) findViewById(R.id.rfconf_bt);
        mRFConfigButton.setOnClickListener(buttonListener);
        mConnectButton.setOnClickListener(buttonListener);
        mRFAccessButton.setOnClickListener(buttonListener);
        mPutAway.setOnClickListener(buttonListener);
        mReceiving.setOnClickListener(buttonListener);
        mRepl.setOnClickListener(buttonListener);
        mSettingsButton = (ImageButton) findViewById(R.id.rfsettings);
        mSettingsButton.setMinimumHeight(buttonHeight);

        RFIDWrtie = (ImageButton)findViewById(R.id.RFIDWrtie);
        RFIDWrtie.setOnClickListener(buttonListener);
        RFIDWrtie.setMinimumHeight(buttonHeight);





        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close) {
            String mDrawerTitle = "Functions";

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };


        mCycleCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, cycleCount.class);
                startActivity(intent2);
            }
        });
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this,settings.class);
                startActivity(intent1);
            }
        });


        mPutAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, PutAwayActivity.class);
                startActivity(intent2);
            }
        });
        mReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, ReceivingActivity.class);
                startActivity(intent2);
            }
        });




//        mInventoryButtonbr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        mFunctionsString = getResources().getStringArray(R.array.functions_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mFunctionsString));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mFragmentManager = getFragmentManager();
        
        mIsConnected = false;
    }

    public View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = 0;
            switch(v.getId()) {
                case R.id.connect_bt:
                    id = 0;
                    break;
                case R.id.rfacc_bt:
                    id = 1;
                    break;
                case R.id.rfsettings:
                    id = 2;
                    break;
                case R.id.rfconf_bt:
                    id = 3;
                    break;
                case R.id.senddata:
                    id = 4;
                    break;
                case R.id.info_bt:
                    id = 5;
                    break;
                case R.id.bat_bt:
                    id = 8;
                    break;


                case R.id.Repl:
                    id = 10;
                    break;
                case R.id.transaction1:
                    id = 11;
                    break;
                case R.id.rfCycleCount:
                    id = 13;
                    break;
                case R.id.RFIDWrtie:
                    id=14;
                    break;

            }
            selectItem(id);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (mIsConnected)
            menu.getItem(0).setVisible(true);


        else
            menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_connected) {
            Toast.makeText(this, getString(R.string.sled_connected_str), Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_home) {
            switchToHome();
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        switch (position) {
            case 0:
                if (mConnectivityFragment == null)
                    mConnectivityFragment = ConnectivityFragment.newInstance();
                mCurrentFragment = mConnectivityFragment;
                break;
            case 1:
                if (mRFAccessFragment == null)
                    mRFAccessFragment = RFAccessFragment.newInstance();
                mCurrentFragment = mRFAccessFragment;
                break;
            case 2:
                if (mSettingFragment == null)
                    mSettingFragment = settings.newInstance();
                mCurrentFragment = mSettingFragment;
                break;
            case 3:
                if (mRFConfigFragment == null)
                    mRFConfigFragment = RFConfigFragment.newInstance();
                mCurrentFragment = mRFConfigFragment;
                break;
            case 4:
                if (msenddatafragment == null)
                    msenddatafragment = senddatafragment.newInstance();
                mCurrentFragment = msenddatafragment;
                break;
            case 5:
                if (mInfoFragment == null)
                    mInfoFragment = InfoFragment.newInstance();
                mCurrentFragment = mInfoFragment;
                break;
            case 12:
                if (mInfoFragment == null)
                    mInfoFragment = InfoFragment.newInstance();
                mCurrentFragment = mInfoFragment;
                break;


            case 6:
                if (mBarcodeFragment == null)
                    mBarcodeFragment = BarcodeFragment.newInstance();
                mCurrentFragment = mBarcodeFragment;
                break;
            case 7:
                if (mBatteryFragment == null)
                    mBatteryFragment = BatteryFragment.newInstance();
                mCurrentFragment = mBatteryFragment;
                break;

            case 8:
                if (mRapidFragment == null)
                    mRapidFragment = RapidFragment.newInstance();
                mCurrentFragment = mRapidFragment;
                break;
            case 9:
                if (msenddatafragment == null)
                    msenddatafragment = senddatafragment.newInstance();
                mCurrentFragment = msenddatafragment;
                break;
            case 10:
                if (mReplenishmentFragment == null)
                    mReplenishmentFragment = BRReplFragment.newInstance();
                mCurrentFragment = mReplenishmentFragment;
                break;
            case 11:
                if (mInfoFragment == null)
                    mInfoFragment = InfoFragment.newInstance();
                mCurrentFragment = mInfoFragment;
                break;

                case 14:
                    if (mRFIDWriteFragment == null)
                        mRFIDWriteFragment = RFIDWriteFragment.newInstance();
                    mCurrentFragment = mRFIDWriteFragment;
                    break;


            default:
                return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.content, mCurrentFragment);
        ft.commit();
        mDrawerList.setItemChecked(position, true);
       // setTitle(mFunctionsString[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        mUILayout.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, " onStart");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        boolean openResult = false;
        boolean isConnected = false;
        mReader = Reader.getReader(mContext, mMainHandler);
        mReader = Reader.getReader(mContext, mBatteryHandler);

        if (mReader != null && mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {
            int value = mReader.SD_GetBatteryStatus();

            if (MainActivity.class != null) {
                mBatteryProgress.setProgress(value);
            }
        }
        if (mReader != null)
            openResult = mReader.SD_Open();
        if (openResult == SDConsts.RF_OPEN_SUCCESS) {
            Log.i(TAG, "Reader opened");
            if (mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
                isConnected = true;


        }
        else if (openResult == SDConsts.RF_OPEN_FAIL)
            if (D) Log.e(TAG, "Reader open failed");
        else if (mReader.SD_GetConnectState() != SDConsts.SDConnectState.CONNECTED)
        Toast.makeText(MainActivity.this, "NOT CONNECTED WITH GUN SLED",
                Toast.LENGTH_LONG).show();
        updateConnectState(isConnected);
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
    protected void onSaveInstanceState(Bundle outState) {
    }
    
    @Override
    protected void onStop() {
        if (D) Log.d(TAG, " onStop");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mReader = Reader.getReader(mContext, mMainHandler);
        if (mReader != null)
        if (mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {

        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (D) Log.d(TAG, "onRequestPermissionsResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mCurrentFragment != null)
                mCurrentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment != null)
            switchToHome();
        else
            super.onDestroy();
    }

    private static class MainHandler extends Handler {
        private final WeakReference<MainActivity> mExecutor;
        public MainHandler(MainActivity ac) {
            mExecutor = new WeakReference<>(ac);
        }
    
        @Override
        public void handleMessage(Message msg) {
            MainActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }

    private static class BatteryHandler extends Handler {
        private final WeakReference<MainActivity> mExecutor;
        public BatteryHandler(MainActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }


    void handleMessage(Message m) {
        if (D) Log.d(TAG, "handleMessage");
        if (D) Log.d(TAG, "command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
        switch (m.what) {
        case SDConsts.Msg.SDMsg:
            break;
        case SDConsts.Msg.RFMsg:
            break;
        case SDConsts.Msg.BCMsg:
            break;
        }
    }

    private void switchToHome() {
        if (D) Log.d(TAG, "switchToHome");
        try {
            mDrawerLayout.closeDrawer(mDrawerList);
            if (mCurrentFragment != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.remove(mCurrentFragment);
                ft.commit();
                mCurrentFragment = null;
                mReader = Reader.getReader(mContext, mMainHandler);
            }
            setTitle(getString(R.string.app_name));
            if (mUILayout.getVisibility() != View.VISIBLE) {
                mUILayout.setVisibility(View.VISIBLE);
            }
        }
        catch (java.lang.IllegalStateException e) {
        }
        return;
    }

    private void updateConnectState(boolean b) {
        mIsConnected = b;
        invalidateOptionsMenu();
    }
    
    private static class UpdateConnectHandler extends Handler {
        private final WeakReference<MainActivity> mExecutor;
        public UpdateConnectHandler(MainActivity ac) {
            mExecutor = new WeakReference<>(ac);
        }
        
        @Override
        public void handleMessage(Message msg) {
            MainActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleUpdateConnectHandler(msg);
            }
        }
    }
    
    public void handleUpdateConnectHandler(Message m) {
        if (m.what == MSG_OPTION_DISCONNECTED)
            updateConnectState(false);
        else if (m.what == MSG_OPTION_CONNECTED)
            updateConnectState(true);
        else if (m.what == MSG_BACK_PRESSED)
            switchToHome();
    }
}