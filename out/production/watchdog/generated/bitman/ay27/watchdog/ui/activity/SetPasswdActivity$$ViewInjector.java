// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetPasswdActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.SetPasswdActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558589, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131558592, "field 'newPasswdEdt'");
    target.newPasswdEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131558590, "field 'oldPasswdEdt'");
    target.oldPasswdEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131558593, "field 'confirmEdt'");
    target.confirmEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131558595, "field 'okBtn' and method 'okClick'");
    target.okBtn = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.okClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558478, "field 'keyboardView'");
    target.keyboardView = (android.inputmethodservice.KeyboardView) view;
    view = finder.findRequiredView(source, 2131558591, "field 'oldPasswdError'");
    target.oldPasswdError = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131558594, "field 'newPasswdError'");
    target.newPasswdError = (android.widget.TextView) view;
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.SetPasswdActivity target) {
    target.toolbar = null;
    target.newPasswdEdt = null;
    target.oldPasswdEdt = null;
    target.confirmEdt = null;
    target.okBtn = null;
    target.keyboardView = null;
    target.oldPasswdError = null;
    target.newPasswdError = null;
  }
}
