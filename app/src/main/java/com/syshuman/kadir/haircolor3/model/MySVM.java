package com.syshuman.kadir.haircolor3.model;

import android.content.Context;
import android.widget.Toast;

import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import libsvm.svm;

import static com.syshuman.kadir.haircolor3.R.id.txtRecipe;

/**
 * Created by kerturkx on 2017-08-28.
 */

public class MySVM {

    svm _svm;
    RestServer restServer;
    Context context;


    public MySVM(Context context, RestServer restServer, svm _svm) {

        this.context = context;
        this._svm = _svm;
        this.restServer = restServer;

    }

    public void getTrainData() {

        restServer.getTrainData("data.txt");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrainData(MessageEvents.onTrainData event) {
        Toast.makeText(context, event.data.length, Toast.LENGTH_LONG).show();


    }
}
