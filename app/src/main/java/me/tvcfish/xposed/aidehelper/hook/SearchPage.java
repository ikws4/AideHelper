package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import me.tvcfish.xposed.aidehelper.util.XUtil;

enum  SearchPage {

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
    return XUtil.getPref().getBoolean("search_page_adjustment", false);
  }

  /**
   * Hook代码实现
   */
  private void hookMethod() {
    Class clazz = findClass("com.aide.ui.browsers.FindResultTextView",
        XUtil.getClassLoader());
    Class findResult = findClass("com.aide.engine.FindResult", XUtil.getClassLoader());
    findAndHookMethod(clazz, "setContent", findResult, new XC_MethodHook() {
      @Override
      protected void afterHookedMethod(MethodHookParam param) {
        TextView textView = (TextView) param.thisObject;
        textView.setSingleLine(false);
      }
    });
  }
}
