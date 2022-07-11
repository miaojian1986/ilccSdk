package com.illcc.libbase.servce;


import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;


import com.google.gson.reflect.TypeToken;
import com.illcc.libbase.listener.ResultListener;
import com.illcc.libbase.model.BaseModel;

import com.illcc.libbase.model.CallNoteNew;
import com.illcc.libbase.util.Constant;
import com.illcc.libbase.util.DataUtil;
import com.illcc.libbase.util.JsonUtil;
import com.illcc.libbase.util.NetFailDoUtil;
import com.illcc.libbase.util.NetworkUtils;
import com.illcc.libbase.util.SaveDataUtil;
import com.illcc.libbase.util.UploadTianyiUtil;
import com.illcc.libnet.okhttp.listener.DisposeDataListener;
import com.illcc.ndk2.Tools;

import java.io.File;
import java.util.List;


public class XLManagerService2 extends IntentService {


    public XLManagerService2() {//
        super("someName");// 关键是这句话
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public XLManagerService2(String name) {
        super(name);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = null;
            notificationChannel = new NotificationChannel("xservice2",
                    "ilccsdk", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), "xservice2").build();
            startForeground(1, notification);
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String option = intent.getStringExtra(Constant.KEY_CALL_SERVICE);
        if (option != null) {
            actiontag = option;
        }
        String dos = intent.getStringExtra(Constant.KEY_CALL_SERVICE_DO);
        if (dos != null) {
            if (dos.equals("offhook")) {//电话前

                String callid = intent.getStringExtra("callid");
                String callee = intent.getStringExtra("callee");
                String taskid = intent.getStringExtra("taskid");

                //保存当前所有的录音文件（用于打完电话后比较）
                DataUtil.getAllRecordsList(XLManagerService2.this);

                Intent bintent2 = new Intent(XLManagerService.class.getName());
                Bundle bundle2 = new Bundle();
                bundle2.putString("option", "callbegin");
                bundle2.putString("callid", callid);
                bundle2.putString("callee", callee);
                bundle2.putString("taskid", taskid);
                bintent2.putExtras(bundle2);
                sendBroadcast(bintent2);
            } else if (dos.equals("uplog")) {//上传日志
                long logDuration = 0;
                String callid = intent.getStringExtra("callid");
                String calltypeid = intent.getStringExtra("calltypeid");
                String endtimestr = intent.getStringExtra(Constant.KEY_CALL_EDNTIME_STR);

                try {
                    Thread.sleep(1000);
                    logDuration = loadSysCallNote();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                autoNext(intent.getStringExtra("callee"), logDuration);

                String jsondata = SaveDataUtil.getInstance(XLManagerService2.this)
                        .getJsonStringFromDbByKey(XLManagerService2.this, callid);
                if (jsondata != null) {
                    CallNoteNew c = JsonUtil.convertObjectBean(jsondata, CallNoteNew.class);

                    if (c != null && c.getCallid() != null) {
                        c.setCalltime(String.valueOf(logDuration));
                        c.setEndtime(endtimestr);
                        SaveDataUtil.getInstance(XLManagerService2.this).saveToDb(XLManagerService2.this, c.getCallid(),
                                JsonUtil.tojson(c), Constant.SAVEKEY_TYPE_CALL);
                        doUplog(callid, logDuration, c, calltypeid);
                    }
                }
            } else if (dos.equals("asyrecord")) {//同步录音

                actiontag = intent.getStringExtra("actiontag");
                String json = SaveDataUtil.getInstance(XLManagerService2.this)
                        .getJsonStringFromDbByType(XLManagerService2.this, Constant.SAVEKEY_TYPE_CALL);
                if (json != null) {
                    json = json.replace("\\", "");
                    json = JsonUtil.toJsonString(json);
                    data_records_waitupload = JsonUtil.convertListBean2(json, new TypeToken<List<CallNoteNew>>() {
                    }.getType());
                    if (data_records_waitupload != null && data_records_waitupload.size() > 0) {
                        uplocalRecord();
                    } else {
                        asyrecordfinish();
                    }
                } else {
                    asyrecordfinish();
                }
            } else if (dos.equals("aynslogs")) {//同步日志
                actiontag = intent.getStringExtra("actiontag");
                String json = SaveDataUtil.getInstance(XLManagerService2.this)
                        .getJsonStringFromDbByType(XLManagerService2.this, Constant.SAVEKEY_TYPE_CALL);
                if (json != null) {
                    json = json.replace("\\", "");
                    json = JsonUtil.toJsonString(json);
                    data_calllogs_waitupload = JsonUtil.convertListBean2(json, new TypeToken<List<CallNoteNew>>() {
                    }.getType());
                    if (data_calllogs_waitupload != null && data_calllogs_waitupload.size() > 0) {
                        uplocalLogs();
                    } else {
                        asyuplogsfinish();
                    }
                } else {
                    asyuplogsfinish();
                }
            }
        }
    }

    boolean isNoUseData(CallNoteNew callNoteNew) {
        boolean result = false;
        if (callNoteNew.getIsuplog() == 1) {
            if (callNoteNew.getIsuprecord() == 1) {
                SaveDataUtil.getInstance(XLManagerService2.this)
                        .deletbyKey(XLManagerService2.this, callNoteNew.getCallid());
                result = true;
            } else {
                if (callNoteNew.getRecordpath() == null) {
                    SaveDataUtil.getInstance(XLManagerService2.this)
                            .deletbyKey(XLManagerService2.this, callNoteNew.getCallid());
                    result = true;
                }
            }
        }
        return result;
    }

    boolean isWaitUpload(CallNoteNew callNoteNew) {
        boolean result = false;
        if (callNoteNew.getIsuprecord() == 0 && callNoteNew.getRecordpath() != null) {
            result = true;
        }
        return result;
    }

    boolean isWaitUplog(CallNoteNew callNoteNew) {
        boolean result = false;
        if (callNoteNew.getIsuplog() == 0) {
            result = true;
        }
        return result;
    }

    //同步未上传的日志文件
    private void uplocalLogs() {
        if (data_calllogs_waitupload != null && data_calllogs_waitupload.size() > 0) {
            CallNoteNew current = data_calllogs_waitupload.get(0);
            //如果是无效数据就删除本地数据
            if (!isNoUseData(current)) {
                if (isWaitUplog(current)) {
                    handUP(current.getCallid(),
                            Long.parseLong(current.getCalltime()),
                            current.getAi_number_id(),
                            current,
                            current.getCall_type_id(), true);
                } else {
                    movenextforuplogs();
                }
            } else {
                movenextforuplogs();
            }
        } else {
            asyuplogsfinish();
        }
    }

    private void uplocalRecord() {
        if (data_records_waitupload.size() > 0) {
            CallNoteNew current = data_records_waitupload.get(0);
            //如果是无效数据就删除本地数据
            if (!isNoUseData(current)) {
                if (isWaitUpload(current)) {
                    String path = current.getRecordpath();
                    if (path.startsWith("http")) {//保存到服务器
                        saveRecordPathToserver2(current, current.getCallid(), current.getRecordpath());
                    } else {
                        File file1 = new File(current.getRecordpath());
                        if (file1 == null || !file1.exists()) {//文件不存在
                            if (current.getIsuplog() == 1) {//无效数据删除
                                SaveDataUtil.getInstance(XLManagerService2.this)
                                        .deletbyKey(XLManagerService2.this, current.getCallid());
                            }
                            movenextforuprecord();
                        } else {
                            UploadTianyiUtil.getInstance(XLManagerService2.this)
                                    .upLoadFile(file1,
                                            Constant.DIR_RECORD, new ResultListener() {
                                                @Override
                                                public void backResult(String result) {
                                                    if (result != null) {
                                                        if (result.startsWith("http")) {
                                                            current.setRecordpath(result);
                                                            SaveDataUtil.getInstance(XLManagerService2.this)
                                                                    .saveToDb(XLManagerService2.this,
                                                                            current.getCallid(), JsonUtil.tojson(current),
                                                                            Constant.SAVEKEY_TYPE_CALL);
                                                            saveRecordPathToserver2(current, current.getCallid(), result);
                                                        }
                                                    } else {
                                                        movenextforuprecord();
                                                    }
                                                }
                                            });
                        }
                    }
                } else {
                    movenextforuprecord();
                }
            } else {
                movenextforuprecord();
            }
        } else {
            asyrecordfinish();
        }
    }

    void asyrecordfinish() {
        if (actiontag != null) {
            Intent intent = new Intent(actiontag);
            intent.putExtra(Constant.SHOW_DISMISS, "dismiss");
            sendBroadcast(intent);
        }
    }

    void asyuplogsfinish() {
        if (actiontag != null) {
            Intent intent = new Intent(actiontag);
            intent.putExtra(Constant.SHOW_DISMISS, "dismiss");
            sendBroadcast(intent);
        }
    }


    List<CallNoteNew> data_records_waitupload;
    List<CallNoteNew> data_calllogs_waitupload;

    private void autoNext(String callee, long duaration) {
        Intent s = new Intent(actiontag);
        s.putExtra(Constant.SHOW_DISMISS, "5");
        s.putExtra("phone", callee);
        s.putExtra("calltime", duaration);
        sendBroadcast(s);
    }


    private String actiontag;


    private void doUplog(String noteid,
                         long logDuration,
                         CallNoteNew callNoteNew, String calltypeid) {


        try {
            //保证挂机每次调用
            handUP(noteid, logDuration, callNoteNew.getAi_number_id(), callNoteNew, calltypeid, false);
            if (logDuration > 0) {
                //同步上传录音
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        upRecord(Long.parseLong(noteid), callNoteNew);
                    }
                }).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            handUP(noteid, logDuration, callNoteNew.getAi_number_id(), callNoteNew, calltypeid, false);
        }
    }


    private void upRecord(long noteId, CallNoteNew callNoteNew) {

        String path = null;
        if (DataUtil.getRecordStatus(XLManagerService2.this) == 1) {
            path = DataUtil.getNewGenerRecord(XLManagerService2.this);
            if (path != null) {
                callNoteNew.setRecordpath(path);
                SaveDataUtil.getInstance(XLManagerService2.this).saveToShare(XLManagerService2.this,
                        callNoteNew.getCallid(), JsonUtil.tojson(callNoteNew));
            }
        }

        clearsmallBean();
        if (path != null && NetworkUtils.canUploadRecord(this)) {
            //产生了新的录音文件
            File file = new File(path);
            if (file != null) {
                UploadTianyiUtil.getInstance(XLManagerService2.this)
                        .upLoadFile(file, Constant.DIR_RECORD,
                                new ResultListener() {
                                    @Override
                                    public void backResult(String result) {
                                        if (result != null && result.startsWith("http")) {
                                            saveRecordPathToserver(String.valueOf(noteId), result, callNoteNew);
                                        }
                                    }
                                });
            }
        }
    }

    void handUP(String call_id, long logDuration, String ai_numberid,
                CallNoteNew callNoteNew, String calltypeid, boolean isasys) {
        String waitime = "0";
        if (calltypeid.equals("3") || calltypeid.equals("4") || calltypeid.equals("5")) {
            waitime = DataUtil.getWaitTime(XLManagerService2.this);
        }
        Tools.hangup(XLManagerService2.this, call_id, ai_numberid,
                logDuration + "",
                waitime, callNoteNew.getEndtime(), new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        if (responseObj != null) {
                            BaseModel baseModel = (BaseModel) responseObj;
                            if (baseModel.getCode() == Constant.NET_OK) {
                                Log.d("callservice", "succeshangup=");
                                //修改本地记录
                                if (callNoteNew != null) {
                                    callNoteNew.setIsuplog(1);
                                    SaveDataUtil.getInstance(XLManagerService2.this).saveToDb(XLManagerService2.this, call_id,
                                            JsonUtil.tojson(callNoteNew), Constant.SAVEKEY_TYPE_CALL);
                                }
                            } else {
                                NetFailDoUtil.doWithCode(XLManagerService2.this,
                                        baseModel.getCode(), baseModel.getMessage());
                            }

                            if (isasys) {
                                movenextforuplogs();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        NetFailDoUtil.doWithFail(XLManagerService2.this,
                                "接口:hangUpAction",
                                Constant.ERROR_INTER + ">>call_id=" + call_id
                                        + "logDuration=" + logDuration
                                        + "ai_numberid=" + ai_numberid, reasonObj);


                        if (isasys) {
                            movenextforuplogs();
                        }
                    }
                });

    }


    void saveRecordPathToserver2(CallNoteNew callNoteNew, String call_id, String record_path) {
        Tools.uprecord(XLManagerService2.this, call_id, record_path, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                if (responseObj != null) {
                    BaseModel baseModel = (BaseModel) responseObj;
                    if (baseModel.getCode() == Constant.NET_OK) {
                        //修改本地录音上传记录
                        if (callNoteNew != null) {
                            callNoteNew.setIsuprecord(1);
                            SaveDataUtil.getInstance(XLManagerService2.this).saveToDb(XLManagerService2.this, call_id,
                                    JsonUtil.tojson(callNoteNew), Constant.SAVEKEY_TYPE_CALL);
                        }
                        movenextforuprecord();

                    } else {
                        NetFailDoUtil.doWithCode(XLManagerService2.this, baseModel.getCode(), baseModel.getMessage());
                        movenextforuprecord();
                    }
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                movenextforuprecord();
                NetFailDoUtil.doWithFail(XLManagerService2.this, "接口:recordingAction",
                        Constant.ERROR_INTER + ">>call_id=" + call_id + "record_path=" + record_path,
                        reasonObj);
            }
        });

    }

    void movenextforuprecord() {
        if (data_records_waitupload != null && data_records_waitupload.size() > 0) {
            data_records_waitupload.remove(0);
        }
        uplocalRecord();
    }

    void movenextforuplogs() {
        if (data_calllogs_waitupload != null && data_calllogs_waitupload.size() > 0) {
            data_calllogs_waitupload.remove(0);
        }
        uplocalLogs();
    }

    void saveRecordPathToserver(String call_id, String record_path, CallNoteNew callNoteNew) {
        Tools.uprecord(XLManagerService2.this, call_id, record_path, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                if (responseObj != null) {
                    BaseModel baseModel = (BaseModel) responseObj;
                    if (baseModel.getCode() == Constant.NET_OK) {
                        Log.d("record", "succ=" + record_path);
                        //修改本地录音上传记录
                        if (callNoteNew != null) {
                            callNoteNew.setIsuprecord(1);
                            SaveDataUtil.getInstance(XLManagerService2.this)
                                    .saveToDb(XLManagerService2.this, call_id,
                                            JsonUtil.tojson(callNoteNew), Constant.SAVEKEY_TYPE_CALL);
                        }
                    } else {
                        NetFailDoUtil.doWithCode(XLManagerService2.this,
                                baseModel.getCode(), baseModel.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                NetFailDoUtil.doWithFail(XLManagerService2.this, "接口:recordingAction",
                        Constant.ERROR_INTER + ">>call_id=" + call_id + "record_path=" + record_path,
                        reasonObj);
            }
        });
    }


    // 加载系统的通话记录
    public long loadSysCallNote() {
        long outgoing = 0L;
        try {
            @SuppressLint("MissingPermission") Cursor cursor =
                    getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
                            new String[]{android.provider.CallLog.Calls.DURATION,
                                    android.provider.CallLog.Calls.TYPE,
                                    android.provider.CallLog.Calls.DATE},
                            null,
                            null,
                            android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);

            if (cursor != null) {
                boolean hasRecord = cursor.moveToFirst();

                if (hasRecord) {
                    int type = cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                    long duration = cursor.getLong(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION));
                    if (type == android.provider.CallLog.Calls.OUTGOING_TYPE) {
                        outgoing = duration;
                        Log.i("yk : ", "outgoing 通话时: " + outgoing);
                        //break;
                    } else {
                        outgoing = 0;
                    }
                }
            } else {

            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return outgoing;
    }


    private void clearsmallBean() {
        Intent bintent = new Intent(XLManagerService.class.getName());
        Bundle bundle = new Bundle();
        bundle.putString("option", "clear");
        bintent.putExtras(bundle);
        sendBroadcast(bintent);
    }

    boolean isDestory = false;


    @Override
    public void onDestroy() {
        isDestory = true;
        super.onDestroy();
    }
}







