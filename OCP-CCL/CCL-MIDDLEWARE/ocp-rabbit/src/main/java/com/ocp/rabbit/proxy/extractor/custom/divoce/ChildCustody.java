package com.ocp.rabbit.proxy.extractor.custom.divoce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChildCustody {
    private List<String[]> childName;
    private List<Map<String,String>> children;
    private String custodian;
    private String payer;
    private double alimoney;
    private String paymentFrequency;

    public ChildCustody() {
        childName = new ArrayList<>();
    }

    public List<Map<String,String>> getChildren() {
        return children;
    }

    public void setChildren(List<Map<String, String>> children) {
        this.children = children;
    }

    public List<String[]> getChildName() {
        return childName;
    }

    public void setChildName(List<String[]> childName) {
        this.childName = childName;
    }

    public String getCustodian() {
        return custodian;
    }

    public void setCustodian(String custodian) {
        this.custodian = custodian;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public double getAlimoney() {
        return alimoney;
    }

    public void setAlimoney(double alimoney) {
        this.alimoney = alimoney;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

}
