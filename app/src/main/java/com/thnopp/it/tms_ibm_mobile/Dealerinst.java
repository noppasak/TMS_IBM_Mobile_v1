package com.thnopp.it.tms_ibm_mobile;

public class Dealerinst {
    private String dealercd;
    private String dealer;
    private String dealername;
    private String instruction1;
    private String instruction2;
    private String instruction3;
    private double lat;
    private double lon;
    private int tmpid;

    public Dealerinst(){}



    public Dealerinst(String dealer, String dealername, String instruction1, String instruction2,
                      String instruction3){
        this.dealer=dealer;
        this.dealername = dealername;
        this.instruction1 = instruction1;
        this.instruction2 = instruction2;
        this.instruction3 = instruction3;

    }

    public String getDealercd() {
        return dealercd;
    }

    public void setDealercd(String dealercd) {
        this.dealercd = dealercd;
    }

    public int getTmpid() {
        return tmpid;
    }

    public void setTmpid(int tmpid) {
        this.tmpid = tmpid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getDealername() {
        return dealername;
    }

    public void setDealername(String dealername) {
        this.dealername = dealername;
    }

    public String getInstruction1() {
        return instruction1;
    }

    public void setInstruction1(String instruction1) {
        this.instruction1 = instruction1;
    }

    public String getInstruction2() {
        return instruction2;
    }

    public void setInstruction2(String instruction2) {
        this.instruction2 = instruction2;
    }

    public String getInstruction3() {       return instruction3;    }

    public void setInstruction3(String instruction3) {        this.instruction3 = instruction3;    }
}
