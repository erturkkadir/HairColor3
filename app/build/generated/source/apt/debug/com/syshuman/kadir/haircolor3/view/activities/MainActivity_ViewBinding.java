// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.syshuman.kadir.haircolor3.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    this.target = target;

    target.txtZone1 = Utils.findRequiredViewAsType(source, R.id.txtZone1, "field 'txtZone1'", TextView.class);
    target.txtZone2 = Utils.findRequiredViewAsType(source, R.id.txtZone2, "field 'txtZone2'", TextView.class);
    target.txtZone3 = Utils.findRequiredViewAsType(source, R.id.txtZone3, "field 'txtZone3'", TextView.class);
    target.txtTarget = Utils.findRequiredViewAsType(source, R.id.txtTarget, "field 'txtTarget'", TextView.class);
    target.btnZone1 = Utils.findRequiredViewAsType(source, R.id.btnZone1, "field 'btnZone1'", Button.class);
    target.btnZone2 = Utils.findRequiredViewAsType(source, R.id.btnZone2, "field 'btnZone2'", Button.class);
    target.btnZone3 = Utils.findRequiredViewAsType(source, R.id.btnZone3, "field 'btnZone3'", Button.class);
    target.btnTarget = Utils.findRequiredViewAsType(source, R.id.btnTarget, "field 'btnTarget'", Button.class);
    target.txtRecipe = Utils.findRequiredViewAsType(source, R.id.txtRecipe, "field 'txtRecipe'", TextView.class);
    target.btnGetRecipe = Utils.findRequiredViewAsType(source, R.id.btnGetRecipe, "field 'btnGetRecipe'", ImageButton.class);
    target.btnBLE = Utils.findRequiredViewAsType(source, R.id.btnBLE, "field 'btnBLE'", FloatingActionButton.class);
    target.spCompanies = Utils.findRequiredViewAsType(source, R.id.spCompany, "field 'spCompanies'", Spinner.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txtZone1 = null;
    target.txtZone2 = null;
    target.txtZone3 = null;
    target.txtTarget = null;
    target.btnZone1 = null;
    target.btnZone2 = null;
    target.btnZone3 = null;
    target.btnTarget = null;
    target.txtRecipe = null;
    target.btnGetRecipe = null;
    target.btnBLE = null;
    target.spCompanies = null;
    target.toolbar = null;
  }
}
