package com.illcc.libbase;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import uni.dcloud.io.uniplugin_module.BuildConfig;

/**
 * 创建数据库、创建数据库表、包含增删改查的操作 
 */

public class GreenDaoManager {
    private static final String TAG = GreenDaoManager.class.getSimpleName();
    private static final String DB_NAME = "icc_db";



    //多线程中要被共享的使用volatile关键字修饰
    private volatile static GreenDaoManager manager = new GreenDaoManager();
    private DaoMaster mDaoMaster;
    private DaoMaster.DevOpenHelper mHelper;
    private DaoSession mDaoSession;

    /**
     * 单例模式获得操作数据库对象
     */
    public static GreenDaoManager getInstance() {
        return manager;
    }

    private GreenDaoManager() {
        setDebug();
    }



    /**
     * 判断是否有存在数据库，如果没有则创建
     */
    public DaoMaster getDaoMaster(Context context) {
        if (mDaoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     */
    public DaoSession getDaoSession(Context context) {
        if (mDaoSession == null) {
            if (mDaoMaster == null) {
                mDaoMaster = getDaoMaster(context);
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug() {
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    public void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }

    public void closeDaoSession() {
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}