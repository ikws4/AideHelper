package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.XC_MethodReplacement;
import me.tvcfish.xposed.util.XHelper;

enum UnlockPremium {

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
    return XHelper.getSharedPreferences().getBoolean("unlock_premium", false);
  }

  /**
   * Hook代码实现
   */
  private void hookTarget() {
    Class clazz = XHelper.findClass("pc");
    String[] strings = new String[]{"a8", "FH", "VH", "tp", "EQ", "QX", "XL", "j3"};
    for (String value : strings) {
      findAndHookMethod(clazz, value, new XC_MethodReplacement() {
        @Override
        protected Object replaceHookedMethod(MethodHookParam param) {
          return true;
        }
      });
    }
  }
}
