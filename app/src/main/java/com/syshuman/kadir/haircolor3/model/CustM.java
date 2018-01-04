package com.syshuman.kadir.haircolor3.model;

public class CustM {

    private int cm_no; // Customer id
    private int sm_no; // Salon id
    private String cm_email; // email to login web app
    private String cm_upass; // upass to login web app
    private String cm_phone; // phone
    private String cm_fname; // first name
    private String cm_lname; // last name
    private String cm_last_visit;

    public CustM() {
    }

    public int getCm_no() { return cm_no; } public void setCm_no(int cm_no) { this.cm_no = cm_no; }
    public int getSm_no() { return sm_no; } public void setSm_no(int sm_no) { this.sm_no = sm_no; }

    public String getCm_email() { return cm_email; } public void setCm_email(String cm_email) { this.cm_email = cm_email; }
    public String getCm_upass() { return cm_upass; } public void setCm_upass(String cm_upass) { this.cm_upass = cm_upass; }
    public String getCm_phone() { return cm_phone; } public void setCm_phone(String cm_phone) { this.cm_phone = cm_phone; }
    public String getCm_fname() { return cm_fname; } public void setCm_fname(String cm_fname) { this.cm_fname = cm_fname; }
    public String getCm_lname() { return cm_lname; } public void setCm_lname(String cm_lname) { this.cm_lname = cm_lname; }
    public String getCm_last_visit() { return cm_last_visit; } public void setCm_last_visit(String cm_last_visit) { this.cm_last_visit = cm_last_visit;}

}
