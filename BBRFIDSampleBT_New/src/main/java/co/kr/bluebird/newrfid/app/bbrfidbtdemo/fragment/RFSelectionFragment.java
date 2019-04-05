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
import co.kr.bluebird.sled.SelectionCriterias;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class RFSelectionFragment extends Fragment {
    private static final String TAG = RFSelectionFragment.class.getSimpleName(); 
    
    private static final boolean D = Constants.SEL_D;
    
    private Context mContext;
    
    private TextView mMonitorText;
    
    private Spinner mMemtypeSpin;
    private ArrayAdapter<CharSequence> mMemtypeChar;
    
    private Spinner mActionSpin;
    private ArrayAdapter<CharSequence> mActionChar;
    
    private EditText mMaskEdit;
    
    private EditText mStartPosEdit;
    
    private EditText mMaskLengthEdit;
    
    private Button mSetCriteriaBt;
    
    private Button mRemoveCriteriaBt;
    
    private Button mSetSelectionBt;
    
    private Button mGetSelectionBt;
    
    private Button mRemoveSelectionBt;
    
    private BTReader mReader;
    
    private SelectionCriterias mCurrentSelectionCriterias;
    
    private Handler mOptionHandler;

    private Fragment mFragment;
    
    private RFSelectionHandler mRFSelectionHandler = new RFSelectionHandler(this);
    
    public static RFSelectionFragment newInstance() {
        return new RFSelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.rf_selection_frag, container,false);
        mContext = inflater.getContext();

        mFragment = this;

        mOptionHandler = ((MainActivity)getActivity()).mUpdateConnectHandler;

        mMonitorText = (TextView)v.findViewById(R.id.text_criteria);
  
        mMemtypeSpin = (Spinner)v.findViewById(R.id.memtype_spin);
        mMemtypeChar = ArrayAdapter.createFromResource(mContext, R.array.selection_memtype_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mMemtypeSpin.setAdapter(mMemtypeChar);

        mActionSpin = (Spinner)v.findViewById(R.id.action_spin);
        mActionChar = ArrayAdapter.createFromResource(mContext, R.array.selection_action_array, 
                android.R.layout.simple_spinner_dropdown_item);
        mActionSpin.setAdapter(mActionChar);
  
        mMaskEdit = (EditText)v.findViewById(R.id.mask_edit);

        mStartPosEdit = (EditText)v.findViewById(R.id.start_pos_edit);
  
        mMaskLengthEdit = (EditText)v.findViewById(R.id.mask_length_edit);
  
        mSetCriteriaBt = (Button)v.findViewById(R.id.set_criteria);
        mSetCriteriaBt.setOnClickListener(sledListener);
  
        mRemoveCriteriaBt = (Button)v.findViewById(R.id.remove_criteria);
        mRemoveCriteriaBt.setOnClickListener(sledListener);
  
        mSetSelectionBt = (Button)v.findViewById(R.id.set_selection);
        mSetSelectionBt.setOnClickListener(sledListener);
  
        mGetSelectionBt = (Button)v.findViewById(R.id.get_selection);
        mGetSelectionBt.setOnClickListener(sledListener);
  
        mRemoveSelectionBt = (Button)v.findViewById(R.id.remove_selection);
        mRemoveSelectionBt.setOnClickListener(sledListener);
        
        mCurrentSelectionCriterias = new SelectionCriterias();
        return v;
    }

    @Override
    public void onStart() {
        if (D) Log.d(TAG, "onStart");
        mReader = BTReader.getReader(mContext, mRFSelectionHandler);
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
        super.onStop();
    }

    private OnClickListener sledListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (D) Log.d(TAG, "sledListener");
            String value = null;
            int memPos = 0;
            int actionPos = 0;
            int id = v.getId();
            String mask;
            int selectStartPosByte = 0;
            int selectMaskLengthBit = 0;
            switch (id) {
            case R.id.set_criteria:
                memPos = mMemtypeSpin.getSelectedItemPosition();
                actionPos = mActionSpin.getSelectedItemPosition();
                value = mStartPosEdit.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            selectStartPosByte = Integer.parseInt(value);
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                value = mMaskLengthEdit.getText().toString();
                if (value != null) {
                    if (value != "") {
                        try {
                            selectMaskLengthBit = Integer.parseInt(value);
                        } catch(java.lang.NumberFormatException e) {
                            if (D) Log.e(TAG, e.toString());
                        }
                    }
                }
                mask = mMaskEdit.getText().toString();
                mCurrentSelectionCriterias.makeCriteria(memPos + 1, mask, selectStartPosByte, selectMaskLengthBit, actionPos);
                break;
            case R.id.remove_criteria:
                if (mCurrentSelectionCriterias != null && mCurrentSelectionCriterias.getCriteria() != null && 
                        mCurrentSelectionCriterias.getCriteria().size() > 0) {
                    mCurrentSelectionCriterias.getCriteria().remove(mCurrentSelectionCriterias.getCriteria().size() - 1);
                }
                break;
            case R.id.set_selection:
                if (mCurrentSelectionCriterias != null && mCurrentSelectionCriterias.getCriteria() != null && 
                        mCurrentSelectionCriterias.getCriteria().size() > 0) {
                    mReader.RF_SetSelection(mCurrentSelectionCriterias);
                    mCurrentSelectionCriterias.getCriteria().clear();
                }
                break;
            case R.id.get_selection:
                SelectionCriterias sc = mReader.RF_GetSelection();
                getSelectionCriterias(sc);
                break;
            case R.id.remove_selection:
                mReader.RF_RemoveSelection();
                break;
            }
            updateCurrentSelectionCriterias();
        }
    }; 
    
    private void getSelectionCriterias(SelectionCriterias sc) {
        StringBuilder sb = new StringBuilder();
        if (sc != null && sc.getCriteria() != null && sc.getCriteria().size() > 0) {
            for (SelectionCriterias.Criteria c : sc.getCriteria()) {
                sb.append("memtype = " + c.getSelectMemType());
                sb.append(" mask = " + c.getSelectMask());
                sb.append(" startPosByte = " + c.getSelectStartPosByte());
                sb.append(" maskLengthBit = " + c.getSelectMaskLengthBit());
                sb.append(" action = " + c.getSelectAction());
                sb.append("\n");
            }
            String str = sb.toString();
            if (str != null) {
                createDialog(str);
            }
        }
        return;
    }
    
    private void updateCurrentSelectionCriterias() {
        StringBuilder sb = new StringBuilder();
        if (mCurrentSelectionCriterias != null && mCurrentSelectionCriterias.getCriteria() != null && mCurrentSelectionCriterias.getCriteria().size() > 0) {
            for (SelectionCriterias.Criteria c : mCurrentSelectionCriterias.getCriteria()) {
                sb.append("memtype = " + c.getSelectMemType());
                sb.append(" mask = " + c.getSelectMask());
                sb.append(" startPosByte = " + c.getSelectStartPosByte());
                sb.append(" maskLengthBit = " + c.getSelectMaskLengthBit());
                sb.append(" action = " + c.getSelectAction());
                sb.append("\n");
            }
            String str = sb.toString();
            if (str != null) {
                mMonitorText.setText(str);
                return;
            }
        }
        mMonitorText.setText("");
    }
    
    private static class RFSelectionHandler extends Handler {
        private final WeakReference<RFSelectionFragment> mExecutor;
        public RFSelectionHandler(RFSelectionFragment f) {
            mExecutor = new WeakReference<>(f);
        }
        
        @Override
        public void handleMessage(Message msg) {
            RFSelectionFragment executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }
    
    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mRFSelectionHandler");
        if (D) Log.d(TAG, "m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
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
    
    private void createDialog(String message) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
        Activity activity = getActivity();
        if (activity != null)
            dlg.setTitle(activity.getString(R.string.selection_str));
        dlg.setMessage(message);
        dlg.setCancelable(false);
        
        dlg.setNeutralButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
            
        });
        dlg.show();
    }
}