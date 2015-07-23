// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class KeyguardKeyboardActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.KeyguardKeyboardActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427428, "field 'keyguardAppLock'");
    target.keyguardAppLock = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131427429, "field 'keyguardChangeBtn' and method 'changeMode'");
    target.keyguardChangeBtn = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.changeMode(p0);
        }
      });
    view = finder.findRequiredView(source, 2131427431, "field 'passwdEdt'");
    target.passwdEdt = (bitman.ay27.watchdog.widget.PasswdEdt) view;
    view = finder.findRequiredView(source, 2131427441, "field 'keyBtnCancel'");
    target.keyBtnCancel = (android.widget.Button) view;
    view = finder.findRequiredView(source, 2131427443, "field 'keyBtnBack'");
    target.keyBtnBack = (android.widget.Button) view;
    target.btns = Finder.arrayOf(
        (android.widget.Button) finder.findRequiredView(source, 2131427442, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427432, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427433, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427434, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427435, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427436, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427437, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427438, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427439, "btns"),
        (android.widget.Button) finder.findRequiredView(source, 2131427440, "btns")
    );  }

  public static void reset(bitman.ay27.watchdog.ui.activity.KeyguardKeyboardActivity target) {
    target.keyguardAppLock = null;
    target.keyguardChangeBtn = null;
    target.passwdEdt = null;
    target.keyBtnCancel = null;
    target.keyBtnBack = null;
    target.btns = null;
  }
}
