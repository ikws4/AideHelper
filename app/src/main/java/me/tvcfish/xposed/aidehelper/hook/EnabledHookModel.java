package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import de.robv.android.xposed.XC_MethodReplacement;
import me.tvcfish.xposed.aidehelper.util.XUtil;

enum EnabledHookModel {

  INSTANCE;

  /**
   * 开始Hook
   */
  public void startHook() {
    hookTarget();
  }

  /**
   * Hook代码实现
   */
  private void hookTarget() {
    Class clazz = findClass("me.tvcfish.xposed.aidehelper.activity.MainActivity",
        XUtil.getClassLoader());
    findAndHookMethod(clazz, "isEnabledModel", new XC_MethodReplacement() {
      @Override
      protected Object replaceHookedMethod(MethodHookParam param) {
        return null;
      }
    });
  }
}
