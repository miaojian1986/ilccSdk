package com.illcc.libbase.util;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil {

    private static Toast toast;
    private static Toast longToast;

    private static final int duration = Toast.LENGTH_SHORT;


    private static Toast getToast(Context context, String text) {
        if (context != null) {
            if (toast == null) {
                toast = Toast.makeText(context.getApplicationContext(), text, duration);
            } else {
                toast.setText(text);
                toast.setDuration(duration);
            }
            return toast;
        } else {
            return null;
        }

    }

    public static void showMsg(Context context, String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(text!=null){
                    Toast toast = getToast(context, text);
                    if (toast != null) {
                        toast.show();
                    }
                }
            }
        });
    }
    public static void showMsg(Context context, int resId) {
        if (context != null) {
            String text = context.getResources().getString(resId);
            showMsg(context, text);
        }
    }

    private static Toast getToastLongTime(Context context, String text) {
        if (longToast == null) {
            longToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
        } else {
            longToast.setText(text);
            longToast.setDuration(Toast.LENGTH_LONG);
        }
        return longToast;
    }

    public static void showMsgLongTime(Context context, String text) {
        if (context != null) {
            Toast toast = getToastLongTime(context, text);
            if (toast != null) {
                toast.show();
            }
        }

    }

    public static void cancelMsg() {
        if (toast != null) {
            toast.cancel();
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void showMsgInCenterLong(Context context, String text) {
        if (isMainThread()) {
            Toast toast = getToastLongTime(context, text);

            //toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            if (toast != null) {
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }
    }

    public static void showMsgInCenterShort(Context context, String text) {
        if (isMainThread()) {
            Toast toast = getToast(context, text);
            if (toast != null) {
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public static void showMsgInCenterShort(Context context, int resId) {
        if (context != null) {
            String text = context.getResources().getString(resId);
            showMsgInCenterShort(context, text);
        }
    }


    public static void showMsgInCenterLongLager(Context context, String text, int texSize) {
        if (context != null) {
            Toast toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
            if (toast != null) {
                LinearLayout layout = (LinearLayout) toast.getView();
                TextView tv = (TextView) layout.getChildAt(0);
                tv.setTextSize(texSize);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setText(text);
                toast.show();
            }
        }
    }

    public static void showColorMsgInCenter(Context context, String text, String color) {
        if (context != null) {
            String toastStr = "<font color='" + color + "'>" + text + " </font>";
            Toast toast = Toast.makeText(context.getApplicationContext(), Html.fromHtml(toastStr), Toast.LENGTH_LONG);
            if (toast != null) {
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setText(text);
                toast.show();
            }
        }
    }
}
