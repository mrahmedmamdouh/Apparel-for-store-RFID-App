package co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShipmentFragment extends android.app.Fragment {

    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    EditText cartonNumber;
    Button back;

    public static ShipmentFragment newInstance() {
        return new ShipmentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbhelper = new WDxDBHelper(getActivity());
        db = dbhelper.getWritableDatabase();
    }

    public ShipmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shipment, container, false);

        cartonNumber= view.findViewById(R.id.txt_cartonNumber);
        back= view.findViewById(R.id.btn_back);

        cartonNumber.requestFocus();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        cartonNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

             if (!cartonNumber.getText().toString().equals(""))
             {
                 shipmentTransactions();
                 cartonNumber.setText("");
             }

            }
        });
        return view;
    }


    private void shipmentTransactions()
    {
        Cursor c = db.rawQuery("Select ShipNo From TblCarton where ShipNo  = '" + cartonNumber.getText().toString()  + "' ",null);
        if(c.moveToNext())
        {
            Toast.makeText(getActivity(),cartonNumber.getText().toString() + "  Shipment Number Already Exist",Toast.LENGTH_LONG).show();
            c.close();
        }
        else
        {

            c.close();
        }
    }

}
