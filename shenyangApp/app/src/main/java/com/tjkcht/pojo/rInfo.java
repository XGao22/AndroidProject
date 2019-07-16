package com.tjkcht.pojo;

import java.io.Serializable;

/**
 * Created by APPLE on 2019/1/30.
 */

public class rInfo  implements Serializable{
    private String rId;
    private  String findTime;
    private int maxRssi;
    private int minRssi;
    private String rCheckNum;

    public int getMaxRssi() {
        return maxRssi;
    }

    public int getMinRssi() {
        return minRssi;
    }

    public String getFindTime() {
        return findTime;
    };

    public String getrCheckNum() {
        return rCheckNum;
    };

    public String getrId() {
        return rId;
    }


    public void setFindTime(String findTime) {
        this.findTime = findTime;
    };

    public void setrCheckNum(String rCheckNum) {
        this.rCheckNum = rCheckNum;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public void setMaxRssi(int maxRssi) {
        this.maxRssi = maxRssi;
    }

    public void setMinRssi(int minRssi) {
        this.minRssi = minRssi;
    }
}
