package com.example.authenticationprocess;

public class model_patient_recview {

    String Tabname,time1,time2,time3;

    model_patient_recview()
    {

    }

    public model_patient_recview(String tabname, String time1, String time2, String time3) {
        Tabname = tabname;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
    }

    public String getTabname() {
        return Tabname;
    }

    public void setTabname(String tabname) {
        Tabname = tabname;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getTime3() {
        return time3;
    }

    public void setTime3(String time3) {
        this.time3 = time3;
    }
}
