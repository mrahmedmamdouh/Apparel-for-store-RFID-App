package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;

import static co.kr.bluebird.rfid.app.bbrfiddemo.Constants.ACTION_BARCODE_CLOSE;

public class ShipItems extends AppCompatActivity {

    private String mCurrentStatus;
    private boolean mIsRegisterReceiver;
    private static final String STATUS_CLOSE = "STATUS_CLOSE";
    private static final String STATUS_OPEN = "STATUS_OPEN";
    private static final String STATUS_TRIGGER_ON = "STATUS_TRIGGER_ON";

    private static final int SEQ_BARCODE_OPEN = 100;
    private static final int SEQ_BARCODE_CLOSE = 200;

    EditText itemShip,qty;
    TextView label1,zoneQty;
    Button newCarton,utilities;
    Context mContext;
    Integer chkEanNoCheckDig,devID;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    String cartonNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_items);

        mContext = ShipItems.this;
        dbhelper = new WDxDBHelper(this);
        db = dbhelper.getWritableDatabase();

        Intent intent = getIntent();
        cartonNo = intent.getStringExtra("CARTONNO");

        itemShip= findViewById(R.id.TxtItemShip);
        qty= findViewById(R.id.txtQty);
        label1= findViewById(R.id.label1);
        zoneQty= findViewById(R.id.lblZoneQty);
        newCarton= findViewById(R.id.btn_newCarton);
        utilities= findViewById(R.id.btn_utilities);


        label1.setText(getIntent().getStringExtra("CARTONNO"));

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



        newCarton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,Shipment.class));
            }
        });
        utilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mContext,UtilitiesShipItems.class);
                intent.putExtra("CARTONQTY",zoneQty.getText());
                startActivity(intent);
            }
        });
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
        itemShip.setText(text);
        Transactions();
    }
    private void Transactions()
    {
     if(Integer.parseInt(qty.getText().toString())== 0)
     {
         qty.setText("1");
     }
     String str = "";
     if(chkEanNoCheckDig ==1)
     {
        str = itemShip.getText().toString();
        itemShip.setText(str.substring(0,str.length() - 1));
     }

     Cursor c = db.rawQuery("Select Iserial From TblCarton where Iserial  = '" + itemShip.getText().toString() + "' and ShipNo= '" + cartonNo + "'",null);
     if(c.moveToNext())
     {
         db.execSQL(" update TblCarton set Counted = Counted +" + Integer.parseInt(qty.getText().toString())+ " where Iserial  = '" + itemShip.getText() + "' and ShipNo= '" + cartonNo + "' ");
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
         db.execSQL("insert into  TblCarton (ShipNo,iserial,counted) values ( '" + cartonNo + "' ,'" + itemShip.getText() + "' , " + Integer.parseInt(qty.getText().toString()) + " )");
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
