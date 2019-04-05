/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 * 
 * http://www.bluebirdcorp.com/
 * 
 * Author : Bogon Jun
 *
 * Date : 2016.04.04
 */

package co.kr.bluebird.newrfid.app.bbrfidbtdemo.fragment;

import co.kr.bluebird.newrfid.app.bbrfidbtdemo.Constants;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.MainActivity;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.R;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class BatteryFragment extends Fragment {
    private static final String TAG = BatteryFragment.class.getSimpleName();

    private static final boolean D = Constants.BAT_D;

    private TextView mBatteryTv;
    
    private Button mGetChargeBt;

    private Button mGetBatBt;

    private TextView mMessageTextView;
    
    private ProgressBar mBatteryProgress;
    
    private BTReader mReader;

    private Context mContext;

    private Handler mOptionHandler;

    private Fragment mFragment;
    
    private final BatteryHandler mBatteryHandler = new BatteryHandler(this);
    
    public static BatteryFragment newInstance() {
        return new BatteryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.battery_frag, container, false);

        mContext = inflater.getContext();

        mFragment = this;

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        mBatteryTv = (TextView)v.findViewById(R.id.battery_state_textview);
        
        mMessageTextView = (TextView)v.findViewById(R.id.message_textview);

        mGetChargeBt = (Button)v.findViewById(R.id.bt_charge);
        mGetChargeBt.setOnClickListener(buttonListener);

        mGetBatBt = (Button)v.findViewById(R.id.bt_bat);
        mGetBatBt.setOnClickListener(buttonListener);
    
        mBatteryProgress = (ProgressBar)v.findViewById(R.id.batt_progress);
        return v;
    }

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mReader = BTReader.getReader(mContext, mBatteryHandler);
        if (mReader != null && mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
            int value = mReader.SD_GetBatteryStatus();
            Activity activity = getActivity();
            if (activity != null) {
                mBatteryTv.setText(activity.getString(R.string.battery_state_str) +
                        value + " " + activity.getString(R.string.percent_str));
                mBatteryProgress.setProgress(value);
            }
        }
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

    private OnClickListener buttonListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int ret = -100;
            String retString = null;
            switch (v.getId()) {
            case R.id.bt_charge:
                retString = "SD_GetChargeState ";
                ret = mReader.SD_GetChargeState();
                if (D) Log.d(TAG, "get charge state = " + ret);
                break;
            case R.id.bt_bat:
                retString = "SD_GetBatteryStatus ";
                ret = mReader.SD_GetBatteryStatus();
                if (D) Log.d(TAG, "bat = " + ret);
                Activity activity = getActivity();
                if (activity != null) {
                    mBatteryTv.setText(activity.getString(R.string.battery_state_str) + ret +
                            " " + activity.getString(R.string.percent_str));
                    mBatteryProgress.setProgress(ret);
                }
                break;
            }
            if (ret != -100) {
                retString += ret;
            }
            mMessageTextView.setText(" " + retString);
        }
    };
    
    private static class BatteryHandler extends Handler {
        private final WeakReference<BatteryFragment> mExecutor;
        public BatteryHandler(BatteryFragment f) {
            mExecutor = new WeakReference<>(f);
        }
        
        @Override
        public void handleMessage(Message msg) {
            BatteryFragment executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }
    
    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mBatteryHandler");
        if (D) Log.d(TAG, "command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
        
        switch (m.what) {
        case SDConsts.Msg.SDMsg:
            //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
            if (m.arg1 == SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED) {
                Activity activity = getActivity();
                if (activity != null) {
                    mBatteryTv.setText(activity.getString(R.string.battery_state_str) +
                            m.arg2 + " " + activity.getString(R.string.percent_str));
                    mBatteryProgress.setProgress(m.arg2);
                }
            }
            else if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                    Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                    Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(mFragment).attach(mFragment).commit();
            }
            else if (m.arg1 == SDConsts.SDCmdMsg.SLED_UNKNOWN_DISCONNECTED) {
            }
            break;
        case SDConsts.Msg.BTMsg:
            if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);
                if (mOptionHandler != null)
                    mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_CONNECT_STATE_CHANGED).sendToTarget();
            }
            break;
        }
    }
}