package com.illcc.libbase.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.illcc.libbase.model.BaseModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    public static <T> T convertObject(BaseModel b, Type clazz) {
        Object o = b.getData();
        if (o != null) {
            String json = JsonUtil.tojson(o);
            return convertObjectBean(json, clazz);
        }
        return null;
    }

    public static <T> List<T> convertListBean2(BaseModel b, Type clazz) {
        Object o = b.getData();
        if (o != null) {
            String json = JsonUtil.tojson(o);
            return convertListBean2(json, clazz);
        }

        return null;
    }


    public static String tojson(Object data) {
        Gson gson = new Gson();
        return gson.toJson(data);
    }

    public static <T> T convertObjectBean(Object data, Type clazz) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String json = "";
            if (data instanceof LinkedTreeMap) {
                json = gson.toJson(data);
            } else {
                json = String.valueOf(data);
            }
            if (json != null) {
                T result = gson.fromJson(json, clazz);
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> convertListBean2(String json, Type clazz) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

            List<T> mblist = gson.fromJson(json, clazz);
            return mblist;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> convertListBean(Object data, Type clazz) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String json = "";
            if (data instanceof ArrayList) {
                json = gson.toJson(data);
            } else {
                json = String.valueOf(data);
            }
            if (data instanceof ArrayList) {
                List<T> mblist = gson.fromJson(json, clazz);
                return mblist;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJsonString(String s) {

        char[] tempArr = s.toCharArray();

        int tempLength = tempArr.length;

        for (int i = 0; i < tempLength; i++) {

            if (tempArr[i] == '"' && tempArr[i + 1] == '{') {

                tempArr[i] = ' '; // 将value中的
            }else  if(tempArr[i] == '}' && tempArr[i + 1] == '"'){
                tempArr[i + 1] = ' '; // 将value中的
            }else{
                continue;
            }

        }
        return new String(tempArr);
    }
}
