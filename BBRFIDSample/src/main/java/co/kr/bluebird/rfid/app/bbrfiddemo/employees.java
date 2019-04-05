package co.kr.bluebird.rfid.app.bbrfiddemo;

public class employees {
    public int mIv;



    public String mDt;

    public int mDupCount;


    public boolean mHasPc;


    public employees() {


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getEmployee_salary() {
        return employee_salary;
    }

    public void setEmployee_salary(String employee_salary) {
        this.employee_salary = employee_salary;
    }

    public String getEmployee_age() {
        return employee_age;
    }

    public void setEmployee_age(String employee_age) {
        this.employee_age = employee_age;
    }

    public employees(String id, String employee_name, String employee_salary, String employee_age) {

        this.id = id;
        this.employee_name = employee_name;
        this.employee_salary = employee_salary;
        this.employee_age = employee_age;
    }

    public String id,employee_name,employee_salary,employee_age;

}
