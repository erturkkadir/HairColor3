package com.syshuman.kadir.haircolor3.dagger.components;

import android.content.SharedPreferences;

import com.syshuman.kadir.haircolor3.dagger.modules.HC3Module;
import com.syshuman.kadir.haircolor3.model.BluetoothLeUart;
import com.syshuman.kadir.haircolor3.model.RestServer;
import dagger.Component;

/**
 * Created by kerturkx on 2017-09-01.
 */

@Component(modules=HC3Module.class)
public interface HC3Component {

    BluetoothLeUart getBluetoothLeUart();
    RestServer getRestServer();
    SharedPreferences getSharedPreferences();

}
