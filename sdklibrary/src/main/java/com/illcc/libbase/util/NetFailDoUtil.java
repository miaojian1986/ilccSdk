package com.illcc.libbase.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.illcc.libbase.model.BaseModel;
import com.illcc.libnet.okhttp.exception.OkHttpException;
import com.illcc.libnet.okhttp.listener.DisposeDataListener;
import com.illcc.ndk2.Tools;
import com.illcc.sdklibrary.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class NetFailDoUtil {

    private final static int OUT_LOGIN = -100;

    public static boolean doWithFailObj(Context context,Object o) {
        if (o == null) {
            return true;
        } else {
            if (o instanceof BaseModel) {
                BaseModel baseModel = (BaseModel) o;
                if (baseModel.getCode() == OUT_LOGIN || baseModel.getCode() == -200) {
                    NetFailDoUtil.caseoutlogin(context);
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean doWithFail(Context context, String title, String message, Object o) {
        String s = "";
        if (o != null) {
            if (o instanceof OkHttpException) {
                int errocode = ((OkHttpException) o).getEcode();
                if (errocode == -1) {
                    s = "网络异常";
                } else if (errocode == -2) {
                    s = "json解析异常";
                } else if(errocode ==-3){
                    s =  (((OkHttpException) o).getEmsg()).toString();
                }
            }
        }
        Tools.postLogToServer(context, DateUtil.getCurrentDate2() + "=======" + title,
                "token=" + NewSharePUtil.getValueWithContext(context,Constant.KEY_TOKEN) + "message=" + message + ">>>" + s,
                NewSharePUtil.getValueWithContext(context,Constant.KEY_MOBILE), new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {

                    }

                    @Override
                    public void onFailure(Object reasonObj) {

                    }
                });
        return true;
    }


    public static void doWithCode(Context context,int code, String message) {
        ToastUtil.showMsg(context, message);
        switch (code) {
            case -100:
            case -200:
                caseoutlogin(context);
                break;
            default:
        }
    }

    public static void caseoutlogin(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put(Constant.KEY_LOGIN, "");
        map.put(Constant.KEY_TOKEN, "");
        map.put(Constant.KEY_AUTO_LOGINICCID, "");

        DataUtil.restartApp(context, BuildConfig.LIBRARY_PACKAGE_NAME);
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
