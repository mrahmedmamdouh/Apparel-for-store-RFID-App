package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBAdapter {
    Context c;
    SQLiteDatabase db;
    WDxDBHelper helper;

    /*
    1. INITIALIZE DB HELPER AND PASS IT A CONTEXT

     */
    public DBAdapter(Context c) {
        this.c = c;
        helper = new WDxDBHelper(c);
    }

    /*
    SAVE DATA TO DB
     */
    public boolean saveReplElements(repl_elements elements) {
        try {
            db = helper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(Constants.it_id, elements.getIt_id());
            cv.put(Constants.style, elements.getStyle());
            cv.put(Constants.color, elements.getColor());
            cv.put(Constants.size, elements.getSize());


            long result = db.insert(Constants.TB_NAME, Constants.ROW_ID, cv);
            if (result > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }

        return false;
    }

    /*
     1. RETRIEVE SPACECRAFTS FROM DB AND POPULATE ARRAYLIST
     2. RETURN THE LIST
     */

    public ArrayList<repl_elements> retrieveSpacecrafts()
    {
        ArrayList<repl_elements> elements=new ArrayList<>();

        String[] columns={Constants.ROW_ID,Constants.it_id,Constants.style,Constants.color, Constants.size};

        try
        {
            db = helper.getWritableDatabase();
            Cursor c=db.query(Constants.TB_NAME,columns,null,null,null,null,null);

            repl_elements s;

            if(c != null)
            {
                while (c.moveToNext())
                {
                    String s_id=c.getString(1);
                    String s_style=c.getString(2);
                    String s_color=c.getString(3);
                    String s_size=c.getString(4);

                    s=new repl_elements();
                    s.setIt_id(s_id);
                    s.setStyle(s_style);
                    s.setColor(s_color);
                    s.setSize(s_size);

                    elements.add(s);
                }
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return elements;
    }

}
