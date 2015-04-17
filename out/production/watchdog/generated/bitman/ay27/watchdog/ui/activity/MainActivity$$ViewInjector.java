// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492939, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131492943, "field 'bootLoaderSummer'");
    target.bootLoaderSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131492944, "field 'bootLoaderSwitch' and method 'bootLoaderCheckChanged'");
    target.bootLoaderSwitch = (com.kyleduo.switchbutton.SwitchButton) view;
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.bootLoaderCheckChanged(p0, p1);
        }
      });
    view = finder.findRequiredView(source, 2131492945, "field 'sdEncryptSummer'");
    target.sdEncryptSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131492946, "field 'sdEncryptSwitch' and method 'sdEncryptCheckChanged'");
    target.sdEncryptSwitch = (com.kyleduo.switchbutton.SwitchButton) view;
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.sdEncryptCheckChanged(p0, p1);
        }
      });
    view = finder.findRequiredView(source, 2131492947, "field 'keyguardSummer'");
    target.keyguardSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131492948, "field 'keyguardSwitch' and method 'keyguardCheckChanged'");
    target.keyguardSwitch = (com.kyleduo.switchbutton.SwitchButton) view;
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.keyguardCheckChanged(p0, p1);
        }
      });
    view = finder.findRequiredView(source, 2131492949, "field 'usbSummer'");
    target.usbSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131492950, "field 'usbSwitch' and method 'usbCheckChanged'");
    target.usbSwitch = (com.kyleduo.switchbutton.SwitchButton) view;
    ((android.widget.CompoundButton) view).setOnCheckedChangeListener(
      new android.widget.CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(
          android.widget.CompoundButton p0,
          boolean p1
        ) {
          target.usbCheckChanged(p0, p1);
        }
      });
    view = finder.findRequiredView(source, 2131492940, "method 'loginClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.loginClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492951, "method 'drawPanelClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.drawPanelClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492952, "method 'setPasswdPanelClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.setPasswdPanelClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492957, "method 'aboutClick'");
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
    target.bootLoaderSwitch = null;
    target.sdEncryptSummer = null;
    target.sdEncryptSwitch = null;
    target.keyguardSummer = null;
    target.keyguardSwitch = null;
    target.usbSummer = null;
    target.usbSwitch = null;
  }
}
