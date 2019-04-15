package co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles.CZoneFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles.MainMenu;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.RFAccessFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.ShipItemsFragment1;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.TransRFIDFragment;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShipItemsFragment extends android.app.Fragment {

    public Reader mReader;
    private android.app.Fragment mCurrentFragment;
    private android.app.FragmentManager mFragmentManager;
    ArrayList<ShipItemsFragment.itemModel> data= new ArrayList<>();

    EditText itemShip,qty;
    TextView label1,zoneQty;
    Button newCarton,utilities,send;
    Integer chkEanNoCheckDig,devID;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    private Context mContext;
    TextView transactionType2;
    String cartonNo,_NewIserial,SPName;
    private CZoneFragment mCZoneFragment;
    private CartonFragment mCartonFragment;
    private MainMenu mMainMenu;
    private TransRFIDFragment mTransRFIDFragment;
    public static ShipItemsFragment newInstance() {
        return new ShipItemsFragment();
    }
    private ShipItemsFragment.AccessHandler mAccessHandler = new ShipItemsFragment.AccessHandler(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbhelper = new WDxDBHelper(getActivity());
        db = dbhelper.getWritableDatabase();
    }

    public ShipItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ship_items, container, false);
        SharedPreferences pref4 = getActivity().getSharedPreferences("Ename", Context.MODE_PRIVATE);
        String Ename = pref4.getString("Ename", "DEFAULT");
        transactionType2 = (TextView)view.findViewById(R.id.transactionType2);
        transactionType2.setText(Ename);
        itemShip= view.findViewById(R.id.TxtItemShip);
        qty= view.findViewById(R.id.txtQty);
        label1= view.findViewById(R.id.label1);
        zoneQty= view.findViewById(R.id.lblZoneQty);
        mCurrentFragment = null;
        mFragmentManager = getFragmentManager();
        send= (Button) view.findViewById(R.id.send);

        mReader = Reader.getReader(mContext, mAccessHandler);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getActivity().getSharedPreferences("NewIserial", MODE_PRIVATE);
                _NewIserial =  pref.getString("NewIserial", "DEFAULT");
                Gson g = new Gson();
                StringEntity entity1 = null;
                data.clear();
                Cursor c = db.rawQuery("select distinct EBC,tblRFidTransHeader,Qty from TblCarton where tblRFidTransHeader = " + _NewIserial,null);
                if(c!=null && c.getCount()>0)
                {
                    while(c.moveToNext())
                    {
                        data.add(new itemModel(c.getString(c.getColumnIndex("EBC")), (c.getInt(c.getColumnIndex("tblRFidTransHeader"))),(c.getInt(c.getColumnIndex("Qty")))));
                    }
                }
                c.close();
                String userData = g.toJson(data);
                try {
                    entity1= new StringEntity(userData,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String serviceURL = "http://41.65.223.218:8888/api/RFIDTransDetailQty";
                Log.d(getTag(),serviceURL);
                AsyncHttpClient client = new AsyncHttpClient();
                try {
                    client.post(getActivity(), serviceURL  , entity1,"application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                        }
                        @Override
                        public void onSuccess(String id) {
                            Log.d(TAG, "onSuccess: " + id);
                            if(id.equals("true")){
                                db.delete("TransactionDetails","tblRFidTransHeader= ?", new String[]{_NewIserial});
                                Toast.makeText(getActivity(), "DATA HAS BEEN SENT SUCCESSFULLY ", Toast.LENGTH_LONG).show();


                                SharedPreferences pref1 = getActivity().getSharedPreferences("SPName", MODE_PRIVATE);
                                SPName = pref1.getString("SPName", "DEFAULT");

                                AsyncHttpClient client = new AsyncHttpClient();
                                RequestParams params = new RequestParams();
                                params.put("NewIserial", _NewIserial);
                                params.put("SPName", SPName);
                                client.post("http://41.65.223.218:8888/API/RFIDTransSP?TransId=41&SPName=SP1", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(String numberOfRowsBarCode) {
                                        Log.d(TAG, "onSuccess: " + numberOfRowsBarCode);
                                        SharedPreferences pref = getActivity().getSharedPreferences("numberOfRowsBarCode", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("numberOfRowsBarCode", numberOfRowsBarCode);  // Saving string
                                        editor.apply();
                                        super.onSuccess(numberOfRowsBarCode); }
                                });

                            }
                            else if (id.equals("false")){ Toast.makeText(getActivity(), "DATA HAS NOT BEEN SENT .... PLEASE TRY AGAIN LATER ", Toast.LENGTH_LONG).show();}

                            super.onSuccess(id);
                        }
                        @Override
                        public void onFailure(Throwable error) {
                            Toast.makeText(getActivity(), "Data has not been sent", Toast.LENGTH_SHORT).show();

                            super.onFailure(error);
                        }
                        @Override
                        public void onFinish() {
                            data.clear();
                            super.onFinish();
                        }
                    });
                }
                catch (IllegalArgumentException exception)
                {

                }
            }
        });

     //   label1.setText(getActivity().getIntent().getStringExtra("CARTONNO"));

        try{
            cartonNo=getArguments().getString("CARTONNO");
            label1.setText(cartonNo);
        }
        catch (NullPointerException ex)
        {
        }
        itemShip.requestFocus();

        FrameLayout layout = (FrameLayout) view.findViewById(R.id.id_shipItemsfrag);
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                String qy = qty.getText().toString();
                itemShip.requestFocus();
                hideKeyboard(view);
                if(qy.equals(""))
                {
                    qty.setText("1");

                }else
                {
                    qty.setText(qy);
                }

                return false;
            }
        });

        itemShip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!itemShip.getText().toString().equals(""))
                {
                    Transactions();
                    itemShip.setText("");
                }
            }
        });

        Cursor c = db.rawQuery("SELECT DevId,EanNoCheckDig FROM Tblsetting",null);
        if(c.moveToNext())
        {
            devID = c.getInt(c.getColumnIndex("DevId"));
            chkEanNoCheckDig = c.getInt(c.getColumnIndex("EanNoCheckDig"));
        }
        else
        {
            devID=-1;
            chkEanNoCheckDig=-1;
        }





        return view;
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void Transactions()
    {
        String _itemShip = itemShip.getText().toString();
        if(Integer.parseInt(qty.getText().toString())== 0)
        {
            qty.setText("1");
        }
        if(chkEanNoCheckDig ==1)
        {
            _itemShip = (_itemShip.substring(0,_itemShip.length() - 1));
        }
        Cursor c = db.rawQuery("Select EBC From TblCarton where EBC  = '" + _itemShip + "' and ShipNo= '" + cartonNo + "'",null);
        if(c.moveToNext())
        {
            db.execSQL(" update TblCarton set Qty = Qty +" + Integer.parseInt(qty.getText().toString())+ " where EBC  = '" + _itemShip + "' and ShipNo= '" + cartonNo + "' ");
            Integer _qty=0;
            if(!zoneQty.getText().toString().equals(""))
            {
                _qty = Integer.parseInt(zoneQty.getText().toString());
            }

            Integer newQty = _qty + Integer.parseInt(qty.getText().toString());
            zoneQty.setText(Integer.toString(newQty));
        }
        else
        {
            db.execSQL("insert into  TblCarton (ShipNo,EBC,Qty,tblRFidTransHeader) values ( '" + cartonNo + "' ,'" + _itemShip + "' , " + Integer.parseInt(qty.getText().toString()) + " ),'" + _NewIserial + "'");
            Integer _qty=0;
            if(!zoneQty.getText().toString().equals(""))
            {
                _qty = Integer.parseInt(zoneQty.getText().toString());
            }
            Integer newQty = _qty + Integer.parseInt(qty.getText().toString());
            zoneQty.setText(Integer.toString(newQty));
        }
       //  itemShip.setText("");
        qty.setText("1");
    }

    private class itemModel
    {
        private String EBC;
        private int tblRFidTransHeader;
        private int Qty;

        public int getQty() {
            return Qty;
        }

        public void setQty(int qty) {
            Qty = qty;
        }

        public int gettblRFidTransHeader() {
            return tblRFidTransHeader;
        }

        public void settblRFidTransHeader(int tblRFidTransHeader) {
            this.tblRFidTransHeader = tblRFidTransHeader;
        }

        public itemModel(String EBC, int tblRFidTransHeader,int Qty)
        {
            this.EBC=EBC;
            this.Qty=Qty;
            this.tblRFidTransHeader= tblRFidTransHeader;
        }
        public String getEBC() {
            return EBC;
        }

        public void setEBC (String EBC) {
            this.EBC = EBC;
        }
    }
    int ret = -100;
    int mode;



    private static class AccessHandler extends Handler {
        private final WeakReference<ShipItemsFragment> mExecutor;
        public AccessHandler(ShipItemsFragment f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            ShipItemsFragment executor = mExecutor.get();
            if (executor != null) {

            }
        }
    }
}
