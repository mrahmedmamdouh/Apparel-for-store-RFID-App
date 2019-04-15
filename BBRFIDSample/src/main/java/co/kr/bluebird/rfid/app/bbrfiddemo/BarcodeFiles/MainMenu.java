package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.TransRFIDFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.transaction_listView;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments.CartonFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments.SelectStoreFormFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments.ShipItemsFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments.ShipmentFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BatteryFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.InfoFragment;

import static com.android.volley.VolleyLog.TAG;


public class MainMenu extends Activity {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button setupZone,item,carton,shipItem,handleConnection,exit;
    private static final String TAG = MainMenu.class.getSimpleName();
    private boolean mIsConnected;
    private static final boolean D = _Constants.MAIN_D;
    private Reader mReader;

    public static final int MSG_OPTION_DISCONNECTED = 0;

    public static final int MSG_OPTION_CONNECTED = 1;

    public static final int MSG_BACK_PRESSED = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout x;
    private android.app.FragmentManager mFragmentManager;
    private android.app.Fragment mCurrentFragment;

    private Context mContext;
    private CZoneFragment mCZoneFragment;
    private ShipItemsFragment mShipmentFragment;
    private SelectStoreFormFragment mSelectStoreFormFragment;
    private TransRFIDFragment mTransRFIDFragment;

    private Handler mOptionHandler;

    private final MainHandler mMainHandler = new MainHandler(this);

    public final UpdateConnectHandler mUpdateConnectHandler = new UpdateConnectHandler(MainMenu.this);


    public MainMenu() {
        // Required empty public constructor
    }

   //TODO: Rename and change types and number of parameters
    public static MainMenu newInstance() {
        return new MainMenu();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_menu);
        mCurrentFragment = null;
        mFragmentManager = getFragmentManager();
        mContext = this;
        shipItem = findViewById(R.id.btn_shipItem);
        x = findViewById(R.id.frame_layout);
        setupZone = findViewById(R.id.btn_setupZone);
        item = findViewById(R.id.btn_item);


        setupZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(getActivity(),CZone.class));
                if (mCZoneFragment == null)
                    mCZoneFragment = CZoneFragment.newInstance();
                mCurrentFragment = mCZoneFragment;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.frame_layout, mCurrentFragment);
                ft.commit();
            }
        });
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(getActivity(),SelectStoreForm.class));
                if (mSelectStoreFormFragment == null)
                    mSelectStoreFormFragment = SelectStoreFormFragment.newInstance();
                mCurrentFragment = mSelectStoreFormFragment;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.frame_layout, mCurrentFragment);
                ft.commit();
            }
        });

        shipItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mShipmentFragment == null)
                    mShipmentFragment = ShipItemsFragment.newInstance();
                mCurrentFragment = mShipmentFragment;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.frame_layout, mCurrentFragment);
                ft.commit();
            }
        });




    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, " onStart");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        boolean openResult = false;
        boolean isConnected = false;
        mReader = Reader.getReader(mContext, mMainHandler);
        if (mReader != null)
            openResult = mReader.SD_Open();
        if (openResult == SDConsts.RF_OPEN_SUCCESS) {
            Log.i(TAG, "Reader opened");
            if (mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
                isConnected = true;
        }
        else if (openResult == SDConsts.RF_OPEN_FAIL)
            if (D) Log.e(TAG, "Reader open failed");

        updateConnectState(isConnected);
        super.onStart();
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onResume");
//        int ret = mReader.SD_Connect();
//        if (ret == SDConsts.SDConnectState.CONNECTED) {
//            if (D) Log.d(TAG, "connected");
//            try {
//                mReader.SD_SetTriggerMode(SDConsts.SDTriggerMode.BARCODE);
//            } catch (NullPointerException ex) {
//            }
//        }
        super.onResume();
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onPause");
        super.onPause();
    }

    //    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        android.support.v4.app.Fragment fragment = new MainMenu();
//        Bundle bundle = new Bundle();
//        fragment.setArguments(bundle);
//
//        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
//
//    }
//    @Override
//    public void onBackPressed() {
//
//        int count = getFragmentManager().getBackStackEntryCount();
//
//        if (count == 0) {
//            startActivity(new Intent(this,MainForm.class));
//            super.onBackPressed();
//            //additional code
//        } else {
//            getFragmentManager().popBackStack();
//        }
//
//    }
    @Override
    public void onBackPressed() {
        if (mCurrentFragment == null)
//        switchToHome();
        {
            if (mTransRFIDFragment == null)
                mTransRFIDFragment = TransRFIDFragment.newInstance();
            mCurrentFragment = mTransRFIDFragment;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.frame_layout, mCurrentFragment);
            ft.commit();
        }
        else
        {
            super.onBackPressed();
        }
    }
    //    @Override
//    public void onBackPressed() {
//        if (!shouldAllowBack()) {
//            doSomething();
//        } else {
//            super.onBackPressed();
//        }
//    }
    @Override
    protected void onStop() {
        if (D) Log.d(TAG, " onStop");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mReader = Reader.getReader(mContext, mMainHandler);
        if (mReader != null)
            if (mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {
                mReader.SD_Disconnect();
            }
        mReader.SD_Close();
        super.onStop();
    }

    private static class MainHandler extends Handler {
        private final WeakReference<MainMenu> mExecutor;
        public MainHandler(MainMenu ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }
    private void handleMessage(Message m) {
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
    private void updateConnectState(boolean b) {
        mIsConnected = b;
        invalidateOptionsMenu();
    }
    private static class UpdateConnectHandler extends Handler {
        private final WeakReference<MainMenu> mExecutor;
        public UpdateConnectHandler(MainMenu ac) {
            mExecutor = new WeakReference<>(ac);
        }

        public void handleMessage(Message msg) {
            MainMenu executor = mExecutor.get();
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
//        else if (m.what == MSG_BACK_PRESSED)
//            switchToHome();
    }
}

