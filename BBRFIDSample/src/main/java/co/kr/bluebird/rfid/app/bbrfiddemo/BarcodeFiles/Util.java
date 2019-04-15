package co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.kr.bluebird.rfid.app.bbrfiddemo.WDxDBHelper;
import co.kr.bluebird.rfid.app.bbrfiddemo.R;

public class Util extends AppCompatActivity {

    Button back,clearZone,exportZone,exportAllClear,importItemList;
    WDxDBHelper dbhelper;
    SQLiteDatabase db;
    Context mContext;
    ImportTask task1;
    Integer flg = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util);

        mContext = Util.this;
        dbhelper = new WDxDBHelper(this);
        db = dbhelper.getWritableDatabase();

        back = findViewById(R.id.btn_back);
        clearZone = findViewById(R.id.btn_clearZone);
        exportZone = findViewById(R.id.btn_exportZone);
        exportAllClear = findViewById(R.id.btn_exportAllClear);
        importItemList = findViewById(R.id.btn_importItemList);

        Intent intent = getIntent();
        final Integer zoneNo = intent.getIntExtra("ZONENO",0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        clearZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Clear Zone");
                mBuilder.setMessage("Important Question");

                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        db.execSQL("Update tblstock set counted =0 where Zone= " + zoneNo + " ");
                        Toast.makeText(mContext,"Zone Cleared Successfully",Toast.LENGTH_LONG).show();
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

        exportZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm");
                Date date = new Date();
                final String activeDate = dateFormat.format(date);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Export Zone # " + zoneNo);
                mBuilder.setMessage("Important Question");
                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        flg=0;
                        if(isExternalStorageWritable())
                        {
                            File root = android.os.Environment.getExternalStorageDirectory();
                            File dir = new File (root.getAbsolutePath() + "/DataCollectorFiles");
                            dir.mkdirs();
                            File file = new File(dir, "CountZ " + zoneNo + "Z " + activeDate + ".txt");

                            if (!file.exists()) {
                                try{
                                    file.createNewFile();}
                                catch (IOException ex)
                                {
                                }
                            }
                            else
                            {
                                flg=1;
                               Toast.makeText(mContext,"This File  already exists.",Toast.LENGTH_SHORT).show();
                               return;
                            }
                            try{
                                FileOutputStream writer = new FileOutputStream(file);
                                PrintWriter pw = new PrintWriter(writer);
                                Cursor c = db.rawQuery("SELECT  tblstock.iserial, tblstock.Counted, tblstock.Zone, tblstock.StoreCode,TblSetting.DevId FROM  TblSetting CROSS JOIN tblstock  where  counted <>0  and zone=" + zoneNo +" ",null);
                                if(c.getCount()>0 && c!=null) {
                                    while (c.moveToNext()) {
                                        String _iserial = c.getString(c.getColumnIndex("iserial"));
                                        Integer _counted = c.getInt(c.getColumnIndex("Counted"));
                                        String _zone = c.getString(c.getColumnIndex("Zone"));
                                        String _storeCode = c.getString(c.getColumnIndex("StoreCode"));
                                        writer.write(_iserial.getBytes());
                                        writer.write(',');
                                        writer.write(_counted.toString().getBytes());
                                        writer.write(',');
                                        writer.write(_zone.getBytes());
                                        writer.write(',');
                                        writer.write(_storeCode.getBytes());
                                        writer.write('\n');
                                        writer.flush();
                                        pw.flush();
                                    }
                                    pw.close();
                                    writer.close();
                                }
                            }
                            catch (FileNotFoundException ex)
                            {}
                            catch (IOException ex)
                            {}

                            if(flg==0)
                            {
                                db.execSQL("delete   from  tblstock");
                                Toast.makeText(mContext,"File Generated Successfully ",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(mContext,"File exists",Toast.LENGTH_SHORT).show();
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
        exportAllClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm");
                Date date = new Date();
                final String activeDate = dateFormat.format(date);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Export data to file and clear count");
                mBuilder.setMessage("Important Question");
                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        flg=0;
                        if(isExternalStorageWritable())
                        {
                            File root = android.os.Environment.getExternalStorageDirectory();
                            File dir = new File (root.getAbsolutePath() + "/DataCollectorFiles");
                            dir.mkdirs();
                            File file = new File(dir, "Count " + activeDate + ".txt");
                            File file2 = new File(dir, "CountZ " + activeDate + ".txt");

                            if ((!file.exists()) || (!file2.exists()) ) {
                                try{
                                    file.createNewFile();
                                    file2.createNewFile();
                                }
                                catch (IOException ex)
                                {
                                }
                            }
                            else
                            {
                                flg=1;
                                Toast.makeText(mContext,"This File  already exists.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            try{
                                FileOutputStream writer = new FileOutputStream(file);
                                PrintWriter pw = new PrintWriter(writer);

                                Cursor c = db.rawQuery("select iserial,Counted    FROM tblstock where counted <> 0 ",null);
                                if(c.getCount()>0 && c!=null) {
                                    while (c.moveToNext()) {
                                        String _iserial = c.getString(c.getColumnIndex("iserial"));
                                        Integer _counted = c.getInt(c.getColumnIndex("Counted"));
                                        writer.write(_iserial.getBytes());
                                        writer.write(',');
                                        writer.write(_counted.toString().getBytes());
                                        writer.write('\n');
                                        writer.flush();
                                        pw.flush();
                                    }
                                    pw.close();
                                    writer.close();
                                }
                            }
                            catch (FileNotFoundException ex)
                            {}
                            catch (IOException ex)
                            {}

                            // file 2
                            try{
                                FileOutputStream writer = new FileOutputStream(file2);
                                PrintWriter pw = new PrintWriter(writer);

                                Cursor c = db.rawQuery("SELECT iserial,Counted,zone,StoreCode FROM tblstock where counted <> 0 ",null);
                                if(c.getCount()>0 && c!=null) {
                                    while (c.moveToNext()) {
                                        String _iserial = c.getString(c.getColumnIndex("iserial"));
                                        Integer _counted = c.getInt(c.getColumnIndex("Counted"));
                                        String _zone = c.getString(c.getColumnIndex("Zone"));
                                        String _storeCode = c.getString(c.getColumnIndex("StoreCode"));
                                        writer.write(_iserial.getBytes());
                                        writer.write(',');
                                        writer.write(_counted.toString().getBytes());
                                        writer.write(',');
                                        writer.write(_zone.getBytes());
                                        writer.write(',');
                                        writer.write(_storeCode.getBytes());
                                        writer.write('\n');
                                        writer.flush();
                                        pw.flush();
                                    }
                                    pw.close();
                                    writer.close();
                                }
                            }
                            catch (FileNotFoundException ex)
                            {}
                            catch (IOException ex)
                            {}

                            if(flg==0)
                            {
                                db.execSQL("delete   from  tblstock where zone = "+ zoneNo +" ");
                                Toast.makeText(mContext,"File Generated Successfully ",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(mContext,"File exists",Toast.LENGTH_SHORT).show();
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

        importItemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                fileDialog = new FileDialog(Util.this, mPath);
                fileDialog.setFileEndsWith(".txt");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        task1=new ImportTask();
                        task1.execute(file.toString());
                    }
                });
                fileDialog.showDialog();
                */
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

    public class ImportTask extends AsyncTask<String, Void, String>
    {
        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            try
            {
                pdialog=new ProgressDialog(mContext);
                pdialog.setTitle("Importing Items");
                pdialog.setMessage("Please Wait...");
                pdialog.setCancelable(false);
                pdialog.setCanceledOnTouchOutside(false);
                pdialog.show();
            }
            catch (Exception ex)
            {
                Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (result !=null)
            {
                Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(mContext, "Import Completed", Toast.LENGTH_LONG).show();
            }
            pdialog.dismiss();
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            //publishProgress();
            String result=null;
            try {
                result=ImportItems(params[0]);
            } catch (Exception ex) {
                result= ex.getMessage();
            }
            return result;
        }

    }
    public String ImportItems(String fpath)
    {
        try
        {
            FileReader file = new FileReader(fpath);
            BufferedReader buffer = new BufferedReader(file);
            String line = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            db.beginTransaction();
            db.delete("tblstock", null, null);
            try {
                while ((line = buffer.readLine()) != null) {
                    String[] colums = line.split("\t");
                    if (colums.length == 5) {
                        ContentValues cv=new ContentValues();
                        cv.put("ArName", colums[0].trim());
                        cv.put("EnName", colums[1].trim());
                        cv.put("Barcode", colums[2].trim());
                        cv.put("Price", Float.parseFloat(colums[3].trim()));
                        if (colums[4].trim().equals(""))
                            cv.put("ExpDate", 0);
                        else
                            cv.put("ExpDate", dateFormat.parse(colums[4].trim()).getTime());
                        db.insert("Items", null, cv);
                    }
                    else if (colums.length==4)
                    {
                        ContentValues cv=new ContentValues();
                        cv.put("ArName", colums[0].trim());
                        cv.put("EnName", colums[1].trim());
                        cv.put("Barcode",  colums[2].trim());
                        cv.put("Price", Float.parseFloat(colums[3].trim()));
                        cv.put("ExpDate", 0);
                        db.insert("Items", null, cv);
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
                return  e.getMessage();
            }
            db.setTransactionSuccessful();
            db.endTransaction();

            return null;
        }
        catch (Exception e)
        {
            return e.getMessage();

        }

    }
}
