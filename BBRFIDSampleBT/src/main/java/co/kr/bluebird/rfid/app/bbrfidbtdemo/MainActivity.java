/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 * 
 * http://www.bluebirdcorp.com/
 * 
 * Author : Bogon Jun
 *
 * Date : 2016.04.04
 */

package co.kr.bluebird.rfid.app.bbrfidbtdemo;

import co.kr.bluebird.rfid.app.bbrfidbtdemo.fragment.*;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.lang.ref.WeakReference;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    
    private static final String TAG = MainActivity.class.getSimpleName();
    
    private static final boolean D = Constants.MAIN_D;

    public static final int MSG_OPTION_CONNECT_STATE_CHANGED = 0;

    public static final int MSG_BACK_PRESSED = 2;

    private String[] mFunctionsString;
    
    private DrawerLayout mDrawerLayout;
    
    private ListView mDrawerList;
    
    private ActionBarDrawerToggle mDrawerToggle;

    private BTReader mReader;
    
    private Context mContext;
    
    private FragmentManager mFragmentManager;
    
    private boolean mIsConnected;
    
    private BTConnectivityFragment mBTConnectivityFragment;
    private SDFragment mSDFragment;
    private RFAccessFragment mRFAccessFragment;
    private RFConfigFragment mRFConfigFragment;
    private RFSelectionFragment mRFSelectionFragment;
    private RapidFragment mRapidFragment;
    private InventoryFragment mInventoryFragment;
    private BarcodeFragment mBarcodeFragment;
    private InfoFragment mInfoFragment;
    private BatteryFragment mBatteryFragment;

    private LinearLayout mUILayout;

    private Fragment mCurrentFragment;

    private ImageButton mConnectButton;
    private ImageButton mSDFunctionButton;
    private ImageButton mRFConfigButton;
    private ImageButton mRFAccessButton;
    private ImageButton mRFSelectButton;
    private ImageButton mRapidButton;
    private ImageButton mInventoryButton;
    private ImageButton mBarcodeButton;
    private ImageButton mBatteryButton;
    private ImageButton mInformationButton;
    private ImageView mCILogoImage;
    private ImageView mCILogoImage2;
    
    private final MainHandler mMainHandler = new MainHandler(this);
    
    public final UpdateConnectHandler mUpdateConnectHandler = new UpdateConnectHandler(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

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

        mConnectButton = (ImageButton)findViewById(R.id.connect_bt);
        mConnectButton.setMinimumHeight(buttonHeight);

        mSDFunctionButton = (ImageButton)findViewById(R.id.sdfunc_bt);
        mSDFunctionButton.setMinimumHeight(buttonHeight);

        mRFConfigButton = (ImageButton)findViewById(R.id.rfconf_bt);
        mRFConfigButton.setMinimumHeight(buttonHeight);

        mRFAccessButton = (ImageButton)findViewById(R.id.rfacc_bt);
        mRFAccessButton.setMinimumHeight(buttonHeight);

        mRFSelectButton = (ImageButton)findViewById(R.id.rfsel_bt);
        mRFSelectButton.setMinimumHeight(buttonHeight);

        mRapidButton = (ImageButton)findViewById(R.id.rapid_bt);
        mRapidButton.setMinimumHeight(buttonHeight);

        mInventoryButton = (ImageButton)findViewById(R.id.inv_bt);
        mInventoryButton.setMinimumHeight(buttonHeight);

        mBarcodeButton = (ImageButton)findViewById(R.id.bar_bt);
        mBarcodeButton.setMinimumHeight(buttonHeight);

        mBatteryButton = (ImageButton)findViewById(R.id.bat_bt);
        mBatteryButton.setMinimumHeight(buttonHeight);

        mInformationButton = (ImageButton)findViewById(R.id.info_bt);
        mInformationButton.setMinimumHeight(buttonHeight);

        mCILogoImage = (ImageView)findViewById(R.id.ci_logo);
        mCILogoImage.setMinimumHeight(buttonHeight);

        mCILogoImage2 = (ImageView)findViewById(R.id.ci_logo2);
        mCILogoImage2.setMinimumHeight(buttonHeight);

        mConnectButton.setOnClickListener(buttonListener);
        mSDFunctionButton.setOnClickListener(buttonListener);
        mRFConfigButton.setOnClickListener(buttonListener);
        mRFAccessButton.setOnClickListener(buttonListener);
        mRFSelectButton.setOnClickListener(buttonListener);
        mRapidButton.setOnClickListener(buttonListener);
        mInventoryButton.setOnClickListener(buttonListener);
        mBarcodeButton.setOnClickListener(buttonListener);
        mBatteryButton.setOnClickListener(buttonListener);
        mInformationButton.setOnClickListener(buttonListener);

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
                case R.id.sdfunc_bt:
                    id = 1;
                    break;
                case R.id.rfconf_bt:
                    id = 2;
                    break;
                case R.id.rfacc_bt:
                    id = 3;
                    break;
                case R.id.rfsel_bt:
                    id = 4;
                    break;
                case R.id.rapid_bt:
                    id = 5;
                    break;
                case R.id.inv_bt:
                    id = 6;
                    break;
                case R.id.bar_bt:
                    id = 7;
                    break;
                case R.id.bat_bt:
                    id = 8;
                    break;
                case R.id.info_bt:
                    id = 9;
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
                if (mBTConnectivityFragment == null)
                    mBTConnectivityFragment = BTConnectivityFragment.newInstance();
                mCurrentFragment = mBTConnectivityFragment;
                break;
            case 1:
                if (mSDFragment == null)
                    mSDFragment = SDFragment.newInstance();
                mCurrentFragment = mSDFragment;
                break;
            case 2:
                if (mRFConfigFragment == null)
                    mRFConfigFragment = RFConfigFragment.newInstance();
                mCurrentFragment = mRFConfigFragment;
                break;
            case 3:
                if (mRFAccessFragment == null)
                    mRFAccessFragment = RFAccessFragment.newInstance();
                mCurrentFragment = mRFAccessFragment;
                break;
            case 4:
                if (mRFSelectionFragment == null)
                    mRFSelectionFragment = RFSelectionFragment.newInstance();
                mCurrentFragment = mRFSelectionFragment;
                break;
            case 5:
                if (mRapidFragment == null)
                    mRapidFragment = RapidFragment.newInstance();
                mCurrentFragment = mRapidFragment;
                break;
            case 6:
                if (mInventoryFragment == null)
                    mInventoryFragment = InventoryFragment.newInstance();
                mCurrentFragment = mInventoryFragment;
                break;
            case 7:
                if (mBarcodeFragment == null)
                    mBarcodeFragment = BarcodeFragment.newInstance();
                mCurrentFragment = mBarcodeFragment;
                break;
            case 8:
                if (mBatteryFragment == null)
                    mBatteryFragment = BatteryFragment.newInstance();
                mCurrentFragment = mBatteryFragment;
                break;
            case 9:
                if (mInfoFragment == null)
                    mInfoFragment = InfoFragment.newInstance();
                mCurrentFragment = mInfoFragment;
                break;
            default:
                return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.content, mCurrentFragment);
        ft.commit();
        mDrawerList.setItemChecked(position, true);
        setTitle(mFunctionsString[position]);
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
        mReader = BTReader.getReader(mContext, mMainHandler);
        if (mReader != null)
            openResult = mReader.SD_Open();
        if (openResult == SDConsts.RF_OPEN_SUCCESS) {
            Log.i(TAG, "Reader opened");
        }
        else if (openResult == SDConsts.RF_OPEN_FAIL)
            if (D) Log.e(TAG, "Reader open failed");

        updateConnectState();
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
        if (D) Log.d(TAG, " onPause");
        super.onPause();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }
    
    @Override
    protected void onStop() {
        if (D) Log.d(TAG, " onStop");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mReader = BTReader.getReader(mContext, mMainHandler);
        if (mReader != null && mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
            mReader.BT_Disconnect();
        }
        mReader.SD_Close();
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
            super.onBackPressed();
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

    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mMainHandler");
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
        try {
            mDrawerLayout.closeDrawer(mDrawerList);
            if (mCurrentFragment != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.remove(mCurrentFragment);
                ft.commit();
                mCurrentFragment = null;
                mReader = BTReader.getReader(mContext, mMainHandler);
            }
            setTitle(getString(R.string.app_name));
            if (mUILayout.getVisibility() != View.VISIBLE)
                mUILayout.setVisibility(View.VISIBLE);
        }
        catch (java.lang.IllegalStateException e) {
        }
        return;
    }

    private void updateConnectState() {
        if (mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED)
            mIsConnected = true;
        else 
            mIsConnected = false;
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
        if (m.what == MSG_OPTION_CONNECT_STATE_CHANGED) {
            updateConnectState();
        }
        else if (m.what == MSG_BACK_PRESSED)
            switchToHome();
    }
}