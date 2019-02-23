package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import me.tvcfish.xposed.aidehelper.util.XUtil;

enum  AutoRotate {

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
    return XUtil.getPref().getBoolean("auto_rotate", true);
  }

  /**
   * Hook代码实现
   */
  private void hookMethod(){
    findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        Activity activity = (Activity) param.thisObject;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      }
    });
  }
}
