package com.illcc.libbase.util;

import android.content.Context;

import java.io.File;

public class PluginUtil {



    public static void getPlugin(Context context, String name, FileUtil.CallBack callBack) {
        if (checkHasPlugin(context, name)) {//存在插件
            callBack.finishDo("", name);
        } else {
            FileUtil.copyAssetFileToCache(name, context, callBack);
        }
    }

    private static boolean checkHasPlugin(Context context, String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }


}
