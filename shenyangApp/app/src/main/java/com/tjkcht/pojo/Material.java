package com.tjkcht.pojo;

import com.tjkcht.rfid.sdk.RfidTag;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by APPLE on 2019/1/30.
 */

public class Material implements Serializable{
    private  String MaterialId;
    private   String  RfidNum;
    private Set<RfidTag> RfidTagSet;

    public Set<RfidTag> getRfidTagSet() {
        return RfidTagSet;
    }

    public void setRfidTagSet(Set<RfidTag> rfidTagSet) {
        RfidTagSet = rfidTagSet;
    }
    //private List<> ;

    public String  getRfidNum() {
        return RfidNum;
    }

    public String getMaterialId() {
        return MaterialId;
    }

    public void setMaterialId(String materialId) {
        MaterialId = materialId;
    }

    public void setRfidNum(String  rfidNum) {
        RfidNum = rfidNum;
    }
}
