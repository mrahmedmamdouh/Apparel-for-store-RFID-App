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
}