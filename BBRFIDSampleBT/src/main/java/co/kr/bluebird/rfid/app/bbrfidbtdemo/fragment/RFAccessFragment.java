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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.io.File;
import java.lang.ref.WeakReference;

public class RFAccessFragment extends Fragment {
    
    private static final String TAG = RFAccessFragment.class.getSimpleName(); 

    private static final boolean D = Constants.ACC_D;
    
    private static final String FILE_DIR_PRE = "//DIR//";
    
    private static final String FILE_EXT = ".bin";
    
    private static final int PROGRESS_DIALOG = 1;

    private ListView mRfidList;

    private BTReader mReader;

    private Context mContext;

    private ProgressDialog mDialog;

    private ArrayAdapter<CharSequence> mItemChar;

    private Handler mOptionHandler;

    private FileSelectorDialog mFileSelecter;
    
    private AccessHandler mAccessHandler = new AccessHandler(this);
    
    public static RFAccessFragment newInstance() {
        return new RFAccessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.rf_access_frag, container,false);
        mContext = inflater.getContext();

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        mRfidList = (ListView)v.findViewById(R.id.access_list);
        mRfidList.setOnItemClickListener(listListener);
        
        mItemChar = ArrayAdapter.createFromResource(mContext, R.array.items_array, 
                android.R.layout.simple_list_item_1);
        
        mRfidList.setAdapter(mItemChar);
        return v;
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
                result = mReader.RF_READ(SDConsts.RFMemType.EPC, 1, 7, "00000000", false);
                if (result == SDConsts.RFResult.SUCCESS)
                    message.append("RF_READ");
                else 
                    message.append("RF_READ failed " + result);
                break;
            case 1:
                //result = mReader.RF_WRITE(SDConsts.RFMemType.USER, 0, "0000000000000000", "00000000", false);
                //result = mReader.RF_WRITE(SDConsts.RFMemType.USER, 0, "1111222233334444", "00000000", false);
                result = mReader.RF_WRITE(SDConsts.RFMemType.EPC, 2, "111122223333444455556666", "00000000",  false);
                if (result == SDConsts.RFResult.SUCCESS)
                    message.append("RF_WRITE");
                else 
                    message.append("RF_WRITE failed " + result);
                break;
            case 2:
                // epc write
                //result = mReader.RF_WriteTagID(1, "3000666655554444333322223333", "00000000", true);
                result = mReader.RF_WriteTagID(1, "3000666655554444333322221111", "00000000", false);
                if (result == SDConsts.RFResult.SUCCESS)
                    message.append("RF_WriteTagID");
                else 
                    message.append("RF_WriteTagID failed " + result);
                break;
            case 3:
                // new password , old password
                result = mReader.RF_WriteAccessPassword("00000000", "00000000", false);
                if (result == SDConsts.RFResult.SUCCESS)
                    message.append("RF_WriteAccessPassword");
                else 
                    message.append("RF_WriteAccessPassword failed " + result);
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
                //result = mReader.RF_BlockPermalock(0, 1, 0x8000, "00000000");
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
                executor.handleMessage(msg);
            }
        }
    }

    public void handleMessage(Message m) {
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
                messageStr = "READ result = " + result + "\n" + data;
                break;
            case SDConsts.RFCmdMsg.WRITE:
                messageStr = "WRITE result = " + result + " " + data;
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
            break;
        case SDConsts.Msg.BTMsg:
            if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);
                if (mOptionHandler != null)
                    mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_CONNECT_STATE_CHANGED).sendToTarget();
            }
            break;
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