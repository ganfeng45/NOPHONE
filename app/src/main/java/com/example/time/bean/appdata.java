package com.example.time.bean;

import org.litepal.crud.LitePalSupport;

public class appdata extends LitePalSupport {
    private String appname;
    private int apptime;
    public appdata(String appname,int apptime){
        this.appname=appname;
        this.apptime=apptime;
    }


    public String getAppname() {
        return appname;
    }

    public int getApptime() {
        return apptime;
    }

    public void setApptime(int apptime) {
        this.apptime = apptime;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }
    public appdata(){}

}
