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
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class RFConfigFragment extends Fragment {
    private static final String TAG = RFConfigFragment.class.getSimpleName(); 
    
    private static final boolean D = Constants.CFG_D;
    
    private Context mContext;

    private Spinner mRegionSpin;
    private ArrayAdapter<CharSequence> mRegionChar;
    private Button mSetRegionBt;
    private Button mGetRegionBt;
    private Button mGetAvailableRegionBt;

    private Spinner mTargetSpin;
    private ArrayAdapter<CharSequence> mTargetChar;
    private Button mSetTargetBt;
    private Button mGetTargetBt;

    private EditText mDutyEditText;
    private Button mSetDutyButton;
    private Button mGetDutyButton;
    
    private EditText mAccessTimeoutEditText;
    private Button mSetAccessTimeoutButton;
    private Button mGetAccessTimeoutButton;
    
    private EditText mPowerEditText;
    private Button mSetPowerButton;
    private Button mGetPowerButton;

    private EditText mSingulationEditText;
    private Button mSetSingulationButton;
    private Button mGetSingulationButton;

    private EditText mRFmodeEditText;
    private Button mSetRFmodeButton;
    private Button mGetRFmodeButton;

    private EditText mDwellEditText;
    private Button mSetDwellButton;
    private Button mGetDwellButton;

    private Spinner mToggleSpin;
    private ArrayAdapter<CharSequence> mToggleChar;
    private Button mSetToggleBt;
    private Button mGetToggleBt;

    private Spinner mRssiSpin;
    private ArrayAdapter<CharSequence> mRssiChar;
    private Button mSetRssiBt;
    private Button mGetRssiBt;

    private Reader mReader;

    private ProgressDialog mDialog;
    
    private Handler mOptionHandler;
    
    private final RFConfigHandler mRFConfigHandler = new RFConfigHandler(this);
    
    public static RFConfigFragment newInstance() {
        return new RFConfigFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.rf_config_frag, container,false);

        mContext = inflater.getContext();

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        mSetRegionBt = (Button)v.findViewById(R.id.bt_set_region);
        mSetRegionBt.setOnClickListener(sledListener);
        mGetRegionBt = (Button)v.findViewById(R.id.bt_get_region);
        mGetRegionBt.setOnClickListener(sledListener);
        mGetAvailableRegionBt = (Button)v.findViewById(R.id.bt_get_available_region);
        mGetAvailableRegionBt.setOnClickListener(sledListener);        
        mRegionSpin = (Spinner)v.findViewById(R.id.region_spin);
        mRegionChar = ArrayAdapter.createFromResource(mContext, R.array.region_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mRegionSpin.setAdapter(mRegionChar);
    

        mSetTargetBt = (Button)v.findViewById(R.id.bt_set_target);
        mSetTargetBt.setOnClickListener(sledListener);
        mGetTargetBt = (Button)v.findViewById(R.id.bt_get_target);
        mGetTargetBt.setOnClickListener(sledListener);
        mTargetSpin = (Spinner)v.findViewById(R.id.target_spin);
        mTargetChar = ArrayAdapter.createFromResource(mContext, R.array.target_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mTargetSpin.setAdapter(mTargetChar);

        mDutyEditText = (EditText)v.findViewById(R.id.duty_edit);
        mSetDutyButton = (Button)v.findViewById(R.id.set_duty_button);
        mSetDutyButton.setOnClickListener(sledListener);
        mGetDutyButton = (Button)v.findViewById(R.id.get_duty_button);
        mGetDutyButton.setOnClickListener(sledListener);
    
        mAccessTimeoutEditText = (EditText)v.findViewById(R.id.accesstime_edit);
        mSetAccessTimeoutButton = (Button)v.findViewById(R.id.set_accesstime_button);
        mSetAccessTimeoutButton.setOnClickListener(sledListener);
        mGetAccessTimeoutButton = (Button)v.findViewById(R.id.get_accesstime_button);
        mGetAccessTimeoutButton.setOnClickListener(sledListener);
    
        mPowerEditText = (EditText)v.findViewById(R.id.power_edit);
        mSetPowerButton = (Button)v.findViewById(R.id.set_power_button);
        mSetPowerButton.setOnClickListener(sledListener);
        mGetPowerButton = (Button)v.findViewById(R.id.get_power_button);
        mGetPowerButton.setOnClickListener(sledListener);
        
        mSingulationEditText = (EditText)v.findViewById(R.id.singulation_edit);
        mSetSingulationButton = (Button)v.findViewById(R.id.set_singulation_button);
        mSetSingulationButton.setOnClickListener(sledListener);
        mGetSingulationButton = (Button)v.findViewById(R.id.get_singulation_button);
        mGetSingulationButton.setOnClickListener(sledListener);
        
        mRFmodeEditText = (EditText)v.findViewById(R.id.rfmode_edit);
        mSetRFmodeButton = (Button)v.findViewById(R.id.set_rfmode_button);
        mSetRFmodeButton.setOnClickListener(sledListener);
        mGetRFmodeButton = (Button)v.findViewById(R.id.get_rfmode_button);
        mGetRFmodeButton.setOnClickListener(sledListener);
        
        mDwellEditText = (EditText)v.findViewById(R.id.dwell_edit);
        mSetDwellButton = (Button)v.findViewById(R.id.set_dwell_button);
        mSetDwellButton.setOnClickListener(sledListener);
        mGetDwellButton = (Button)v.findViewById(R.id.get_dwell_button);
        mGetDwellButton.setOnClickListener(sledListener);
        

        mSetToggleBt = (Button)v.findViewById(R.id.bt_set_toggle);
        mSetToggleBt.setOnClickListener(sledListener);
        mGetToggleBt = (Button)v.findViewById(R.id.bt_get_toggle);
        mGetToggleBt.setOnClickListener(sledListener);
        mToggleSpin = (Spinner)v.findViewById(R.id.toggle_spin);
        mToggleChar = ArrayAdapter.createFromResource(mContext, R.array.on_off_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mToggleSpin.setAdapter(mToggleChar);

        mSetRssiBt = (Button)v.findViewById(R.id.bt_set_rssi);
        mSetRssiBt.setOnClickListener(sledListener);
        mGetRssiBt = (Button)v.findViewById(R.id.bt_get_rssi);
        mGetRssiBt.setOnClickListener(sledListener);
        mRssiSpin = (Spinner)v.findViewById(R.id.rssi_spin);
        mRssiChar = ArrayAdapter.createFromResource(mContext, R.array.on_off_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mRssiSpin.setAdapter(mRssiChar);

        return v;
    }

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mReader = Reader.getReader(mContext, mRFConfigHandler);
        if (mReader != null && mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {
            mDutyEditText.setText(Integer.toString(mReader.RF_GetDutyCycle()));
            mAccessTimeoutEditText.setText(Integer.toString(mReader.RF_GetAccessTimeout()));
            mPowerEditText.setText(Integer.toString(mReader.RF_GetRadioPowerState()));
            mSingulationEditText.setText(Integer.toString(mReader.RF_GetSingulationControl()));
            mRFmodeEditText.setText(Integer.toString(mReader.RF_GetRFMode()));
            mDwellEditText.setText(Integer.toString(mReader.RF_GetDwelltime()));

            int v = mReader.RF_GetRegion();
            if (v == SDConsts.RFRegion.NOT_SETTED)
                mRegionSpin.setSelection(mRegionChar.getCount() - 1);
            else if (v < SDConsts.RFRegion.KOREA || v > SDConsts.RFRegion.CHILE)
                mRegionSpin.setSelection(0);
            else
                mRegionSpin.setSelection(v);
            v =  mReader.RF_GetInventorySessionTarget();
            if (v < SDConsts.RFInvSessionTarget.TARGET_A || v > SDConsts.RFInvSessionTarget.TARGET_B)
                mTargetSpin.setSelection(0);
            else
                mTargetSpin.setSelection(v);
            v = mReader.RF_GetToggle();
            if (v < SDConsts.RFToggle.OFF || v > SDConsts.RFToggle.ON)
                mToggleSpin.setSelection(0);
            else
                mToggleSpin.setSelection(v);
            v = mReader.RF_GetRssiTrackingState();
            if (v < SDConsts.RFRssi.OFF || v > SDConsts.RFRssi.ON)
                mRssiSpin.setSelection(0);
            else
                mRssiSpin.setSelection(v);
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
        if (D) Log.d(TAG, " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (D) Log.d(TAG, " onStop");
        closeDialog();
        super.onStop();
    }

    private OnClickListener sledListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (D) Log.d(TAG, "sledListener");
            String value;
            int spinPos = 0;
            int id = v.getId();
            int ret = -1;
            switch (id) {
            case R.id.bt_set_region:
                spinPos = mRegionSpin.getSelectedItemPosition();
                if (spinPos == mRegionChar.getCount() - 1)
                    Toast.makeText(mContext, "Can't set \"NOT_SETTED\"", Toast.LENGTH_SHORT).show();
                else {
                    ret = mReader.RF_SetRegion(spinPos);
                    Toast.makeText(mContext, "Set region result = " + ret, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_get_region:
                ret = mReader.RF_GetRegion();
                Toast.makeText(mContext, "Get region result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_get_available_region:
                value = mReader.RF_GetAvailableRegionAtThisDevice();
                createAlertDialog(value);
                break;
            case R.id.bt_set_target:
                spinPos = mTargetSpin.getSelectedItemPosition();
                ret = mReader.RF_SetInventorySessionTarget(spinPos);
                Toast.makeText(mContext, "Set inv session target result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_get_target:
                ret = mReader.RF_GetInventorySessionTarget();
                Toast.makeText(mContext, "Get inv session target result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_duty_button:
                value = mDutyEditText.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            int var = Integer.parseInt(value);
                            if (var < SDConsts.RFDutyCycle.MIN_DUTY || var > SDConsts.RFDutyCycle.MAX_DUTY) {
                                Toast.makeText(mContext, "Set duty cycle range = " + SDConsts.RFDutyCycle.MIN_DUTY + " ~ " + 
                                        SDConsts.RFDutyCycle.MAX_DUTY, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            mReader.RF_SetDutyCycle(var);
                            Toast.makeText(mContext, "Set duty cycle", Toast.LENGTH_SHORT).show();
                            return;
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                Toast.makeText(mContext, "Failed set duty cycle", Toast.LENGTH_SHORT).show();
                break;
            case R.id.get_duty_button:
                ret = mReader.RF_GetDutyCycle();
                Toast.makeText(mContext, "Get Duty result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_accesstime_button:
                value = mAccessTimeoutEditText.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            int var = Integer.parseInt(value);
                            if (var < SDConsts.RFAccessTimeout.MIN_ACCESS_TIMEOUT || var > SDConsts.RFAccessTimeout.MAX_ACCESS_TIMEOUT) {
                                Toast.makeText(mContext, "Set access timeout = " + SDConsts.RFAccessTimeout.MIN_ACCESS_TIMEOUT + " ~ " +
                                        SDConsts.RFAccessTimeout.MAX_ACCESS_TIMEOUT, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            mReader.RF_SetAccessTimeout(var);
                            Toast.makeText(mContext, "Set access timeout", Toast.LENGTH_SHORT).show();
                            return;
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                Toast.makeText(mContext, "Failed set access timeout", Toast.LENGTH_SHORT).show();
                break;
            case R.id.get_accesstime_button:
                ret = mReader.RF_GetAccessTimeout();
                Toast.makeText(mContext, "Get access timeout result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_power_button:
                value = mPowerEditText.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            int var = Integer.parseInt(value);
                            if (var < SDConsts.RFPower.MIN_POWER || var > SDConsts.RFPower.MAX_POWER) {
                                Toast.makeText(mContext, "set power range = " + SDConsts.RFPower.MIN_POWER + " ~ " + 
                                        SDConsts.RFPower.MAX_POWER, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            mReader.RF_SetRadioPowerState(var);
                            Toast.makeText(mContext, "set power", Toast.LENGTH_SHORT).show();
                            return;
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                Toast.makeText(mContext, "failed set power", Toast.LENGTH_SHORT).show();
                break;
            case R.id.get_power_button:
                ret = mReader.RF_GetRadioPowerState();
                Toast.makeText(mContext, "Get power result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_rfmode_button:
                value = mRFmodeEditText.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            int var = Integer.parseInt(value);
                            if (var < SDConsts.RFMode.DSB_ASK_1 || var > SDConsts.RFMode.DSB_ASK_2) {
                                Toast.makeText(mContext, "Set rfmode range = " + SDConsts.RFMode.DSB_ASK_1 + " ~ " 
                                        + SDConsts.RFMode.DSB_ASK_2, Toast.LENGTH_SHORT).show();
                                break;
                            }

                            mReader.RF_SetRFMode(var);
                            Toast.makeText(mContext, "Set rfmode", Toast.LENGTH_SHORT).show();
                            return;
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                Toast.makeText(mContext, "Failed set rfmode", Toast.LENGTH_SHORT).show();
                break;
            case R.id.get_rfmode_button:
                ret = mReader.RF_GetRFMode();
                Toast.makeText(mContext, "Get rfmode result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_dwell_button:
                value = mDwellEditText.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            int var = Integer.parseInt(value);
                            if (var < SDConsts.RFDwell.MIN_DWELL || var > SDConsts.RFDwell.MAX_DWELL) {
                                Toast.makeText(mContext, "set dwell range = " + SDConsts.RFDwell.MIN_DWELL + " ~ " + 
                                        SDConsts.RFDwell.MAX_DWELL, Toast.LENGTH_SHORT).show();
                                break;
                            }

                            mReader.RF_SetDwelltime(var);
                            Toast.makeText(mContext, "set dwelltime", Toast.LENGTH_SHORT).show();
                            return;
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                Toast.makeText(mContext, "failed set dwelltime", Toast.LENGTH_SHORT).show();
                break;
            case R.id.get_dwell_button:
                ret = mReader.RF_GetDwelltime();
                Toast.makeText(mContext, "Get dwelltime result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_singulation_button:
                value = mSingulationEditText.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            int var = Integer.parseInt(value);
                            if (var < SDConsts.RFSingulation.MIN_SINGULATION || var > SDConsts.RFSingulation.MAX_SINGULATION) {
                                Toast.makeText(mContext, "set singulation range = " + SDConsts.RFSingulation.MIN_SINGULATION + " ~ " + 
                                        SDConsts.RFSingulation.MAX_SINGULATION, Toast.LENGTH_SHORT).show();
                                break;
                            }

                            mReader.RF_SetSingulationControl(var, SDConsts.RFSingulation.MIN_SINGULATION, SDConsts.RFSingulation.MAX_SINGULATION);
                            Toast.makeText(mContext, "set singulation", Toast.LENGTH_SHORT).show();
                            return;
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                Toast.makeText(mContext, "failed set singulation", Toast.LENGTH_SHORT).show();
                break;
            case R.id.get_singulation_button:
                ret = mReader.RF_GetSingulationControl();
                Toast.makeText(mContext, "Get singulation min = " + mReader.RF_GetMinSingulationControl() + 
                        " start = " + ret + " max = " + mReader.RF_GetMaxSingulationControl(), 
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_set_toggle:
                spinPos = mToggleSpin.getSelectedItemPosition();
                ret = mReader.RF_SetToggle(spinPos);
                Toast.makeText(mContext, "Set toggle result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_get_toggle:
                ret = mReader.RF_GetToggle();
                Toast.makeText(mContext, "Get toggle result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_set_rssi:
                spinPos = mRssiSpin.getSelectedItemPosition();
                ret = mReader.RF_SetRssiTrackingState(spinPos);
                Toast.makeText(mContext, "Set rssi result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_get_rssi:
                ret = mReader.RF_GetRssiTrackingState();
                Toast.makeText(mContext, "Get rssi result = " + ret, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    private static class RFConfigHandler extends Handler {
        private final WeakReference<RFConfigFragment> mExecutor;
        public RFConfigHandler(RFConfigFragment f) {
            mExecutor = new WeakReference<>(f);
        }
        
        @Override
        public void handleMessage(Message msg) {
            RFConfigFragment executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }
    
    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mRFConfigHandler");
        if (D) Log.d(TAG, "m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        int result = m.arg2;
        switch (m.what) {
        case SDConsts.Msg.RFMsg:
            switch(m.arg1) {
            case SDConsts.RFCmdMsg.REGION_CHANGE_START:
                if (result == SDConsts.RFResult.SUCCESS) {
                    createDialog("Region is changing...");
                }
                break;
            case SDConsts.RFCmdMsg.REGION_CHANGE_END:
                closeDialog();
                int v = mReader.RF_GetRegion();
                if (v == SDConsts.RFRegion.NOT_SETTED)
                    mRegionSpin.setSelection(mRegionChar.getCount() - 1);
                else if (v < SDConsts.RFRegion.KOREA || v > SDConsts.RFRegion.CHILE)
                    mRegionSpin.setSelection(0);
                else
                    mRegionSpin.setSelection(v);
                break;
            }
            break;
        case SDConsts.Msg.SDMsg:
            if (m.arg1 == SDConsts.SDCmdMsg.SLED_UNKNOWN_DISCONNECTED) {
                if (mOptionHandler != null)
                    mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_DISCONNECTED).sendToTarget();
            }
            break;
        }
    }

    private void createDialog(String message) {
        if (mDialog != null) {
            if (mDialog.isShowing())
                mDialog.cancel();
            mDialog = null;
        }
        mDialog = new ProgressDialog(mContext);
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }
    
    private void createAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.available_regions_str));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.drawer_close), null);
        builder.show();
    }

    private void closeDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing())
                mDialog.cancel();
            mDialog = null;
        }
    }
}