package com.illcc.libbase.util;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;


public class NewSharePUtil {

    public static void saveWithContext(Context context, Map<String, String> map) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHRE_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

    public static String getValueWithContext(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHRE_FILE_NAME,
                Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }


}
