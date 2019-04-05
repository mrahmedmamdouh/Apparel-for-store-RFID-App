package co.kr.bluebird.newrfid.app.bbrfidbtdemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Calendar;

/**
 * Created by AHMED on 10/15/2017.
 */

public class BuisnessLogic {

    public static BuisnessLogic BL;
    private BuisnessLogic(){}

    public static synchronized BuisnessLogic getInstance(){
        if(BL==null)
        {
            BL = new BuisnessLogic();
        }
        return BL;
    }

    public Cursor getUser(String username , String password,SQLiteDatabase db) {
        Cursor c = null;
        try {
            // Cursor c = db.rawQuery("  select EmpName,EmpID from Employees as E where  E.EmpName = '" + _username + "' and E.EmpPass = '" + _password + "'   ", null);
            String[] columns=new String[]{" UserID,UserName,Pass,SecurityLevel,RoleID"};
             c = db.query("Users", columns, "UserName=? and Pass =? ", new String[]{username,password}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return c;
    }
    public Cursor getNotScannedAssets(SQLiteDatabase db) {
        Cursor c = null;
        try {
            String[] columns=new String[]{" AssetDesc,AssetCode"};
            c = db.query("Assets", columns, "IsScanned=?", new String[]{"0"}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}
        return c;
    }

    public String GetUser_Screen(String roleID ,SQLiteDatabase db) {
        Cursor c = null;
        String screenNm = null;
        try {
            String[] columns=new String[]{"ScreenNm"};
            c = db.query("Screens", columns, "UserID=? ", new String[]{roleID}, null,null,null);

            if(c!=null && c.getCount() > 0) {
                if(c.moveToNext())
                {
                    int screen_index = c.getColumnIndex("ScreenNM");
                    screenNm = c.getString(screen_index);
                }
            }
        } catch (Exception e) {String ex = e.getMessage();}
      //  c.close();
        return screenNm;
    }

    public Cursor getBuilding_barcode(String barcode,String plantCode,SQLiteDatabase db) {
        Cursor c = null;
        try {
            String[] columns=new String[]{"BuildingID,BuildingCode,BuildingName"};
            c = db.query("Building", columns, "Barcode=? and PlantCode =?  ", new String[]{barcode,plantCode}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return c;
    }
    public Cursor getFloor_barcode(String barcode,String buildingCode,String plantCode,SQLiteDatabase db) {
        Cursor c = null;
        try {
            String[] columns=new String[]{"FloorID,FloorCode,FloorDesc"};
            c = db.query("Floor", columns, "Barcode=? and BuildingCode =? and PlantCode =?  ", new String[]{barcode,buildingCode,plantCode}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return c;
    }
    public Cursor getRoom_barcode(String barcode,String floorCode,String buildingCode,String plantCode,SQLiteDatabase db) {
        Cursor c = null;
        try {
            String[] columns=new String[]{"RoomID,RoomCode,RoomName"};
            c = db.query("Room", columns, "Barcode=? and FloorCode=? and BuldingCode =? and PlantCode =?  ", new String[]{barcode,floorCode,buildingCode,plantCode}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return c;
    }
    public int selectAudit_Assets(String roomID,String floorID,String buildingID,SQLiteDatabase db) {
        int count = 0;
        try {

             Cursor c = db.rawQuery("Select count(*) as Count from Assets where RoomCode= '"+ roomID + "' and FloorCode ='" + floorID + "' and BuildingCode='" + buildingID + "' ",null);
            if(c!=null && c.getCount() > 0) {
                if(c.moveToNext())
                {
                    int index = c.getColumnIndex("Count");
                    count = c.getInt(index);
                }
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return count;
    }


    public Bitmap getassetImage(String assetCode, SQLiteDatabase db) {
        Bitmap image = null;
        try {
            String[] columns=new String[]{"Image"};
           Cursor c = db.query("Images", columns, "AssetCode=? ", new String[]{assetCode}, null,null,null);

            if(c!=null && c.getCount() > 0) {
                if(c.moveToNext())
                {
                    int index = c.getColumnIndex("Image");
                    byte[] data = c.getBlob(c.getColumnIndex("Image"));
                     image = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return image ;
    }

    public Cursor getAssetDetails(String assetCode,String roomCode,SQLiteDatabase db) {
        Cursor c = null;
        try {
            c = db.rawQuery("select * from Assets where AssetCode = '"+ assetCode +"' and RoomCode = '"+ roomCode +"' ",null);
            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return c;
    }
    public void UpdateStatus(String statusIDUpdate, String statusUpdate, String assetCode, SQLiteDatabase db) {
       try {
           ContentValues cv = new ContentValues();
           cv.put("Updated", 1);
           cv.put("StatusDesc", statusUpdate);
           db.update("Assets", cv, "AssetCode=" + assetCode, null);
       }
       catch (Exception ex){String exMessage = ex.getStackTrace().toString();}
     try {
            ContentValues cv1 = new ContentValues();
            cv1.put("Updated", 1);
            cv1.put("Status", statusIDUpdate);
            db.update("AssetDetails", cv1, "AssetCode=" + assetCode, null);
       }

     catch (Exception ex){String exMessage = ex.getStackTrace().toString();}

    }
    public int SelectScannedCount(SQLiteDatabase db,String PlantCode) {
        Globals g=Globals.getInstance();
        int Count =0;
        try {

            Cursor c = db.rawQuery("Select Count(*) as Counter  from assets where IsScanned=1 and PlantCode='" + PlantCode + "' and BuildingCode='" + g.getBuildingID() + "' and FloorCode = '" + g.getFloorID() + "' and RoomCode= '" + g.getRoomID() + "'",null);
            if(c!=null && c.getCount() > 0) {
                if(c.moveToNext())
                {
                    Count =c.getInt( c.getColumnIndex("Counter"));
                    return Count;
                }
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return Count;
    }

    public Cursor getAsset( String AssetBarcode, SQLiteDatabase db) {
        Cursor c = null;
        try {
            //"Create Table Assets ( AssetID integer Primary Key  ,AssetCode  text,SerialNo  text,Class  text,CompanyCode  text,AssetDesc  text,PlantCode  text,Location  text,BuildingCode  text,FloorCode  text,RoomCode  text,StatusDesc  text,IsScanned  Integer Default 0 ,IsAuditClosed  integer Default 0 ,Updated  integer Default 0 ,NBV integer);";
            Globals g=Globals.getInstance();
            String[] columns=new String[]{"AssetID,AssetDesc,Class,StatusDesc,NBV"};
            c = db.query("Assets", columns, "AssetCode=? and BuildingCode=?", new String[]{AssetBarcode,g.getBuildingID()}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}

        return c;
    }
    public void InsertMisplaced(String assetCode, String assetDesc, int statusID, String classCode, String aClass, String nbv,String PlantCode, String Plant,String CompanyCode , String Company,SQLiteDatabase db) {
        //"Create Table AssetDetails ( AssetID  integer Primary Key,AssetCode text not null,AssetDesc text , CompanyCode text,PlantCode text,BuildingCode text,FloorCode text,RoomCode text , Status text , Movement text ,ADate text , Anumber text , Class text , BuildingID integer ,FloorID text , RoomID integer , UserID integer , CompanyName text , PlantName text , BuildingName text , FloorName text , RoomName text , ClassName text , NBV REAL , Updated INTEGER DEFAULT 0 );";
        //AssetDetails(ClassName,NBV)
        Globals g=Globals.getInstance();
        ContentValues cv = new ContentValues();
        cv.put("AssetCode", assetCode);
        cv.put("AssetDesc", assetDesc);
        cv.put("Status", statusID);
        cv.put("Class", classCode);
        cv.put("ClassName", aClass);
        cv.put("NBV", nbv);
        cv.put("CompanyCode",CompanyCode );
        cv.put("PlantCode",PlantCode);
        cv.put("BuildingCode",g.getBuildingID());
        cv.put("FloorCode",g.getFloorID());
        cv.put("RoomCode",g.getRoomID());
        cv.put("Movement","Missplaced");
        cv.put("ADate", Calendar.getInstance().toString());
        cv.put("ANumber",g.getAuditNumber());
        cv.put("BuildingID",g.getBuildingAutoID());
        cv.put("FloorID",g.getFloorAutoID());
        cv.put("RoomID",g.getRoomAutoID());
        cv.put("UserID",g.getUserID());
        cv.put("CompanyName",Company);
        cv.put("PlantName",Plant);
        cv.put("BuildingName",g.getBuildingName());
        cv.put("FloorName",g.getFloorName());
        cv.put("RoomName",g.getRoomName());
        db.insert("AssetDetails", null, cv);
    }
    public Cursor getAsset1( String AssetBarcode, SQLiteDatabase db) {
        Cursor c = null;
        try {
            //"Create Table Assets ( AssetID integer Primary Key  ,AssetCode  text,SerialNo  text,Class  text,CompanyCode  text,AssetDesc  text,PlantCode  text,Location  text,BuildingCode  text,FloorCode  text,RoomCode  text,StatusDesc  text,IsScanned  Integer Default 0 ,IsAuditClosed  integer Default 0 ,Updated  integer Default 0 ,NBV integer);";
            Globals g=Globals.getInstance();
            String[] columns=new String[]{"AssetID,AssetDesc,Class,StatusDesc,NBV"};
            c = db.query("Assets", columns, "AssetCode=?", new String[]{AssetBarcode}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}

        return c;
    }


    public int getStatusID(String Statusdesc, SQLiteDatabase db) {
        int ID = 0;
        try {

            Cursor c = db.rawQuery("Select *  from Status where StatusDesc= '"+ Statusdesc + "'",null);
            if(c!=null && c.getCount() > 0) {
                if(c.moveToNext())
                {
                    ID =c.getInt( c.getColumnIndex("StatusID"));
                    return ID;
                }
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return ID;
    }

    public String GetClassName(String classcode, SQLiteDatabase db) {
        String ClassName = "";
        try {

            Cursor c = db.rawQuery("Select *  from Class where ClassCode= '"+ classcode + "'",null);
            if(c!=null && c.getCount() > 0) {
                if(c.moveToNext())
                {
                    ClassName =c.getString( c.getColumnIndex("ClassDesc"));

                }
            }
        } catch (Exception e) {String ex = e.getMessage();}
        //c.close();
        return ClassName;
    }
    public Cursor getAsset(String roomBarCode, String AssetBarcode, SQLiteDatabase db) {
        Cursor c = null;
        try {
            //"Create Table Assets ( AssetID integer Primary Key  ,AssetCode  text,SerialNo  text,Class  text,CompanyCode  text,AssetDesc  text,PlantCode  text,Location  text,BuildingCode  text,FloorCode  text,RoomCode  text,StatusDesc  text,IsScanned  Integer Default 0 ,IsAuditClosed  integer Default 0 ,Updated  integer Default 0 ,NBV integer);";
            String[] columns=new String[]{"AssetID,AssetDesc,Class,StatusDesc,NBV"};
            c = db.query("Assets", columns, "AssetCode=? and RoomCode=?", new String[]{AssetBarcode,roomBarCode}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}

        return c;
    }
    public Cursor CheckAssetScaned(String AssetBarcode, SQLiteDatabase db) {
        Cursor c = null;
        try {
            //"Create Table Assets ( AssetID integer Primary Key  ,AssetCode  text,SerialNo  text,Class  text,CompanyCode  text,AssetDesc  text,PlantCode  text,Location  text,BuildingCode  text,FloorCode  text,RoomCode  text,StatusDesc  text,IsScanned  Integer Default 0 ,IsAuditClosed  integer Default 0 ,Updated  integer Default 0 ,NBV integer);";
            String[] columns=new String[]{"AssetID"};
            c = db.query("AssetDetails", columns, "AssetCode=?", new String[]{AssetBarcode}, null,null,null);

            if(c!=null && c.getCount() > 0) {
            }
        } catch (Exception e) {String ex = e.getMessage();}

        return c;
    }
    public void InsertasstDetails(String assetCode, String assetDesc, int statusID, String classCode, String aClass, String nbv,String PlantCode, String Plant,String CompanyCode , String Company,String auditNumber,String roomID,SQLiteDatabase db) {
        //"Create Table AssetDetails ( AssetID  integer Primary Key,AssetCode text not null,AssetDesc text , CompanyCode text,PlantCode text,BuildingCode text,FloorCode text,RoomCode text , Status text , Movement text ,ADate text , Anumber text , Class text , BuildingID integer ,FloorID text , RoomID integer , UserID integer , CompanyName text , PlantName text , BuildingName text , FloorName text , RoomName text , ClassName text , NBV REAL , Updated INTEGER DEFAULT 0 );";
        //AssetDetails(ClassName,NBV)
        Globals g=Globals.getInstance();
        ContentValues cv = new ContentValues();
        cv.put("AssetCode", assetCode);
        cv.put("AssetDesc", assetDesc);
        cv.put("Status", statusID);
        cv.put("Class", classCode);
        cv.put("ClassName", aClass);
        cv.put("NBV", nbv);
        cv.put("CompanyCode",CompanyCode );
        cv.put("PlantCode",PlantCode);
        cv.put("BuildingCode",g.getBuildingID());
        cv.put("FloorCode",g.getFloorID());
        cv.put("RoomCode",g.getRoomID());
        cv.put("Movement","Current");
        cv.put("ADate", Calendar.getInstance().toString());
        cv.put("ANumber",auditNumber);
        cv.put("BuildingID",g.getBuildingAutoID());
        cv.put("FloorID",g.getFloorAutoID());
        cv.put("RoomID",roomID);
        cv.put("UserID",g.getUserID());
        cv.put("CompanyName",Company);
        cv.put("PlantName",Plant);
        cv.put("BuildingName",g.getBuildingName());
        cv.put("FloorName",g.getFloorName());
        cv.put("RoomName",g.getRoomName());
        db.insert("AssetDetails", null, cv);
    }

    public void InsertEPC(String epc,SQLiteDatabase db) {
        //"Create Table AssetDetails ( AssetID  integer Primary Key,AssetCode text not null,AssetDesc text , CompanyCode text,PlantCode text,BuildingCode text,FloorCode text,RoomCode text , Status text , Movement text ,ADate text , Anumber text , Class text , BuildingID integer ,FloorID text , RoomID integer , UserID integer , CompanyName text , PlantName text , BuildingName text , FloorName text , RoomName text , ClassName text , NBV REAL , Updated INTEGER DEFAULT 0 );";
        //AssetDetails(ClassName,NBV)
        Globals g=Globals.getInstance();
        ContentValues cv = new ContentValues();
        cv.put("epc", epc);
        db.insert("TagDetails", null, cv);
    }
    public void InsertTID(String tid,SQLiteDatabase db) {
        //"Create Table AssetDetails ( AssetID  integer Primary Key,AssetCode text not null,AssetDesc text , CompanyCode text,PlantCode text,BuildingCode text,FloorCode text,RoomCode text , Status text , Movement text ,ADate text , Anumber text , Class text , BuildingID integer ,FloorID text , RoomID integer , UserID integer , CompanyName text , PlantName text , BuildingName text , FloorName text , RoomName text , ClassName text , NBV REAL , Updated INTEGER DEFAULT 0 );";
        //AssetDetails(ClassName,NBV)
        Globals g=Globals.getInstance();
        ContentValues cv = new ContentValues();
        cv.put("tid", tid);
        db.insert("TagDetails", null, cv);
    }
    public void UpdateAssetScanned(String assetCode,SQLiteDatabase db) {


        ContentValues cv = new ContentValues();
        cv.put("IsScanned", "1");
        db.update("Assets",cv, "AssetCode=" + assetCode , null);
    }


    public void InsertMobileassets(String Barcode , String AsstName ,String Status ,String AssetCategoryID , String note , String className,SQLiteDatabase db, byte[] image,
                                   String companyCode,String companyName,String plantCode,String plantName,String auditNumber) {

        Globals g=Globals.getInstance();
        ContentValues cv = new ContentValues();
        cv.put("AssetCode", Barcode);
        cv.put("AssetDesc", AsstName);
        cv.put("CompanyCode", companyCode);
        cv.put("PlantCode",plantCode );
        cv.put("BuildingCode", g.getBuildingID());
        cv.put("FloorCode", g.getFloorID());
        cv.put("RoomCode",g.getRoomID());
        cv.put("Status",Status);
        cv.put("Class",AssetCategoryID);
        cv.put("Note",note);
        cv.put("ANumber",auditNumber);
        cv.put("CompanyName",companyName);
        cv.put("PlantName", plantName);
        cv.put("BuildingName",g.getBuildingName());
        cv.put("FloorName",g.getFloorName());
        cv.put("RoomName",g.getRoomName());
        cv.put("ClassName",className);
        cv.put("NewImage",image);

        db.insert("MobileAssets", null, cv);
    }

    public void InsertMobileassets(String Barcode , String AsstName ,String Status ,String AssetCategoryID , String note , String className,SQLiteDatabase db,
                                   String companyCode,String companyName,String plantCode,String plantName,String auditNumber) {

        Globals g=Globals.getInstance();
        ContentValues cv = new ContentValues();
        cv.put("AssetCode", Barcode);
        cv.put("AssetDesc", AsstName);
        cv.put("CompanyCode", companyCode);
        cv.put("PlantCode",plantCode );
        cv.put("BuildingCode", g.getBuildingID());
        cv.put("FloorCode", g.getFloorID());
        cv.put("RoomCode",g.getRoomID());
        cv.put("Status",Status);
        cv.put("Class",AssetCategoryID);
        cv.put("Note",note);
        cv.put("ANumber",auditNumber);
        cv.put("CompanyName",companyName);
        cv.put("PlantName", plantName);
        cv.put("BuildingName",g.getBuildingName());
        cv.put("FloorName",g.getFloorName());
        cv.put("RoomName",g.getRoomName());
        cv.put("ClassName",className);
        db.insert("MobileAssets", null, cv);
    }

    public boolean GetImage (String assetCode,SQLiteDatabase db)
    {
        Cursor c = db.rawQuery("Select image From Images where AssetCode='" + assetCode + "' ",null);
        if(c.getCount()>0 && c!=null)
        {
            return true;
        }
        return false;
    }
    public void UpdateImage(String assetCode,byte [] image,SQLiteDatabase db)
    {
        db.execSQL("Update Images Set image= '"+ image +"'  where AssetCode='"+assetCode+"' ");
    }

    public void InsertImage(String assetCode,byte [] image,SQLiteDatabase db)
    {
        db.execSQL("Insert into Images (image,AssetCode) values ('"+ image +"' , '"+ assetCode +"') ");
    }

}
