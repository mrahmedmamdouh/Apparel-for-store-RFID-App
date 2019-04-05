/*import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.control.ListItem;
import co.kr.bluebird.rfid.app.bbrfiddemo.control.TagListAdapter;
import co.kr.bluebird.rfid.app.bbrfiddemo.employees;
import co.kr.bluebird.rfid.app.bbrfiddemo.fetchData;
import co.kr.bluebird.rfid.app.bbrfiddemo.fileutil.FileManager;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BRInventoryFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.listAdapter;
import co.kr.bluebird.rfid.app.bbrfiddemo.stopwatch.StopwatchService;

i&mport android.app.Activity;
        /package co.kr.bluebird.rfid.app.bbrfiddemo.fragment;


public class Repl extends Activity implements fetchData.download_complete {

    public static ListView list;
    public ArrayList<employees> elements = new ArrayList<>();
    public listAdapter adapter;
    private LinearLayout mLocateLayout;

    private LinearLayout mListLayout;

    private ProgressBar mTagLocateProgress;

    private int mLocateValue;

    private ImageButton mBackButton;
    private Spinner mSessionSpin;
    private ArrayAdapter<CharSequence> mSessionChar;

    private Spinner mSelFlagSpin;
    private ArrayAdapter<CharSequence> mSelFlagChar;

    private static final String TAG = BRInventoryFragment.class.getSimpleName();

    private static final boolean D = Constants.INV_D;

    private StopwatchService mStopwatchSvc;

    private TagListAdapter mAdapter;

    private ListView mRfidList;
    String date;
    private TextView mBatteryText;
    String timeStamp;
    private TextView mTimerText;
    public EditText boxnum;
    private TextView mCountText;

    private TextView mSpeedCountText;

    private TextView mAvrSpeedCountTest;

    private Button mClearButton, donebt;

    private Button mInvenButton;

    private Button mStopInvenButton;
    Button plus,minus, newfile;
    private Switch mTurboSwitch;

    private Switch mRssiSwitch;

    private Switch mFilterSwitch;

    private Switch mSoundSwitch;

    private Switch mMaskSwitch;

    private Switch mToggleSwitch;

    private Switch mPCSwitch;

    private Switch mFileSwitch;

    private ProgressBar mProgressBar;

    private Reader mReader;

    private Context mContext;

    private boolean mTagFilter = false;

    private boolean mSoundPlay = true;

    private boolean mMask = false;

    private boolean mInventory = false;

    private boolean mIsTurbo = true;

    private boolean mToggle = false;

    private boolean mIgnorePC = false;

    private boolean mRssi = false;

    private boolean mFile = false;

    private Handler mOptionHandler;

    private SoundPool mSoundPool;

    private int mSoundId;

    private float mSoundVolume;

    private boolean mSoundFileLoadState;


    private double mOldTotalCount = 0;

    private double mOldSec = 0;

    private FileManager mFileManager;

    private String mLocateTag;

    private String mLocateEPC;

    private int mLocateStartPos;



    private TextView mLocateTv;

    private boolean mLocate;

    private TimerTask mLocateTimerTask;

    private Timer mClearLocateTimer;


    private TextView tagText;


    private  String idi;

    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_repl);

        list = (ListView) findViewById(R.id.tableLayout);
        adapter = new listAdapter(this);
        list.setAdapter(adapter);

        fetchData download_data = new fetchData(this);
        download_data.download_data_from_link("https://api.myjson.com/bins/eirvm");

        mInvenButton = (Button)findViewById(R.id.inven_button);
        tagText = (TextView) findViewById(R.id.tag_locate_text);
        mLocateLayout = (LinearLayout)findViewById(R.id.tag_locate_container);

        mListLayout = (LinearLayout)findViewById(R.id.tag_list_container);


        mTagLocateProgress = (ProgressBar)findViewById(R.id.tag_locate_progress);


        mStopInvenButton = (Button)findViewById(R.id.stop_inven_button);


        mBackButton = (ImageButton)findViewById(R.id.back_button);


        new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                ListItem item = (ListItem) list.getItemAtPosition(position);
                mTagLocateProgress.setProgress(0);
                mListLayout.setVisibility(View.GONE);
                mLocateLayout.setVisibility(View.VISIBLE);
                mInvenButton.setText(R.string.track_str);
                mStopInvenButton.setText(R.string.stop_track_str);



                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Locating");
                alert.setMessage("Do you want to track selected tag?");

                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        mLocateTv.setText(mLocateTag);
                        //enableControl(false);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();

            }
        };
    }


            public void get_data(String data) {
                try {
                    JSONArray data_array = new JSONArray(data);

                    for (int i = 0; i < data_array.length(); i++) {
                        JSONObject obj = data_array.getJSONObject(i);
                        employees add = new employees();
                        add.id = obj.getString("id");
                        add.employee_salary = obj.getString("employee_salary");
                        add.employee_age = obj.getString("employee_age");
                        add.employee_name = obj.getString("employee_name");


                        elements.add(add);
                        Log.d(TAG, "get_data: " + elements);

                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void startStopwatch() {
            }

            private void clearAll() {
            }


            private void enableControl(boolean b) {
                if (b)
                    ;
                else
                    mRfidList.setOnItemClickListener(null);
                mTurboSwitch.setEnabled(b);
                mRssiSwitch.setEnabled(b);
                mFilterSwitch.setEnabled(b);
                mSoundSwitch.setEnabled(b);
                mMaskSwitch.setEnabled(b);
                mToggleSwitch.setEnabled(b);
                mPCSwitch.setEnabled(b);
                mFileSwitch.setEnabled(b);
                mSessionSpin.setEnabled(b);
                mSelFlagSpin.setEnabled(b);
                mBackButton.setVisibility(b ? View.VISIBLE : View.INVISIBLE);

            }

            private void pauseStopwatch() {
                if (D) Log.d(TAG, "pauseStopwatch");

                if (mStopwatchSvc != null && mStopwatchSvc.isRunning())
                    mStopwatchSvc.pause();


                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }*/