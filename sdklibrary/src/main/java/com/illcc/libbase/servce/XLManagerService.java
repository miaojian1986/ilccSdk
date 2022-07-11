package com.illcc.libbase.servce;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;


import com.illcc.libbase.listener.MyPhoneStateListener;
import com.illcc.libbase.model.BaseModel;
import com.illcc.libbase.model.CallNoteNew;
import com.illcc.libbase.model.SmallCallBean;

import com.illcc.libbase.util.Constant;
import com.illcc.libbase.util.DataUtil;
import com.illcc.libbase.util.DateUtil;
import com.illcc.libbase.util.JsonUtil;
import com.illcc.libbase.util.NetFailDoUtil;
import com.illcc.libbase.util.NewSharePUtil;
import com.illcc.libbase.util.SaveDataUtil;
import com.illcc.libbase.util.SimCardUtils;
import com.illcc.libbase.util.ToastUtil;
import com.illcc.libnet.okhttp.listener.DisposeDataListener;
import com.illcc.ndk2.Tools;
import java.util.HashMap;
import java.util.Map;


public class XLManagerService extends Service implements LifecycleOwner {

    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    TelephonyManager telM;
    MyPhoneStateListener myPhoneStateListener_0;
    MyPhoneStateListener myPhoneStateListener_1;
    TelephonyManager telephonyManager0;
    TelephonyManager telephonyManager1;

    boolean isCanUseLinCall1;
    boolean isCanUseLinCall2;

    private String actiontag;
    boolean isDestory = false;
    String current_xNumber;


    private Object[] getCallForwordStatus(int id, int weizhi) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Object[] result = new Object[2];
            TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                    .createForSubscriptionId(id);
            MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener(this, weizhi,
                    getClass().getName());
            telephonyManager.listen(myPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
            result[0] = myPhoneStateListener;
            result[1] = telephonyManager;
            return result;
        }
        return null;
    }


    public void getForwardListner() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int siId = SimCardUtils.getSubcriptionId(this, 0);
                if (siId != -1) {
                    Object[] objects1 = getCallForwordStatus(siId, 0);
                    if (objects1 != null) {
                        myPhoneStateListener_0 = (MyPhoneStateListener) objects1[0];
                        telephonyManager0 = (TelephonyManager) objects1[1];
                    }
                }
                int siId2 = SimCardUtils.getSubcriptionId(this, 1);
                if (siId2 != -1) {
                    Object[] objects2 = getCallForwordStatus(siId2, 1);
                    if (objects2 != null) {
                        myPhoneStateListener_1 = (MyPhoneStateListener) objects2[0];
                        telephonyManager1 = (TelephonyManager) objects2[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void unregisetlistenphonestat(TelephonyManager telephonyManager, PhoneStateListener myPhoneStateListener) {
        if (telephonyManager != null && myPhoneStateListener != null) {
            telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            telephonyManager = null;
        }
    }


    @Override
    public void onCreate() {

        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        myBroadcast = new MyBroadcast();
        IntentFilter intentFilter = new IntentFilter(XLManagerService.class.getName());
        registerReceiver(myBroadcast, intentFilter);

        super.onCreate();
    }

    private void registPhoneListner() {
        telM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telM.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    MyBroadcast myBroadcast;


    boolean issyning = false;


    class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(XLManagerService.class.getName())) {
                String option = intent.getStringExtra(Constant.OPTION);
                if (option != null) {
                    if (option.equals("callbefore")) {
                        ai_number_id = intent.getStringExtra("ai_number_id");
                        taskid = intent.getStringExtra("taskid");
                        callee = intent.getStringExtra("callee");
                        actiontag = intent.getStringExtra("actiontag");
                        isCanUseLinCall1 = SimCardUtils.hasSimCard(XLManagerService.this, 0);
                        isCanUseLinCall2 = SimCardUtils.hasSimCard(XLManagerService.this, 1);
                        callAccrossServer(callee, taskid, ai_number_id);
                    } else if (option.equals("callbegin")) {
                        String callee = intent.getStringExtra("callee");
                        if (smallCallBean != null && smallCallBean.getCall_type_id() + "" != null) {
                            callWithCallTypeid(callee, smallCallBean);
                        }
                    } else if (option.equals("clear")) {
                        smallCallBean = null;
                    } else if (option.equals("hangup")) {
                    } else if (option.equals("sysnfinish")) {
                        issyning = false;
                    } else if (option.equals(Constant.EVENT_REGIST_CALL_STATE_LISTEN)) {
                        registPhoneListner();
                        getForwardListner();
                    } else if (option.equals(Constant.FORWARD_SELECT_VALUE)) {
                        callMainDismissalert();
                        if (TextUtils.isEmpty(NewSharePUtil.getValueWithContext(XLManagerService.this,
                                Constant.KEY_WAIT_CALL_FORWARD_TYPE))
                                || NewSharePUtil.getValueWithContext(XLManagerService.this,
                                Constant.KEY_WAIT_CALL_FORWARD_TYPE).equals("0")
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            boolean forstatus = intent.getBooleanExtra("forstatus", false);
                            if (current_xNumber != null && forstatus) {//表示正在打呼转电话
                                int simpistion = intent.getIntExtra("simpistion", -1);
                                if (simpistion != -1 && simpistion == waitcancleid) {
                                    if (forstatus) {//呼叫转移成功
                                        SimCardUtils.call(context, callslotid, "tel:" + current_xNumber);
                                        current_xNumber = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void callWithCallTypeid(String callee, SmallCallBean smallCallBean) {
        int sw = smallCallBean.getCall_type_id();
        switch (sw) {
            case 1:
                aZhihu(callee,
                        smallCallBean.getA_position());
                break;
            case 8:
            case 2:
                xZhihu(callee,
                        smallCallBean.getX_position());
                break;
            case 5:
            case 3:
                callForwardingAA(callee,
                        smallCallBean.getA_position(),
                        smallCallBean.getA_number());
                break;
            case 4:
                callForwardingAX(callee,
                        smallCallBean.getA_position(),
                        smallCallBean.getX_position(),
                        smallCallBean.getX_number());
                break;
            case 6:
            case 7:
                xiaohao(smallCallBean.getA_position(), smallCallBean.getX_number());
                break;
            default:
                ToastUtil.showMsg(XLManagerService.this,
                        "未知拨打方式");
                break;

        }
    }


    SmallCallBean smallCallBean;
    public String[] uplogBean;
    String myCallPhoneNum;

    public void setUplogBean(String[] mylogbean) {
        uplogBean = mylogbean;
    }


    void callMainDismissalert() {
        if (actiontag != null) {
            Intent intent = new Intent(actiontag);
            intent.putExtra(Constant.SHOW_DISMISS, "1");
            sendBroadcast(intent);
        }
    }

    void callMainShowalert() {
        if (actiontag != null) {
            Intent intent = new Intent(actiontag);
            intent.putExtra(Constant.SHOW_DISMISS, "0");
            sendBroadcast(intent);
        }
    }

    public void callAccrossServer(String callee, String task_id, String ai_number_id) {
        Tools.callbefore(XLManagerService.this, callee, ai_number_id,
                DataUtil.getRecordStatus(this) + "",
                new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        callMainDismissalert();
                        if (responseObj != null) {
                            BaseModel baseModel = (BaseModel) responseObj;
                            if (baseModel.getCode() == Constant.NET_OK) {
                                smallCallBean = JsonUtil
                                        .convertObject(baseModel,
                                                SmallCallBean.class);

                                if (smallCallBean != null && smallCallBean.getCall_id() != null) {
                                    if (!TextUtils.isEmpty(smallCallBean.getCall_type_id_jump_word())) {
                                        ToastUtil.showMsg(XLManagerService.this, smallCallBean.getCall_type_id_jump_word());
                                    }
                                    //保存到数据库
                                    saveCallNoteToDb(
                                            smallCallBean.getCall_id() + "",
                                            callee, smallCallBean.getCall_type_id() + "", ai_number_id);

                                    DataUtil.saveCallStatus(XLManagerService.this, smallCallBean.getCall_id() + "",
                                            task_id, callee, smallCallBean.getCall_type_id() + "",
                                            smallCallBean.getA_position(), smallCallBean.getX_position());
                                    Intent intent2 = new Intent(XLManagerService.this,
                                            XLManagerService2.class);
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putString(Constant.KEY_CALL_SERVICE_DO, "offhook");
                                    bundle2.putString(Constant.KEY_CALL_SERVICE, actiontag);
                                    bundle2.putString("callid", String.valueOf(smallCallBean.getCall_id()));
                                    bundle2.putString("callee", callee);
                                    bundle2.putString("taskid", task_id);
                                    intent2.putExtras(bundle2);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(intent2);
                                    } else {
                                        startService(intent2);
                                    }
                                } else {
                                    ToastUtil.showMsg(XLManagerService.this, "服务器数据异常,请稍后重试");
                                }
                            } else {
                                NetFailDoUtil.doWithCode(XLManagerService.this, baseModel.getCode(),
                                        baseModel.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        callMainDismissalert();

                        autoNext();

                        NetFailDoUtil.doWithFail(XLManagerService.this, "接口:addAction",
                                Constant.ERROR_INTER + "callee=" + callee + "task_id=" + task_id + "ai_number_id="
                                        + ai_number_id + "is_open_recording="
                                        + DataUtil.getRecordStatus(XLManagerService.this),
                                reasonObj);
                    }
                });
    }

    private CallNoteNew saveCallNoteToDb(String call_id, String callee, String calltype, String ai_number_id) {
        if (call_id != null) {
            CallNoteNew callNoteNew = new CallNoteNew();
            callNoteNew.setCallid(call_id);
            callNoteNew.setCallee(callee);
            callNoteNew.setIsuplog(0);
            callNoteNew.setIsuprecord(0);
            callNoteNew.setCall_type_id(calltype);
            callNoteNew.setCaller(NewSharePUtil.getValueWithContext(this, Constant.KEY_MOBILE));
            if (ai_number_id != null) {
                callNoteNew.setAi_number_id(ai_number_id);
            }
            SaveDataUtil.getInstance(this).saveToDb(this, call_id, JsonUtil.tojson(callNoteNew), Constant.SAVEKEY_TYPE_CALL);
            return callNoteNew;
        } else {
            return null;
        }

    }

    private void autoNext() {
        Intent s = new Intent(actiontag);
        s.putExtra(Constant.SHOW_DISMISS, "4");
        sendBroadcast(s);
    }


    public void xiaohao(int a_position, String x_number) {
        int id = a_position - 1;
        SimCardUtils.callAXB(this, id,
                "tel:" + x_number);
    }

    //a直呼
    public void aZhihu(String phoneNum, int a_postion) {

        SimCardUtils.callAXB(this, a_postion - 1,
                "tel:" + phoneNum);
    }

    //x直呼
    public void xZhihu(String phoneNum, int x_position) {

        SimCardUtils.callAXB(this, x_position - 1,
                "tel:" + phoneNum);
    }

    int waitcancleid = -1;
    int callslotid = -1;


    //aa呼转
    public void callForwardingAA(String callee, int a_postion, String a_number) {

        current_xNumber = a_number;
        waitcancleid = a_postion - 1;
        callslotid = waitcancleid;
        dowithforward(waitcancleid, callee);
    }

    //ax呼转
    public void callForwardingAX(String phoneNum, int a_postion, int x_postion, String x_number) {
        current_xNumber = x_number;
        waitcancleid = x_postion - 1;
        callslotid = a_postion - 1;
        dowithforward(waitcancleid, phoneNum);
    }

    private void dowithforward(int slotid, String callee) {
        callMainShowalert();
        Constant.CANCEL_FORWARD_TAG = null;
        Map<String, String> map = new HashMap<>();
        map.put(Constant.KEY_WAIT_CALCEL, slotid + "");
        map.put(Constant.KEY_WAIT_CALCEL_XNUMBER, current_xNumber);
        NewSharePUtil.saveWithContext(this, map);
        SimCardUtils.callAXB(this, slotid, "tel:" + "**21*" + callee + "%23");
        callf();
    }

    private void callf() {
        int waiting = Integer.parseInt(DataUtil.getWaitTime(this));
        if (waiting != 6) {
            new Handler(Looper.getMainLooper())
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callMainDismissalert();
                            if (current_xNumber != null) {//表示正在打呼转电话
                                SimCardUtils.call(XLManagerService.this, callslotid,
                                        "tel:" + current_xNumber);
                                current_xNumber = null;
                            }
                        }
                    }, waiting * 1000);
        }
    }


    private static String callee;
    private static String taskid;
    String ai_number_id;
    Long callingtime;

    void callCentrefresh() {
        Intent intent = new Intent(actiontag);
        intent.putExtra(Constant.SHOW_DISMISS, Constant.CALL_CENTEREFRESH);
        sendBroadcast(intent);
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {

            switch (state) {

                case TelephonyManager.CALL_STATE_IDLE:// 无任何状态
                    String callStatus = DataUtil.getCallStatus(XLManagerService.this);


                    if (!TextUtils.isEmpty(callStatus)) {

                        Intent intent = new Intent(XLManagerService.this,
                                XLManagerService2.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.KEY_CALL_SERVICE_DO, "uplog");

                        String[] tem = callStatus.split("-");
                        if (tem.length == 6) {
                            smallCallBean = null;
                            bundle.putString(Constant.KEY_CALL_SERVICE, actiontag);
                            bundle.putString("callid", tem[0]);
                            bundle.putString("taskid", tem[1]);
                            bundle.putString("callee", tem[2]);
                            bundle.putString("calltypeid", tem[3]);
                            bundle.putString("nowxnumber", NewSharePUtil.getValueWithContext(XLManagerService.this, Constant.KEY_WAIT_CALCEL_XNUMBER));
                            bundle.putString(Constant.KEY_WAIT_CALCEL, NewSharePUtil.getValueWithContext(XLManagerService.this, Constant.KEY_WAIT_CALCEL));

                            bundle.putString(Constant.KEY_CALL_EDNTIME_STR, DateUtil.getCurrentDate2());
                            intent.putExtras(bundle);
                            DataUtil.clearCallStatus(XLManagerService.this);
                            callCentrefresh();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(intent);
                            } else {
                                startService(intent);
                            }
                        }

                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 来电铃响时

                    String s = "1";
                    Constant.CANCEL_FORWARD_TAG = actiontag;
                    break;
                case TelephonyManager.CALL_STATE_RINGING://相应操作

                    String s2 = "1";

                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, phoneNumber);
        }
    };


    @Override
    public boolean onUnbind(Intent intent) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        return super.onUnbind(intent);

    }

    @Override
    public void onDestroy() {
        unregisetlistenphonestat(telM, phoneStateListener);
        unregisetlistenphonestat(telephonyManager0, myPhoneStateListener_0);
        unregisetlistenphonestat(telephonyManager1, myPhoneStateListener_1);
        try {
            if (myBroadcast != null) {
                unregisterReceiver(myBroadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isDestory = true;
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }


}





