package com.illcc.libbase.util;

import android.content.Context;
import android.content.Intent;

import com.illcc.libbase.model.BaseModel;
import com.illcc.libnet.okhttp.exception.OkHttpException;

public class NetFailDoUtil {

    private final static int OUT_LOGIN = -100;

    public static boolean doWithFailObj(Object o) {
        if (o == null) {
            return true;
        } else {
            if (o instanceof BaseModel) {
                BaseModel baseModel = (BaseModel) o;
                if (baseModel.getCode() == OUT_LOGIN || baseModel.getCode() == -200) {
                    //NetFailDoUtil.caseoutlogin();

                    return false;
                }
            }
        }
        return true;
    }


    public static boolean doWithFail(Context context, String title, String message, Object o, String actiontag) {
        if (actiontag != null) {
            String s = "";
            if (o != null) {
                if (o instanceof OkHttpException) {
                    int errocode = ((OkHttpException) o).getEcode();
                    if (errocode == -1) {
                        s = "网络异常";
                    } else if (errocode == -2) {
                        s = "json解析异常";
                    } else if (errocode == -3) {
                        s = (((OkHttpException) o).getEmsg()).toString();
                    }
                }
            }
            Intent intent = new Intent(actiontag);
            intent.putExtra("code", String.valueOf(-1));
            intent.putExtra("message", title + ">>" + message + ">>" + s);
            context.sendBroadcast(intent);
        }
        return true;
    }


    public static void doWithCode(Context context, int code, String message, String actiontag) {
        if (actiontag != null) {
            Intent intent = new Intent(actiontag);
            intent.putExtra("code", String.valueOf(code));
            intent.putExtra("message", message);
            context.sendBroadcast(intent);
        }
    }

}
