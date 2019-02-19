package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import de.robv.android.xposed.XC_MethodHook;
import java.io.File;
import java.io.FileInputStream;
import me.tvcfish.xposed.aidehelper.util.XUtil;

enum SwitchVersion {
  INSTANCE;

  /**
   * 开始Hook
   */
  public void startHook() {
    if (isOpen().equals("com.aide.ui")) {
      return;
    }
    hookMethod();
  }

  /**
   * 是否开启此功能
   *
   * @return boolean
   */
  private String isOpen() {
    return XUtil.getPref().getString("switch_version", "com.aide.ui");
  }

  /**
   * Hook代码实现
   */
  private void hookMethod() {
    Class clazz = findClass("com.aide.ui.f", XUtil.getClassLoader());
    findAndHookMethod(clazz, "j6", String.class, new XC_MethodHook() {
      @Override
      protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        param.args[0] = isOpen();
      }
    });

    //模版添加
    if (!isOpen().equals("com.aide.ui")) {
      Class assetManagerClass = findClass("android.content.res.AssetManager",
          XUtil.getClassLoader());
      findAndHookMethod(assetManagerClass, "open", String.class, new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
          super.beforeHookedMethod(param);
          String fileName = param.args[0].toString();
          String aideHelper = "/storage/emulated/0/Android/data/me.tvcfish.xposed.aidehelper/files/assets/templates/";
          switch (fileName) {
            case "templates/WebsiteBootstrap.zip":
              File websiteBootstrap = new File(aideHelper + "WebsiteBootstrap.zip");
              param.setResult(new FileInputStream(websiteBootstrap));
              break;
            case "templates/HelloJavaScript.zip":
              File helloJavaScript = new File(aideHelper + "HelloJavaScript.zip");
              param.setResult(new FileInputStream(helloJavaScript));
              break;
            case "templates/PhoneGapAppNew.zip":
              File phoneGapAppNew = new File(aideHelper + "PhoneGapAppNew.zip");
              param.setResult(new FileInputStream(phoneGapAppNew));
              break;
          }
        }
      });
    }
  }

}
