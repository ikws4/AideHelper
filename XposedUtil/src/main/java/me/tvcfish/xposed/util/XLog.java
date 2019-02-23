package me.tvcfish.xposed.util;

import de.robv.android.xposed.XposedBridge;

/**
 * 对XposedBridge.log的封住
 *
 * @date: 2019/2/23
 * @author: TvcFish
 * @email: zhipingne@gmail.com
 */
public class XLog {

  /**
   * 打印日志
   *
   * @param tag TAG
   * @param message 打印信息
   */
  public static void show(String tag, String message) {
    //如果是release，则不打印
    if (!BuildConfig.DEBUG) {
      XposedBridge.log(tag + "->" + message);
    }
  }

  /**
   * 打印日志
   *
   * @param tag TAG
   * @param message 打印异常信息
   */
  public static void show(String tag, Throwable message) {
    //如果是release，则不打印
    if (!BuildConfig.DEBUG) {
      XposedBridge.log(tag + "->" + message);
    }
  }

}
