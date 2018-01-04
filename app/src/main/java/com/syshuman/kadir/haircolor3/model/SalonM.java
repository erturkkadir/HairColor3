package com.syshuman.kadir.haircolor3.model;



public class SalonM {
    private int sm_no; // Salon id
    private String sm_email; // salon username
    private String sm_upass; // No need to save here
    private String sm_phone;
    private String sm_address1;
    private String sm_address2;
    private String sm_city;
    private String sm_province;
    private String sm_country;

    public int getSm_no() {return sm_no;} public void setSm_no(int sm_no) {this.sm_no = sm_no;}

    public String getSm_email()     {return sm_email;}      public void setSm_email(String sm_email)        {this.sm_email      = sm_email;}
    public String getSm_upass()     {return sm_upass;}      public void setSm_upass(String sm_upass)        {this.sm_upass      = sm_upass;}
    public String getSm_phone()     {return sm_phone;}      public void setSm_phone(String sm_phone)        {this.sm_phone      = sm_phone;}
    public String getSm_address1()  {return sm_address1;}   public void setSm_address1(String sm_address1)  {this.sm_address1   = sm_address1;}
    public String getSm_address2()  {return sm_address2;}   public void setSm_address2(String sm_address2)  {this.sm_address2   = sm_address2;}
    public String getSm_city()      {return sm_city;}       public void setSm_city(String sm_city)          {this.sm_city       = sm_city;}
    public String getSm_province()  {return sm_province;}   public void setSm_province(String sm_province)  {this.sm_province   = sm_province;}
    public String getSm_country()   {return sm_country;}    public void setSm_country(String sm_country)    {this.sm_country    = sm_country;}

}
