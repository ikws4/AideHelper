package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.XC_MethodReplacement;
import me.tvcfish.xposed.aidehelper.BuildConfig;
import me.tvcfish.xposed.util.XHelper;

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
    Class clazz = XHelper.findClass(BuildConfig.APPLICATION_ID + ".activity.MainActivity");
    findAndHookMethod(clazz, "isEnabledModel", new XC_MethodReplacement() {
      @Override
      protected Object replaceHookedMethod(MethodHookParam param) {
        return null;
      }
    });
  }
}
