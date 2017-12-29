// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.syshuman.kadir.haircolor3.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CustomerFragment_ViewBinding implements Unbinder {
  private CustomerFragment target;

  @UiThread
  public CustomerFragment_ViewBinding(CustomerFragment target, View source) {
    this.target = target;

    target.rvCustomer = Utils.findRequiredViewAsType(source, R.id.rvCustomer, "field 'rvCustomer'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CustomerFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rvCustomer = null;
  }
}
