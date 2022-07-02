package com.illcc.libbase.util;


import android.content.Context;
import android.content.res.AssetManager;

import com.illcc.libbase.listener.ResultListener;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class UploadTianyiUtil {
    DexClassLoader dexClassLoader;
    AssetManager assetManager;
    private String classname = "com.illcc.libtianyi.util.TianyiOOsUtil";
    private Object object;
    Class<?> cls;
    private String dexppath;
    private String pluginname = "libtianyi.pp";

    private static UploadTianyiUtil uploadTianyiUtil;

    private UploadTianyiUtil(Context context) {
        dexppath = new File(context.getCacheDir(),
                pluginname).getAbsolutePath();
        loadPlugin(context);
    }

    public static UploadTianyiUtil getInstance(Context context) {
        if (uploadTianyiUtil == null) {
            synchronized (UploadTianyiUtil.class) {
                if (uploadTianyiUtil == null) {
                    uploadTianyiUtil = new UploadTianyiUtil(context);
                }
            }
        }
        return uploadTianyiUtil;
    }

    //处理插件中的资源问题
    private void handleRes() {
        try {
            Class<?> clazz = Class.forName("android.content.res.AssetManager");
            assetManager = (AssetManager) clazz.newInstance();
            Method method = clazz.getMethod("addAssetPath", String.class);
            method.invoke(assetManager, dexppath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initClassLoader(Context context) {
        File librarySearchPath = new File(context.getCacheDir(), "librarySearchPath");
        dexClassLoader = new DexClassLoader(dexppath,
                context.getCacheDir().getAbsolutePath(),
                librarySearchPath.getAbsolutePath(),
                context.getClassLoader());
    }

    //加载插件
    public void loadPlugin(Context context) {
        PluginUtil.getPlugin(context, pluginname,
                new FileUtil.CallBack() {
                    @Override
                    public void finishDo(String path, String name) {
                        try {
                            handleRes();
                            initClassLoader(context);
                            cls = dexClassLoader.loadClass(classname);
                            object = cls.getMethod("getInstance") // 参数1：方法名, 参数2：方法的参数类型
                                    .invoke(null);

                            Method method = cls.getDeclaredMethod("initClient");
                            method.invoke(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void upLoadFile(final File file, final String dir, final ResultListener uploadListner) {
        try {
            Method method = cls.getDeclaredMethod("upLoadFile",
                    File.class, ResultListener.class, String.class);
            method.invoke(object, file, uploadListner, dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

