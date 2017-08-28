package com.syshuman.kadir.haircolor3.model;

import libsvm.svm;

/**
 * Created by kerturkx on 2017-08-28.
 */

public class MySVM {

    svm _svm;
    RestServer restServer;


    public MySVM(RestServer restServer, svm _svm) {
        this._svm = _svm;
        this.restServer = restServer;
    }

    public void train() {
        restServer.getTrainData("data.txt");
    }
}
