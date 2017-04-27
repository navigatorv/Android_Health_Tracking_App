package com.example.kzha6954.mysteps.Me;

import java.io.Serializable;

/**
 * Created by zkd on 10-07-2016.
 */
public class historySet implements Serializable{
    private String tm;
    private String stps;

    public historySet(){}
    public historySet(String tm, String stps) {
        this.tm = tm;
        this.stps = stps;
    }
    public void setTm(String tm){this.tm = tm;}
    public void setStps(String stps){this.stps = stps;}
    public String getTm() {return tm;}
    public String getStps() {return stps;}

}
