package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;

import static co.kr.bluebird.rfid.app.bbrfiddemo.Constants.ACTION_BARCODE_CLOSE;

public class Form1 extends AppCompatActivity {

    private String mCurrentStatus;
    private boolean mIsRegisterReceiver;

    private static final String STATUS_CLOSE = "STATUS_CLOSE";
    private static final String STATUS_OPEN = "STATUS_OPEN";
    private static final String STATUS_TRIGGER_ON = "STATUS_TRIGGER_ON";

    private static final int SEQ_BARCODE_OPEN = 100;
    private static final int SEQ_BARCODE_CLOSE = 200;

    Button setup,exit,utilities;
    EditText zoneNo,Qty,barcode;
    CheckBox freeScan,delete;
    TextView zoneQty;
    Integer chkEanNoCheckDig,IntFreeScan;
    String ActiveStoreCode;
    Context mContext;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);

        mContext = Form1.this;
        dbhelper = new WDxDBHelper(this);
        db = dbhelper.getWritableDatabase();

        chkEanNoCheckDig=-1;
        IntFreeScan=-1;
        setup = findViewById(R.id.btn_setup);
        exit = findViewById(R.id.btn_exit);
        utilities = findViewById(R.id.btn_utilities);
        freeScan = findViewById(R.id.ch_freeScan);
        delete = findViewById(R.id.ch_delete);
        zoneNo = findViewById(R.id.txt_zoneNo);
        Qty = findViewById(R.id.txt_Qty);
        barcode = findViewById(R.id.txtbarcode);
        zoneQty = findViewById(R.id.lblZoneQty);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
               // moveTaskToBack(true);
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(1);
//                finish();
//                System.exit(0);
//                Intent intent = new Intent(getApplicationContext(), MainForm.class);
//                intent.putExtra("APPEXIT",1);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        });
        utilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,Util.class);
                intent.putExtra("ZONENO",Integer.parseInt(zoneNo.getText().toString()));
                startActivity(intent);
            }
        });
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SelectStoreForm.class));
            }
        });

        Qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(isFocused)
                {
                    Qty.setText("");
                }
                else
                {
                    Qty.setText("1");
                }
            }
        });

     /*   Qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if(Integer.parseInt(Qty.getText().toString())<1 || Integer.parseInt(Qty.getText().toString())>9999)
                {
                    Toast.makeText(mContext,"Quantity out of range   1-9999",Toast.LENGTH_SHORT).show();
                }

            }
        });
        */

        Integer flg=0;

      //  db.execSQL("Insert Into Tblsetting(ZoneNo,ActiveZoneNo,DevId,EanNoCheckDig,ActiveStoreCode,CheckFreeScan) values (1,2,'232',1,1,1)");
        Cursor c = db.rawQuery("SELECT   ZoneNo,ActiveZoneNo,DevId,EanNoCheckDig,ActiveStoreCode,CheckFreeScan FROM Tblsetting ",null);
        while (c.moveToNext())
        {
           flg=1;
            zoneNo.setText(c.getString(c.getColumnIndex("ActiveZoneNo")));
          //  Me.Text = "Dev#" + IIf(IsDBNull((rdr(2))), 0, rdr(2)).ToString + "-" + IIf(IsDBNull((rdr(4))), 0, rdr(4)).ToString
            ActiveStoreCode = c.getString(c.getColumnIndex("ActiveStoreCode"));
            chkEanNoCheckDig = c.getInt(c.getColumnIndex("EanNoCheckDig"));
            IntFreeScan = c.getInt(c.getColumnIndex("CheckFreeScan"));
        }
        if(IntFreeScan  == 1)
        {
            freeScan.setChecked(true);
        }
        else
        {
            freeScan.setChecked(false);
        }
          db.execSQL("UPDATE tblstock SET  Counted= Counted + " + Integer.parseInt(Qty.getText().toString()) + " ,Zone = '" + zoneNo.getText().toString() + "', StoreCode='" + ActiveStoreCode + "'  where iserial= '"+ barcode.getText().toString() +"' and Zone in ('" + zoneNo.getText().toString() + "',0)");

           Cursor cursor =db.rawQuery("SELECT  Sum(Counted) as Count FROM tblstock where zone=  '" + zoneNo.getText() + "' and storecode ='" + ActiveStoreCode + "' ",null);
           flg=0;

           while(cursor.moveToNext())
           {
               flg=1;
               int counted;
               counted = cursor.getInt(cursor.getColumnIndex("Count"));
               zoneQty.setText(Integer.toString(counted));
           }
       }

    private void Transactions()
    {
        String str;
        if(barcode.getText().toString().length()>3)
        {
            if(Qty.getText().toString().equals(""))
            {
                Qty.setText("1");
            }
            if(Integer.parseInt(Qty.getText().toString())==0  )
            {
                Qty.setText("1");
            }
            if(Integer.parseInt(Qty.getText().toString())<1 || Integer.parseInt(Qty.getText().toString())>9999)
            {
                Toast.makeText(mContext,"Quantity out of range   1-9999",Toast.LENGTH_SHORT).show();
                return;
            }
            if((Integer.parseInt(Qty.getText().toString())> Integer.parseInt(zoneQty.getText().toString())) && delete.isChecked()==true)
            {
                Toast.makeText(mContext,"Quantity to be deleted is greater than zone quantity",Toast.LENGTH_SHORT).show();
                return;
            }

            if (chkEanNoCheckDig == 1)
            {

                str = barcode.getText().toString();
                barcode.setText(str.substring(0, str.length() - 1));
            }

            Cursor c = db.rawQuery("SELECT   Counted,iserial,Zone FROM tblstock  where Iserial= '"+ barcode.getText().toString() +"' ",null);
            Integer flg=0;
            while (c.moveToNext())
            {
                flg=1;
                Integer zone = c.getInt(c.getColumnIndex("Zone"));
                if(zone == Integer.parseInt(zoneNo.getText().toString())|| zone ==0)
                {
                    flg=2;
                    if(delete.isChecked())
                    {
                      db.execSQL("UPDATE  tblstock SET  Counted=Counted - " + Integer.parseInt(Qty.getText().toString()) + " ,Zone= " + Integer.parseInt(zoneNo.getText().toString()) + ",storecode='" + ActiveStoreCode + "'  where Iserial= '"+ barcode.getText().toString() +"' and zone in (" + Integer.parseInt(zoneNo.getText().toString()) + ",0)");
                      Integer qty = Integer.parseInt(zoneQty.getText().toString())- Integer.parseInt(Qty.getText().toString());
                      zoneQty.setText(Integer.toString(qty));
                      delete.setChecked(false);
                    }
                    else
                    {
                        db.execSQL("UPDATE  tblstock SET  Counted=Counted + " + Integer.parseInt(Qty.getText().toString()) + " ,Zone= " + Integer.parseInt(zoneNo.getText().toString()) + ",storecode='" + ActiveStoreCode + "'  where Iserial= '"+ barcode.getText().toString() +"' and zone in (" + Integer.parseInt(zoneNo.getText().toString()) + ",0)");
                        Integer qty = Integer.parseInt(zoneQty.getText().toString())+ Integer.parseInt(Qty.getText().toString());
                        zoneQty.setText(Integer.toString(qty));
                    }
                }
            }

            if(flg == 1 || (flg==0 && freeScan.isChecked()) )
            {
              db.execSQL("insert into  tblstock(iserial ,Counted,Zone,storecode) values( '" + barcode.getText().toString() + "'," + Qty.getText().toString() + ", '" + zoneNo.getText().toString() + "','" + ActiveStoreCode + "')");
              Integer qty = Integer.parseInt(zoneQty.getText().toString())+Integer.parseInt(Qty.getText().toString());
              zoneQty.setText(Integer.toString(qty));
              delete.setChecked(false);
            }
            else if (freeScan.isChecked()==false && flg == 0)
            {
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                Toast.makeText(mContext,"Barcode Not found " + barcode.getText().toString() ,Toast.LENGTH_SHORT).show();
            }

         //   Me.LBLSTYLEV.Text = ""
           // barcode.setText("");
            Qty.setText("1");
        }
    }
    @Override
    protected void onResume() {
        mIsRegisterReceiver = false;
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_BARCODE_OPEN);
        if (mIsOpened)
            intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
        intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_OPEN);
        sendBroadcast(intent);
        mIsOpened = true;
        registerReceiver();

        barcode.setText("");
        Cursor cursor =db.rawQuery("SELECT  Sum(Counted) as Count FROM tblstock where zone=  '" + zoneNo.getText() + "' and storecode ='" + ActiveStoreCode + "' ",null);

        while(cursor.moveToNext())
        {
            int counted;
            counted = cursor.getInt(cursor.getColumnIndex("Count"));
            zoneQty.setText(Integer.toString(counted));
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        destroyEvent();
        super.onPause();
    }
    private boolean mIsOpened = false;
    private void registerReceiver() {
        if (mIsRegisterReceiver)
            return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS);

        registerReceiver(mReceiver, filter);
        mIsRegisterReceiver = true;
    }
    private void unregisterReceiver() {
        if (!mIsRegisterReceiver)
            return;
        unregisterReceiver(mReceiver);
        mIsRegisterReceiver = false;
    }
    private int mBarcodeHandle;
    private int mCount = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int seq = intent.getIntExtra(Constants.EXTRA_INT_DATA3, 0);

            if (action.equals(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA)) {
                int handle = intent.getIntExtra(Constants.EXTRA_HANDLE, 0);
                byte[] data = intent
                        .getByteArrayExtra(Constants.EXTRA_BARCODE_DECODING_DATA);
                String result = "";
                if (data != null)
                    result = new String(data);
                setResultText(result);
                mCount++;
            } else if (action
                    .equals(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS)) {
                mBarcodeHandle = intent.getIntExtra(Constants.EXTRA_HANDLE, 0);
                setSuccessFailText("");
                if (seq == SEQ_BARCODE_OPEN) {
                    mCurrentStatus = STATUS_OPEN;
                } else if (seq == SEQ_BARCODE_CLOSE) {
                    mCurrentStatus = STATUS_CLOSE;
                }

                refreshCurrentStatus();

            } else if (action
                    .equals(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED)) {
                int result = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0);
                if (result == Constants.ERROR_BARCODE_DECODING_TIMEOUT) {

                } else if (result == Constants.ERROR_NOT_SUPPORTED) {

                } else if (result == Constants.ERROR_BARCODE_ERROR_USE_TIMEOUT) {
                    mCurrentStatus = STATUS_CLOSE;

                } else

                    refreshCurrentStatus();
            } else if (action
                    .equals(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS)) {
                int status = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0);

                switch (status) {
                    case 0:
                        mCurrentStatus = STATUS_CLOSE;
                        break;
                    case 1:
                        mCurrentStatus = STATUS_OPEN;
                        break;
                    case 2:
                        mCurrentStatus = STATUS_TRIGGER_ON;
                        break;
                }
                setResultText(mCurrentStatus);
            }
        }
    };
    private void setResultText(String text) {
       String txt = text;
        barcode.setText(text);
       Transactions();
    }
    private void refreshCurrentStatus() {

    }
    private void setSuccessFailText(String text) {
    }
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }
    private void destroyEvent() {

        if (mIsOpened) {
            Intent intent = new Intent();
            intent.setAction(ACTION_BARCODE_CLOSE);
            intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
            intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_CLOSE);
            sendBroadcast(intent);
        }
        unregisterReceiver();
    }
    @Override
    protected void onDestroy() {
        destroyEvent();
        super.onDestroy();
    }
}
