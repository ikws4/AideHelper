package me.tvcfish.xposed.aidehelper.util;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import me.tvcfish.xposed.aidehelper.provider.DBProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TranslateUtil {

  private static final String from = "en";
  private static final String to = "zh-CHS";
  private static final String pid = "85c2ddface5a4e913fc22fa34df0783c";
  private static final String salt = "1434918491";


  public static void getResult(final Context context, final String q) {
    String en = wordSegmentation(q.toCharArray(), 0,
        q.length(), false, "");
    OkHttpClient client = new OkHttpClient();
    final FormBody.Builder builder = new FormBody.Builder();
    builder.add("from", from)
        .add("to", to)
        .add("pid", pid)
        .add("q", en)
        .add("salt", salt)
        .add("sign", getMD5(pid + en + salt + "d6888569844199aa4d7b6353b5396095"));
    final Request request = new Request.Builder()
        .url("http://fanyi.sogou.com/reventondc/api/sogouTranslate")
        .addHeader("content-type", "application/x-www-form-urlencoded")
        .addHeader("accept", "application/json")
        .post(builder.build())
        .build();
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        //数据库操作，翻译失败.添加标记
        Uri uri = Uri
            .parse(DBProvider.TRANSLATION_CONTENT_URL);
        Bundle bundle = new Bundle();
        bundle.putString("english", q);
        bundle.putInt("state", DBProvider.TRANSLATE_STATE_FAIL);
        context.getContentResolver().call(uri, "update", DBProvider.TRANSLATE, bundle);
      }

      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        assert response.body() != null;
        JSONTokener jsonToken = new JSONTokener(response.body().string());
        try {
          JSONObject translation = new JSONObject(jsonToken);
          String chinese = translation.getString("translation");
          //数据库操作，更新数据
          Uri uri = Uri
              .parse(DBProvider.TRANSLATION_CONTENT_URL);
          Bundle bundle = new Bundle();
          bundle.putString("english", q);
          bundle.putString("chinese", chinese);
          bundle.putInt("state", DBProvider.TRANSLATE_STATE_OK);
          context.getContentResolver().call(uri, "update", DBProvider.TRANSLATE, bundle);
        } catch (JSONException e) {
          XUtil.log(e);
        }
      }
    });
  }

  /**
   * 分词
   *
   * @param word 原词组
   * @param index 索引 0
   * @param wordLength 词组长度
   * @param result 结果
   * @return result
   */
  private static String wordSegmentation(char[] word, int index, int wordLength, boolean mark,
      String result) {
    if (index == wordLength) {
      return result.trim();
    } else {
      char ch = word[index];
      //如果是字母,继续判断大小写
      if (Character.isLetter(ch)) {
        //是大写
        if (Character.isUpperCase(ch)) {
          if (index > 0 && index < wordLength) {
            boolean before = Character.isLowerCase(word[index - 1]);
            boolean after = Character.isLowerCase(word[index + 1]);
            if (before && after) {
              result += " " + ch;
            } else {
              result += ch;
            }
          } else {
            result += ch;
          }
        } else {
          result += ch;
        }
      } else {
        //后面是类包名，不分词，直接退出
        if (ch == '-') {
          if (word[index - 1] == ' ' && word[index + 1] == ' ') {
            return result.trim();
          } else {
            result += '-';
          }
        } else if (ch == '(') { //是否为方法
          mark = true;
          result += "(";
        } else if (ch == '.') {
          result += '-';
        } else if (mark && ch == ' ') {
          if (word[index - 1] == ',') {
            result += ch;
          } else {
            result += ":";
          }
        }
        //不是字母，前后加一个空格
        else {
          result += " " + ch + " ";
        }
      }
      return wordSegmentation(word, index + 1, wordLength, mark, result);
    }
  }

  private static String getMD5(String text) {
    String s = null;
    char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'};// 用来将字节转换成16进制表示的字符
    try {
      java.security.MessageDigest md = java.security.MessageDigest
          .getInstance("MD5");
      md.update(text.getBytes());
      byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
      // 用字节表示就是 16 个字节
      char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
      // 进制需要 32 个字符
      int k = 0;// 表示转换结果中对应的字符位置
      for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
        // 进制字符的转换
        byte byte0 = tmp[i];// 取第 i 个字节
        str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
        // 为逻辑右移，将符号位一起右移
        str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换

      }
      s = new String(str);// 换后的结果转换为字符串

    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return s;
  }
}
