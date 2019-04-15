package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;

import static co.kr.bluebird.rfid.app.bbrfiddemo.Constants.ACTION_BARCODE_CLOSE;


public class Carton extends AppCompatActivity {
    private String mCurrentStatus;
    private boolean mIsRegisterReceiver;

    private static final String STATUS_CLOSE = "STATUS_CLOSE";
    private static final String STATUS_OPEN = "STATUS_OPEN";
    private static final String STATUS_TRIGGER_ON = "STATUS_TRIGGER_ON";

    private static final int SEQ_BARCODE_OPEN = 100;
    private static final int SEQ_BARCODE_CLOSE = 200;


    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    Button back,utilities;
    Context mContext;
    TextView cartonCount;
    EditText ship;
    CheckBox delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carton);
        mContext = Carton.this;
        dbhelper = new WDxDBHelper(this);
        db = dbhelper.getWritableDatabase();

        utilities = findViewById(R.id.btn_utilities);
        cartonCount = findViewById(R.id.lblCartonCount);
        ship = findViewById(R.id.txt_ship);
        delete = findViewById(R.id.ch_delete);


        utilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext,UtilitiesCarton.class);
                intent.putExtra("CARTONQTY",cartonCount.getText().toString());
                startActivity(intent);
            }
        });

        Cursor c = db.rawQuery("Select Count(ShipNo) as ShipCount from TblShip",null);
        if(c!=null && c.getCount()>0)
        {
            if(c.moveToNext()) {
                cartonCount.setText(c.getString(c.getColumnIndex("ShipCount")));
            }
        }
    }
    Cursor c;
    private void cartonTransactions()
    {
         c = db.rawQuery("Select ShipNo from TblShip where ShipNo = '" + ship.getText().toString() + "' ",null);

        if (!delete.isChecked())
        {
            if(c.moveToNext())
            {
                Toast.makeText(mContext,"Carton Number Already Exists",Toast.LENGTH_LONG).show();
            }
            else
            {
                db.execSQL("Insert into TblShip (ShipNo) values ('" + ship.getText().toString() +"')");
               // ship.setText("");
                Integer newCount = Integer.parseInt(cartonCount.getText().toString())+1;
                cartonCount.setText(newCount.toString());
            }
        }
        else
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setTitle("Delete Item");
            mBuilder.setMessage("Important Question");

            mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(c.getCount()<1 || c==null )
                    {
                     Toast.makeText(mContext,"Carton Number Doesn't Exists",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        db.execSQL("delete from TblShip Where ShipNo = '" + ship.getText().toString() + "' ");
                        ship.setText("");
                        Integer newCount = Integer.parseInt(cartonCount.getText().toString())-1;
                        cartonCount.setText(newCount.toString());
                        delete.setChecked(false);
                    }

                }
            });

            mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            AlertDialog dialog = mBuilder.create();
            dialog.show();

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

        Cursor c = db.rawQuery("Select Count(ShipNo) as ShipCount from TblShip",null);
        if(c!=null && c.getCount()>0)
        {
            if(c.moveToNext()) {
                cartonCount.setText(c.getString(c.getColumnIndex("ShipCount")));
            }
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
       ship.setText(text);
       cartonTransactions();
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
