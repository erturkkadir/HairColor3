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

public class LoginFragment_ViewBinding implements Unbinder {
  private LoginFragment target;

  @UiThread
  public LoginFragment_ViewBinding(LoginFragment target, View source) {
    this.target = target;

    target.btnLogin = Utils.findRequiredViewAsType(source, R.id.login_btn, "field 'btnLogin'", Button.class);
    target.btnRegister = Utils.findRequiredViewAsType(source, R.id.register_btn, "field 'btnRegister'", Button.class);
    target.btnForgot = Utils.findRequiredViewAsType(source, R.id.forgot_btn, "field 'btnForgot'", Button.class);
    target.uname = Utils.findRequiredViewAsType(source, R.id.account_email_input, "field 'uname'", EditText.class);
    target.upass = Utils.findRequiredViewAsType(source, R.id.password_input, "field 'upass'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btnLogin = null;
    target.btnRegister = null;
    target.btnForgot = null;
    target.uname = null;
    target.upass = null;
  }
}
