package com.thnopp.it.tms_ibm_mobile;

public class Eta {
    String Ltcode;
    String Etadt;

    public Eta(){}

    public Eta(String ltcode){
             this.Ltcode=ltcode;
    }

    public String getLtcode() {
        return Ltcode;
    }

    public void setLtcode(String ltcode) {
        Ltcode = ltcode;
    }

    public String getEtadt() {
        return Etadt;
    }

    public void setEtadt(String etadt) {
        Etadt = etadt;
    }
}
