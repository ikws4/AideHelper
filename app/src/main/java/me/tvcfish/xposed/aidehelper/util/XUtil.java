package me.tvcfish.xposed.aidehelper.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.tvcfish.xposed.aidehelper.BuildConfig;

public class XUtil {

  //Log控制
  private static final boolean isShowLog = true;

  //Print控制
  private static final boolean isPrint = true;

  private static LoadPackageParam sLoadPackageParam;

  //配置参数
  private static WeakReference<XSharedPreferences> xSharedPreferences = new WeakReference<>(null);


  /**
   * 初始化
   */
  public static void init(LoadPackageParam loadPackageParam) {
    sLoadPackageParam = loadPackageParam;
  }

  /**
   * 打印日志
   */
  public static void log(String text) {
    if (!isShowLog) {
      return;
    }
    XposedBridge.log("AideHelper->" + text);
  }

  /**
   * 打印日志
   */
  public static void log(Throwable t) {
    if (!isShowLog) {
      return;
    }
    if (!BuildConfig.DEBUG) {
      return;
    }
    XposedBridge.log(t);
  }

  /**
   * 获取XSharedPreferences
   */
  public static XSharedPreferences getPref() {
    XSharedPreferences preferences = xSharedPreferences.get();
    if (preferences == null) {
      preferences = new XSharedPreferences(BuildConfig.APPLICATION_ID);
      preferences.makeWorldReadable();
      preferences.reload();
      xSharedPreferences = new WeakReference<>(preferences);
    } else {
      preferences.reload();
    }
    return preferences;
  }

  /**
   * 获取ClassLoader
   */
  public static ClassLoader getClassLoader() {
    return sLoadPackageParam.classLoader;
  }

  /**
   * 获取Field
   *
   * @param className className
   * @param fieldName fieldName
   * @return 获取到的Field
   */
  public static Field getField(String className, String fieldName) throws NoSuchFieldException {
    Class clazz = XposedHelpers.findClass(className, sLoadPackageParam.classLoader);
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }

  /**
   * 获取Field
   */
  public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }

  /**
   * 测试用，打印Bundle
   */
  public static void printBundle(Bundle bundle) {
    if (!isPrint) {
      return;
    }
    for (String key : bundle.keySet()) {
      XUtil.log("bundle.key: " + key + ", value: " + bundle.get(key));
    }
  }

  /**
   * 测试用，打印视图树
   */
  public static void printTreeView(Activity activity) {
    if (!isPrint) {
      return;
    }
    View rootView = activity.getWindow().getDecorView();
    printTreeView(rootView);
  }

  /**
   * 测试用，打印视图树
   */
  private static void printTreeView(View rootView) {
    if (!isPrint) {
      return;
    }

    if (rootView instanceof ViewGroup) {
      ViewGroup parentView = (ViewGroup) rootView;
      for (int i = 0; i < parentView.getChildCount(); i++) {
        printTreeView(parentView.getChildAt(i));
      }
    } else {
      XUtil.log("view: " + rootView.getId() + ", class: " + rootView.getClass());
      // any view if you want something different
      if (rootView instanceof EditText) {
        XUtil.log("edit:" + rootView.getTag() + "， hint: " + ((EditText) rootView).getHint());
      } else if (rootView instanceof TextView) {
        XUtil.log("text:" + ((TextView) rootView).getText().toString());
      }
    }
  }

  /**
   * 打印所有方法
   */
  public static void printMethods(Class clazz) {
    if (!isPrint) {
      return;
    }

    for (Method method : clazz.getDeclaredMethods()) {
      XUtil.log(clazz.getName() + ":" + method);
    }
  }

  /**
   * 打印所有Field
   */
  public static void printFields(Class clazz) {
    if (!isPrint) {
      return;
    }

    for (Field field : clazz.getDeclaredFields()) {
      XUtil.log(clazz.getName() + ":" + field);
    }
  }
}
