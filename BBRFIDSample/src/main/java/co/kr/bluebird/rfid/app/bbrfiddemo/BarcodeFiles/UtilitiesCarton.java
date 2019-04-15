package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
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

public class UtilitiesCarton extends AppCompatActivity {

    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    Button back,clearCarton,exportCarton,exportAllClear;
    Context mContext;
    String cartonQty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilities_carton);

        mContext= UtilitiesCarton.this;
        dbhelper = new WDxDBHelper(this);
        db = dbhelper.getWritableDatabase();
        back= findViewById(R.id.btn_back);
        exportCarton= findViewById(R.id.btn_exportCarton);
        clearCarton= findViewById(R.id.btn_clearCarton);
        exportAllClear= findViewById(R.id.btn_exportAllClear);

        Intent intent = getIntent();
        cartonQty=intent.getStringExtra("CARTONQTY");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             onBackPressed();
            }
        });
        clearCarton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Clear Cartons");
                mBuilder.setMessage("Important Question");

                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        db.execSQL("Delete from TblShip");
                        Toast.makeText(mContext,"Carton Cleared Succesfully",Toast.LENGTH_LONG).show();
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
        exportCarton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm");
                Date date = new Date();
                final String activeDate = dateFormat.format(date);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Export Carton #");
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
                         //   MediaScannerConnection.scanFile(mContext, new String[] {dir.toString()}, null, null);
                            File file = new File(dir, "CountZ " + cartonQty + "Z " + activeDate + ".txt");

                            if (!file.exists()) {
                                try{
                                    file.createNewFile();}
                                catch (IOException ex)
                                {
                                }
                            }
                            try{
                                // FileOutputStream writer = openFileOutput(file.getName(), Context.MODE_PRIVATE);
                                FileOutputStream writer = new FileOutputStream(file);
                                PrintWriter pw = new PrintWriter(writer);
                                //  writer.close();
                                Cursor c = db.rawQuery("select ShipNo from TblShip",null);
                                while(c.moveToNext())
                                {
                                    String shipNo = c.getString(c.getColumnIndex("ShipNo"));
                                    writer.write(shipNo.getBytes());
                                    writer.write('\n');
                                    writer.flush();
                                    pw.flush();
                                }
                                pw.close();
                                writer.close();
                                Toast.makeText(mContext,"File Generated Successfully",Toast.LENGTH_LONG).show();
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
        exportAllClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm");
                Date date = new Date();
                final String activeDate = dateFormat.format(date);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Export Carton # And Clear");
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
                                Cursor c = db.rawQuery("select ShipNo from TblShip",null);
                                while(c.moveToNext())
                                {
                                    String shipNo = c.getString(c.getColumnIndex("ShipNo"));
                                    writer.write(shipNo.getBytes());
                                    writer.write('\n');
                                    writer.flush();
                                    pw.flush();
                                }
                                pw.close();
                                writer.close();
                                db.execSQL("Delete from TblShip");
                                Toast.makeText(mContext,"File Generated Successfully",Toast.LENGTH_LONG).show();

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
