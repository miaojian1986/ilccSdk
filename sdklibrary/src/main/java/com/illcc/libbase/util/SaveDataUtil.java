package com.illcc.libbase.util;

import android.content.Context;

import com.illcc.libbase.GreenDaoManager;
import com.illcc.libbase.SaveDataDao;
import com.illcc.libbase.model.SaveData;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveDataUtil {


    private static SaveDataUtil saveDataUtil;

    private SaveDataUtil() {
    }

    public static SaveDataUtil getInstance(Context context) {
        if (saveDataUtil == null) {
            synchronized (SaveDataUtil.class) {
                if (saveDataUtil == null) {
                    saveDataUtil = new SaveDataUtil(context);
                }
            }
        }
        return saveDataUtil;
    }




    public String getJsonStringFromDbByKey(Context context, String key) {
        SaveDataDao saveDataDao = GreenDaoManager.getInstance().getDaoSession(context).getSaveDataDao();
        if (saveDataDao != null) {
            SaveData saveData = saveDataDao.load(key);
            if (saveData != null) {
                return saveData.getJsonStrng();
            }
        }
        return null;
    }


    public String getJsonStringFromDbByType(Context context, String type) {
        SaveDataDao saveDataDao = GreenDaoManager.getInstance().getDaoSession(context).getSaveDataDao();
        QueryBuilder<SaveData> builder = saveDataDao.queryBuilder().orderAsc(SaveDataDao.Properties.Type);
        List<SaveData> list = builder.where(SaveDataDao.Properties.Type.eq(type)).list();
        if (list != null && list.size() > 0) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.get(i).getJsonStrng());
            }
            return JsonUtil.tojson(result);
        }
        return null;
    }


    public void deletbyKey(Context context, String key) {
        SaveDataDao saveDataDao = GreenDaoManager.getInstance().getDaoSession(context).getSaveDataDao();
        if (saveDataDao != null) {
            saveDataDao.deleteByKey(key);
        }
    }

    int maxnumber = 1000;

    public void saveToDb(Context context, String key, String jsonData, String type) {
        SaveDataDao saveDataDao = GreenDaoManager.getInstance().getDaoSession(context).getSaveDataDao();
        if (saveDataDao != null) {
            if (saveDataDao.count() >= maxnumber) {//保证不超过指定的数据
                saveDataDao.deleteInTx(getOldList(context, maxnumber / 2));
            }
            SaveData old = saveDataDao.load(key);
            //存在就更新 不存在就插入
            if (old != null) {
                old.setJsonStrng(jsonData);
                saveDataDao.save(old);
            } else {
                SaveData data = new SaveData();
                data.setKey(key);
                data.setJsonStrng(jsonData);
                data.setType(type);
                data.setTime(System.currentTimeMillis());
                saveDataDao.insert(data);
            }
        }
    }

    private List<SaveData> getOldList(Context context, int number) {
        SaveDataDao saveDataDao = GreenDaoManager.getInstance().getDaoSession(context).getSaveDataDao();
        QueryBuilder<SaveData> builder = saveDataDao.queryBuilder().orderAsc(SaveDataDao.Properties.Time);
        List<SaveData> list = builder.list().subList(0, number);
        return list;
    }

    public void saveToShare(Context context, String key, String json) {
        Map<String, String> map = new HashMap<>();
        map.put(key, json);
        NewSharePUtil.saveWithContext(context, map);
    }


    private SaveDataUtil(Context context) {
        GreenDaoManager.getInstance().getDaoMaster(context);
    }
}
