// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558497, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131558499, "field 'userNameTxv'");
    target.userNameTxv = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131558500, "field 'userSummer'");
    target.userSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131558502, "field 'sdTitle'");
    target.sdTitle = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131558503, "field 'sdSummer'");
    target.sdSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131558485, "field 'bootLoaderSwitch'");
    target.bootLoaderSwitch = (com.kyleduo.switchbutton.SwitchButton) view;
    view = finder.findRequiredView(source, 2131558486, "field 'keyguardSwitch'");
    target.keyguardSwitch = (com.kyleduo.switchbutton.SwitchButton) view;
    view = finder.findRequiredView(source, 2131558487, "field 'usbSwitch'");
    target.usbSwitch = (com.kyleduo.switchbutton.SwitchButton) view;
    view = finder.findRequiredView(source, 2131558504, "field 'formatPanel' and method 'formatClick'");
    target.formatPanel = view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.formatClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558505, "field 'formatTitle'");
    target.formatTitle = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131558506, "field 'formatSummer'");
    target.formatSummer = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131558498, "method 'loginClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.loginClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558501, "method 'sdPanelClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.sdPanelClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558488, "method 'drawPanelClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.drawPanelClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558507, "method 'setPasswdPanelClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.setPasswdPanelClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558508, "method 'watchClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.watchClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558510, "method 'nfcClick'");
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.nfcClick(p0);
        }
      });
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.MainActivity target) {
    target.toolbar = null;
    target.userNameTxv = null;
    target.userSummer = null;
    target.sdTitle = null;
    target.sdSummer = null;
    target.bootLoaderSwitch = null;
    target.keyguardSwitch = null;
    target.usbSwitch = null;
    target.formatPanel = null;
    target.formatTitle = null;
    target.formatSummer = null;
  }
}
