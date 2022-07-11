package com.illcc.libbase.util;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataUtil {

    public static String getNewGenerRecord(Context context) {
        File file = new File(DataUtil.getRecordPath(context));
        if (file != null && file.exists() && file.isDirectory()) {
            String[] d = file.list();
            if (d != null && d.length > 0) {
                for (int i = 0; i < d.length; i++) {
                    if (!Constant.saveRecord.contains(d[i])) {
                        return file.getAbsolutePath() + "/" + d[i];
                    }
                }
                return null;
            } else {
                return null;
            }
        }
        return null;
    }

    public static String getWaitTime(Context context) {
        String s = NewSharePUtil.getValueWithContext(context,Constant.KEY_CUSTOM_TIME);
        if (s.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return "6";
            } else {
                return "8";
            }
        } else {
            return s;
        }
    }

    public static void getAllRecordsList(Context context) {
        File file = new File(DataUtil.getRecordPath(context));
        Constant.saveRecord.clear();
        if (file != null && file.exists() && file.isDirectory()) {
            String[] d = file.list();
            if (d != null && d.length > 0) {
                for (int i = 0; i < d.length; i++) {
                    Constant.saveRecord.add(d[i]);
                }
            }
        }
    }



    public static void restartApp(Context context,String packname) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage((packname));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }
    public static boolean checkPermission1(Context context, String[] permissions) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        for (String permission : permissions) {
            int per = packageManager.checkPermission(permission, packageName);
            if (PackageManager.PERMISSION_DENIED == per) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkGrandPermission(int[] permissions) {
        int dex = 0;
        if (permissions != null && permissions.length > 0) {
            for (int p : permissions) {
                if (p == PackageManager.PERMISSION_GRANTED) {
                    dex++;
                }
            }
        }
        return dex == permissions.length;
    }

    public static void saveShareRecordFirst(Context context,
                                            String currentfirst) {
        Map<String, String> map = new HashMap<>();
        map.put("currentfirst", currentfirst);
        NewSharePUtil.saveWithContext(context, map);
    }

    public static String getShareRecordFirst(Context context) {

        return NewSharePUtil.getValueWithContext(context, "currentfirst");
    }

    //取消呼叫转移
    public static void cancelgoesNoWaitWithSlotid(Context cxt, int id) {

        SimCardUtils sim = new SimCardUtils();
        if (RomUtil.isXiaomi()) {//如果是小米设备，则初始化小米推送
            sim.call(cxt, id, "tel:%23%2321%23");
        } else if (RomUtil.isOppo()) {
            sim.call(cxt, id, "tel:%23%23002%23");
        } else {
            sim.call(cxt, id, "tel:%23%2321%23");
        }
    }

    public static String getRecordPath(Context context) {
        String s = NewSharePUtil.getValueWithContext(context, Constant.KEY_RECORDPATH);
        if (s.equals("")) {
            return RecordUtil.getAvailabeRecordDir();
        }
        return s;
    }

    public static void clearCallStatus(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("callstatus", "");
        map.put(Constant.KEY_WAIT_CALCEL, "");
        map.put(Constant.KEY_WAIT_CALCEL_XNUMBER, "");
        NewSharePUtil.saveWithContext(context, map);

    }

    public static String getCallStatus(Context context) {
        return NewSharePUtil.getValueWithContext(context, "callstatus");
    }


    public static void saveCallStatus(Context context,String callid, String taskid, String callee, String call_type_id, int a_postion, int x_position) {
        SaveDataUtil.getInstance(context).saveToShare(context,"callstatus", callid + "-" + taskid + "-"
                + callee + "-" + call_type_id + "-" + a_postion + "-" + x_position);

    }

    /**
     * 获取录音状态
     *
     * @param context
     * @return
     */
    public static int getRecordStatus(Context context) {
        String isopen = NewSharePUtil.getValueWithContext(context, Constant.SHARE_KEY_RECORD_NEW);
        String[] ss = getUnGrandPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, context);
        if (ss != null && ss.length == 0) {//有读写存储卡的权限
            if (checkIsOpenAudioRecord(context)) {//有打开系统录音
                return isopen == null ? 0 : isopen.equals("0") ? 0 : 1;
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(Constant.SHARE_KEY_RECORD_NEW, "0");
                NewSharePUtil.saveWithContext(context, map);
                return 0;
            }
        } else {
            Map<String, String> map = new HashMap<>();
            map.put(Constant.SHARE_KEY_RECORD_NEW, "0");
            NewSharePUtil.saveWithContext(context, map);
            return 0;
        }
    }


    /**
     * 检测是否开启自动录音
     *
     * @param context
     * @return
     */
    public static boolean checkIsOpenAudioRecord(Context context) {
        if (RomUtil.isXiaomi()) {
            return checkXiaomiRecord(context);
        } else if (RomUtil.isHuawei()) {
            return checkHuaweiRecord(context);
        } else if (Build.MODEL.equals("PFUM10")) {
            return checkOppoPFUMRecord(context);
        } else if (RomUtil.isOppo()) {
            return checkOppoRecord(context);
        } else if (RomUtil.isLeeco()) {
            return checkLeshiRecord(context);
        } else {
            return true;
        }
    }


    /**
     * 检查乐视自动录音功能是否开启，true已开启  false未开启
     *
     * @return
     */
    private static boolean checkLeshiRecord(Context context) {
        try {
            int key = Settings.Global.getInt(context.getContentResolver(), "leui_call_auto_record");
            //0是开启
            return key == 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 检查OPPO手机自动录音功能是否开启，true已开启  false未开启
     *
     * @return
     */
    private static boolean checkOppoRecord(Context context) {
        try {

            int key = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    ? Settings.Global.getInt(context.getContentResolver(), "oppo_all_call_audio_record") : 0;
            //0代表OPPO自动录音未开启,1代表OPPO自动录音已开启
            return key != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查OPPO手机自动录音功能是否开启，true已开启  false未开启
     *
     * @return
     */
    private static boolean checkOppoPFUMRecord(Context context) {
        try {

            int key = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    ? Settings.Global.getInt(context.getContentResolver(), "oplus_customize_all_call_audio_record") : 0;
            //0代表OPPO自动录音未开启,1代表OPPO自动录音已开启
            return key != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 检查华为手机自动录音功能是否开启，true已开启  false未开启
     *
     * @return
     */
    private static boolean checkHuaweiRecord(Context context) {
        try {
            int key = Settings.Secure.getInt(context.getContentResolver(), "enable_record_auto_key");
            //0代表华为自动录音未开启,1代表华为自动录音已开启
            return key != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查小米手机自动录音功能是否开启，true已开启  false未开启
     *
     * @return
     */
    private static boolean checkXiaomiRecord(Context context) {
        try {
            int key = Settings.System.getInt(context.getContentResolver(), "button_auto_record_call");
            //0是未开启,1是开启
            return key != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取没有同意的权限
     *
     * @param permissions
     * @param context
     * @return
     */
    public static String[] getUnGrandPermission(String[] permissions, Context context) {
        if (permissions != null && permissions.length > 0) {
            List<String> unGrantPermissions = new ArrayList<>();
            for (String p : permissions) {
                int grant = ContextCompat.checkSelfPermission(context, p);
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    unGrantPermissions.add(p);
                }
            }
            return coverListToArray(unGrantPermissions);
        } else {
            return null;
        }
    }

    private static String[] coverListToArray(List<String> list) {
        if (list.size() > 0) {
            String[] results = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                results[i] = list.get(i);
            }
            return results;
        } else {
            return new String[]{};
        }
    }

}
