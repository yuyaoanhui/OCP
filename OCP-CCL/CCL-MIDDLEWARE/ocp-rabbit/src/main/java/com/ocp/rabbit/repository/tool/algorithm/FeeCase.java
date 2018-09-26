package com.ocp.rabbit.repository.tool.algorithm;

import java.util.List;

import com.ocp.rabbit.repository.tool.algorithm.personage.PeopleType;

/**
 * Created by chengyong on 2017/6/14.
 */
public class FeeCase {
    private double feeCourt;
    private double feePart;
    private double share;
    private PeopleType pt;
    private List<String> names;

    public FeeCase() {}

    public FeeCase(double feeCourt, double feePart, double share, PeopleType pt, List<String> names) {
        this.feeCourt = feeCourt;
        this.feePart  = feePart;
        this.pt = pt;
        this.names = names;
    }

    public double getFeeCourt() {
        return feeCourt;
    }
    public double getFeePart() {
        return feePart;
    }
    public double getShare() {
        return share;
    }
    public PeopleType getPt() {
        return pt;
    }
    public List<String> getNames() {
        return names;
    }

    public void setFeeCourt(double feeCourt) {
        this.feeCourt = feeCourt;
    }
    public void setFeePart(double feePart) {
        this.feePart = feePart;
    }
    public void setShare(double share) {
        this.share = share;
    }
    public void setPt(PeopleType pt) {
        this.pt = pt;
    }
    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return "FeeCase [names=" + names +
                ", feeCourt=" + feeCourt +
                ", feePart=" + feePart +
                ", pt=" + pt + "]";
    }
}
