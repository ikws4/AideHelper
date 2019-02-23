package me.tvcfish.xposed.util;

import static de.robv.android.xposed.XposedHelpers.findClassIfExists;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.annotation.NonNull;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Xposed辅助类
 *
 * @date: 2019/2/23
 * @author: TvcFish
 * @email: zhipingne@gmail.com
 */
public class XHelper {

  private static LoadPackageParam sLoadPackageParam;
  //配置参数
  private static WeakReference<XSharedPreferences> xSharedPreferences = new WeakReference<>(null);
  //包名
  private static String sAppPackageName;

  /**
   * 初始化操作
   *
   * @param loadPackageParam 用来加载类
   */
  public static void init(@NonNull LoadPackageParam loadPackageParam,String appPackageName) {
    sLoadPackageParam = loadPackageParam;
    sAppPackageName = appPackageName;
  }

  /**
   * 获取ClassLoader
   *
   * @return ClassLoader
   * @throws NullPointerException 如果sLoadPackageParam为空，那么抛出一个异常，提示调用init方法后在调用此方法
   */
  private static ClassLoader getClassLoader() throws NullPointerException {
    if (sLoadPackageParam == null) {
      throw new NullPointerException(
          "loadPackageParam is null, please invoked init method, then use the method.");
    } else {
      return sLoadPackageParam.classLoader;
    }
  }

  /**
   * 获取Class
   *
   * @param name 类的路径
   * @return Class
   */
  public static Class findClass(String name) {
    return XposedHelpers.findClassIfExists(name, getClassLoader());
  }

  /**
   * 创建一个实例
   *
   * @param className 类名
   * @param args 类参数
   * @return Object
   */
  public static Object newInstance(String className, Object... args) {
    return XposedHelpers.newInstance(findClass(className), args);
  }

  /**
   * 调用静态方法
   *
   * @param className 类名
   * @param methodName 方法名
   * @param args 参数
   * @return Object
   */
  public static Object callStaticMethod(String className, String methodName, Object... args) {
    return XposedHelpers.callStaticMethod(findClass(className), methodName, args);
  }

  /**
   * 获取Filed
   *
   * @param className 类名
   * @param name filed的名字
   * @return Filed
   */
  public static Field getField(String className, String name) throws NoSuchFieldException {
    Class clazz = findClassIfExists(className, getClassLoader());
    Field field = clazz.getField(name);
    field.setAccessible(true);
    return field;
  }

  /**
   * 获取Filed
   *
   * @param clazz Filed所在的类
   * @param name filed的名字
   * @return Filed
   */
  public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
    Field field = clazz.getField(name);
    field.setAccessible(true);
    return field;
  }

  /**
   * 获取SharedPreferences
   *
   * @return preferences
   */
  public static XSharedPreferences getSharedPreferences() {
    XSharedPreferences preferences = xSharedPreferences.get();
    if (preferences == null) {
      preferences = new XSharedPreferences(sAppPackageName);
      preferences.makeWorldReadable();
      preferences.reload();
      xSharedPreferences = new WeakReference<>(preferences);
    } else {
      preferences.reload();
    }
    return preferences;
  }

  /**
   * 使shared_prefs文件夹下所以preferences文件可读
   *
   * @param activity 用来获取App内置存储路径
   */
  @SuppressLint("SetWorldReadable")
  public static void setPreferencesReadable(Activity activity) {
    File dataDir = new File(activity.getApplicationInfo().dataDir);
    File prefsDir = new File(dataDir, "shared_prefs");
    File[] arrayOfFiles = prefsDir.listFiles();
    for (File prefsFile : arrayOfFiles) {
      if (prefsFile.exists()) {
        for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
          file.setReadable(true, false);
          file.setExecutable(true, false);
        }
      }
    }
  }
}
