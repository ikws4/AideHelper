package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import me.tvcfish.xposed.util.XHelper;

enum SearchPage {

  INSTANCE;

  /**
   * 开始Hook
   */
  public void startHook() {
    if (!isOpen()) {
      return;
    }
    hookMethod();
  }

  /**
   * 是否开启此功能
   *
   * @return boolean
   */
  private boolean isOpen() {
    return XHelper.getSharedPreferences().getBoolean("search_page_adjustment", false);
  }

  /**
   * Hook代码实现
   */
  private void hookMethod() {
    Class clazz = XHelper.findClass("com.aide.ui.browsers.FindResultTextView");
    Class findResult = XHelper.findClass("com.aide.engine.FindResult");
    findAndHookMethod(clazz, "setContent", findResult, new XC_MethodHook() {
      @Override
      protected void afterHookedMethod(MethodHookParam param) {
        TextView textView = (TextView) param.thisObject;
        textView.setSingleLine(false);
      }
    });
  }
}
