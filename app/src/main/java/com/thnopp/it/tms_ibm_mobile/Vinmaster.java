package com.thnopp.it.tms_ibm_mobile;

import java.util.Date;

/**
 * Created by THLT88 on 3/24/2018.
 */

public class Vinmaster {
    String vin;
    String engine;
    String id;
    String dealer;
    String dealer_name;
    String status;
    Date scandt;
    Date shipdt;
    Date arrivaldt;

    public Date getArrivaldt() {
        return arrivaldt;
    }

    public void setArrivaldt(Date arrivaldt) {
        this.arrivaldt = arrivaldt;
    }

    String trailer;
    String ltcode;
    String transdt;
    String widealer;
    String inst1;
    String inst2;

    public Vinmaster(){}
    public Vinmaster(String vin, String engine, String id, String dealer, String dealer_name, String status, String trailer
            , Date scandt, String ltcode){
        this.vin=vin;
        this.engine = engine;
        this.id=id;
        this.dealer=dealer;
        this.dealer_name = dealer_name;
        this.status=status;
        this.trailer=trailer;
        this.scandt =scandt;
        this.ltcode=ltcode;

    }

    public Vinmaster(String vin, String engine, String id, String dealer, String dealer_name, String status, String trailer
            , Date scandt, String ltcode, String inst1, String inst2, String widealer){
        this.vin=vin;
        this.engine = engine;
        this.id=id;
        this.dealer=dealer;
        this.dealer_name = dealer_name;
        this.status=status;
        this.trailer=trailer;
        this.scandt =scandt;
        this.ltcode=ltcode;
        this.inst1=inst1;
        this.inst2=inst2;
        this.widealer=widealer;

    }

    public Vinmaster(String vin, String engine, String id, String dealer, String dealer_name, String status, String trailer
            , Date scandt, String ltcode, Date shipdt){
        this.vin=vin;
        this.engine = engine;
        this.id=id;
        this.dealer=dealer;
        this.dealer_name = dealer_name;
        this.status=status;
        this.trailer=trailer;
        this.scandt =scandt;
        this.ltcode=ltcode;
        this.shipdt = shipdt;

    }

    public String getWidealer() {
        return widealer;
    }

    public void setWidealer(String widealer) {
        this.widealer = widealer;
    }

    public String getInst1() {
        return inst1;
    }

    public void setInst1(String inst1) {
        this.inst1 = inst1;
    }

    public String getInst2() {
        return inst2;
    }

    public void setInst2(String inst2) {
        this.inst2 = inst2;
    }

    public Date getShipdt() {
        return shipdt;
    }

    public void setShipdt(Date shipdt) {
        this.shipdt = shipdt;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getStatus() {
        return status;
    }

    public String getLtcode() {
        return ltcode;
    }

    public void setLtcode(String ltcode) {
        this.ltcode = ltcode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getScandt() {
        return scandt;
    }

    public void setScandt(Date scandt) {
        this.scandt = scandt;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getDealer_name() {
        return dealer_name;
    }

    public void setDealer_name(String dealer_name) {
        this.dealer_name = dealer_name;
    }

    public String getTransdt() {
        return transdt;
    }

    public void setTransdt(String transdt) {
        this.transdt = transdt;
    }
}
