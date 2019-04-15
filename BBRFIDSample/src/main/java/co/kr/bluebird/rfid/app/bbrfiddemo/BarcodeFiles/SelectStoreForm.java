package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;

public class SelectStoreForm extends AppCompatActivity {

    Button back,save,newCarton,resetCarton,getStore;
    Spinner zoneSpinner;
    EditText storeNo;
    TextView zoneNo;
    Context mContext;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    ArrayAdapter<String> adp_Store;
    List<String> lst_Store;
    HashMap<String, String> storeHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store_form);

        mContext = SelectStoreForm.this;
        dbhelper = new WDxDBHelper(this);
        db = dbhelper.getWritableDatabase();

        lst_Store = new ArrayList<String>();
        storeHashMap = new HashMap<String, String>();

        back = findViewById(R.id.btn_back);
        save = findViewById(R.id.btn_save);
        zoneSpinner = findViewById(R.id.spn_Zone);
        getStore = findViewById(R.id.btn_getStore);
        storeNo = findViewById(R.id.txt_StoreNo);
        resetCarton = findViewById(R.id.btn_resetCarton);
        newCarton = findViewById(R.id.btn_newCarton);
        zoneNo = findViewById(R.id.LblZone);


        Cursor c = db.rawQuery("SELECT ActiveZoneNo FROM Tblsetting",null);
        if(c.moveToNext())
        {
          String _zoneNo = c.getString(c.getColumnIndex("ActiveZoneNo"));
          zoneNo.setText(_zoneNo);
        }

/*
        db.execSQL("delete from TblStore");

        db.execSQL("insert into TblStore(Code,EName)values ('1234','Name1')");
        db.execSQL("insert into TblStore(Code,EName)values ('1235','Name2')");
        db.execSQL("insert into TblStore(Code,EName)values ('1236','Name3')");
        db.execSQL("insert into TblStore(Code,EName)values ('1237','Name4')");

        db.execSQL("delete from TblSetting");
        db.execSQL("insert into TblSetting (ZoneNo,ActiveZoneNo,DevId,showStore,EanNoCheckDig,CheckFreeScan,CheckServerConnection,ServiceUrl)" +
                " values (100,1,123,1,1,1,1,'http://192.168.1.9/ImportData')");
*/



        fillSpinner("select * from TblStore", "Code", "Ename", lst_Store, storeHashMap, adp_Store, zoneSpinner);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        resetCarton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               zoneNo.setText("1");
            }
        });
        newCarton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!zoneNo.getText().toString().equals("Zone"))
                {
                    int number = Integer.parseInt(zoneNo.getText().toString());
                    zoneNo.setText((Integer.toString(number+1)));
                }
            }
        });

        getStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storeNo.getText().toString().equals(""))
                {
                    Toast.makeText(mContext,"Please Enter Code",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Cursor c = db.rawQuery("SELECT Code FROM TblStore where Code = '" + storeNo.getText().toString() + "' ",null);
                    if(c.moveToNext())
                    {
                        String Code = storeHashMap.get(c.getString(c.getColumnIndexOrThrow("Code"))) ;
                        zoneSpinner.setSelection(lst_Store.indexOf(Code));
                    }
                    else
                    {
                        Toast.makeText(mContext,"Invalid Code",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    if(lst_Store.size()>0)
                    {
                        Object obj = getKeyFromValue(storeHashMap, zoneSpinner.getSelectedItem().toString());
                        String code = obj.toString();
                        db.execSQL("UPDATE  tblSetting SET  [ActiveZoneNo]= '" + zoneNo.getText() + "',[ActiveStorecode]= '" + code + "' ");
                        startActivity(new Intent(mContext,Form1.class));
                    }
                    else
                    {
                        db.execSQL("UPDATE  tblSetting SET  [ActiveZoneNo]= '" + zoneNo.getText() + "' ");
                        startActivity(new Intent(mContext,Form1.class));
                    }

                }
                catch (Exception ex)
                {
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                    Toast.makeText(mContext,"Error while saving",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fillSpinner(String query,String firstColumn ,String secondColumn,List<String> arraylist,HashMap<String,String> hashMap,ArrayAdapter arrayadapter,Spinner spinner)
    {
        try {
            Cursor c = db.rawQuery(query, null);

            int dataValueFiledIndex = c.getColumnIndexOrThrow(firstColumn);
            int dataTextFiledIndex = c.getColumnIndexOrThrow(secondColumn);
            c.moveToFirst();

            while(!c.isAfterLast())
            {
                arraylist.add(c.getString(dataValueFiledIndex) + ' ' + c.getString(dataTextFiledIndex));
                hashMap.put(c.getString(dataValueFiledIndex),c.getString(dataValueFiledIndex) + ' ' + c.getString(dataTextFiledIndex));
                c.moveToNext();
            }
            arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,arraylist);
            spinner.setAdapter(arrayadapter);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }
}
