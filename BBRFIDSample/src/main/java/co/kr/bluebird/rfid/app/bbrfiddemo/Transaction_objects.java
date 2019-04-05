package co.kr.bluebird.rfid.app.bbrfiddemo;

public class Transaction_objects {
    public int UserId;
    public int StoreId;
    public int Iserial;
    public int NewIserial;

    public Transaction_objects(int UserId, int StoreId, int Iserial,int NewIserial, String Code, String Ename, String Aname, String SPName) {
        this.UserId = UserId;
        this.StoreId = StoreId;
        this.Iserial = Iserial;
        this.Code = Code;
        this.Ename = Ename;
        this.Aname = Aname;
        this.SPName = SPName;
        this.NewIserial=NewIserial;
    }

    public int getNewIserial() {
        return NewIserial;
    }

    public void setNewIserial(int newIserial) {
        NewIserial = newIserial;
    }

    public String Code;
    public String Ename;
    public String Aname;
    public String SPName;

    public Transaction_objects() {

    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getStoreId() {
        return StoreId;
    }

    public void setStoreId(int storeId) {
        StoreId = storeId;
    }

    public int getIserial() {
        return Iserial;
    }

    public void setIserial(int iserial) {
        Iserial = iserial;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getEname() {
        return Ename;
    }

    public void setEname(String ename) {
        Ename = ename;
    }

    public String getAname() {
        return Aname;
    }

    public void setAname(String aname) {
        Aname = aname;
    }

    public String getSPName() {
        return SPName;
    }

    public void setSPName(String SPName) {
        this.SPName = SPName;
    }
}
