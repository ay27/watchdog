// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361861, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131361862, "field 'loginPanel'");
    target.loginPanel = view;
    view = finder.findRequiredView(source, 2131361863, "field 'setWatchPanel'");
    target.setWatchPanel = view;
    view = finder.findRequiredView(source, 2131361864, "field 'setPasswdPanel'");
    target.setPasswdPanel = view;
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.MainActivity target) {
    target.toolbar = null;
    target.loginPanel = null;
    target.setWatchPanel = null;
    target.setPasswdPanel = null;
  }
}
