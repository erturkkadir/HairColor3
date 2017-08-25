// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.syshuman.kadir.haircolor3.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TrainingActivity_ViewBinding implements Unbinder {
  private TrainingActivity target;

  @UiThread
  public TrainingActivity_ViewBinding(TrainingActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public TrainingActivity_ViewBinding(TrainingActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.spTCompany = Utils.findRequiredViewAsType(source, R.id.spTCompany, "field 'spTCompany'", Spinner.class);
    target.spTCatalog = Utils.findRequiredViewAsType(source, R.id.spTCatalog, "field 'spTCatalog'", Spinner.class);
    target.spTColor = Utils.findRequiredViewAsType(source, R.id.spTColor, "field 'spTColor'", Spinner.class);
    target.btnTrain = Utils.findRequiredViewAsType(source, R.id.btnTrain, "field 'btnTrain'", ImageButton.class);
    target.btnBLE = Utils.findRequiredViewAsType(source, R.id.btnBLE, "field 'btnBLE'", FloatingActionButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TrainingActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.spTCompany = null;
    target.spTCatalog = null;
    target.spTColor = null;
    target.btnTrain = null;
    target.btnBLE = null;
  }
}
