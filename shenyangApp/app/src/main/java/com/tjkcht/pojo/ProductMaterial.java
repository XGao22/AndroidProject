package com.tjkcht.pojo;

import java.util.List;

/**
 * @Description: 产品物料信息
 * @Author: lenovo
 * @CreateDate: 2019/5/20 13:09
 */
public class ProductMaterial {
    /**
     * EPC编码-扫描标签
     */
    private String epc;
    /**
     * 产品唯一标识
     */
    private String productUniqueIdentifier;
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 计划/上线
     */
    private String plan_goOnline;
    /**
     * 扫描条码
     */
    private String barCode;
    /**
     * 标签TID
     */
    private String tid;

    private List<MaterialsBarCode> materials;

    public ProductMaterial() {
    }

    public ProductMaterial(String epc, String productUniqueIdentifier, String productCode, String productName, String plan_goOnline, String barCode, List<MaterialsBarCode> materials) {
        this.epc = epc;
        this.productUniqueIdentifier = productUniqueIdentifier;
        this.productCode = productCode;
        this.productName = productName;
        this.plan_goOnline = plan_goOnline;
        this.barCode = barCode;
        this.materials = materials;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getProductUniqueIdentifier() {
        return productUniqueIdentifier;
    }

    public void setProductUniqueIdentifier(String productUniqueIdentifier) {
        this.productUniqueIdentifier = productUniqueIdentifier;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPlan_goOnline() {
        return plan_goOnline;
    }

    public void setPlan_goOnline(String plan_goOnline) {
        this.plan_goOnline = plan_goOnline;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<MaterialsBarCode> getMaterials() {
        return materials;
    }

    public void setMaterials(List<MaterialsBarCode> materials) {
        this.materials = materials;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
