package co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartonFragment extends android.app.Fragment {

    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    Button back,utilities;
    Context mContext;
    TextView cartonCount;
    EditText ship;
    CheckBox delete;
    public CartonFragment() {
        // Required empty public constructor
    }

    public static CartonFragment newInstance() {
        return new CartonFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbhelper = new WDxDBHelper(getActivity());
        db = dbhelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carton, container, false);

        back = view.findViewById(R.id.btn_back);
        utilities = view.findViewById(R.id.btn_utilities);
        cartonCount = view.findViewById(R.id.lblCartonCount);
        ship = view.findViewById(R.id.txt_Fship);
        delete = view.findViewById(R.id.ch_delete);

        ship.requestFocus();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(),MainForm.class));
               getActivity().onBackPressed();
            }
        });
        utilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent= new Intent(getActivity(),UtilitiesCarton.class);
//                intent.putExtra("CARTONQTY",cartonCount.getText().toString());
//                startActivity(intent);

                UtilitiesCartonFragment fragment = new UtilitiesCartonFragment ();
                Bundle args = new Bundle();
                args.putString("CARTONQTY", cartonCount.getText().toString());
                fragment.setArguments(args);
                android.support.v4.app.FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();

            }
        });

        ship.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(!ship.getText().toString().equals(""))
                {cartonTransactions();}
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
//                if(!ship.getText().toString().equals(""))
//                {
//                 ship.setText("");
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });


        Cursor c = db.rawQuery("Select Count(ShipNo) as ShipCount from TblShip",null);
        if(c!=null && c.getCount()>0)
        {
            if(c.moveToNext()) {
                cartonCount.setText(c.getString(c.getColumnIndex("ShipCount")));
            }
        }

        return view;
    }
    Cursor c;
    private void cartonTransactions()
    {
        c = db.rawQuery("Select ShipNo from TblShip where ShipNo = '" + ship.getText().toString() + "' ",null);

        if (!delete.isChecked())
        {
            if(c.moveToNext())
            {
                Toast.makeText(getActivity(),ship.getText().toString() + "  Carton Number Already Exists" ,Toast.LENGTH_LONG).show();
                ship.setText("");
            }
            else
            {
                db.execSQL("Insert into TblShip (ShipNo) values ('" + ship.getText().toString() +"')");
                // ship.setText("");
                Integer newCount = Integer.parseInt(cartonCount.getText().toString())+1;
                cartonCount.setText(newCount.toString());
                ship.setText("");
            }
        }
        else
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            mBuilder.setTitle("Delete Item");
            mBuilder.setMessage("Important Question");

            mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(c.getCount()<1 || c==null )
                    {
                        Toast.makeText(getActivity(),ship.getText().toString() + "  Carton Number Doesn't Exist",Toast.LENGTH_SHORT).show();
                        ship.setText("");
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

}
