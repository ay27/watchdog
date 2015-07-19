// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class KeyguardKeyboardActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.KeyguardKeyboardActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427419, "field 'inputEdt'");
    target.inputEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427414, "field 'keyboardView'");
    target.keyboardView = (android.inputmethodservice.KeyboardView) view;
    view = finder.findRequiredView(source, 2131427417, "method 'changeMode'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.changeMode(p0);
        }
      });
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.KeyguardKeyboardActivity target) {
    target.inputEdt = null;
    target.keyboardView = null;
  }
}
