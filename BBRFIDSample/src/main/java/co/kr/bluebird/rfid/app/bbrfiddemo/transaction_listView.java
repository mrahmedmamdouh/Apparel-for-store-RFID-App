package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import co.kr.bluebird.rfid.app.bbrfiddemo.control.ListItem;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.TransRFIDFragment;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;

public class transaction_listView extends Activity implements fetchData.download_complete {
    public Context mContext;
    public transaction_listView main;
    public customAdapter adapter;
    public ListView list;
    ArrayList<headerRow> data= new ArrayList<>();
    private Reader mReader;
    public headerRow headerrow;

    public FrameLayout TransList;
    public String urlNumber;
    public String portNumber;
    public String store_id;
    public static String x;
    public String RFID_CreationDate;
    public StringEntity entity1;
    public Spinner changeMode;
    public String ModeIndicator;
    public int tblRFIDTransType, tblUser, tblStore;
    public boolean barcode = false;
    private static final String TAG = transaction_listView.class.getSimpleName();

    private static final boolean D = Constants.MAIN_D;

    public static ArrayList <Transaction_objects> transElements = new ArrayList<>();

    private final transaction_listView.MainHandler mcycleCountHandler = new transaction_listView.MainHandler(transaction_listView.this);

    public final transaction_listView.UpdateConnectHandler mUpdateConnectHandler = new transaction_listView.UpdateConnectHandler(this);
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_listview);
        dbhelper = new WDxDBHelper(getApplicationContext());
        db =dbhelper.getWritableDatabase();

        TransList = (FrameLayout)findViewById(R.id.content4);
        changeMode = (Spinner) findViewById(R.id.changeMode);
        changeMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                ModeIndicator = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }});


            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.change_Mode, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        changeMode.setAdapter(adapter1);


        SharedPreferences pref = getApplication().getSharedPreferences("storeNumber", Context.MODE_PRIVATE);
        store_id = pref.getString("storeNumber", "DEFAULT");

        SharedPreferences pref1 = getApplication().getSharedPreferences("urlNumber", Context.MODE_PRIVATE);
        urlNumber = pref1.getString("urlNumber", "DEFAULT");

        SharedPreferences pref2 = getApplication().getSharedPreferences("portNumber", Context.MODE_PRIVATE);
        portNumber = pref2.getString("portNumber", "DEFAULT");

        fetchData download_data = new fetchData(this);
        download_data.download_data_from_link("http://41.65.223.218:8888/api/RFIDUserTransType?UserId=150");


        list = (ListView) findViewById(R.id.list1);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);


        adapter = new customAdapter(main, mContext);

        list.setAdapter(adapter);
        list.setOnItemClickListener(listItemClickListener);


    }



    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {


            Transaction_objects i = (Transaction_objects) parent.getItemAtPosition(position);


            db.beginTransaction();
            try {
                 headerrow = new headerRow();

                headerrow.tblRFIDTransType = i.Iserial;
                SharedPreferences pref1 = getApplication().getSharedPreferences("UserId", Context.MODE_PRIVATE);
                headerrow.tblUser = pref1.getInt("UserId", 1);
                SharedPreferences pref = getApplication().getSharedPreferences("StoreId", Context.MODE_PRIVATE);
                headerrow.tblStore = pref.getInt("StoreId", 1);

                headerrow.RFID_CreationDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS",
                        Locale.getDefault()).format(System.currentTimeMillis());

                if (changeMode.getSelectedItem().toString().equals("BARCODE")) {
                    headerrow.barcode = "true";
                } else if (changeMode.getSelectedItem().toString().equals("DEFAULT")) {
                    headerrow.barcode = "false";
                }


                db.execSQL("insert into TransactionHeader  (tblRFIDTransType, tblUser, tblStore,RFID_CreationDate,barcode) values ('" + headerrow.tblRFIDTransType + "','" + headerrow.tblUser + "','" + headerrow.tblStore + "','" + headerrow.RFID_CreationDate + "','" + headerrow.barcode + "')");


                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            Cursor c = db.rawQuery("select count(*) as COUNT from TransactionHeader", null);
            while (c.moveToNext()) {
                x = c.getString(c.getColumnIndex("COUNT")); }
            Gson g = new Gson();
            StringEntity entity1 = null;
            data.clear();
            c = db.rawQuery("select distinct tblRFIDTransType, tblUser, tblStore, RFID_CreationDate, barcode from TransactionHeader where tblRFIDTransType = " + headerrow.tblRFIDTransType , null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    data.add(new transaction_listView.headerRow(c.getInt(c.getColumnIndex("tblRFIDTransType")), (c.getInt(c.getColumnIndex("tblUser"))), (c.getInt(c.getColumnIndex("tblStore"))), (c.getString(c.getColumnIndex("RFID_CreationDate"))), (c.getString(c.getColumnIndex("barcode")))));
                }
            }
            c.close();
            String userData = g.toJson(data);
            Log.d(TAG, "onSuccess: " + userData);

            try {
                entity1 = new StringEntity(userData, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String serviceURL = "http://41.65.223.218:8888/api/RFIDTransHeader";
            Log.d(TAG, serviceURL);
            AsyncHttpClient client = new AsyncHttpClient();
            try {
                client.post(getApplication(), serviceURL, entity1, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(String NewIserial) {
                        Log.d(TAG, "onSuccess: " + NewIserial);
                        SharedPreferences pref = getApplication().getSharedPreferences("NewIserial", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("NewIserial", NewIserial);  // Saving string
                        editor.apply();
                        super.onSuccess(NewIserial);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Toast.makeText(mContext, "Data has not been sent", Toast.LENGTH_SHORT).show();

                        super.onFailure(error);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
            } catch (IllegalArgumentException exception) {
            }


            if (ModeIndicator.equals("DEFAULT")){
                barcode = false;
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.xxx,TransRFIDFragment.newInstance());
                TransList.setVisibility(View.GONE);
                transaction.commit();
            }
            else if (ModeIndicator.equals("BARCODE")){
                barcode = true;
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.xxx,TransRFIDFragment.newInstance());
                TransList.setVisibility(View.VISIBLE);
                transaction.commit();
            }

            }



        };


    @Override
    public void get_data(String data) {
        try {
            JSONArray data_array = new JSONArray(data);

            for (int i = 0; i < data_array.length(); i++) {
                JSONObject obj = data_array.getJSONObject(i);
                Transaction_objects add = new Transaction_objects();
                add.Iserial = obj.getInt("Iserial");
                add.Code = obj.getString("Code");
                add.Aname = obj.getString("Aname");
                add.Ename =obj.getString("Ename");
                add.SPName = obj.getString("SPName");
                transElements.add(add);
                Log.d(TAG, "get_data: " + transElements);

            }

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class headerRow
    {
        private int tblRFIDTransType;
        private int tblUser;
        private  int tblStore;
        private String RFID_CreationDate;
        private String barcode;

        public headerRow(int tblRFIDTransType, int tblUser, int tblStore, String RFID_CreationDate, String barcode) {
            this.tblRFIDTransType = tblRFIDTransType;
            this.tblUser = tblUser;
            this.tblStore = tblStore;
            this.RFID_CreationDate = RFID_CreationDate;
            this.barcode = barcode;
        }

        public headerRow() {

        }

        public int getTblRFIDTransType() {
            return tblRFIDTransType;
        }

        public void setTblRFIDTransType(int tblRFIDTransType) {
            this.tblRFIDTransType = tblRFIDTransType;
        }

        public int getTblUser() {
            return tblUser;
        }

        public void setTblUser(int tblUser) {
            this.tblUser = tblUser;
        }

        public int getTblStore() {
            return tblStore;
        }

        public void setTblStore(int tblStore) {
            this.tblStore = tblStore;
        }

        public String getRFID_CreationDate() {
            return RFID_CreationDate;
        }

        public void setRFID_CreationDate(String RFID_CreationDate) {
            this.RFID_CreationDate = RFID_CreationDate;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

    }

    private static class MainHandler extends Handler {
        private final WeakReference<transaction_listView> mExecutor;
        public MainHandler(transaction_listView ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            transaction_listView executor = mExecutor.get();
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
    private static class UpdateConnectHandler extends Handler {
        private final WeakReference<transaction_listView> mExecutor;
        public UpdateConnectHandler(transaction_listView ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            transaction_listView executor = mExecutor.get();
            if (executor != null) {
                executor.handleUpdateConnectHandler(msg);
            }
        }
    }

    public void handleUpdateConnectHandler(Message m) {

    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onResume");
         transElements = new ArrayList<>();
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onPause");
         transElements = new ArrayList<>();
        super.onPause();
    }

    private class itemModel
    {
        private String items;
        private int NewIserial;




        public int getNewIserial() {
            return NewIserial;
        }

        public void setNewIserial(int store_id) {
            this.NewIserial = NewIserial;
        }

        public itemModel(String items, int NewIserial)
        {
            this.items=items;
            this.NewIserial= NewIserial;
        }
        public String getItem() {
            return items;
        }

        public void setItem(String items) {
            this.items = items;
        }
    }

}
