package io.dcloud.uniplugin;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.illcc.libbase.api.RequestCenter;
import com.illcc.libbase.listener.ResultListener;
import com.illcc.libbase.servce.XLManagerService;
import com.illcc.libbase.servce.XLManagerService2;
import com.illcc.libbase.util.Constant;
import com.illcc.libbase.util.DataUtil;
import com.illcc.libbase.util.NewSharePUtil;
import com.illcc.libbase.util.SimCardUtils;
import com.illcc.libbase.util.UploadTianyiUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class TestModule extends UniModule {

    public static int REQUEST_CODE = 1000;
    MyBroadcast myBroadcast;
    MyBroadcast myBroadcast_record;
    String[] permission = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE
    };
    private final int re_sigal = 58;

    private void sentregi(Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    Intent intent = new Intent(XLManagerService.class.getName());
                    intent.putExtra(Constant.OPTION,
                            Constant.EVENT_REGIST_CALL_STATE_LISTEN);
                    context.sendBroadcast(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);

    }

    @UniJSMethod
    public void bindService(JSONObject options) {
        Intent intent = null;
        try {
            RequestCenter.HttpConstants.ROOT_URL = String.valueOf(options.get("baseurl"));
            intent = new Intent(mUniSDKInstance.getContext(), Class.forName("com.illcc.libbase.servce.XLManagerService"));
            mUniSDKInstance.getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            UploadTianyiUtil.getInstance(mUniSDKInstance.getContext());
            registphonelisten();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void registphonelisten() {
        if (DataUtil.checkPermission1(mUniSDKInstance.getContext(),
                permission)) {
            sentregi(mUniSDKInstance.getContext());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) mUniSDKInstance.getContext()).requestPermissions(permission, re_sigal);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == re_sigal) {
            if (DataUtil.checkGrandPermission(grantResults)) {
                sentregi(mUniSDKInstance.getContext());
            }
        }
    }

    @UniJSMethod
    public void unbindService() {
        if (serviceConnection != null) {
            mUniSDKInstance.getContext().unbindService(serviceConnection);
        }
    }


    @UniJSMethod
    public void release() {
        try {
            if (myBroadcast != null) {
                mUniSDKInstance.getContext().unregisterReceiver(myBroadcast);
            }
            if (myBroadcast_record != null) {
                mUniSDKInstance.getContext().unregisterReceiver(myBroadcast_record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @UniJSMethod(uiThread = false)
    public void upImage(JSONObject options, UniJSCallback callback) {
        String url = options.getString("url");
        File file = new File(url);
        if (file.exists()) {
            UploadTianyiUtil.getInstance(mUniSDKInstance.getContext())
                    .upLoadFile(file, Constant.DIR_RECORD, new ResultListener() {
                        @Override
                        public void backResult(String result) {
                            if (result != null && result.startsWith("http")) {
                                JSONObject data = new JSONObject();
                                data.put("neturl", result);
                                callback.invoke(data);
                            }
                        }
                    });
        }
    }

    @UniJSMethod
    public void getIccId(JSONObject options, UniJSCallback callback) {
        JSONObject data = new JSONObject();
        data.put("result", SimCardUtils.getSimIccId(mUniSDKInstance.getContext(),
                Integer.parseInt(options.getString("slotid"))));
        callback.invoke(data);
    }

    @UniJSMethod
    public void getForwardStatus(UniJSCallback callback) {
        JSONObject data = new JSONObject();
        data.put("sim1", Constant.SIM1_CALL_FORWARD);
        data.put("sim2", Constant.SIM2_CALL_FORWARD);
        callback.invoke(data);
    }

    @UniJSMethod
    public void cancelForward(JSONObject options) {
        String slotid = options.getString("slotid");
        DataUtil.cancelgoesNoWaitWithSlotid(mUniSDKInstance.getContext(), Integer.parseInt(slotid));
    }

    //录音校验
    @UniJSMethod(uiThread = false)
    public void sysRecord(JSONObject options) {
        Intent intent = null;

        String actiontag = options.getString("actiontag");
        intent = new Intent(mUniSDKInstance.getContext(), XLManagerService2.class);
        Bundle bundle = new Bundle();
        if (actiontag != null) {
            bundle.putString(Constant.KEY_ACTIONTAG, actiontag);
        }

        bundle.putString(Constant.KEY_CALL_SERVICE_DO, "asyrecord");
        intent.putExtras(bundle);


        if (myBroadcast_record == null) {
            IntentFilter intentFilter = new IntentFilter(actiontag);
            myBroadcast_record = new MyBroadcast(actiontag);
            mUniSDKInstance.getContext().registerReceiver(myBroadcast_record, intentFilter);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mUniSDKInstance.getContext().startForegroundService(intent);
        } else {
            mUniSDKInstance.getContext().startService(intent);
        }
    }

    @UniJSMethod(uiThread = false)
    public void interminalCall(JSONObject options) {
        String token = options.getString("token");
        String wait_time = options.getString("wait_time");
        String recordpath = options.getString("recordpath");


        if (token != null) {
            Map<String, String> map = new HashMap<>();
            map.put(Constant.KEY_TOKEN, token);
            map.put(Constant.KEY_WAIT_TIME, wait_time);
            map.put(Constant.KEY_RECORDPATH, recordpath);
            NewSharePUtil.saveWithContext(mUniSDKInstance.getContext(),
                    map);
        }
        String callee = options.getString("callee");
        String ai_number_id = options.getString("ai_number_id");
        String actiontag = options.getString("actiontag");

        if (myBroadcast == null) {
            IntentFilter intentFilter = new IntentFilter(actiontag);
            myBroadcast = new MyBroadcast(actiontag);
            mUniSDKInstance.getContext().registerReceiver(myBroadcast, intentFilter);
        }

        Intent intent = new Intent(XLManagerService.class.getName());
        intent.putExtra(Constant.OPTION, "callbefore");
        intent.putExtra("callee", callee);
        intent.putExtra("taskid", ai_number_id);
        intent.putExtra("actiontag", actiontag);
        if (ai_number_id != null) {
            intent.putExtra("ai_number_id", ai_number_id);
        }
        mUniSDKInstance.getContext().sendBroadcast(intent);
    }

    class MyBroadcast extends BroadcastReceiver {
        String actiontag;


        public MyBroadcast(String actiontag) {
            this.actiontag = actiontag;

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(actiontag)) {

                Map<String, Object> params = new HashMap<>();

                String showdiss = intent.getStringExtra(Constant.SHOW_DISMISS);

                if (showdiss != null) {
                    params.put("showdiss", showdiss);
                }

                String code = intent.getStringExtra("code");
                String message = intent.getStringExtra("message");
                if (code != null) {
                    params.put("code", code);
                }
                if (message != null) {
                    params.put("message", message);
                }
                mUniSDKInstance.fireGlobalEventCallback("interminalCall", params);
            }
        }
    }

    @UniJSMethod(uiThread = false)
    public void testmm(JSONObject options, UniJSCallback callback) {
//        String path = options.getString("url");
//        JSONObject data = new JSONObject();
//        Tools.checkUpdate(mUniSDKInstance.getContext(),
//                new com.illcc.libnet.okhttp.listener.DisposeDataListener() {
//                    @Override
//                    public void onSuccess(Object responseObj) {
//                        if (responseObj != null && responseObj instanceof BaseModel) {
//                            BaseModel baseModel = (BaseModel) responseObj;
//                            if (baseModel != null && baseModel.getCode() == 200) {
//                                ApkInfo apkInfo = JsonUtil.convertObject(baseModel, ApkInfo.class);
//                                if (apkInfo != null) {
//                                    data.put("code", "200");
//                                    data.put("url", apkInfo.path);
//                                    callback.invoke(data);
//                                }
//                            } else {
//
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Object reasonObj) {
//
//                        String s = "1";
//                    }
//                });

    }


    //run JS thread
    @UniJSMethod(uiThread = false)
    public void getRecordFile(JSONObject options, UniJSCallback callback) {
        String path = options.getString("url");
        List<File> list = getFiles(path, new ArrayList<File>());
        File currentFile = list.get(0);
        if (currentFile.isFile()) {
            if (list != null && list.size() > 0) {
                Collections.sort(list, new Comparator<File>() {
                    public int compare(File oldFile, File newFile) {
                        if (oldFile.lastModified() < newFile.lastModified()) {
                            return 1;
                        } else if (oldFile.lastModified() == newFile.lastModified()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                File itemFile = list.get(i);
                String fileName = itemFile.getName();
                boolean isCall = fileName.contains("call") || fileName.contains("Call");
                if (itemFile.isDirectory() && isCall) {
                    list = getFiles(itemFile.getPath(), new ArrayList<File>());
                    if (list != null && list.size() > 0) {
                        Collections.sort(list, new Comparator<File>() {
                            public int compare(File oldFile, File newFile) {
                                if (oldFile.lastModified() < newFile.lastModified()) {
                                    return 1;
                                } else if (oldFile.lastModified() == newFile.lastModified()) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    }
                }
            }
        }
        JSONObject data = new JSONObject();
        boolean status = list != null && list.size() > 0;
        data.put("code", status ? "200" : "400");
        data.put("url", status ? list.get(0).toString() : "");
        callback.invoke(data);
    }

    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    //run JS thread
    @RequiresApi(api = Build.VERSION_CODES.O)
    @UniJSMethod(uiThread = false)
    public void getFilesByte(JSONObject options, UniJSCallback callback) {
        String path = options.getString("url");
        File file = new File(path);
        JSONObject data = new JSONObject();
        if (!file.isFile()) {
            data.put("code", "error");
            callback.invoke(data);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = bis.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            String Str = Base64.encodeToString(bos.toByteArray(), Base64.NO_PADDING);
            data.put("code", "success");
            data.put("data", "成功了");
            data.put("base64", Str);
            callback.invoke(data);
        } catch (IOException e) {
            data.put("code", "error");
            callback.invoke(data);
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                data.put("code", "error");
                callback.invoke(data);
            }
            try {
                bos.close();
            } catch (IOException e) {
                data.put("code", "error");
                callback.invoke(data);
            }
        }
    }

    public Timer timer;

    //run JS thread
    @RequiresApi(api = Build.VERSION_CODES.N)
    @UniJSMethod(uiThread = false)
    public void onCallListener(int id, int time) {
        TelephonyManager manager = ((TelephonyManager) mUniSDKInstance
                .getContext().getSystemService(Context.TELEPHONY_SERVICE)).createForSubscriptionId(id);
        myPhoneStateListener phoneState = new myPhoneStateListener();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                manager.listen(phoneState, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
            }
        };
        timer.schedule(timerTask, 0, time);
    }

    //run JS thread
    @UniJSMethod(uiThread = false)
    public void onRemoveCall() {
        timer.cancel();
    }

    private static final String HARMONY_OS = "harmony";

    //run JS thread
    @UniJSMethod(uiThread = false)
    public void isHarmonyOS(UniJSCallback callback) {
        JSONObject data = new JSONObject();
        try {
            Class clz = Class.forName("com.huawei.system.BuildEx");
            Method method = clz.getMethod("getOsBrand");
            data.put("status", HARMONY_OS.equals(method.invoke(clz)));
            callback.invoke(data);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        data.put("status", false);
        callback.invoke(data);
        return;
    }

    // phoneStateListener
    class myPhoneStateListener extends PhoneStateListener {
        @UniJSMethod(uiThread = false)
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            Map<String, Object> params = new HashMap<>();
            params.put("callState", cfi);
            mUniSDKInstance.fireGlobalEventCallback("lelianCall", params);
            super.onCallForwardingIndicatorChanged(cfi);
        }
    }
}