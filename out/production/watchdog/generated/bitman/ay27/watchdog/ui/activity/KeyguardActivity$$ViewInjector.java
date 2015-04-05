// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class KeyguardActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.KeyguardActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361857, "field 'charKeyboard'");
    target.charKeyboard = (bitman.ay27.watchdog.widget.keyboard.RandomCharKeyboard) view;
    view = finder.findRequiredView(source, 2131361856, "field 'numericKeyboard'");
    target.numericKeyboard = (bitman.ay27.watchdog.widget.keyboard.RandomNumericKeyboard) view;
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.KeyguardActivity target) {
    target.charKeyboard = null;
    target.numericKeyboard = null;
  }
}
