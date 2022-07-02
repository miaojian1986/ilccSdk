package com.illcc.libbase.util;

import android.text.TextUtils;

import java.util.Map;


public class StringUtil {

    public static String getString(String str, String defStr) {
        if (TextUtils.isEmpty(str)) {
            return defStr;
        }
        return str;
    }

    public static String getString(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            stringBuilder.append(key + "=" + map.get(key));
        }
        return stringBuilder.toString();
    }

    public static String getString(String str, String exceptStr, String defValue) {
        if (str == null) {
            return defValue;
        } else if (exceptStr != null && str.equals(exceptStr)) {
            return defValue;
        }
        return str;
    }


}
