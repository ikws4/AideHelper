package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.newInstance;

import de.robv.android.xposed.XC_MethodHook;
import me.tvcfish.xposed.aidehelper.util.XUtil;

enum GradleCompletionExpand {

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
    return XUtil.getPref().getBoolean("gradle_completion_expand", false);
  }

  /**
   * Hook代码实现
   */
  private void hookTarget() {
    Class clazz = findClass("com.aide.engine.b$b$b", XUtil.getClassLoader());
    findAndHookMethod(clazz, "FH", String.class, new XC_MethodHook() {
      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        if (param.args[0].equals("compile 'com.google.android.gms:play-services:+'")) {
          Object thisObject = param.thisObject;
          Class SourceEntity = findClass("com.aide.engine.SourceEntity", XUtil.getClassLoader());
          //api和implementation
          String[] strings = new String[]{"api", "implementation"};
          customGradleCompletion(thisObject, SourceEntity, strings);

          //自定义
          String customs = XUtil.getPref().getString("gradle_completion_custom", "");
          String[] split = customs.split("\n");
          customGradleCompletion(thisObject, SourceEntity, split);
        }
      }
    });
  }

  /**
   * 自定义补全
   */
  private void customGradleCompletion(Object thisObject, Class SourceEntityClass,
      String[] strings) {
    for (String value : strings) {
      Object SourceEntity = newInstance(SourceEntityClass, value);
      callMethod(thisObject, "j6", SourceEntity);
    }
  }
}
