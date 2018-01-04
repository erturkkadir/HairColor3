package com.syshuman.kadir.haircolor3.model;

public class VisitM {

    private int vz_no; // Visit Number, autoincrement
    private int sm_no; // Salon id
    private int cm_no; // Customer id
    private int st_no; // Stylist id
    private String vz_date; // Visit date
    private String vz_note; // Notes about visit
    private String vz_color; // Read from MHC

    public VisitM(int vz_no, int sm_no, int cm_no, int st_no, String vz_date, String vz_note, String vz_color) {

        this.vz_no = vz_no;
        this.sm_no = sm_no;
        this.cm_no = cm_no;
        this.st_no = st_no;

        this.vz_date = vz_date;
        this.vz_note = vz_note;
        this.vz_color = vz_color;
    }

    public void setVz_no(int vz_no) { this.vz_no = vz_no;  } public int getVz_no() { return vz_no; }
    public void setSm_no(int sm_no) { this.sm_no = sm_no;  } public int getSm_no() { return sm_no; }
    public void setCm_no(int cm_no) { this.cm_no = cm_no;  } public int getCm_no() { return cm_no; }
    public void setSt_no(int st_no) { this.st_no = st_no;  } public int getSt_no() { return st_no; }

    public void setVz_date(String vz_date) {this.vz_date = vz_date;} public String getVz_date() {return vz_date; }
    public void setVz_note(String vz_note) {this.vz_note = vz_note;} public String getVz_note() {return vz_note; }
    public void setVz_color(String vz_color) {this.vz_color = vz_color;} public String getVz_color() {return vz_color;}
}
