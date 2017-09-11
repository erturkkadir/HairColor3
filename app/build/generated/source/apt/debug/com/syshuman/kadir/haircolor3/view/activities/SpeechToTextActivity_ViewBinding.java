// Generated code from Butter Knife. Do not modify!
package com.syshuman.kadir.haircolor3.view.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.syshuman.kadir.haircolor3.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SpeechToTextActivity_ViewBinding implements Unbinder {
  private SpeechToTextActivity target;

  @UiThread
  public SpeechToTextActivity_ViewBinding(SpeechToTextActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SpeechToTextActivity_ViewBinding(SpeechToTextActivity target, View source) {
    this.target = target;

    target.txtSpeechInput = Utils.findRequiredViewAsType(source, R.id.txtSpeechInput, "field 'txtSpeechInput'", TextView.class);
    target.btnSpeak = Utils.findRequiredViewAsType(source, R.id.btnSpeak, "field 'btnSpeak'", ImageButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SpeechToTextActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txtSpeechInput = null;
    target.btnSpeak = null;
  }
}
