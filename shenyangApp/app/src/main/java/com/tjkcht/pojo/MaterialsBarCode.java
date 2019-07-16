package com.tjkcht.pojo;

public class MaterialsBarCode {
    /** 条码 */
    private String barCode;
    private String materialCode;
    private String materialName;

    public MaterialsBarCode() {
    }

    public MaterialsBarCode(String barCode, String materialCode, String materialName) {
        this.barCode = barCode;
        this.materialCode = materialCode;
        this.materialName = materialName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
