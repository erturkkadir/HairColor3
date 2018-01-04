package com.syshuman.kadir.haircolor3.eventbus;


import com.syshuman.kadir.haircolor3.model.CustM;
import com.syshuman.kadir.haircolor3.model.SalonM;

public class SalonEvents {

    public static class onSalonUpdated {
        public SalonM salonM;
        public onSalonUpdated(SalonM salonM) {
            this.salonM = salonM;
        }
    }

    public static class onSalonFetched {
        public SalonM salonM;
        public onSalonFetched(SalonM salonM) {
            this.salonM = salonM;
        }
    }

}
