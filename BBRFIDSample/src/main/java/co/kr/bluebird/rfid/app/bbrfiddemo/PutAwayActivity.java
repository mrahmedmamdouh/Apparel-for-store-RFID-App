package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;
import co.kr.bluebird.sled.SelectionCriterias;
import java.lang.ref.WeakReference;

import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.PutAwayToBackRoom;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.PutAwayToStore;

public class PutAwayActivity extends Activity {

    private static final String TAG = PutAwayActivity.class.getSimpleName();

    private static final boolean D = Constants.MAIN_D;

    public static final int MSG_OPTION_DISCONNECTED = 0;

    public static final int MSG_OPTION_CONNECTED = 1;

    public static final int MSG_BACK_PRESSED = 2;

    private String[] mFunctionsString;

    private DrawerLayout mDrawerLayout;


private Reader mReader;

    private Context mContext;

    private FragmentManager mFragmentManager;

    private boolean mIsConnected;


    private PutAwayToStore mPutAwayToStore;
    private PutAwayToBackRoom mPutAwayToBackRoom;
    private LinearLayout mUILayout;
    private Fragment mCurrentFragment;
    private Button mInventoryButton;
    private Button mInventoryButtonbr;
    private TextView brcount,storeCount;
    private String x,y;

    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    private ProgressBar mBatteryProgress;
    private final PutAwayActivity.BatteryHandler mBatteryHandler = new PutAwayActivity.BatteryHandler(this);


    private final PutAwayActivity.MainHandler mPutAwayActivityHandler = new PutAwayActivity.MainHandler(PutAwayActivity.this);

    public final PutAwayActivity.UpdateConnectHandler mUpdateConnectHandler = new PutAwayActivity.UpdateConnectHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway);
        dbhelper = new WDxDBHelper(getApplication());
        mBatteryProgress = (ProgressBar) findViewById(R.id.batt_progress);



        db =dbhelper.getWritableDatabase();
        mContext = this;
        mCurrentFragment = null;
        Cursor c = db.rawQuery("select count(*) as COUNT from brcount",null);
        while (c.moveToNext())
        {
            x = c.getString(c.getColumnIndex("COUNT"));}

        storeCount = (TextView) findViewById(R.id.storeCount);

        Cursor c1 = db.rawQuery("select count(*) as COUNT from StoreCount",null);
        while (c1.moveToNext())
        {
            y = c1.getString(c.getColumnIndex("COUNT"));}


        brcount = (TextView) findViewById(R.id.brCount);
        storeCount.setText("Count : " + y);
        brcount.setText("Count : " + x);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int buttonHeight = size.x / 3;


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        mInventoryButton = (Button) findViewById(R.id.inv_bt);
        mInventoryButton.setMinimumHeight(buttonHeight);

        mInventoryButtonbr = (Button) findViewById(R.id.inv_bt_br);
        mInventoryButtonbr.setMinimumHeight(buttonHeight);



        mUILayout = (LinearLayout)findViewById(R.id.ui_layout);

        mCurrentFragment = null;

        mInventoryButton.setOnClickListener(buttonListener);
        mInventoryButtonbr.setOnClickListener(buttonListener);






        mFunctionsString = getResources().getStringArray(R.array.functions_array);

        mFragmentManager = getFragmentManager();

        mIsConnected = false;
    }

    public View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = 0;
            switch(v.getId()) {

                case R.id.inv_bt:
                    id = 0;
                    break;
                case R.id.inv_bt_br:
                    id = 1;
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
            Intent i1 = new Intent(this, MainActivity.class);
            startActivity(i1);
        }
        else if (id == android.R.id.home) {
            if (mCurrentFragment != null)
                switchToHome();
            else
                super.onBackPressed();
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
                if (mPutAwayToStore == null)
                    mPutAwayToStore = PutAwayToStore.newInstance();
                mCurrentFragment = mPutAwayToStore;
                break;
            case 1:
                if (mPutAwayToBackRoom == null)
                    mPutAwayToBackRoom = PutAwayToBackRoom.newInstance();
                mCurrentFragment = mPutAwayToBackRoom;
                break;
            default:
                return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.xx, mCurrentFragment);
        ft.commit();
        // setTitle(mFunctionsString[position]);
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
    }

    public static PutAwayActivity newInstance() {
        return new PutAwayActivity();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, " onStart");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        boolean openResult = false;
        boolean isConnected = false;
        mReader = Reader.getReader(mContext, mPutAwayActivityHandler);
        mReader = Reader.getReader(mContext, mBatteryHandler);

        if (mReader != null && mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED) {
            int value = mReader.SD_GetBatteryStatus();

            if (PutAwayActivity.class != null) {
                mBatteryProgress.setProgress(value);
            }
        }
        if (mReader != null&& mReader.SD_GetConnectState() == SDConsts.SDConnectState.CONNECTED)
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

        storeCount.setText("Count : " + y);
        brcount.setText("Count : " + x);
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
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

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
    private static class BatteryHandler extends Handler {
        private final WeakReference<PutAwayActivity> mExecutor;
        public BatteryHandler(PutAwayActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            PutAwayActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }
    private static class MainHandler extends Handler {
        private final WeakReference<PutAwayActivity> mExecutor;
        public MainHandler(PutAwayActivity ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            PutAwayActivity executor = mExecutor.get();
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

    private void switchToHome() {
        if (D) Log.d(TAG, "switchToHome");
        try {
            if (mCurrentFragment != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.remove(mCurrentFragment);
                ft.commit();
                mCurrentFragment = null;
                mReader = Reader.getReader(mContext, mPutAwayActivityHandler);
            }
            setTitle(getString(R.string.app_name));
            if (mUILayout.getVisibility() != View.VISIBLE) {
                mUILayout.setVisibility(View.VISIBLE);
            }
        }
        catch (java.lang.IllegalStateException e) {
        }
        return;
    }

    private void updateConnectState(boolean b) {
        mIsConnected = b;
        invalidateOptionsMenu();
    }

    private static class UpdateConnectHandler extends Handler {
        private final WeakReference<PutAwayActivity> mExecutor;
        public UpdateConnectHandler(PutAwayActivity ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            PutAwayActivity executor = mExecutor.get();
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
        else if (m.what == MSG_BACK_PRESSED)
            switchToHome();
    }
}