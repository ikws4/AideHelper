package me.tvcfish.xposed.aidehelper.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import com.canking.minipay.Config;
import com.canking.minipay.MiniPayUtils;
import java.io.File;
import me.tvcfish.xposed.aidehelper.BuildConfig;
import me.tvcfish.xposed.aidehelper.R;
import me.tvcfish.xposed.aidehelper.util.FileUtil;

@SuppressLint("ExportedPreferenceActivity")
public class MainActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
    addPreferencesFromResource(R.xml.main_preference);
    setWorldReadable();
    updateLogDialog(false);
    FileUtil.copyAssetsFileToSdcard(this, "templates");
  }

  /**
   * 初始化操作
   */
  private void init() {
    // assets/templates文件夹创建
    FileUtil.mkdirs(FileUtil.getExternalFilesDirPath(this) + "/assets/templates");
  }


  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    switch (preference.getKey()) {
      case "donation":
        Config config = new Config.Builder("FKX03835LYVF3XNJFBLVB0", R.drawable.alipay,
            R.drawable.wechat)
            .build();
        MiniPayUtils.setupPay(this, config);
        break;
      case "update_log":
        updateLogDialog(true);
        break;
    }
    return super.onPreferenceTreeClick(preferenceScreen, preference);
  }

  /**
   * 更新日志对话框
   *
   * @param isShow 是否   直接显示（false就按versionCode进行判断）
   */
  private void updateLogDialog(boolean isShow) {

    final SharedPreferences sharedPreferences = getSharedPreferences("app_version",
        Context.MODE_PRIVATE);
    int versionCode = sharedPreferences.getInt("versionCode", 10000);

    if (BuildConfig.VERSION_CODE > versionCode || isShow) {
      //读取raw下的update_log文件
      String updateLog = FileUtil.readTextFromRaw(this, R.raw.update_log);

      //对话框
      new AlertDialog.Builder(this)
          .setTitle(R.string.pref_update_log)
          .setMessage(updateLog)
          .setCancelable(false)
          .setNegativeButton(R.string.app_ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              Editor editor = sharedPreferences.edit();
              editor.putInt("versionCode", BuildConfig.VERSION_CODE);
              editor.apply();
              //判断是否开启模块，使用Hook改变方法内容
              isEnabledModel();
            }
          })
          .show();
    } else {
      //判断是否开启模块，使用Hook改变方法内容
      isEnabledModel();
    }

  }

  /**
   * 判断是否开启模块，使用Hook改变方法内容 EnabledHookModel.java
   */
  private void isEnabledModel() {
    new AlertDialog.Builder(this)
        .setTitle(R.string.main_activity_hook_dialog_title)
        .setMessage(R.string.main_activity_hook_dialog_message)
        .setCancelable(false)
        .setPositiveButton(R.string.app_exit, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        })
        .setNegativeButton(R.string.app_get_vxp, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/android-hacker/VirtualXposed/releases"));
            startActivity(intent);
            finish();
          }
        }).show();
  }

  /**
   * 把SharedPreferences数据设置为公开
   */
  @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
  @SuppressLint({"SetWorldReadable", "WorldReadableFiles"})
  private void setWorldReadable() {
    File dataDir = new File(getApplicationInfo().dataDir);
    File prefsDir = new File(dataDir, "shared_prefs");
    File prefsFile = new File(prefsDir, BuildConfig.APPLICATION_ID + "_preferences.xml");
    if (prefsFile.exists()) {
      for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
        file.setReadable(true, false);
        file.setExecutable(true, false);
      }
    }
  }
}
