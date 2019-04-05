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

import java.lang.ref.WeakReference;
import java.util.Set;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.Constants;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.control.ListItem;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.MainActivity;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.permission.PermissionHelper;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.R;
import co.kr.bluebird.newrfid.app.bbrfidbtdemo.control.TagListAdapter;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BTConnectivityFragment extends Fragment {
    private static final String TAG = BTConnectivityFragment.class.getSimpleName();

    private static final boolean D = Constants.CON_D;

    private TextView mActionTextView;
    
    private TextView mMessageTextView;

    private TextView mConnectedDeviceTextView;

    private Button mDisconnectBt;
    
    private Button mEnableBt;
    
    private Button mDisableBt;
    
    private Button mGetBtStateBt;

    private Button mScanBt;
    
    private Button mStopScanBt;
    
    private Button mRemoveAllPairedBt;

    private BTReader mReader;

    private Context mContext;
    
    private Handler mOptionHandler;
    
    private TagListAdapter mAdapter;
    
    private ListView mDeviceList;
    
    private ProgressBar mProgressBar;

    private Fragment mFragment;
    
    private final ConnectivityHandler mConnectivityHandler = new ConnectivityHandler(this);
    
    public static BTConnectivityFragment newInstance() {
        return new BTConnectivityFragment();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.connectivity_frag, container, false);
        mContext = inflater.getContext();

        mFragment = this;

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        mActionTextView = (TextView)v.findViewById(R.id.action_textview);
        
        mMessageTextView = (TextView)v.findViewById(R.id.message_textview);

        mConnectedDeviceTextView = (TextView)v.findViewById(R.id.connected_device_textview); 
        
        mDisconnectBt = (Button)v.findViewById(R.id.bt_disconnect);
        mDisconnectBt.setOnClickListener(buttonListener);
        
        mScanBt = (Button)v.findViewById(R.id.bt_scan);
        mScanBt.setOnClickListener(buttonListener);
        
        mStopScanBt = (Button)v.findViewById(R.id.bt_stop_scan);
        mStopScanBt.setOnClickListener(buttonListener);
        
        mEnableBt = (Button)v.findViewById(R.id.bt_enable);
        mEnableBt.setOnClickListener(buttonListener);

        mDisableBt = (Button)v.findViewById(R.id.bt_disable);
        mDisableBt.setOnClickListener(buttonListener);
        
        mGetBtStateBt = (Button)v.findViewById(R.id.bt_state);
        mGetBtStateBt.setOnClickListener(buttonListener);
        
        mProgressBar = (ProgressBar)v.findViewById(R.id.scan_progress);
        
        mRemoveAllPairedBt = (Button)v.findViewById(R.id.bt_remove_pair);
        mRemoveAllPairedBt.setOnClickListener(buttonListener);
        
        mDeviceList = (ListView)v.findViewById(R.id.device_list);
        mDeviceList.setOnItemClickListener(listListener);
        mAdapter = new TagListAdapter(mContext);
        
        mDeviceList.setAdapter(mAdapter);
        
        return v;
    }

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mReader = BTReader.getReader(mContext, mConnectivityHandler);
        if (mReader != null) {
            if (mReader.BT_IsEnabled())
                addPairedDevices();
            if (mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED)
                updateConnectedInfo(mReader.BT_GetConnectedDeviceName() + "\n" + mReader.BT_GetConnectedDeviceAddr());
            else
                updateConnectedInfo("");
        }
        updateConnectStateTextView();
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
            Set<BluetoothDevice> pairedDevices = null;
            switch (v.getId()) {
            case R.id.bt_disconnect:
                if (mReader.BT_Disconnect() == SDConsts.BTResult.SUCCESS)
                    retString = "Disconnect";
                else
                    retString = "Disconnect failed";
                break;
            case R.id.bt_enable:
                if (mReader.BT_Enable())
                    retString = "Bluetooth Enable";
                else 
                    retString = "Bluetooth Enable failed";
                break;
            case R.id.bt_disable:
                mAdapter.removeAllItem();
                if (mReader.BT_Disable())
                    retString = "Bluetooth Disable";
                else
                    retString = "Bluetooth Disable failed";
                break;
            case R.id.bt_state:
                retString = "Bluetooth State = ";
                if (mReader.BT_IsEnabled())
                    retString += "Enabled";
                else
                    retString += "Disabled";
                break;
            case R.id.bt_scan:
                addPairedDevices();
                boolean b = PermissionHelper.checkPermission(mContext, PermissionHelper.mLocationPerms[0]);
                if (b) {
                    if (mReader.BT_StartScan()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        retString = "Bluetooth Scan";
                    } else
                        retString = "Bluetooth Scan failed";
                }
                else
                    PermissionHelper.requestPermission(getActivity(), PermissionHelper.mLocationPerms);
                break;
            case R.id.bt_stop_scan:
                if (mReader.BT_StopScan()) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    retString = "Bluetooth Stop Scan";
                }
                else 
                    retString = "Bluetooth Stop Scan failed";
                break;
            case R.id.bt_remove_pair:
                if (mReader.BT_StopScan())
                    mProgressBar.setVisibility(View.INVISIBLE);
                pairedDevices = mReader.BT_GetPairedDevices();
                if (pairedDevices != null && pairedDevices.size() > 0) {
                    for (BluetoothDevice d : pairedDevices)
                        mReader.BT_UnpairDevice(d.getAddress());
                    retString = "Bluetooth Remove All Paired";
                }
                mAdapter.removeAllItem();
                break;
            }
            if (ret != -100) {
                retString += ret;
            }
            mActionTextView.setText(" " + retString);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (D) Log.d(TAG, "onRequestPermissionsResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case PermissionHelper.REQ_PERMISSION_CODE:
                    if (permissions != null) {
                        boolean hasResult = false;
                        for (String p : permissions) {
                            if (p.equals(PermissionHelper.mLocationPerms[0])) {
                                hasResult = true;
                                break;
                            }
                        }
                        if (hasResult) {
                            if (grantResults != null && grantResults.length != 0 &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                if (mReader.BT_StartScan())
                                    mProgressBar.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    break;
            }
        }
    }
    
    private OnItemClickListener listListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            ListItem li = (ListItem) mAdapter.getItem(arg2);
            int result = -100;
            if (mReader.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
                result = mReader.BT_Connect(li.mDt);
                mActionTextView.setText(" " + "Connect result = " + result);
            }
            else { 
                result = mReader.BT_Disconnect();
                mActionTextView.setText(" " + "Disconnect result = " + result);
            }
            if (D) Log.d(TAG, "Click Result = " + result);
        }
    };
    
    private static class ConnectivityHandler extends Handler {
        private final WeakReference<BTConnectivityFragment> mExecutor;
        public ConnectivityHandler(BTConnectivityFragment f) {
            mExecutor = new WeakReference<>(f);
        }
        
        @Override
        public void handleMessage(Message msg) {
            BTConnectivityFragment executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }
    
    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mConnectivityHandler");
        if (true) Log.d(TAG, "command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
        String receivedData = "";
        switch (m.what) {
        case SDConsts.Msg.SDMsg:
            if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                    Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                    Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(mFragment).attach(mFragment).commit();
            }
            break;
        case SDConsts.Msg.BTMsg:
            if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DEVICE_FOUND) {
                receivedData = "SLED_BT_DEVICE_FOUND";
                if (m.obj != null  && m.obj instanceof Bundle) {
                    Bundle b = (Bundle)m.obj;
                    String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                    String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                    int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                    if (D) Log.d(TAG, "SLED_BT_DEVICE_FOUND " + name + " " + addr + " " + bondState);
                    mAdapter.addItem(-1, name, addr, false, false);
                }
            }
            else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_BOND_STATE_CHAGNED) {
                receivedData = "SLED_BT_BOND_STATE_CHAGNED";
                if (m.obj != null  && m.obj instanceof Bundle) {
                    Bundle b = (Bundle)m.obj;
                    String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                    String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                    int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                    int newBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_NEW_STATE_KEY);
                    int prevBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_PREV_STATE_KEY);
                    if (D) Log.d(TAG, "SLED_BT_BOND_STATE_CHAGNED " + name + " " + addr + " " + bondState + " " + newBondState + " " + prevBondState);
                }
            }
            else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_PAIRING_REQUEST) {
                receivedData = "SLED_BT_PAIRING_REQUEST";
                updateConnectStateTextView();
            }
            else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCOVERY_STARTED) {
                receivedData = "SLED_BT_DISCOVERY_STARTED";
                mProgressBar.setVisibility(View.VISIBLE);
            }
            else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCOVERY_FINISHED) {
                receivedData = "SLED_BT_DISCOVERY_FINISHED";
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_STATE_CHANGED) {
                receivedData = "SLED_BT_STATE_CHANGED";
                if (m.obj != null  && m.obj instanceof Bundle) {
                    Bundle b = (Bundle)m.obj;
                    int newBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_NEW_STATE_KEY);
                    int prevBondState = b.getInt(SDConsts.BT_BUNDLE_BOND_PREV_STATE_KEY);
                    if (D) Log.d(TAG, "SLED_BT_STATE_CHANGED " + newBondState + " " + prevBondState);
                }
                if (mReader.BT_IsEnabled())
                    addPairedDevices();
                if (D) Log.d(TAG, "BT State changed = " + m.arg2);
            }
            else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                receivedData = "SLED_BT_CONNECTION_STATE_CHANGED";
                if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);
                updateConnectStateTextView();
            }
            else if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_ESTABLISHED) {
                receivedData = "SLED_BT_CONNECTION_ESTABLISHED";
                updateConnectedInfo(mReader.BT_GetConnectedDeviceName() + "\n" + mReader.BT_GetConnectedDeviceAddr());
                addPairedDevices();
            }
            else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED) {
                receivedData = "SLED_BT_DISCONNECTED";
                updateConnectedInfo("");
            }
            else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {
                receivedData = "SLED_BT_CONNECTION_LOST";
                updateConnectedInfo("");
            }
            else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ACL_CONNECTED) {
                receivedData = "SLED_BT_ACL_CONNECTED";
                if (m.obj != null  && m.obj instanceof Bundle) {
                    Bundle b = (Bundle)m.obj;
                    String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                    String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                    int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                    if (D) Log.d(TAG, "SLED_BT_ACL_CONNECTED " + name + " " + addr + " " + bondState);
                }
            }
            else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ACL_DISCONNECT_REQUESTED) {
                receivedData = "SLED_BT_ACL_DISCONNECT_REQUESTED";
                updateConnectedInfo("");
            }
            else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ACL_DISCONNECTED) {
                receivedData = "SLED_BT_ACL_DISCONNECTED";
                if (m.obj != null  && m.obj instanceof Bundle) {
                    Bundle b = (Bundle)m.obj;
                    String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                    String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                    int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                    if (D) Log.d(TAG, "SLED_BT_ACL_DISCONNECTED " + name + " " + addr + " " + bondState);
                }
                updateConnectedInfo("");
            }
            else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_ADAPTER_CONNECTION_STATE_CHANGED) {
                receivedData = "SLED_BT_ADAPTER_CONNECTION_STATE_CHANGED";
                if (m.obj != null  && m.obj instanceof Bundle) {
                    Bundle b = (Bundle)m.obj;
                    String name = b.getString(SDConsts.BT_BUNDLE_NAME_KEY);
                    String addr = b.getString(SDConsts.BT_BUNDLE_ADDR_KEY);
                    int bondState = b.getInt(SDConsts.BT_BUNDLE_BOND_STATE_KEY);
                    int newConState = b.getInt(SDConsts.BT_BUNDLE_CON_NEW_STATE_KEY);
                    int prevConState = b.getInt(SDConsts.BT_BUNDLE_CON_PREV_STATE_KEY);
                    if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED " + name + " " + addr + " " + bondState + " " + newConState + " " +  prevConState);
                }
            }
            break;
        }
        mMessageTextView.setText(receivedData);
    }
    
    private void addPairedDevices() {
        if (mAdapter != null && mReader != null) {
            mAdapter.removeAllItem();
            Set<BluetoothDevice> pairedDevices = mReader.BT_GetPairedDevices();
            if (pairedDevices != null && pairedDevices.size() > 0) {
                for (BluetoothDevice d : pairedDevices)
                    mAdapter.addItem(-1, d.getName() + "\n" + "[paired device]", d.getAddress(), false, false);
            }
        }
    }
    
    private void updateConnectedInfo(String info) {
        if (mReader != null && mConnectedDeviceTextView != null) {
            mConnectedDeviceTextView.setText(info);
            mConnectedDeviceTextView.setTextColor(Color.BLUE);
            if (info != null && info != "")
                mDisconnectBt.setVisibility(View.VISIBLE);
            else
                mDisconnectBt.setVisibility(View.INVISIBLE);
        }
    }
    
    private void updateConnectStateTextView() {
        if (mOptionHandler != null)
            mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_CONNECT_STATE_CHANGED).sendToTarget();
    }
}