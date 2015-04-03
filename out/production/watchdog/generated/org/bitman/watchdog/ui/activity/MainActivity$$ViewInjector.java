// Generated code from Butter Knife. Do not modify!
package org.bitman.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final org.bitman.watchdog.ui.activity.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361858, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
  }

  public static void reset(org.bitman.watchdog.ui.activity.MainActivity target) {
    target.toolbar = null;
  }
}
