// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492937, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131492941, "field 'bootLoaderSummer'");
    target.bootLoaderSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131492943, "field 'sdEncryptSummer'");
    target.sdEncryptSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131492942, "method 'bootLoaderCheckChanged'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.bootLoaderCheckChanged(p0, p1);
        }
      });
    view = finder.findRequiredView(source, 2131492944, "method 'sdEncryptCheckChanged'");
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.sdEncryptCheckChanged(p0, p1);
        }
      });
    view = finder.findRequiredView(source, 2131492938, "method 'loginClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.loginClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492945, "method 'drawPanelClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.drawPanelClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492946, "method 'secPasswdPanelClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.secPasswdPanelClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492951, "method 'aboutClick'");
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
    target.bootLoaderSummer = null;
    target.sdEncryptSummer = null;
  }
}
