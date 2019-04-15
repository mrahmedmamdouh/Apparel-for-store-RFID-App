package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.List;

import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;



/**
 * A simple {@link Fragment} subclass.
 */
public class CZoneFragment extends android.app.Fragment {

    Button importStores,saveSettings;
    CheckBox showStore,removeCheckDigit,freeScan,serverConnection;
    EditText maxZone,activeZone,deviceID,url;
    Context mContext;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    boolean isSettings =false;

    public static CZoneFragment newInstance() {
        return new CZoneFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbhelper = new WDxDBHelper(getActivity());
        db = dbhelper.getWritableDatabase();
    }

    public CZoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_czone, container, false);

        importStores = view.findViewById(R.id.btn_importStores);
        saveSettings = view.findViewById(R.id.btn_saveSettings);
        showStore = view.findViewById(R.id.ch_showStoreNo);
        removeCheckDigit = view.findViewById(R.id.ch_removeCheckDigit);
        freeScan = view.findViewById(R.id.ch_freeScan);
        serverConnection = view.findViewById(R.id.ch_serverConnection);
        maxZone = view.findViewById(R.id.txt_maxZone);
        activeZone = view.findViewById(R.id.txt_activeZone);
        deviceID = view.findViewById(R.id.txt_deviceID);
        url = view.findViewById(R.id.txt_url);


        Cursor c = db.rawQuery("SELECT ZoneNo,ActiveZoneNo,DevId,ShowStore,EanNoCheckDig,CheckFreeScan,CheckServerConnection,ServiceUrl FROM Tblsetting ",null);
        while (c.moveToNext())
        {
            isSettings=true;
            maxZone.setText(Integer.toString(c.getInt(c.getColumnIndex("ZoneNo"))));
            activeZone.setText(Integer.toString(c.getInt(c.getColumnIndex("ActiveZoneNo"))));
            deviceID.setText(Integer.toString(c.getInt(c.getColumnIndex("DevId"))));
            url.setText(c.getString(c.getColumnIndex("ServiceURL")));
            // checkboxes
            int isStoreCheck = c.getInt(c.getColumnIndex("ShowStore"));
            if(isStoreCheck == 1)
            {
                showStore.setChecked(true);
            }
            else
            {
                showStore.setChecked(false);
            }

            int isEanNoChecked = c.getInt(c.getColumnIndex("EanNoCheckDig"));
            if(isEanNoChecked == 1)
            {
                removeCheckDigit.setChecked(true);
            }
            else
            {
                removeCheckDigit.setChecked(false);
            }

            int isFreeScanChecked = c.getInt(c.getColumnIndex("CheckFreeScan"));
            if(isFreeScanChecked == 1)
            {
                freeScan.setChecked(true);
            }
            else
            {
                freeScan.setChecked(false);
            }
            int isServerSettingsChecked = c.getInt(c.getColumnIndex("CheckServerConnection"));
            if(isServerSettingsChecked == 1)
            {
                serverConnection.setChecked(true);
            }
            else
            {
                serverConnection.setChecked(false);
            }
        }


        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _url = url.getText().toString() ;
                int _showStore =0;
                if(showStore.isChecked())
                {
                    _showStore =1;
                }
                int _removeCheckDigit =0;
                if(removeCheckDigit.isChecked())
                {
                    _removeCheckDigit =1;
                }

                int _freeScan=0;
                if(freeScan.isChecked())
                {
                    _freeScan=1;
                }
                int _serverCon=0;
                if(serverConnection.isChecked())
                {
                    _serverCon=1;
                }
                try
                {
                    if(!isSettings)
                    {
                        db.execSQL("insert into TblSetting (ZoneNo,ActiveZoneNo,DevId,showStore,EanNoCheckDig,CheckFreeScan,CheckServerConnection,ServiceUrl)" +
                                " values (" + Integer.parseInt( maxZone.getText().toString()) + "," + Integer.parseInt(activeZone.getText().toString()) + "" +
                                "," + Integer.parseInt(deviceID.getText().toString()) +","+ _showStore +" , " + _removeCheckDigit +"," + _freeScan + "," + _serverCon + ",'"+ _url +"')");
                    }
                    else
                    {
                        db.execSQL("UPDATE tblSetting SET  zoneNo= " + Integer.parseInt( maxZone.getText().toString()) + " ,ActiveZoneNo= " + Integer.parseInt(activeZone.getText().toString()) + ",DevId= " + Integer.parseInt(deviceID.getText().toString()) + ",EanNoCheckDig= " + _removeCheckDigit + ",showstore=" + _showStore + ",CheckFreeScan= " + _freeScan + ",CheckServerConnection= " + _serverCon + " , ServiceUrl ='"+ _url +"' ");
                    }

                    Toast.makeText(getActivity(),"Settings saved successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception exception)
                {
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                    Toast.makeText(getActivity(),"Error while saving",Toast.LENGTH_SHORT).show();
                }
            }
        });
        importStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String serviceURL = url.getText().toString();
                Integer counter;
                Integer Items ;
                String  code ;
                String  Ename ;
                if (serverConnection.isChecked() == true)
                {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    mBuilder.setTitle("Importing Stores !!");
                    mBuilder.setMessage("Are you sure you want to Update the stores ?");

                    mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // Connect to Web API and Get Store Data
                            AsyncHttpClient client = new AsyncHttpClient();
                            try
                            {
                                client.get(serviceURL,new AsyncHttpResponseHandler()
                                {
                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                    }

                                    @Override
                                    public void onSuccess(String content) {
                                        super.onSuccess(content);
                                        db.execSQL("Delete from TblStore");
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<DataModule.Store>>(){}.getType();
                                        List<DataModule.Store> storeList = gson.fromJson(content,type);
                                        for (DataModule.Store item : storeList) {
                                            String _code = item.Code;
                                            String _name = item.Name;
                                            // Insert it into SQLite
                                            db.execSQL("insert into TblStore (Code,Ename) values ('"+ _code +"' , '"+ _name +"')");
                                        }
                                        Toast.makeText(getActivity(),"Stores Imported Successfully",Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onFailure(Throwable error) {
                                        super.onFailure(error);
                                        Toast.makeText(getActivity(),"Connection Error",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                    }
                                });
                            }
                            catch (IllegalArgumentException exception)
                            {
                                Toast.makeText(mContext,"Make sure the service url is correct",Toast.LENGTH_SHORT).show();
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
                else
                {
                    Toast.makeText(getActivity(),"You have to be connected to the server to import Stores",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

}
