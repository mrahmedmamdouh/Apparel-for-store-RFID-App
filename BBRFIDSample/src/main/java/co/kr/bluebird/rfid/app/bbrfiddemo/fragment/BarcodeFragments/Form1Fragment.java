package co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.sled.Reader;
import co.kr.bluebird.sled.SDConsts;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Form1Fragment extends Fragment {
    Button setup,exit,utilities;
    EditText zoneNo,Qty,barcode;
    CheckBox freeScan,delete;
    TextView zoneQty;
    Integer chkEanNoCheckDig,IntFreeScan;
    String ActiveStoreCode;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbhelper = new WDxDBHelper(getActivity());
        db = dbhelper.getWritableDatabase();
    }
    public Form1Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form1, container, false);

        chkEanNoCheckDig=-1;
        IntFreeScan=-1;
        exit =  view.findViewById(R.id.btn_exit);
        utilities =  view.findViewById(R.id.btn_utilities);
        freeScan =  view.findViewById(R.id.ch_freeScan);
        delete =  view.findViewById(R.id.ch_delete);
        zoneNo =  view.findViewById(R.id.txt_zoneNo);
        Qty =  view.findViewById(R.id.txt_Qty);
        barcode =  view.findViewById(R.id.txtbarcode);
        zoneQty =  view.findViewById(R.id.lblZoneQty);

        barcode.requestFocus();
        barcode.setEnabled(false);

        Qty.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        Qty.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event)
            {
                boolean handled = false;
                if (actionId == KeyEvent.KEYCODE_ENDCALL)
                {
                    // Handle pressing "Enter" key here
                    String qy = Qty.getText().toString();
                    barcode.requestFocus();
                    hideKeyboard(v);
                    if(qy.equals(""))
                    {
                        Qty.setText("1");
                    }
                    else
                    {
                        Qty.setText(qy);
                    }
                    handled = true;
                }
                return handled;
            }
        });

        FrameLayout layout = (FrameLayout) view.findViewById(R.id.id_from1fragment);

        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                String qy = Qty.getText().toString();
                barcode.requestFocus();
                hideKeyboard(view);
               if(qy.equals(""))
               {
                   Qty.setText("1");
               }
               else
               {
                   Qty.setText(qy);
               }

                return false;
            }
        });


        Integer flg=0;
        //  db.execSQL("Insert Into Tblsetting(ZoneNo,ActiveZoneNo,DevId,EanNoCheckDig,ActiveStoreCode,CheckFreeScan) values (1,2,'232',1,1,1)");
        Cursor c = db.rawQuery("SELECT   ZoneNo,ActiveZoneNo,DevId,EanNoCheckDig,ActiveStoreCode,CheckFreeScan FROM Tblsetting ",null);
        if (c.moveToNext())
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

        String _barcode = barcode.getText().toString();
        if(!_barcode.equals(""))
        {
            db.execSQL("UPDATE tblstock SET  Counted= Counted + " + Integer.parseInt(Qty.getText().toString()) + " ,Zone = '" + zoneNo.getText().toString() + "', StoreCode='" + ActiveStoreCode + "'  where iserial= '"+ _barcode +"' and Zone in ('" + zoneNo.getText().toString() + "',0)");
        }

       // Cursor cursor =db.rawQuery("SELECT  Sum(Counted) as Count FROM tblstock where zone=  '" + zoneNo.getText() + "' and storecode ='" + ActiveStoreCode + "' ",null);
        Cursor cursor =db.rawQuery("SELECT  Sum(Counted) as Count FROM tblstock where zone=  '" + zoneNo.getText().toString() + "' ",null);

        flg=0;

        if(cursor.moveToNext())
        {
            flg=1;
            int counted;
            counted = cursor.getInt(cursor.getColumnIndex("Count"));
            zoneQty.setText(Integer.toString(counted));
        }


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finishAffinity();
            }
        });
        utilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UtilFragment fragment = new UtilFragment ();
                Bundle args = new Bundle();
                args.putInt("ZONENO", Integer.parseInt(zoneNo.getText().toString()));
                fragment.setArguments(args);
                android.support.v4.app.FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();

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
        barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String _barcode = barcode.getText().toString().trim();
                if(!_barcode.equals(""))
                {
                    Transactions(_barcode);
                    barcode.setText("");
                    _barcode="";
                }
            }
        });
        return view;
    }
    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void Transactions(String barcode)
    {
        String _barcode = barcode;
        if(_barcode.length()>3)
        {
            if(Qty.getText().toString().equals(""))
            {
                Qty.setText("1");
            }
            if(Integer.parseInt(Qty.getText().toString())==0)
            {
                Qty.setText("1");
            }
            if(Integer.parseInt(Qty.getText().toString())<1 || Integer.parseInt(Qty.getText().toString())>9999)
            {
                Toast.makeText(getActivity(),"Quantity out of range   1-9999",Toast.LENGTH_SHORT).show();
                return;
            }
            if((Integer.parseInt(Qty.getText().toString())> Integer.parseInt(zoneQty.getText().toString())) && delete.isChecked()==true)
            {
                Toast.makeText(getActivity(),"Quantity to be deleted is greater than zone quantity",Toast.LENGTH_SHORT).show();
                return;
            }
            if (chkEanNoCheckDig == 1)
            {
                _barcode = (_barcode.substring(0, _barcode.length() - 1));
            }
           // Cursor c = db.rawQuery("SELECT   Counted,iserial,Zone FROM tblstock  where Iserial= '"+ barcode.getText().toString() +"' ",null);
            Cursor c = db.rawQuery("SELECT   Counted,iserial,Zone FROM tblstock  where Iserial= '"+ _barcode +"' ",null);

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
                        db.execSQL("UPDATE  tblstock SET  Counted=Counted - " + Integer.parseInt(Qty.getText().toString()) + " ,Zone= " + Integer.parseInt(zoneNo.getText().toString()) + ",storecode='" + ActiveStoreCode + "'  where Iserial= '"+ _barcode +"' and Zone in (" + Integer.parseInt(zoneNo.getText().toString()) + ",0)");
                        Integer qty = Integer.parseInt(zoneQty.getText().toString())- Integer.parseInt(Qty.getText().toString());
                        zoneQty.setText(Integer.toString(qty));
                        delete.setChecked(false);
                        break;
                    }
                    else
                    {
                        db.execSQL("UPDATE  tblstock SET  Counted=Counted + " + Integer.parseInt(Qty.getText().toString()) + " ,Zone= " + Integer.parseInt(zoneNo.getText().toString()) + ",storecode='" + ActiveStoreCode + "'  where Iserial= '"+ _barcode +"' and Zone in (" + Integer.parseInt(zoneNo.getText().toString()) + ",0)");
                        Integer qty = Integer.parseInt(zoneQty.getText().toString())+ Integer.parseInt(Qty.getText().toString());
                        zoneQty.setText(Integer.toString(qty));
                        break;
                    }
                }
            }
            if( (flg == 1) || (flg==0 && freeScan.isChecked()) )
            {
                //db.execSQL("insert into  tblstock(iserial ,Counted,Zone,storecode) values( '" + barcode.getText().toString() + "'," + Qty.getText().toString() + ", '" + zoneNo.getText().toString() + "','" + ActiveStoreCode + "')");
                db.execSQL("insert into  tblstock(iserial ,Counted,Zone,storecode) values( '" + _barcode + "'," + Qty.getText().toString() + ", '" + zoneNo.getText().toString() + "','" + ActiveStoreCode + "')");

                Integer qty = Integer.parseInt(zoneQty.getText().toString())+Integer.parseInt(Qty.getText().toString());
                zoneQty.setText(Integer.toString(qty));
                delete.setChecked(false);
               // barcode.setText("");
            }
            else if (freeScan.isChecked()==false && flg == 0)
            {
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                Toast.makeText(getActivity(),_barcode + " Barcode Not found ",Toast.LENGTH_LONG).show();
            }
            Qty.setText("1");
        }
    }
}
