// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.SurfaceView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.syshuman.kadir.haircolor3.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CameraFragment_ViewBinding implements Unbinder {
  private CameraFragment target;

  @UiThread
  public CameraFragment_ViewBinding(CameraFragment target, View source) {
    this.target = target;

    target.surfaceView = Utils.findRequiredViewAsType(source, R.id.camera_view, "field 'surfaceView'", SurfaceView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CameraFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.surfaceView = null;
  }
}
