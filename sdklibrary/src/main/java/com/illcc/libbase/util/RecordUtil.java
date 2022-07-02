package com.illcc.libbase.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecordUtil {
    private final static String record_dir_key = "record_dir_key";
    private static int count;
    public static String getAvailabeRecordDir() {
        File parent = Environment.getExternalStorageDirectory();

        if (Build.BRAND.equals("realme") || Build.MODEL.equals(Constant.MODEL_FUM10)) {
            return new File(parent, "Music/Recordings/Call Recordings").getPath();
        } else if (RomUtil.isXiaomi()) {
            return "/sdcard/miui/sound_recorder";
        } else if (RomUtil.isHuawei()) {
            return "/sdcard/Sounds/CallRecord";
        } else if (RomUtil.isMeizu()) {
            return new File(parent, "Recorder").getPath();
        } else if (RomUtil.isOppo()) {
            String path = new File(parent, "Recordings").getPath();
            return path;
        } else if (RomUtil.isVivo()) {
            return new File(parent, "Record/Call").getPath();
        } else if (RomUtil.isSamsung()) {
            return new File(parent, "Sounds").getPath();
        } else if (RomUtil.isLeeco()) {
            return new File(parent, "Recorder/remote").getPath();
        } else {
            return new File(parent, "").getPath();
        }
    }

    // 获取目录下所有文件(按时间排序)
    public static List<File> getFileSort(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        try {
            if (list != null && list.size() > 0) {
                Collections.sort(list, new Comparator<File>() {
                    public int compare(File file, File newFile) {
                        if (file.lastModified() <= newFile.lastModified()) {//降序<;升序>
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 获取目录下所有文件
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            try {
                File[] subfiles = realFile.listFiles();
                if (subfiles != null && subfiles.length > 0) {
                    for (File file : subfiles) {
                        if (file.isDirectory()) {
                            getFiles(file.getAbsolutePath(), files);
                        } else {
                            files.add(file);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return files;
    }
    //寻找到录音文件 ---  并存储录音文件夹(以便下次拿到录音文件)

    private static void saveRecordDir(String dir, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARE_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(record_dir_key, dir).commit();
    }

    private static String getRecordCacheDir(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constant.SHARE_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(record_dir_key, "");
    }


    private static File searchRecordDir(long time, Context context) {
        File parent = Environment.getExternalStorageDirectory();
        File[] files = parent.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    count = 0;
                    File file = searchRecordFile(time, files[i], count, context);
                    if (file != null) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

    private static File searchRecordFile(long time, File dir, int count, Context context) {
        //计算调用次数 --- 层级不必太多
        if (dir.isDirectory() && isNotRecordAppDir(dir) && count < 4) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    //10秒之内生成的文件 默认为当前的录音文件
                    if (matchFileNameIsRecord(file.getName()) && file.lastModified() - time > -10 * 1000
                            && file.length() > 0 && file.isFile()) {
                        saveRecordDir(file.getParent(), context);
                        return file;
                    }
                    if (file.isDirectory()) {
                        return searchRecordFile(time, file, count + 1, context);
                    }
                }
            }
        }
        return null;
    }

    private static String getSystemRecord() {
        File parent = Environment.getExternalStorageDirectory();
        File child;
        if (RomUtil.isHuawei()) {
            child = new File(parent, "record");
            if (!child.exists()) {
                child = new File(parent, "Sounds/CallRecord");
            }
        } else if (RomUtil.isXiaomi()) {
            child = new File(parent, "MIUI/sound_recorder/call_rec");
        } else if (RomUtil.isMeizu()) {
            child = new File(parent, "Recorder");
        } else if (RomUtil.isOppo()) {
            child = new File(parent, "Recordings/Call Recordings");
            if (!child.exists()) {
                child = new File(parent, "Recordings");
            }
        } else if (RomUtil.isVivo()) {
            child = new File(parent, "Record/Call");
        } else if (RomUtil.isSamsung()) {
            child = new File(parent, "Sounds");
        } else {
            child = new File(parent, "");
        }
        if (!child.exists()) {
            return null;
        }
        return child.getAbsolutePath();
    }

    //常用系统录音文件存放文件夹
    private static ArrayList<String> getRecordFiles() {
        String parentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        ArrayList<String> list = new ArrayList<>();
        File file = new File(parentPath, "record");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        file = new File(parentPath, "Sounds/CallRecord");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        file = new File(parentPath, "MIUI/sound_recorder/call_rec");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        file = new File(parentPath, "Recorder");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        file = new File(parentPath, "Recordings/Call Recordings");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        file = new File(parentPath, "Recordings");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        file = new File(parentPath, "Record/Call");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        file = new File(parentPath, "Sounds");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }
        //oppp android-10 手机存储系统录音
        file = new File(parentPath, "Music/Recordings/Call Recordings");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }

        file = new File(parentPath, "PhoneRecord");
        if (file.exists()) {
            list.add(file.getAbsolutePath());
        }

        // 或者其余机型系统录音文件夹 添加
        return list;
    }

    //寻找文件
    public static File getFile(Context context) {
        try {
            long time = Calendar.getInstance().getTimeInMillis();
            File dir;
            //使用记录下的文件夹下搜索
            String recordDir = getRecordCacheDir(context);
            // LogUtils.e("sp是否有缓存文件%s", "filePath" + recordDir + "当前时间" + DateUtil.stringToFormatString(time, DateUtil.DATE_FORMAT_CHINESE));
            if (TextUtils.isEmpty(recordDir)) {
                //使用固定系统下文件夹下搜索
                recordDir = getSystemRecord();
                if (!TextUtils.isEmpty(recordDir)) {
                    dir = new File(recordDir);
                    File file = getRecordFile(time, dir);
                    if (file != null) {
                        saveRecordDir(file.getParent(), context);
                        return file;
                    }
                }
                //使用常用系统下文件夹下搜索
                ArrayList<String> recordFiles = getRecordFiles();
                for (int i = 0; i < recordFiles.size(); i++) {
                    dir = new File(recordFiles.get(i));
                    File file = getRecordFile(time, dir);
                    if (file != null) {
                        saveRecordDir(file.getParent(), context);
                        return file;
                    }
                }
            } else {
                //直接使用已存储文件夹下搜索
                File file = getRecordFile(time, new File(recordDir));
                if (file != null) {
                    saveRecordDir(file.getParent(), context);
                    return file;
                }
            }
            //全局搜索录音文件夹并存储下来
            File file = searchRecordDir(time, context);
            long time2 = Calendar.getInstance().getTimeInMillis();
            // LogUtils.e("全局搜索录音文件夹所花时间%s", (time2 - time) + " 当前时间" + DateUtils.getCustomTime(time2, DateUtils.YYYY_MM_DD_HH_MM_SS));
            return file;
        } catch (Exception e) {
            // LogUtils.e(e);
        }
        return null;
    }


    private static File getRecordFile(long time, File dir) {
        if (dir.isDirectory() && isNotRecordAppDir(dir)) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    //20秒之内生成的文件 默认为当前的录音文件(TODO 这里如果需要更准确可以判断是否是录音,录音时长校对)
                    if (matchFileNameIsRecord(file.getName()) && file.lastModified() - time > -20 * 1000
                            && file.length() > 0 && file.isFile()) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isNotRecordAppDir(File dir) {
        String name = dir.getName();
        if ("Android".equals(name)) {
            return false;
        } else if ("不是录音文件夹都可以写在这".equals(name)) {
            return false;
        }

        //加入一些会录音的app,会生成录音文件,防止使用其他录音文件而没有使用系统录音文件
        return true;
    }

    private static boolean matchFileNameIsRecord(String name) {
        //录音文件匹配规则 -- 可以自行添加其他格式录音匹配
        try {
            if (name.toLowerCase().endsWith(".mp3".toLowerCase())) {
                return true;
            } else if (name.toLowerCase().endsWith(".wav".toLowerCase())) {
                return true;
            } else if (name.toLowerCase().endsWith(".3gp".toLowerCase())) {
                return true;
            }
//            else if (name.toLowerCase().endsWith(".mp4".toLowerCase())) {
//                return true;
//            }
            else if (name.toLowerCase().endsWith(".amr".toLowerCase())) {
                return true;
            } else if (name.toLowerCase().endsWith(".3gpp".toLowerCase())) {
                return true;
            }
        } catch (Exception e) {
            // LogUtils.e(e);
        }
        return false;
    }

}
