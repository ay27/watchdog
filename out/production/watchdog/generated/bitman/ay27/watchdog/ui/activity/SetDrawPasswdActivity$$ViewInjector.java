// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetDrawPasswdActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.SetDrawPasswdActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558560, "field 'canvas'");
    target.canvas = (bitman.ay27.watchdog.ui.DrawingCanvas) view;
    view = finder.findRequiredView(source, 2131558561, "field 'widgetView'");
    target.widgetView = view;
    view = finder.findRequiredView(source, 2131558562, "field 'changeImgBtn' and method 'changeImgClick'");
    target.changeImgBtn = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.changeImgClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558563, "field 'saveBtn' and method 'saveClick'");
    target.saveBtn = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.saveClick(p0);
        }
      });
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.SetDrawPasswdActivity target) {
    target.canvas = null;
    target.widgetView = null;
    target.changeImgBtn = null;
    target.saveBtn = null;
  }
}
