package me.tvcfish.xposed.aidehelper.hook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import me.tvcfish.xposed.aidehelper.util.ConversionUtil;
import me.tvcfish.xposed.aidehelper.util.XUtil;

enum SymbolBar {
  INSTANCE;

  /**
   * 开始Hook
   */
  public void startHook() {
    if (!isOpen()) {
      return;
    }
    hookMethod();
  }


  /**
   * 是否开启此功能
   *
   * @return boolean
   */
  private boolean isOpen() {
    return XUtil.getPref().getBoolean("symbol_bar_adjustment", false);
  }

  /**
   * 符号栏拓展
   */
  private void hookMethod() {

    final Class clazz = findClass("com.aide.ui.o", XUtil.getClassLoader());

    //重写整个j6方法
    findAndHookMethod(clazz, "j6", String.class, new XC_MethodReplacement() {
      @SuppressLint({"ResourceType", "ClickableViewAccessibility", "SetTextI18n"})
      @Override
      protected Object replaceHookedMethod(MethodHookParam param)
          throws NoSuchFieldException, IllegalAccessException {
        final Object thisObject = param.thisObject;
        Field field = XUtil.getField(clazz, "DW");
        Field field1 = XUtil.getField(clazz, "FH");
        Field field2 = XUtil.getField(clazz, "j6");

        String str = (String) param.args[0];
        View DW = (View) field.get(thisObject);
        String FH = (String) field1.get(thisObject);
        final Activity j6 = (Activity) field2.get(thisObject);

        if (DW != null && str != null && !FH.equals(str)) {
          //this.FH = str;
          setObjectField(thisObject, "FH", str);
          LayoutInflater layoutInflater = LayoutInflater.from(j6);

          int i = (int) (40.0f * j6.getResources().getDisplayMetrics().density);
          int i2 = (int) (40.0f * j6.getResources().getDisplayMetrics().density);
          LinearLayout viewGroup = DW.findViewById(0x7f080143);
          viewGroup.removeAllViews();
          for (String replace : str.split(" ")) {
            final String replace2 = replace.replace("s", " ");
            final TextView textView = (TextView) layoutInflater.inflate(0x7f0a003c, null);
            textView.setTextSize(ConversionUtil.sp2px(j6, 5));
            if (replace2.trim().length() == 0) {
              textView.setText("TAB");
            } else {
              textView.setText(replace2);
            }
            viewGroup.addView(textView, new LayoutParams(i, i2));
            //new 3(this,replace2)
            OnClickListener clickListener = (OnClickListener) XposedHelpers
                .newInstance(findClass("com.aide.ui.o$3", XUtil.getClassLoader()),
                    thisObject, replace2);
            textView.setOnClickListener(clickListener);
          }
        }
        return null;
      }
    });
  }
}
