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

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.MainActivity;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.ref.WeakReference;

public class InfoFragment extends Fragment {
    
    private static final String TAG = InfoFragment.class.getSimpleName();

    private static final boolean D = Constants.INFO_D;

    private TextView mOSVersionTv;
    
    private TextView mRFIDLibVersionTv;
    
    private TextView mRFIDModuleVersion;

    private TextView mSDFirmwareVersion;
    
    private TextView mSDBTFirmwareVersion;

    private TextView mSDSerialNumber;
    
    private TextView mSDBootloaderBersion;
    
    private TextView mAppVersion;

    private Reader mReader;

    private Context mContext;
    
    private Handler mOptionHandler;
    
    private InfoHandler mInfoHandler = new InfoHandler(this);
    
    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.info_frag, container, false);

        mContext = inflater.getContext();

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        mOSVersionTv = (TextView)v.findViewById(R.id.tv_os_version);
        mRFIDLibVersionTv = (TextView)v.findViewById(R.id.tv_rf_lib_version);
        mRFIDModuleVersion = (TextView)v.findViewById(R.id.tv_rfid_version);
        mSDFirmwareVersion = (TextView)v.findViewById(R.id.tv_sd_firm_version);
        mSDBTFirmwareVersion = (TextView)v.findViewById(R.id.tv_sd_bt_firm_version);
        mSDSerialNumber = (TextView)v.findViewById(R.id.tv_sd_serial_number);
        mSDBootloaderBersion = (TextView)v.findViewById(R.id.tv_bootloader_version);
        mAppVersion = (TextView)v.findViewById(R.id.tv_app_version);
        return v;
    }

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mReader = Reader.getReader(mContext, mInfoHandler);
        mOSVersionTv.setText(Build.DISPLAY);
        if (mReader != null && mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {
            mRFIDLibVersionTv.setText(mReader.RF_GetLibVersion());
            mRFIDModuleVersion.setText(mReader.RF_GetRFIDVersion());
            mSDFirmwareVersion.setText(mReader.SD_GetVersion());
            mSDSerialNumber.setText(mReader.SD_GetSerialNumber());
            mSDBootloaderBersion.setText(mReader.SD_GetBootLoaderVersion());
            mSDBTFirmwareVersion.setText(mReader.SD_GetBTVersion());
        }
        mAppVersion.setText(Constants.VERSION);
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
    
    private static class InfoHandler extends Handler {
        private final WeakReference<InfoFragment> mExecutor;
        public InfoHandler(InfoFragment f) {
            mExecutor = new WeakReference<>(f);
        }
        
        @Override
        public void handleMessage(Message msg) {
            InfoFragment executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }

    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mInfoHandler");
        if (D) Log.d(TAG, "command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
        
        switch (m.what) {
        case SDConsts.Msg.SDMsg:
            if (m.arg1 == SDConsts.SDCmdMsg.SLED_UNKNOWN_DISCONNECTED) {
                if (mOptionHandler != null)
                    mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_DISCONNECTED).sendToTarget();
            }
            break;
        }
    }
}