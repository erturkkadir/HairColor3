// Generated by dagger.internal.codegen.ComponentProcessor (https://google.github.io/dagger).
package com.syshuman.kadir.haircolor3.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class HC3Module_GetSharedPreferencesFactory implements Factory<SharedPreferences> {
  private final HC3Module module;

  private final Provider<Context> contextProvider;

  public HC3Module_GetSharedPreferencesFactory(
      HC3Module module, Provider<Context> contextProvider) {
    assert module != null;
    this.module = module;
    assert contextProvider != null;
    this.contextProvider = contextProvider;
  }

  @Override
  public SharedPreferences get() {
    return Preconditions.checkNotNull(
        module.getSharedPreferences(contextProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<SharedPreferences> create(
      HC3Module module, Provider<Context> contextProvider) {
    return new HC3Module_GetSharedPreferencesFactory(module, contextProvider);
  }
}
