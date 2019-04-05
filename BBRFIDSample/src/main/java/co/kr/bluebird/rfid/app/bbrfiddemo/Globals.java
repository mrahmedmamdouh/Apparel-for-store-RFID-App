package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.provider.Settings;

/**
 * Created by Ismail on 2/28/2017.
 */
public class Globals {
    public static Globals instance;
    private static int EmpID;
    private static String EmpName;
    private static int UserID;
    private static String UserName;
    private static int canSendData;
    private static int TransactionType;
    private static boolean admin_Logged;
    private static boolean user_Logged;
    private static String CountryCode;
    private static String CountryName;
    private static String CompanyCode;
    private static String CompanyName;
    private static String PlantCode;
    private static String PlantName;
    private static String BuildingID;
    private static String BuildingAutoID;
    private static String BuildingName;
    private static String FloorID;
    private static String FloorAutoID;
    private static String FloorName;
    private static String RoomID;
    private static String RoomAutoID;
    private static String RoomName;


    private static String AuditNumber;

    private static String Server;
    private static String DB;
    private static String DBUser;
    private static String DBPass;

    private Globals(){}

    public int getEmpID()
    {return EmpID;}
    public String getEmpName()
    {return EmpName;}
    public void setEmpID(int empID)
    { Globals.EmpID = empID;}
    public void setEmpName(String empName)
    {Globals.EmpName = empName;}
    public int getUserID()
    {return UserID;}
    public String getUserName()
    {return UserName;}
    public void setUserID(int userID)
    { Globals.UserID = userID;}
    public void setUserName(String userName)
    {Globals.UserName = userName;}
    public int getTransactionType()
    {return Globals.TransactionType;}
    public void setTransactionType(int transactionType)
    {Globals.TransactionType=transactionType;}
    public int getCanSendData()
    {return canSendData;}
    public void setCanSendData(int canSend)
    { Globals.canSendData = canSend;}
    public boolean getAdminLogged()
    {return admin_Logged;}
    public void setAdmin_Logged(boolean admin)
    { Globals.admin_Logged = admin;}
    public boolean getUserLogged()
    {return user_Logged;}
    public void setUser_Logged(boolean user)
    { Globals.user_Logged = user;}

    public void setCountryCode(String countryCode)
    { Globals.CountryCode = countryCode;}
    public void setCountryName(String countryName)
    {Globals.CountryName = countryName;}

    public void setCompanyCode(String companyCode)
    { Globals.CompanyCode = companyCode;}
    public void setCompanyName(String companyName)
    {Globals.CompanyName = companyName;}

    public void setPlantCode(String plantCode)
    { Globals.PlantCode = plantCode;}
    public void setPlantName(String plantName)
    {Globals.PlantName = plantName;}

    public void setBuildingID(String buildingID)
    { Globals.BuildingID = buildingID;}
    public void setBuildingAutoID(String buildingAutoID)
    { Globals.BuildingAutoID = buildingAutoID;}
    public void setBuildingName(String buildingName)
    { Globals.BuildingName = buildingName;}

    public void setFloorID(String floorID)
    { Globals.FloorID = floorID;}
    public void setFloorAutoID(String floorAutoID)
    { Globals.FloorAutoID = floorAutoID;}
    public void setFloorName(String floorName)
    { Globals.FloorName = floorName;}

    public void setRoomID(String roomID)
    { Globals.RoomID = roomID;}
    public void setRoomAutoID(String roomAutoID)
    { Globals.FloorAutoID = roomAutoID;}
    public void setRoomName(String roomName)
    { Globals.RoomName = roomName;}





    public String getCountryCode()
    {return CountryCode;}
    public String getCountryName()
    {return CountryName;}
    public String getCompanyCode()
    {return CompanyCode;}
    public String getCompanyName()
    {return CompanyName;}
    public String getPlantCode()
    {return PlantCode;}
    public String getPlantName()
    {return PlantName;}
    public String getBuildingID()
    {return BuildingID;}
    public String getBuildingAutoID()
    {return BuildingAutoID;}
    public String getBuildingName()
    {return BuildingName;}

    public String getFloorID()
    {return FloorID;}
    public String getFloorAutoID()
    {return FloorAutoID;}
    public String getFloorName()
    {return FloorName;}

    public String getRoomID()
    {return RoomID;}
    public String getRoomAutoID()
    {return RoomAutoID;}
    public String getRoomName()
    {return RoomName;}




    public void setServerName(String serverName)
    {Globals.Server = serverName;}
    public void setDBName(String dbName)
    {Globals.DB = dbName;}
    public void setDBUser(String dbUser)
    {Globals.DBUser = dbUser;}
    public void setDBPass(String dbPass)
    {Globals.DBPass = dbPass;}

    public String getServer()
    {return Server;}
    public String getDB()
    {return DB;}
    public String getDBUser()
    {return DBUser;}
    public String getDBPass()
    {return DBPass;}

    public void setAuditNumber(String auditNumber)
    {Globals.AuditNumber = auditNumber;}
    public String getAuditNumber()
    {return AuditNumber;}







    public static synchronized Globals getInstance(){
        if(instance==null)
        {
            instance = new Globals();
        }
        return instance;
    }



}
