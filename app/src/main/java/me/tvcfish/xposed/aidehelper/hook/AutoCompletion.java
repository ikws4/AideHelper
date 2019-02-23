package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import androidx.annotation.RequiresApi;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import me.tvcfish.xposed.aidehelper.model.MethodCompletion;
import me.tvcfish.xposed.aidehelper.provider.DBProvider;
import me.tvcfish.xposed.aidehelper.util.ConversionUtil;
import me.tvcfish.xposed.aidehelper.util.TranslateUtil;
import me.tvcfish.xposed.util.XHelper;
import me.tvcfish.xposed.util.XLog;

enum AutoCompletion {

  @SuppressLint("StaticFieldLeak")
  INSTANCE;

  //MainActivity
  private Activity mContext;
  //TAG
  private static final String TAG = "AutoCompletion";

  /**
   * 开始Hook
   */
  public void startHook() {
    if (!isOpen()) {
      return;
    }
    //获取上下文
    getContext();

    hookArrayAdapter();

    hookItemLongClick();
  }

  /**
   * 获取上下文
   */
  private void getContext() {
    Class clazz = XHelper.findClass("com.aide.ui.MainActivity");
    findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
      @Override
      protected void beforeHookedMethod(MethodHookParam param) {
        mContext = (Activity) param.thisObject;
      }
    });
  }

  /**
   * 判断是否开启中文补全功能
   *
   * @return boolean
   */
  private boolean isOpen() {
    return XHelper.getSharedPreferences().getBoolean("chinese_completion", true);
  }

  /**
   * 注入翻译代码
   */
  private void hookArrayAdapter() {
    //主要目标Class
    Class clazz = XHelper.findClass("com.aide.ui.a$a");

    //重写整个getView方法
    findAndHookMethod(clazz, "getView", int.class, View.class, ViewGroup.class,
        new XC_MethodReplacement() {
          @SuppressLint({"ResourceType", "SetTextI18n"})
          @Override
          protected Object replaceHookedMethod(MethodHookParam param) {
            ArrayAdapter arrayAdapter = (ArrayAdapter) param.thisObject;
            int position = (int) param.args[0];

            View view = (View) param.args[1];
            ViewGroup viewGroup = (ViewGroup) param.args[2];

            if (view == null) {
              view = LayoutInflater.from(arrayAdapter.getContext())
                  .inflate(0x7f0a0008, viewGroup, false);
            }
            //转换成SourceEntity
            Object sourceEntity = arrayAdapter.getItem(position);
            //是否可见
            int visibility = View.GONE;

            if (sourceEntity == null) {
              TextView textView = view.findViewById(0x7f080025);
              ImageView imageView = view.findViewById(0x7f080024);
              view.findViewById(0x7f080026).setVisibility(visibility);
              textView.setText("No matches");
              imageView.setImageResource(0x7f070011);
            } else {
              TextView textView = view.findViewById(0x7f080025);
              String J8 = (String) callMethod(sourceEntity, "J8");
              String XL = (String) callMethod(sourceEntity, "XL");
              String we = (String) callMethod(sourceEntity, "we");

              //5.j6[sourceEntity.DW().ordinal()]
              int[] j6 = (int[]) getStaticObjectField(
                  XHelper.findClass("com.aide.ui.a$5"), "j6");
              Object sourceEntity$a = callMethod(sourceEntity, "DW");
              int ordinal = (int) callMethod(sourceEntity$a, "ordinal");
              int i2 = j6[ordinal];

              switch (i2) {
                case 1:
                case 2:
                case 3:
                  if (XL == null) {
                    textView.setText(J8);
                    break;
                  }
                  String stringBuilder = J8 + XL;
                  textView.setText(stringBuilder, BufferType.SPANNABLE);
                  break;
                case 4:
                  if (we == null) {
                    textView.setText(J8);
                    break;
                  }
                  String stringBuilder1 = J8 + " - " + we;
                  textView.setText(stringBuilder1, BufferType.SPANNABLE);
                  break;
                case 5:
                  textView.setText(J8, BufferType.SPANNABLE);
                  break;
                default:
                  textView.setText(J8);
                  break;
              }
              //翻译
              try {
                MethodCompletion method = TranslateUtil
                    .getTranslation(mContext, textView.getText().toString());
                textView.append("\n->" + method.getChinese());
                textView.setTag(method);
                callMethod(arrayAdapter, "j6", textView, J8.length(), textView.getText().length(),
                    Color.parseColor("#FFAAAAAA"));
              } catch (Exception e) {
                XLog.show(TAG, e);
              }

              ImageView imageView = view.findViewById(0x7f080024);
              boolean Zo = (boolean) callMethod(sourceEntity, "Zo");
              if (i2 != 6) {
                switch (i2) {
                  case 1:
                    if (!Zo) {
                      imageView.setImageResource(0x7f070010);
                      break;
                    }
                    imageView.setImageResource(0x7f07000e);
                    break;
                  case 2:
                    if (!Zo) {
                      imageView.setImageResource(0x7f07000b);
                      break;
                    }
                    imageView.setImageResource(0x7f07000c);
                    break;
                  case 3:
                    imageView.setImageResource(0x7f07000b);
                    break;
                  case 4:
                    if (!Zo) {
                      imageView.setImageResource(0x7f0700ee);
                      break;
                    }
                    imageView.setImageResource(0x7f0700ef);
                    break;
                  default:
                    imageView.setImageResource(0x7f070011);
                    break;
                }
              } else {
                imageView.setImageResource(0x7f0700f0);
              }
              ImageView helpButton = view.findViewById(0x7f080026);
              helpButton.setVisibility(visibility);
            }
            return view;
          }
        });
  }

  /**
   * 注入item长按代码
   */
  private void hookItemLongClick() {
    //AdapterView长按
    Class clazz = XHelper.findClass("com.aide.ui.a$4");

    findAndHookMethod(clazz, "onItemLongClick", AdapterView.class, View.class,
        int.class,
        long.class, new XC_MethodReplacement() {
          @Override
          protected Object replaceHookedMethod(final MethodHookParam param) {

            //adapterView
            final Object thisObject = param.thisObject;
            //item索引
            final int index = (int) param.args[2];
            //item中的TextView
            final LinearLayout linearLayout = (LinearLayout) param.args[1];
            final TextView textView = (TextView) linearLayout.getChildAt(1);

            //长按弹出popupMenu
            PopupMenu popupMenu = new PopupMenu(mContext, (View) param.args[1]);
            final Menu menu = popupMenu.getMenu();
            menu.add(0, 2, 0, "修正翻译");
            menu.add(0, 3, 0, "代码笔记");
            menu.add(0, 4, 0, "查看文档");

            //设置popupMenu的点击事件
            popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
              @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
              @Override
              public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                  case 2:
                    //dialog中的EditView
                    final EditText editText = new EditText(mContext);
                    //获取上面传输来的Tag
                    final MethodCompletion method = (MethodCompletion) textView
                        .getTag();
                    editText.setText(method.getChinese());
                    //new一个对话框
                    new AlertDialog.Builder(mContext).setTitle("修正翻译").setView(editText)
                        .setCancelable(false)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                            //更新数据库，通过english判断
                            Uri uri = Uri
                                .parse(DBProvider.TRANSLATION_CONTENT_URL);

                            //更新
                            Bundle bundle = new Bundle();
                            bundle.putString("english", method.getEnglish());
                            bundle.putString("chinese", editText.getText().toString());
                            bundle.putInt("state", DBProvider.TRANSLATE_STATE_OK);
                            mContext.getContentResolver()
                                .call(uri, "update", DBProvider.TRANSLATE, bundle);
                          }
                        }).show();
                    break;
                  case 3:
                    //代码笔记
                    //获取上面传输来的Tag
                    final MethodCompletion method1 = (MethodCompletion) textView
                        .getTag();
                    //编辑框
                    LinearLayout layout = new LinearLayout(mContext);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                    final EditText et = new EditText(mContext);
                    et.setText(method1.getNotes());
                    et.setTextSize(ConversionUtil.px2sp(mContext, 28));
                    et.setTextColor(Color.parseColor("#FFAAAAAA"));
                    et.setHint("你可以在这里添加你的笔记");
                    et.setBackground(null);
                    et.setGravity(Gravity.TOP);
                    int padding = ConversionUtil.dip2px(mContext, 24);
                    layout.setPadding(padding, 0, padding, 0);
                    layout.addView(et, layoutParams);

                    new AlertDialog.Builder(mContext)
                        .setTitle("代码笔记")
                        .setView(layout)
                        .setCancelable(false)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("保存", new OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                            //更新代码笔记
                            Uri uri = Uri
                                .parse(DBProvider.TRANSLATION_CONTENT_URL);

                            //更新
                            Bundle bundle = new Bundle();
                            bundle.putString("english", method1.getEnglish());
                            bundle.putString("notes", et.getText().toString());
                            mContext.getContentResolver()
                                .call(uri, "update", DBProvider.CODE_NOTES, bundle);
                          }
                        })
                        .show();

                    break;
                  case 4:
                    //查看文档，勿动
                    try {
                      @SuppressLint("ResourceType")
                      ViewGroup AIDEEditorPager = mContext.findViewById(0x7f0800f1);
                      XposedHelpers.callMethod(AIDEEditorPager, "Ws");
                      Activity activity = (Activity) XHelper
                          .callStaticMethod("com.aide.ui.f", "u7");
                      ListView completionListView = (ListView) XposedHelpers
                          .getObjectField(thisObject, "j6");
                      Object sourceEntity = completionListView.getItemAtPosition(index);
                      String QX = XposedHelpers.callMethod(sourceEntity, "QX").toString();
                      String EQ = XHelper.callStaticMethod("com.aide.ui.activities.a", "EQ")
                          .toString();
                      XHelper.callStaticMethod("com.aide.common.b", "j6", activity,
                          QX, EQ);
                    } catch (Exception e) {
                      XLog.show(TAG, e);
                    }
                    break;
                }
                return true;
              }
            });
            popupMenu.show();
            return true;
          }
        });
  }

}
