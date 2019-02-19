package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import de.robv.android.xposed.XC_MethodReplacement;
import me.tvcfish.xposed.aidehelper.util.XUtil;

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
    return XUtil.getPref().getBoolean("unlock_premium", false);
  }

  /**
   * Hook代码实现
   */
  private void hookTarget() {
    Class clazz = findClass("pc", XUtil.getClassLoader());
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
