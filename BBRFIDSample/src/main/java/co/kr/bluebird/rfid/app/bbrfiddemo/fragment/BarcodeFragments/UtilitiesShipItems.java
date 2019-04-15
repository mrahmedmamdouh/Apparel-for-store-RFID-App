package co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;
import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;

public class UtilitiesShipItems extends AppCompatActivity {

    Button back,exportShipment,clearShipment,exportAllClearShipment;
    Context mContext;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    String cartonQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilities_ship_items);

        dbhelper = new WDxDBHelper(this);
        db = dbhelper.getWritableDatabase();
        mContext= UtilitiesShipItems.this;
        Intent intent = getIntent();
        cartonQty=intent.getStringExtra("CARTONQTY");

        back=findViewById(R.id.btn_back);
        exportShipment=findViewById(R.id.btn_exportShipment);
        clearShipment=findViewById(R.id.btn_clearShipment);
        exportAllClearShipment=findViewById(R.id.btn_exportAllClear);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clearShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Clear Carton");
                mBuilder.setMessage("Important Question");

                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        db.execSQL("delete from TblCarton");
                        Toast.makeText(mContext,"TblCarton Cleared Successfully",Toast.LENGTH_LONG).show();
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

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
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
                                }
                            }
                            try{
                                FileOutputStream writer = new FileOutputStream(file);
                                PrintWriter pw = new PrintWriter(writer);
                                Cursor c = db.rawQuery("select Iserial, Counted, ShipNo from TblCarton",null);
                                if(c.getCount()>0 && c!=null) {
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
                                }
                                Toast.makeText(mContext, "File Generated Successfully", Toast.LENGTH_LONG).show();
                            }
                            catch (FileNotFoundException ex)
                            {}
                            catch (IOException ex)
                            {}
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

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
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
                                Toast.makeText(mContext, "File Generated Successfully", Toast.LENGTH_LONG).show();
                            }
                            catch (FileNotFoundException ex)
                            {}
                            catch (IOException ex)
                            {}
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

    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
