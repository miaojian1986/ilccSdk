package com.illcc.libbase.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class SystemUtil {






    /**
     * 返回当前程序版本名
     */
    public static int getAppVersionCode(Context context) {
        int versionName = 1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

            return  pi.versionCode;
        } catch (Exception e) {
            // Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 获取屏幕高
     * @param activity
     */
    public static int getWindowHeight(Activity activity){
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;
        return heightPixels;
    }

    /**
     * 获取屏幕宽
     * @param activity
     */
    public static int getWindowWith(Activity activity){
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        return widthPixels;
    }



    /**
     * 关闭软键盘
     */
    public static void closeKeybord(Activity mContext) {
        if(mContext.getWindow()!=null){
            View view = mContext.getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    /**
     * 打开/关闭软键盘
     */
    public static void closeOrOpenKeybord(Activity mContext) {
        View view = mContext.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }








    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
           // Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     * @param context
     * @param spVal
     * @return
     *
     */
    public static int sp2px(Context context, float spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,context.getResources().getDisplayMetrics());
    }

}
