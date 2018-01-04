package com.syshuman.kadir.haircolor3.eventbus;

import com.syshuman.kadir.haircolor3.model.CustM;

import java.util.List;

public class CustomerEvents {

    public static class onCustomerCreated {
        public CustM custM;
        public onCustomerCreated(CustM custM) {
            this.custM = custM;
        }
    }

    public static class onCustomerRead {
        public List<CustM> custMList;
        public onCustomerRead(List<CustM> custMList) {
            this.custMList = custMList;
        }
    }

    public static class onCustomerUpdated {
        public CustM custM;
        public onCustomerUpdated(CustM custM) {
            this.custM = custM;
        }
    }

    public static class onCustomerDeleted {
        public CustM custM;
        public onCustomerDeleted(CustM custM) {
            this.custM = custM;
        }
    }
}
