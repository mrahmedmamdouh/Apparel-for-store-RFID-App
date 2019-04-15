package co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class UtilitesShipItemsFragment extends Fragment {

    Button back,exportShipment,clearShipment,exportAllClearShipment;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    String cartonQty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbhelper = new WDxDBHelper(getActivity());
        db = dbhelper.getWritableDatabase();
    }
    public UtilitesShipItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_utilites_ship_items, container, false);

//        Intent intent = getActivity().getIntent();
//        cartonQty=intent.getStringExtra("CARTONQTY");

        cartonQty = getArguments().getString("CARTONQTY");

        back= view.findViewById(R.id.btn_back);
        exportShipment=view.findViewById(R.id.btn_exportShipment);
        clearShipment=view.findViewById(R.id.btn_clearShipment);
        exportAllClearShipment=view.findViewById(R.id.btn_exportAllClear);


        clearShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Clear Carton");
                mBuilder.setMessage("Important Question");

                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        db.execSQL("delete from TblCarton");
                        Toast.makeText(getActivity(),"TblCarton Cleared Successfully",Toast.LENGTH_LONG).show();
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
        });

        exportShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm");
                Date date = new Date();
                final String activeDate = dateFormat.format(date);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Export Current Carton #");
                mBuilder.setMessage("Important Question");

                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if(isExternalStorageWritable())
                        {
                            File root = android.os.Environment.getExternalStorageDirectory();
                            File dir = new File (root.getAbsolutePath() + "/DataCollectorFiles");
                            dir.mkdirs();
                            File file = new File(dir, "CountZ " + cartonQty + "Z " + activeDate + ".txt");

                            if (!file.exists()) {
                                try{
                                    file.createNewFile();}
                                catch (IOException ex)
                                {
                                    Toast.makeText(getActivity(),"Please Check the Application has External Storage Write Permission",Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            try{
//                                FileOutputStream writer = new FileOutputStream(file);
//                                PrintWriter pw = new PrintWriter(writer);

                                FileWriter writer = new FileWriter(file.getAbsoluteFile(),true);
                                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                                Cursor c = db.rawQuery("select Iserial, Counted, ShipNo from TblCarton",null);
                                if(c.getCount()>0 && c!=null) {
                                    while (c.moveToNext()) {
                                        String _iserial = c.getString(c.getColumnIndex("Iserial"));
                                        Integer counted = c.getInt(c.getColumnIndex("Counted"));
                                        String shipNo = c.getString(c.getColumnIndex("ShipNo"));
                                        bufferedWriter.write(_iserial + "\r\n");
//                                        writer.write('\n');
                                        bufferedWriter.write(counted.toString() + "\r\n");
//                                        writer.write('\n');
                                        bufferedWriter.write(shipNo + "\r\n");
//                                        writer.write('\n');
//                                        writer.write('\n');
                                        writer.flush();
                                        bufferedWriter.flush();
                                    }
                                    bufferedWriter.close();
                                    writer.close();
                                }
                                Toast.makeText(getActivity(), "File Generated Successfully", Toast.LENGTH_LONG).show();
                            }
                            catch (FileNotFoundException ex)
                            {
                                Toast.makeText(getActivity(),"Please Check the Application has External Storage Write Permission",Toast.LENGTH_LONG).show();
                                return;
                            }
                            catch (IOException ex)
                            {
                                Toast.makeText(getActivity(),"Please Check the Application has External Storage Write Permission",Toast.LENGTH_LONG).show();
                                return;
                            }
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

        });
        exportAllClearShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm");
                Date date = new Date();
                final String activeDate = dateFormat.format(date);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Export Current Carton #");
                mBuilder.setMessage("Important Question");

                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if(isExternalStorageWritable())
                        {
                            File root = android.os.Environment.getExternalStorageDirectory();
                            File dir = new File (root.getAbsolutePath() + "/DataCollectorFiles");
                            dir.mkdirs();
                            File file = new File(dir, "CountZ " + cartonQty + "Z " + activeDate + ".txt");

                            if (!file.exists()) {
                                try{
                                    file.createNewFile();}
                                catch (IOException ex)
                                {
                                    Toast.makeText(getActivity(),"Please Check the Application has External Storage Write Permission",Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            try {
                                FileOutputStream writer = new FileOutputStream(file);
                                PrintWriter pw = new PrintWriter(writer);
                                Cursor c = db.rawQuery("select iserial, Counted, ShipNo from TblCarton", null);
                                if (c.getCount() > 0 && c != null) {
                                    while (c.moveToNext()) {
                                        String _iserial = c.getString(c.getColumnIndex("Iserial"));
                                        Integer counted = c.getInt(c.getColumnIndex("Counted"));
                                        String shipNo = c.getString(c.getColumnIndex("ShipNo"));
                                        writer.write(_iserial.getBytes());
                                        writer.write('\n');
                                        writer.write(counted.toString().getBytes());
                                        writer.write('\n');
                                        writer.write(shipNo.getBytes());
                                        writer.write('\n');
                                        writer.write('\n');
                                        writer.flush();
                                        pw.flush();
                                    }
                                    pw.close();
                                    writer.close();
                                    db.execSQL("Delete from TblCarton");
                                }
                                Toast.makeText(getActivity(), "File Generated Successfully", Toast.LENGTH_LONG).show();
                            }
                            catch (FileNotFoundException ex)
                            {
                                Toast.makeText(getActivity(),"Please Check the Application has External Storage Write Permission",Toast.LENGTH_LONG).show();
                                return;
                            }
                            catch (IOException ex)
                            {
                                Toast.makeText(getActivity(),"Please Check the Application has External Storage Write Permission",Toast.LENGTH_LONG).show();
                                return;
                            }
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
        });

        return view;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
