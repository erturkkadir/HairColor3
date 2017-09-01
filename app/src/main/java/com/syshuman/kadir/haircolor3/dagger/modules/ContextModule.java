package com.syshuman.kadir.haircolor3.dagger.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kerturkx on 2017-09-01.
 */
@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context context() {
        return context;
    }
}
