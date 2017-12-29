// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.syshuman.kadir.haircolor3.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ForgotFragment_ViewBinding implements Unbinder {
  private ForgotFragment target;

  @UiThread
  public ForgotFragment_ViewBinding(ForgotFragment target, View source) {
    this.target = target;

    target.etEmail = Utils.findRequiredViewAsType(source, R.id.email_forgot, "field 'etEmail'", EditText.class);
    target.btnForgot = Utils.findRequiredViewAsType(source, R.id.send_forgot, "field 'btnForgot'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ForgotFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.etEmail = null;
    target.btnForgot = null;
  }
}
