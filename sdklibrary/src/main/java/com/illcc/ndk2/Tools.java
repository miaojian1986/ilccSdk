package com.illcc.ndk2;

import android.content.Context;

import com.illcc.libnet.okhttp.listener.DisposeDataListener;

import java.io.File;


public class Tools {

    static {
        System.loadLibrary("native-lib");
    }

    public static native String getIccId3(Context context, int slotid);


    //向服务器写日志
    public static native void postLogToServer(Context context, String name, String e,
                                              String operate_name, DisposeDataListener listener);

    //检查token
    public static native void checkTokenAction(Context context, DisposeDataListener listener);


    //自动登录
    public static native void autologin(Context context, String savediccid,
                                        String mobile_brand,
                                        String mobile_model,
                                        String mobile_system,
                                        String apppackname,
                                        DisposeDataListener listener);

    //检查版本
    public static native void checkUpdate(Context context,
                                          String apptype,
                                          DisposeDataListener listener);


    //电话详情
    public static native void callpagedetail(Context context, String callee, String page, DisposeDataListener listener);

    //号码归属地
    public static native void phoneAddressAction(Context context, String mobile, DisposeDataListener listener);

    //上传录音
    public static native void uprecord(Context context, String call_id, String record_path, DisposeDataListener listener);

    //挂机
    public static native void hangup(Context context, String id, String ai_numberid, String use_time, String wait_time,String etime, DisposeDataListener listener);

    //发起呼叫
    public static native void callbefore(Context context,
                                         String callee,
                                         String ai_number_id,
                                         String is_open_recording,
                                         DisposeDataListener listener);

    //电话历史记录
    public static native void getphonelist(Context context, String page, String pagesize, DisposeDataListener listener);

    //获取任务
    public static native void getStatic(Context context, String sday, String eday, DisposeDataListener listener);


    //三方校验手机号后回调
    public static native void wyydValidatePhoneAction(Context context, String iccid, String wy_accessToken,
                                                      String wy_token,
                                                      String wy_phone,
                                                      DisposeDataListener listener);

    //获取用户信息
    public static native void getUserInfo(Context context, DisposeDataListener listener);


    //更新号码
    public static native void updatNnumber(Context context, String a_number, String a_iccid,
                                           String x_number, String x_iccid, DisposeDataListener listener);

    //退出登录
    public static native void loginout(Context context, DisposeDataListener listener);


    //修改名字
    public static native void updatePersonActionName(Context context, String name, DisposeDataListener listener);


    //修改头像
    public static native void updateUserImg(Context context, String image,
                                            DisposeDataListener listener);

    //上传文件
    public static native void upFile(Context context, File file, String taskid, DisposeDataListener listener);


    //导入号码
    public static native void improtNumber(Context context, String txt, String type, String task_id, DisposeDataListener listener);


    //删除任务
    public static native void delTask(Context context, String id, DisposeDataListener listener);

    //保存任务名称
    public static native void saveNameToServer(Context context, String id, String title, DisposeDataListener listener);

    //任务大厅列表
    public static native void taskpageaction(Context context, String currenttype, String pagesize, String page, DisposeDataListener listener);

    //电话任务号码列表
    public static native void getCallList(Context context, String task_id, String page, String pagesize, String status,
                                          DisposeDataListener listener);

}
