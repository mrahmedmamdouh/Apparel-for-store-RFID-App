package co.kr.bluebird.newrfid.app.bbrfidbtdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ismail on 09/10/2017.
 */
public class WDxDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="shorbagy.db";
    public static final int DATABASE_VERSION= 4 ;

    public static final String CREATE_TBL_TagDetails = "Create Table tags ( tagID  integer Primary Key,epc text not null,tid text not null UNIQUE);";
    //public static final String CREATE_TBL_Assets = "Create Table Assets ( AssetID integer Primary Key  ,AssetCode  text,SerialNo  text,Class  text,CompanyCode  text,AssetDesc  text,PlantCode  text,Location  text,BuildingCode  text,FloorCode  text,RoomCode  text,StatusDesc  text,IsScanned  Integer Default 0 ,IsAuditClosed  integer Default 0 ,Updated  integer Default 0 ,NBV integer);";
   // public static final String CREATE_TBL_Building = "Create Table Building  ( BuildingID  integer Primary Key ,BuildingCode text,BuildingName text,PlantCode text,Barcode text);";
   // public static final String CREATE_TBL_Class= "Create Table Class ( ClassID integer Primary Key,ClassCode text,ClassDesc text);";
   // public static final String CREATE_TBL_Company = "Create Table Company ( ComapnyID integer Primary Key ,CompanyCode text ,ComapnyDesc text,CountryCode text);";
   // public static final String CREATE_TBL_Country ="Create Table Country ( CountryID integer Primary Key ,CountryCode text ,CountryDesc text);";
   // public static final String CREATE_TBL_Floor ="Create Table Floor ( FloorID integer Primary Key ,FloorCode text,FloorDesc text , BuildingCode text , PlantCode text , Barcode text );";
   // public static final String CREATE_TBL_Images ="Create Table Images ( AssetCode text , Image BLOB );";
   // public static final String CREATE_TBL_ITAssets ="Create Table ITAssets ( ID integer Primary Key ,AssetCode text,SerialNo text , AssetDesc text, CompanyCode text, PlantCode text);";
   // public static final String CREATE_TBL_MobileAssets ="Create Table MobileAssets ( AssetCode text ,AssetDesc text , CompanyCode text , PlantCode text, BuildingCode text, FloorCode text, RoomCode text, Status text, Class text, Note text, ANumber text, NewImage BLOB, CompanyName text, PlantName text, BuildingName text, FloorName text, RoomName text, ClassName text);";
   // public static final String CREATE_TBL_Plants ="Create Table Plants ( PlantID integer Primary Key  ,CountryCode text ,ComapnyCode text ,PlantCode text ,PlantName text );";
    //public static final String CREATE_TBL_Room ="Create Table Room (RoomID integer Primary Key  ,PlantCode text ,BuldingCode text ,FloorCode text,RoomCode text,RoomName text,Barcode text);";
    //public static final String CREATE_TBL_Screens ="Create Table Screens ( ScreenID integer ,ScreenNM text ,UserID text );";
   // public static final String CREATE_TBL_Status ="Create Table Status ( StatusID integer Primary Key  ,StatusDesc text );";
   // public static final String CREATE_TBL_Users="Create Table Users ( UserID integer Primary Key,UserName text,Pass text,Securitylevel text , RoleID text);";


    public WDxDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TBL_TagDetails);
       // db.execSQL(CREATE_TBL_Assets);
       // db.execSQL(CREATE_TBL_Building);
       // db.execSQL(CREATE_TBL_Class);
       // db.execSQL(CREATE_TBL_Company);
       // db.execSQL(CREATE_TBL_Country);
       // db.execSQL(CREATE_TBL_Floor);
       // db.execSQL(CREATE_TBL_Images);
       // db.execSQL(CREATE_TBL_ITAssets);
       // db.execSQL(CREATE_TBL_MobileAssets);
       // db.execSQL(CREATE_TBL_Plants);
       // db.execSQL(CREATE_TBL_Room);
      //  db.execSQL(CREATE_TBL_Screens);
       // db.execSQL(CREATE_TBL_Status);
       // db.execSQL(CREATE_TBL_Users);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists TagDetails");
        //db.execSQL("drop table if exists Assets");
        //db.execSQL("drop table if exists Building");
        //db.execSQL("drop table if exists Class");
       // db.execSQL("drop table if exists Company");
       // db.execSQL("drop table if exists Country");
       // db.execSQL("drop table if exists Floor");
      //  db.execSQL("drop table if exists Images");
      //  db.execSQL("drop table if exists ITAssets");
      //  db.execSQL("drop table if exists MobileAssets");
      //  db.execSQL("drop table if exists Plants");
      //  db.execSQL("drop table if exists Room");
      //  db.execSQL("drop table if exists Screens");
      //  db.execSQL("drop table if exists Status");
       // db.execSQL("drop table if exists Users");
        onCreate(db);
    }
}

