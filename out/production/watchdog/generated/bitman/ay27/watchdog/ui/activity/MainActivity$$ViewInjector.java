// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427401, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131427402, "method 'loginClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.loginClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131427415, "method 'aboutClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.aboutClick(p0);
        }
      });
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.MainActivity target) {
    target.toolbar = null;
  }
}
