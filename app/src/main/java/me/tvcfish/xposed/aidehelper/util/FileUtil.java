package me.tvcfish.xposed.aidehelper.util;

import android.app.Activity;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

  /**
   * 从Raw读取文本文件
   * @param activity 用来获取Resources
   * @param rawId raw文件Id
   * @return text content
   */
  public static String readTextFromRaw(Activity activity, int rawId) {
    try {
      InputStream inputStream = activity.getResources().openRawResource(rawId);
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      return new String(buffer);
    } catch (IOException e) {
      e.printStackTrace();
      return "操作异常:" + e.getMessage();
    }
  }

  /**
   * 创建文件夹
   */
  public static void mkdirs(String filePath) {
    File file = new File(filePath);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  /**
   * 获取应用外置储存路径
   */
  public static String getExternalFilesDirPath(Activity activity) {
    File file = activity.getExternalFilesDir("");
    if (file != null) {
      return file.getPath();
    } else {
      String path = Environment.getExternalStorageDirectory().getAbsolutePath();
      File appPath = new File(path + "Android/data/me.tvcfish.xposed.aidehelper/files");
      if (!appPath.exists()) {
        appPath.mkdirs();
      }
      return appPath.getPath();
    }
  }

  /**
   * 把assets文件复制到SD卡
   *
   * @param activity 用来获取assets资源
   * @param filePath assets文件路径
   */
  public static void copyAssetsFileToSdcard(Activity activity, String filePath) {
    try {
      String[] strings = activity.getResources().getAssets().list(filePath);

      if (strings != null) {
        for (String value : strings) {
          InputStream is = activity.getResources().getAssets().open(filePath + "/" + value);

          File file = new File(
              getExternalFilesDirPath(activity) + "/assets/" + filePath + "/" + value);

          //如果文件存在，退出该次操作
          if (file.exists()) {
            continue;
          }

          FileOutputStream fos = new FileOutputStream(file);
          byte[] buffer = new byte[1024];
          int byteCount;
          while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
            fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
          }
          fos.flush();//刷新缓冲区
          is.close();
          fos.close();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
