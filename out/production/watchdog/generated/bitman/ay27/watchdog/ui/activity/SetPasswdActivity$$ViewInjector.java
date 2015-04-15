// Generated code from Butter Knife. Do not modify!
package bitman.ay27.watchdog.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetPasswdActivity$$ViewInjector {
  public static void inject(Finder finder, final bitman.ay27.watchdog.ui.activity.SetPasswdActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493012, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131493014, "field 'newPasswdEdt'");
    target.newPasswdEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131493013, "field 'oldPasswdEdt'");
    target.oldPasswdEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131493015, "field 'confirmEdt'");
    target.confirmEdt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131493016, "field 'okBtn'");
    target.okBtn = (android.widget.Button) view;
    view = finder.findRequiredView(source, 2131492927, "field 'keyboardView'");
    target.keyboardView = (android.inputmethodservice.KeyboardView) view;
  }

  public static void reset(bitman.ay27.watchdog.ui.activity.SetPasswdActivity target) {
    target.toolbar = null;
    target.newPasswdEdt = null;
    target.oldPasswdEdt = null;
    target.confirmEdt = null;
    target.okBtn = null;
    target.keyboardView = null;
  }
}
