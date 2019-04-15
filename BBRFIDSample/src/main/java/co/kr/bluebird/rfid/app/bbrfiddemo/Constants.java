/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 * 
 * http://www.bluebirdcorp.com/
 * 
 * Author : Bogon Jun
 *
 * Date : 2016.01.18
 */

package co.kr.bluebird.rfid.app.bbrfiddemo;

public class Constants {


    public static final String it_id="it_id";
    public static final String ROW_ID="id";
    public static final String style="style";
    public static final String color="color";
    public static final String size="size";

    static final String DB_NAME="shorbagy.db";
    static final String TB_NAME="Repl_Table";
    static final String TB_NAME1="PutAwayCount";
    static final String TB_NAME2="ReceivingCount";
    static final String TB_NAME3="TransactionHeader";
    static final String TB_NAME4="TransactionDetails";

    static final int DB_VERSION=5;

    static final String CREATE_TB="CREATE TABLE Repl_Table(it_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "style TEXT NOT NULL,color TEXT NOT NULL,size TEXT NOT NULL);";

    static final String CREATE_TB1="CREATE TABLE PutAwayCount(ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "items TEXT NOT NULL);";
    static final String CREATE_TB2="CREATE TABLE ReceivingCount(ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "items TEXT NOT NULL);";
    static final String CREATE_TB3="CREATE TABLE TransactionHeader(ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "items TEXT NOT NULL," + "tblRFIDTransType INTEGER,"+" tblUser INTEGER,"+" tblStore INTEGER,"+" RFID_CreationDate TEXT NOT NULL,"+" barcode TEXT NOT NULL);";
    static final String CREATE_TB4="CREATE TABLE TransactionDetails(ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "EBC TEXT NOT NULL,"+"tblRFidTransHeader INTEGER);";
    static final String DROP_TB="DROP TABLE IF EXISTS "+TB_NAME;
    static final String DROP_TB1="DROP TABLE IF EXISTS "+TB_NAME1;
    static final String DROP_TB2="DROP TABLE IF EXISTS "+TB_NAME2;
    static final String DROP_TB3="DROP TABLE IF EXISTS "+TB_NAME3;
    static final String DROP_TB4="DROP TABLE IF EXISTS "+TB_NAME4;

    public static final String VERSION = "Version 2017.11.20";
    
    public static final boolean RECV_D = false;
    
    public static final boolean MAIN_D = false;
    
    public static final boolean CON_D = false;
    
    public static final boolean SD_D = false;
    
    public static final boolean CFG_D = false;
    
    public static final boolean ACC_D = false;
    
    public static final boolean SEL_D = false;
    
    public static final boolean INV_D = false;
    
    public static final boolean BAR_D = false;
    
    public static final boolean BAT_D = false;
    
    public static final boolean INFO_D = false;


    public static final String ACTION_BARCODE_OPEN = "kr.co.bluebird.android.bbapi.action.BARCODE_OPEN";
    public static final String ACTION_BARCODE_CLOSE = "kr.co.bluebird.android.bbapi.action.BARCODE_CLOSE";
    public static final String ACTION_BARCODE_SET_TRIGGER = "kr.co.bluebird.android.bbapi.action.BARCODE_SET_TRIGGER";
    public static final String ACTION_BARCODE_SET_DEFAULT_PROFILE = "kr.co.bluebird.android.bbapi.action.BARCODE_SET_DEFAULT_PROFILE";
    public static final String ACTION_BARCODE_SETTING_CHANGED = "kr.co.bluebird.android.bbapi.action.BARCODE_SETTING_CHANGED";
    public static final String ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_REQUEST_SUCCESS";
    public static final String ACTION_BARCODE_CALLBACK_REQUEST_FAILED = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_REQUEST_FAILED";
    public static final String ACTION_BARCODE_CALLBACK_DECODING_DATA = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_DECODING_DATA";
    public static final String ACTION_MDM_BARCODE_SET_SYMBOLOGY = "kr.co.bluebird.android.bbapi.action.MDM_BARCODE_SET_SYMBOLOGY";
    public static final String ACTION_MDM_BARCODE_SET_MODE = "kr.co.bluebird.android.bbapi.action.MDM_BARCODE_SET_MODE";
    public static final String ACTION_MDM_BARCODE_SET_DEFAULT = "kr.co.bluebird.android.bbapi.action.MDM_BARCODE_SET_DEFAULT";

    //default profile setting done.
    public static final String ACTION_BARCODE_CALLBACK_DEFAULT_PROFILE_SETTING_COMPLETE = "kr.co.bluebird.android.bbapi.action.BARCODE_DEFAULT_PROFILE_SETTING_COMPLETE";
    //request barcode status
    public static final String ACTION_BARCODE_GET_STATUS = "kr.co.bluebird.android.action.BARCODE_GET_STATUS";
    //repond barcode status
    public static final String ACTION_BARCODE_CALLBACK_GET_STATUS = "kr.co.bluebird.android.action.BARCODE_CALLBACK_GET_STATUS";

    //barcode status
    public static final int BARCODE_CLOSE = 0;
    public static final int BARCODE_OPEN = 1;
    public static final int BARCODE_TRIGGER_ON = 2;

    public static final String EXTRA_BARCODE_BOOT_COMPLETE = "EXTRA_BARCODE_BOOT_COMPLETE";
    public static final String EXTRA_BARCODE_PROFILE_NAME = "EXTRA_BARCODE_PROFILE_NAME";
    public static final String EXTRA_BARCODE_TRIGGER = "EXTRA_BARCODE_TRIGGER";
    public static final String EXTRA_BARCODE_DECODING_DATA = "EXTRA_BARCODE_DECODING_DATA";
    public static final String EXTRA_HANDLE = "EXTRA_HANDLE";
    public static final String EXTRA_INT_DATA2 = "EXTRA_INT_DATA2";
    public static final String EXTRA_STR_DATA1 = "EXTRA_STR_DATA1";
    public static final String EXTRA_INT_DATA3 = "EXTRA_INT_DATA3";

    public static final int ERROR_FAILED = -1;
    public static final int ERROR_NOT_SUPPORTED = -2;
    public static final int ERROR_NO_RESPONSE = -4;
    public static final int ERROR_BATTERY_LOW = -5;
    public static final int ERROR_BARCODE_DECODING_TIMEOUT = -6;
    public static final int ERROR_BARCODE_ERROR_USE_TIMEOUT = -7;
    public static final int ERROR_BARCODE_ERROR_ALREADY_OPENED = -8;

    public static final int MDM_MSR_MODE__SET_READING_TIMEOUT = 0;

}