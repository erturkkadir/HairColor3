package com.syshuman.kadir.haircolor3.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.syshuman.kadir.haircolor3.model.BluetoothLeUart;
import com.syshuman.kadir.haircolor3.model.RestServer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kerturkx on 2017-09-01.
 */

@Module(includes = ContextModule.class)
public class HC3Module {

    @Provides
    public BluetoothLeUart getBluetoothLeUart(Context context) {
        return new BluetoothLeUart(context);
    }

    @Provides
    public RestServer getRestServer(Context context) {
        return new RestServer(context);
    }

    @Provides
    public SharedPreferences getSharedPreferences(Context context) {
        return  PreferenceManager.getDefaultSharedPreferences(context);
    }

}
