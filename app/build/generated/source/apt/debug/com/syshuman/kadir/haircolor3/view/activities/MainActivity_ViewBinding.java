// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    target.txtZone1_1 = Utils.findRequiredViewAsType(source, R.id.txtZone1_1, "field 'txtZone1_1'", TextView.class);
    target.txtZone2_1 = Utils.findRequiredViewAsType(source, R.id.txtZone2_1, "field 'txtZone2_1'", TextView.class);
    target.txtZone3_1 = Utils.findRequiredViewAsType(source, R.id.txtZone3_1, "field 'txtZone3_1'", TextView.class);
    target.txtTarget_1 = Utils.findRequiredViewAsType(source, R.id.txtTarget_1, "field 'txtTarget_1'", TextView.class);
    target.btnZone1 = Utils.findRequiredViewAsType(source, R.id.btnZone1, "field 'btnZone1'", Button.class);
    target.btnZone2 = Utils.findRequiredViewAsType(source, R.id.btnZone2, "field 'btnZone2'", Button.class);
    target.btnZone3 = Utils.findRequiredViewAsType(source, R.id.btnZone3, "field 'btnZone3'", Button.class);
    target.btnTarget = Utils.findRequiredViewAsType(source, R.id.btnTarget, "field 'btnTarget'", Button.class);
    target.lZone1 = Utils.findRequiredViewAsType(source, R.id.lZone1, "field 'lZone1'", LinearLayout.class);
    target.lZone2 = Utils.findRequiredViewAsType(source, R.id.lZone2, "field 'lZone2'", LinearLayout.class);
    target.lZone3 = Utils.findRequiredViewAsType(source, R.id.lZone3, "field 'lZone3'", LinearLayout.class);
    target.lTarget = Utils.findRequiredViewAsType(source, R.id.lTarget, "field 'lTarget'", LinearLayout.class);
    target.lblZone1 = Utils.findRequiredViewAsType(source, R.id.lblZone1, "field 'lblZone1'", TextView.class);
    target.lblZone2 = Utils.findRequiredViewAsType(source, R.id.lblZone2, "field 'lblZone2'", TextView.class);
    target.lblZone3 = Utils.findRequiredViewAsType(source, R.id.lblZone3, "field 'lblZone3'", TextView.class);
    target.lblTarget = Utils.findRequiredViewAsType(source, R.id.lblTarget, "field 'lblTarget'", TextView.class);
    target.txtRecipe = Utils.findRequiredViewAsType(source, R.id.txtRecipe, "field 'txtRecipe'", TextView.class);
    target.btnGetRecipe = Utils.findRequiredViewAsType(source, R.id.btnGetRecipe, "field 'btnGetRecipe'", ImageButton.class);
    target.btnBLE = Utils.findRequiredViewAsType(source, R.id.btnBLE, "field 'btnBLE'", FloatingActionButton.class);
    target.spCompanies = Utils.findRequiredViewAsType(source, R.id.spCompany, "field 'spCompanies'", Spinner.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.imgBattery = Utils.findRequiredViewAsType(source, R.id.imgBattery, "field 'imgBattery'", ImageButton.class);
    target.txtBattery = Utils.findRequiredViewAsType(source, R.id.txtBattery, "field 'txtBattery'", TextView.class);
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
    target.txtZone1_1 = null;
    target.txtZone2_1 = null;
    target.txtZone3_1 = null;
    target.txtTarget_1 = null;
    target.btnZone1 = null;
    target.btnZone2 = null;
    target.btnZone3 = null;
    target.btnTarget = null;
    target.lZone1 = null;
    target.lZone2 = null;
    target.lZone3 = null;
    target.lTarget = null;
    target.lblZone1 = null;
    target.lblZone2 = null;
    target.lblZone3 = null;
    target.lblTarget = null;
    target.txtRecipe = null;
    target.btnGetRecipe = null;
    target.btnBLE = null;
    target.spCompanies = null;
    target.toolbar = null;
    target.imgBattery = null;
    target.txtBattery = null;
  }
}
