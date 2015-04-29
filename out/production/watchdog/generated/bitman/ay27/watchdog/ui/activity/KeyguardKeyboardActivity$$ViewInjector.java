// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class KeyguardKeyboardActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.KeyguardKeyboardActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558473, "field 'inputEdt'");
    target.inputEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131558469, "field 'keyboardView'");
    target.keyboardView = (android.inputmethodservice.KeyboardView) view;
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.KeyguardKeyboardActivity target) {
    target.inputEdt = null;
    target.keyboardView = null;
  }
}
