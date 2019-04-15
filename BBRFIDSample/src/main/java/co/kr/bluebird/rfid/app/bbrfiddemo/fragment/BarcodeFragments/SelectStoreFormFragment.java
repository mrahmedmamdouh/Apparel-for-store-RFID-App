package co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles.CZoneFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.settings;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectStoreFormFragment extends android.app.Fragment {

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
    private settings mSettings;
    private android.app.FragmentManager mFragmentManager;
    private android.app.Fragment mCurrentFragment;
    LinearLayout framelayout5;



    public static SelectStoreFormFragment newInstance() {
        return new SelectStoreFormFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        dbhelper = new WDxDBHelper(getActivity());
        db = dbhelper.getWritableDatabase();
    }

    public SelectStoreFormFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_store_form, container, false);

        lst_Store = new ArrayList<String>();
        storeHashMap = new HashMap<String, String>();
        mCurrentFragment = null;
        mFragmentManager = getFragmentManager();
        back = view.findViewById(R.id.btn_back);
        save = view.findViewById(R.id.btn_save);
        zoneSpinner = view.findViewById(R.id.spn_Zone);
        framelayout5 = (LinearLayout) view.findViewById(R.id.frame_layout5);
        getStore = view.findViewById(R.id.btn_getStore);
        storeNo = view.findViewById(R.id.txt_StoreNo);
        resetCarton = view.findViewById(R.id.btn_resetCarton);
        newCarton = view.findViewById(R.id.btn_newCarton);
        zoneNo = view.findViewById(R.id.LblZone);


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
                if (mSettings == null)
                    mSettings = settings.newInstance();
                mCurrentFragment = mSettings;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.fr4, mCurrentFragment);
                framelayout5.setVisibility(View.GONE);
                ft.commit();
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
//                        startActivity(new Intent(mContext,Form1.class));
                        Form1Fragment fragment = new Form1Fragment();
                        android.support.v4.app.FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
                    }
                    else
                    {
                        db.execSQL("UPDATE  tblSetting SET  [ActiveZoneNo]= '" + zoneNo.getText() + "' ");
//                        startActivity(new Intent(mContext,Form1.class));
                        Form1Fragment fragment = new Form1Fragment();
                        android.support.v4.app.FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
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

        return view;
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
            arrayadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,arraylist);
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
