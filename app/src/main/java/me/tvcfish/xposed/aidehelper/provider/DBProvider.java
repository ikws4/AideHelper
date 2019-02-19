package me.tvcfish.xposed.aidehelper.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tvcfish.xposed.aidehelper.model.MethodCompletion;
import org.litepal.LitePal;

public class DBProvider extends ContentProvider {

  public static final String CODE_NOTES = "1";
  public static final String TRANSLATE = "2";

  public static final int TRANSLATE_STATE_FAIL = -2;
  public static final int TRANSLATE_STATE_INIT = -1;
  public static final int TRANSLATE_STATE_OK = 1;

  private static final String AUTHORITY = "me.tvcfish.xposed.aidehelper.provider.DBProvider";
  public static final String TRANSLATION_CONTENT_URL =
      "content://" + AUTHORITY + "/MethodCompletion";
  private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    URI_MATCHER.addURI(AUTHORITY, "MethodCompletion", 0);
  }


  @Override
  public boolean onCreate() {
    return false;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
      @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    return null;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    return null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection,
      @Nullable String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
      @Nullable String[] selectionArgs) {
    return 0;
  }

  @Nullable
  @Override
  public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
    switch (method) {
      case "save":
        assert extras != null;
        save(extras);
        break;
      case "query":
        assert extras != null;
        return query(extras);
      case "update":
        assert extras != null;
        assert arg != null;
        update(extras, arg);
        break;
    }
    return super.call(method, arg, extras);
  }

  /**
   * 保存到数据库
   */
  private void save(Bundle bundle) {
    MethodCompletion method = new MethodCompletion();
    method.setEnglish(bundle.getString("english"));
    method.setChinese(bundle.getString("chinese"));
    method.setNotes(bundle.getString("notes"));
    method.setState(bundle.getInt("state"));
    method.save();
  }

  /**
   * 查询
   */
  private Bundle query(Bundle bundle) {
    //获取值用来做查询条件
    String english = bundle.getString("english");
    //查询数据库
    MethodCompletion method = LitePal.where("english=?", english).find(MethodCompletion.class)
        .get(0);

    //把获取的数据填充到Bundle
    bundle.putString("english", method.getEnglish());
    bundle.putString("chinese", method.getChinese());
    bundle.putString("notes", method.getNotes());
    bundle.putInt("state", method.getState());
    return bundle;
  }

  /**
   * 更新
   */
  private void update(Bundle bundle, String arg) {
    //条件
    String english = bundle.getString("english");

    //参数
    ContentValues values = new ContentValues();
    switch (arg) {
      case CODE_NOTES:
        String notes = bundle.getString("notes");
        values.put("notes", notes);
        break;
      case TRANSLATE:
        if (bundle.getInt("state") == TRANSLATE_STATE_OK) {
          //翻译，添加中文翻译
          String chinese = bundle.getString("chinese");
          int state = bundle.getInt("state");
          values.put("chinese", chinese);
          values.put("state", state);
        }
        int state = bundle.getInt("state");
        values.put("state", state);

        break;
    }
    //更新
    LitePal.updateAll(MethodCompletion.class, values, "english=?", english);
  }


}
