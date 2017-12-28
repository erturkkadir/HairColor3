// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.syshuman.kadir.haircolor3.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RegisterFragment_ViewBinding implements Unbinder {
  private RegisterFragment target;

  @UiThread
  public RegisterFragment_ViewBinding(RegisterFragment target, View source) {
    this.target = target;

    target.btnRegister = Utils.findRequiredViewAsType(source, R.id.reg_register, "field 'btnRegister'", Button.class);
    target.fName = Utils.findRequiredViewAsType(source, R.id.reg_fname, "field 'fName'", EditText.class);
    target.lName = Utils.findRequiredViewAsType(source, R.id.reg_lname, "field 'lName'", EditText.class);
    target.email = Utils.findRequiredViewAsType(source, R.id.reg_email, "field 'email'", EditText.class);
    target.pass1 = Utils.findRequiredViewAsType(source, R.id.reg_pass1, "field 'pass1'", EditText.class);
    target.pass2 = Utils.findRequiredViewAsType(source, R.id.reg_pass2, "field 'pass2'", EditText.class);
    target.eula = Utils.findRequiredViewAsType(source, R.id.reg_eula, "field 'eula'", CheckBox.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RegisterFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btnRegister = null;
    target.fName = null;
    target.lName = null;
    target.email = null;
    target.pass1 = null;
    target.pass2 = null;
    target.eula = null;
  }
}
