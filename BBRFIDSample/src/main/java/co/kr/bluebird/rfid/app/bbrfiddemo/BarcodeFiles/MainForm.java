/*package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.lang.ref.WeakReference;

import DataModule._Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;

public class MainForm extends AppCompatActivity {

    private static final String TAG = MainForm.class.getSimpleName();

    private static final boolean D = _Constants.MAIN_D;

    public static final int MSG_OPTION_DISCONNECTED = 0;

    public static final int MSG_OPTION_CONNECTED = 1;

    public static final int MSG_BACK_PRESSED = 2;

    private String[] mFunctionsString;

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Reader mReader;
    private FragmentManager mFragmentManager;
    private boolean mIsConnected;
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


    private final
    private final MainHandler mMainHandler = new MainHandler(this);

    public final UpdateConnectHandler mUpdateConnectHandler = new UpdateConnectHandler(this); mMainHandler = new MainHandler(this);

    public final UpdateConnectHandler mUpdateConnectHandler = new UpdateConnectHandler(this);

    Button setupZone,item,carton,shipItem,handleConnection;
    Context mContext;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_form);
        mContext = MainForm.this;

        android.support.v4.app.Fragment fragment = new MainMenu();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
//        activity=this;
//        setupZone = findViewById(R.id.btn_setupZone);
//        item = findViewById(R.id.btn_item);
//        carton = findViewById(R.id.btn_carton);
//        shipItem = findViewById(R.id.btn_shipItem);
//        handleConnection = findViewById(R.id.btn_connectivity);


//        setupZone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext,CZone.class));
//            }
//        });
//        item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext,SelectStoreForm.class));
//            }
//        });
//        carton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext,Carton.class));
//            }
//        });
//        shipItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext,Shipment.class));
//            }
//        });
//        handleConnection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//              startActivity(new Intent(MainForm.this,Connectivity.class));
//            }
//        });

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
        android.support.v4.app.Fragment fragment = new MainMenu();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
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
        private final WeakReference<MainForm> mExecutor;
        public MainHandler(MainForm ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            MainForm executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
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
        private final WeakReference<MainForm> mExecutor;
        public UpdateConnectHandler(MainForm ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            MainForm executor = mExecutor.get();
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
*/