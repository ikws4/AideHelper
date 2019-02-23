package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import me.tvcfish.xposed.util.XHelper;

enum ActionBarIcon {

  INSTANCE;

  /**
   * 开始Hook
   */
  public void startHook() {
    if (!isOpen()) {
      return;
    }
    hookTarget();
  }

  /**
   * 是否开启此功能
   *
   * @return boolean
   */
  private boolean isOpen() {
    return XHelper.getSharedPreferences().getBoolean("action_bar_icon", false);
  }

  /**
   * Hook代码实现
   */
  private void hookTarget() {
    Class clazz = XHelper.findClass("com.aide.ui.MainActivity");
    findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {

      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        Activity activity = (Activity) param.thisObject;
        activity.getActionBar().setDisplayShowHomeEnabled(false);
      }
    });
  }
}
