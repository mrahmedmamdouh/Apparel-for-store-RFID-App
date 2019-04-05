/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 * 
 * http://www.bluebirdcorp.com/
 * 
 * Author : Bogon Jun
 *
 * Date : 2016.04.04
 */

package co.kr.bluebird.rfid.app.bbrfidbtdemo.fragment;

import co.kr.bluebird.rfid.app.bbrfidbtdemo.Constants;
import co.kr.bluebird.rfid.app.bbrfidbtdemo.fileutil.FileSelectorDialog;
import co.kr.bluebird.rfid.app.bbrfidbtdemo.MainActivity;
import co.kr.bluebird.rfid.app.bbrfidbtdemo.R;
import co.kr.bluebird.rfid.app.bbrfidbtdemo.permission.PermissionHelper;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.lang.ref.WeakReference;

public class SDFragment extends Fragment {
    private static final String TAG = SDFragment.class.getSimpleName();

    private static final boolean D = Constants.SD_D;
    
    private static final String FILE_DIR_PRE = "//DIR//";
    
    private static final String FILE_EXT = ".bin";
    
    private static final int PROGRESS_DIALOG = 1;

    private Button mSetRfidBt;
    private Button mSetBarcodeBt;
    private Button mGetTriggerBt;

    private Spinner mSleepSpin;
    private ArrayAdapter<CharSequence> mSleepChar;
    private Button mSetSleepBt;
    private Button mGetSleepBt;

    private Spinner mBuzzerLVSpin;
    private ArrayAdapter<CharSequence> mBuzzerLvChar;
    private Button mGetBuzzerBt;
    private Button mSetBuzzerBt;

    private Button mSetBuzzerNoisy;
    private Button mSetBuzzerMute;
    private Button mGetBuzzerMute;

    private Button mSetModeKeyEnable;
    private Button mSetModeKeyDisable;
    private Button mGetModeKeyEnableState;
    
    private Button mSetTriggerKeyEnable;
    private Button mSetTriggerKeyDisable;
    private Button mGetTriggerKeyEnableState;
    
    private Button mGetChargeBt;

    private Button mGetBatBt;

    private Button mGetFirmBt;

    private Button mGetSerialNumber;

    private Button mUpdateSled;

    private Button mGetBTFirmBt;
    
    private Button mGetBTNameBt;
    private Button mSetBTNameBt;
    
    private Button mSetSDDefaultBt;
    
    
    private TextView mMessageTextView;

    private BTReader mReader;

    private ProgressDialog mDialog;

    private Context mContext;

    private Handler mOptionHandler;

    private FileSelectorDialog mFileSelecter;
    
    private SDHandler mSDHandler = new SDHandler(this);
    
    public static SDFragment newInstance() {
        return new SDFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.sd_frag, container, false);

        mContext = inflater.getContext();

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        mMessageTextView = (TextView)v.findViewById(R.id.message_textview);

        mSetRfidBt = (Button)v.findViewById(R.id.bt_settigger_rfid);
        mSetRfidBt.setOnClickListener(buttonListener);
        mSetBarcodeBt = (Button)v.findViewById(R.id.bt_settigger_barcode);
        mSetBarcodeBt.setOnClickListener(buttonListener);
        mGetTriggerBt = (Button)v.findViewById(R.id.bt_trigger);
        mGetTriggerBt.setOnClickListener(buttonListener);

        
        mSetSleepBt = (Button)v.findViewById(R.id.bt_sleep);
        mSetSleepBt.setOnClickListener(buttonListener);
        mGetSleepBt = (Button)v.findViewById(R.id.bt_get_sleep);
        mGetSleepBt.setOnClickListener(buttonListener);
        mSleepSpin = (Spinner)v.findViewById(R.id.sleep_spin);
        mSleepChar = ArrayAdapter.createFromResource(mContext, R.array.sleep_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mSleepSpin.setAdapter(mSleepChar);

        mSetBuzzerBt = (Button)v.findViewById(R.id.bt_buzzer);
        mSetBuzzerBt.setOnClickListener(buttonListener);
        mGetBuzzerBt = (Button)v.findViewById(R.id.bt_get_buzzer);
        mGetBuzzerBt.setOnClickListener(buttonListener);
        mBuzzerLVSpin = (Spinner)v.findViewById(R.id.buzzer_lv_spin);
        mBuzzerLvChar = ArrayAdapter.createFromResource(mContext, R.array.buzzerlevel_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mBuzzerLVSpin.setAdapter(mBuzzerLvChar);

        mSetBuzzerMute = (Button)v.findViewById(R.id.bt_setbuzzermute);
        mSetBuzzerMute.setOnClickListener(buttonListener);
        mSetBuzzerNoisy = (Button)v.findViewById(R.id.bt_setbuzzernoisy);
        mSetBuzzerNoisy.setOnClickListener(buttonListener);
        mGetBuzzerMute = (Button)v.findViewById(R.id.bt_getbuzzermute);
        mGetBuzzerMute.setOnClickListener(buttonListener);
        
        
        mSetModeKeyEnable = (Button)v.findViewById(R.id.bt_mode_key_enable);
        mSetModeKeyEnable.setOnClickListener(buttonListener);
        mSetModeKeyDisable = (Button)v.findViewById(R.id.bt_mode_key_disable);
        mSetModeKeyDisable.setOnClickListener(buttonListener);
        mGetModeKeyEnableState = (Button)v.findViewById(R.id.bt_get_mode_key_state);
        mGetModeKeyEnableState.setOnClickListener(buttonListener);
        
        mSetTriggerKeyEnable = (Button)v.findViewById(R.id.bt_trg_key_enable);
        mSetTriggerKeyEnable.setOnClickListener(buttonListener);
        mSetTriggerKeyDisable = (Button)v.findViewById(R.id.bt_trg_key_disable);
        mSetTriggerKeyDisable.setOnClickListener(buttonListener);
        mGetTriggerKeyEnableState = (Button)v.findViewById(R.id.bt_get_trg_key_state);
        mGetTriggerKeyEnableState.setOnClickListener(buttonListener);
        
        
        
        mGetChargeBt = (Button)v.findViewById(R.id.bt_charge);
        mGetChargeBt.setOnClickListener(buttonListener);

        mGetBatBt = (Button)v.findViewById(R.id.bt_bat);
        mGetBatBt.setOnClickListener(buttonListener);
        
        mGetFirmBt = (Button)v.findViewById(R.id.bt_firm);
        mGetFirmBt.setOnClickListener(buttonListener);
        
        mGetSerialNumber = (Button)v.findViewById(R.id.bt_get_serial);
        mGetSerialNumber.setOnClickListener(buttonListener);

        mUpdateSled = (Button)v.findViewById(R.id.bt_update_firm);
        mUpdateSled.setOnClickListener(buttonListener);
        
        mGetBTFirmBt = (Button)v.findViewById(R.id.bt_get_bt_ver);
        mGetBTFirmBt.setOnClickListener(buttonListener);

        
        mGetBTNameBt = (Button)v.findViewById(R.id.bt_get_bt_name);
        mGetBTNameBt.setOnClickListener(buttonListener);
        mSetBTNameBt = (Button)v.findViewById(R.id.bt_set_bt_name);
        mSetBTNameBt.setOnClickListener(buttonListener);
        
        mSetSDDefaultBt = (Button)v.findViewById(R.id.bt_set_sd_default_all);
        mSetSDDefaultBt.setOnClickListener(buttonListener);
        return v;
    }

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mReader = BTReader.getReader(mContext, mSDHandler);
        if (mReader != null && mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
            int v = mReader.SD_GetAutoSleepTimeout();
            if (v < SDConsts.SDSleepTimeout.NO_SLEEP || v > SDConsts.SDSleepTimeout.MIN_10)
                mSleepSpin.setSelection(0);
            else
                mSleepSpin.setSelection(mReader.SD_GetAutoSleepTimeout());
            v = mReader.SD_GetBuzzerLevel();
            if (v < SDConsts.SDBuzzerLevel.LOW || v > SDConsts.SDBuzzerLevel.HIGH)
                mBuzzerLVSpin.setSelection(0);
            else
                mBuzzerLVSpin.setSelection(mReader.SD_GetBuzzerLevel());
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
                                createAlertDialog(getString(R.string.warning_str), getString(R.string.update_warning_sled_str));
                        }
                    }
                    break;
            }
        }
    }

    private OnClickListener buttonListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int ret = -100;
            String retString = null;
            int mode, spinPos, state;
            switch (v.getId()) {
            case R.id.bt_settigger_rfid:
                retString = "SD_RFID_Mode ";
                mode = mReader.SD_GetTriggerMode();
                if (mode == SDConsts.SDTriggerMode.BARCODE)
                    ret = mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.RFID);
                if (D) Log.d(TAG, "set rfid cur mode = " + mode + " ret = " + ret);
                break;
            case R.id.bt_settigger_barcode:
                retString = "SD_BARCODE_Mode ";
                mode = mReader.SD_GetTriggerMode();
                if (mode == SDConsts.SDTriggerMode.RFID)
                    ret = mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
                if (D) Log.d(TAG, "set barcode cur mode = " + mode + " ret = " + ret);
                break;
            case R.id.bt_trigger:
                retString = "SD_GetTriggerMode ";
                ret = mReader.SD_GetTriggerMode();
                if (ret == SDConsts.SDTriggerMode.BARCODE)
                    if (D) Log.d(TAG, "barcode");
                else if (ret == SDConsts.SDTriggerMode.RFID)
                    if (D) Log.d(TAG, "rfid");
                else
                    if (D) Log.d(TAG, "other state");
                if (D) Log.d(TAG, "get trigger mode = " + ret);
                break;
            case R.id.bt_sleep:
                retString = "SD_SetAutoSleepTimeout ";
                spinPos = mSleepSpin.getSelectedItemPosition();
                // ex> SDConsts.SDSleepTimeout.SEC_30;
                ret = mReader.SD_SetAutoSleepTimeout(spinPos);
                if (D) Log.d(TAG, "set sleep timeout = " + ret);
                break;
            case R.id.bt_get_sleep:
                retString = "SD_GetAutoSleepTimeout ";
                ret = mReader.SD_GetAutoSleepTimeout();
                if (D) Log.d(TAG, "get sleep timeout = " + ret);
                break;
            case R.id.bt_buzzer:
                retString = "SD_SetBuzzerLevel ";
                spinPos = mBuzzerLVSpin.getSelectedItemPosition();
                // ex> SDConsts.SDBuzzerLevel.LOW
                ret = mReader.SD_SetBuzzerLevel(spinPos);
                if (D) Log.d(TAG, "set buzzer = " + ret);
                break;
            case R.id.bt_get_buzzer:
                retString = "SD_GetBuzzerLevel ";
                ret = mReader.SD_GetBuzzerLevel();
                if (D) Log.d(TAG, "get buzzer = " + ret);
                break;
            case R.id.bt_setbuzzermute:
                retString = "SD_SetBuzzerMute ";
                state = mReader.SD_GetBuzzerState();
                if (state >= SDConsts.SDBuzzerState.NOISY)
                    ret = mReader.SD_SetBuzzerEnable(SDConsts.SDBuzzerState.MUTE);
                if (D) Log.d(TAG, "set buzzer mute = " + ret);
                break;
            case R.id.bt_setbuzzernoisy:
                retString = "SD_SetBuzzerNoisy ";
                state = mReader.SD_GetBuzzerState();
                if (state <= SDConsts.SDBuzzerState.MUTE)
                    ret = mReader.SD_SetBuzzerEnable(SDConsts.SDBuzzerState.NOISY);
                if (D) Log.d(TAG, "set buzzer noisy = " + ret);
                break;
            case R.id.bt_getbuzzermute:
                retString = "SD_GetBuzzerMute ";
                ret = mReader.SD_GetBuzzerState();
                if (D) Log.d(TAG, "get buzzer mute state = " + ret);
                break;
            case R.id.bt_charge:
                retString = "SD_GetChargeState ";
                ret = mReader.SD_GetChargeState();
                if (D) Log.d(TAG, "get charge state = " + ret);
                break;
            case R.id.bt_bat:
                retString = "SD_GetBatteryStatus ";
                ret = mReader.SD_GetBatteryStatus();
                if (D) Log.d(TAG, "bat = " + ret);
                break;
            case R.id.bt_firm:
                retString = "SD_GetVersion ";
                retString += mReader.SD_GetVersion();
                if (D) Log.d(TAG, "sd firm version= " + retString);
                break;
            case R.id.bt_get_serial:
                retString = "SD_GetSerialNumber ";
                retString += mReader.SD_GetSerialNumber();
                if (D) Log.d(TAG, "sd firm version= " + retString);
                break;
            case R.id.bt_update_firm:
                retString = "SD_UpdateSDFirmware ";
                ret = 0;
                boolean b = PermissionHelper.checkPermission(mContext, PermissionHelper.mStoragePerms[0]);
                if (b)
                    createAlertDialog(getString(R.string.warning_str), getString(R.string.update_warning_sled_str));
                else
                    PermissionHelper.requestPermission(getActivity(), PermissionHelper.mStoragePerms);
                break;
            case R.id.bt_mode_key_enable:
                retString = "SD_SetModeKeyEnable ";
                ret = mReader.SD_SetModeKeyEnable(SDConsts.SDModeKeyState.ENABLE);
                if (D) Log.d(TAG, "set modekey enable = " + ret);
                break;
            case R.id.bt_mode_key_disable:
                retString = "SD_SetModeKeyEnable ";
                ret = mReader.SD_SetModeKeyEnable(SDConsts.SDModeKeyState.DISABLE);
                if (D) Log.d(TAG, "set modekey disable = " + ret);
                break;
            case R.id.bt_get_mode_key_state:
                retString = "SD_GetModeKeyEnableState ";
                ret = mReader.SD_GetModeKeyEnableState();
                if (D) Log.d(TAG, "get modekey enable state = " + ret);
                break;
            case R.id.bt_trg_key_enable:
                retString = "SD_SetTriggerKeyEnable ";
                ret = mReader.SD_SetTriggerKeyEnable(SDConsts.SDTriggerKeyState.ENABLE);
                if (D) Log.d(TAG, "set triggerkey enable = " + ret);
                break;
            case R.id.bt_trg_key_disable:
                retString = "SD_SetTriggerKeyEnable ";
                ret = mReader.SD_SetTriggerKeyEnable(SDConsts.SDTriggerKeyState.DISABLE);
                if (D) Log.d(TAG, "set triggerkey disable = " + ret);
                break;
            case R.id.bt_get_trg_key_state:
                retString = "SD_GetTriggerKeyEnableState ";
                ret = mReader.SD_GetTriggerKeyEnableState();
                if (D) Log.d(TAG, "get triggerkey enable state = " + ret);
                break;
            case R.id.bt_get_bt_ver:
                retString = "SD_GetBTVersion ";
                //retString += mReader.SD_GetSLEDBTName();
                retString += mReader.SD_GetBTVersion();
                if (D) Log.d(TAG, "sd bt firm version= " + retString);
                break;
            case R.id.bt_get_bt_name:
                retString = "SD_GetBTName ";
                retString += mReader.SD_GetBTName();
                if (D) Log.d(TAG, "get bt name= " + retString);
                break;
            case R.id.bt_set_sd_default_all:
                retString = "SD_ResetConfiguration ";
                ret = mReader.SD_ResetConfiguration();
                if (D) Log.d(TAG, "reset sled to default = " + ret);
                break;
            case R.id.bt_set_bt_name:
                retString = "SD_SetBTName ";
                //retString += mReader.SD_SetBTName("hello");
                if (D) Log.d(TAG, "set bt name= " + retString);
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle(getString(R.string.set_bt_name));
                alert.setMessage(getString(R.string.please_input_name));
                final EditText input = new EditText(mContext);
                alert.setView(input);
                
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mReader.SD_SetBTName(input.getText().toString());
                    }
                });
                alert.setNegativeButton("CANCEL" ,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show(); 
                break;
                
            }
            if (ret != -100) {
                retString += ret;
            }
            mMessageTextView.setText(" " + retString);
        }
    };
    
    private static class SDHandler extends Handler {
        private final WeakReference<SDFragment> mExecutor;
        public SDHandler(SDFragment f) {
            mExecutor = new WeakReference<>(f);
        }
        
        @Override
        public void handleMessage(Message msg) {
            SDFragment executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }
    
    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mSDHandler");
        if (D) Log.d(TAG, "command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
        
        switch (m.what) {
        case SDConsts.Msg.SDMsg:
            if (m.arg1 == SDConsts.SDCmdMsg.TRIGGER_PRESSED)
                mMessageTextView.setText(" " + "TRIGGER_PRESSED");
            else if (m.arg1 == SDConsts.SDCmdMsg.TRIGGER_RELEASED)
                mMessageTextView.setText(" " + "TRIGGER_RELEASED");
            else if (m.arg1 == SDConsts.SDCmdMsg.SLED_MODE_CHANGED)
                mMessageTextView.setText(" " + "SLED_MODE_CHANGED " + m.arg2);
            else if (m.arg1 == SDConsts.SDCmdMsg.UPDATE_SD_FW_START) {
                mMessageTextView.setText(" " + "UPDATE_SD_FW_START " + m.arg2);
                if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                    Activity activity = getActivity();
                    if (activity != null)
                        createDialog(PROGRESS_DIALOG, activity.getString(R.string.update_sled_firmware_str));
                }
            }
            else if (m.arg1 == SDConsts.SDCmdMsg.UPDATE_SD_FW) {
                setProgressState(m.arg2);
            }
            else if (m.arg1 == SDConsts.SDCmdMsg.UPDATE_SD_FW_END) {
                closeDialog();
                mMessageTextView.setText(" " + "UPDATE_SD_FW_END " + m.arg2);
            }
            else if (m.arg1 == SDConsts.SDCmdMsg.SLED_UNKNOWN_DISCONNECTED) {
            }
            break;
            
        case SDConsts.Msg.BCMsg:
            StringBuilder readData = new StringBuilder();
            if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_TRIGGER_PRESSED)
                mMessageTextView.setText(" " + "BARCODE_TRIGGER_PRESSED");
            else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_TRIGGER_RELEASED)
                mMessageTextView.setText(" " + "BARCODE_TRIGGER_RELEASED");
            else if (m.arg1 == SDConsts.BCCmdMsg.BARCODE_READ) {
                if (D) Log.d(TAG, "BC_MSG_BARCODE_READ");
                if (m.arg2 == SDConsts.BCResult.SUCCESS)
                    readData.append(" " + "BC_MSG_BARCODE_READ");
                else if (m.arg2 == SDConsts.BCResult.ACCESS_TIMEOUT)
                    readData.append(" " + "BC_MSG_BARCODE_ACCESS_TIMEOUT");
                if (m.obj != null  && m.obj instanceof String)
                    readData.append("\n" + (String)m.obj);
                mMessageTextView.setText(" " + readData.toString());
            }
            if (D) Log.d(TAG, "data = " + readData.toString());
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
                    int ret = mReader.SD_UpdateSLEDFirmware(file.toString());
                    Toast.makeText(mContext, getString(R.string.update_firm_str) + "result = " +
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