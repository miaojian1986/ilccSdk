package com.illcc.libbase.listener;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.util.Log;

import com.illcc.libbase.util.Constant;
import com.illcc.libbase.util.NetFailDoUtil;

public class MyPhoneStateListener extends PhoneStateListener {

    private int id;
    private Context context;
    private String phonstag;

    public MyPhoneStateListener(Context context, int id, String phonstag) {
        this.id = id;
        this.context = context;
        this.phonstag = phonstag;
    }


    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        Log.d("MyPhoneStateListener", "onCallForwardingIndicatorChanged=" + cfi + "id=" + id);
        Intent intent = new Intent(phonstag);
        intent.putExtra(Constant.OPTION, Constant.FORWARD_SELECT_VALUE);
        intent.putExtra("forstatus", cfi);
        intent.putExtra("simpistion", id);
        context.sendBroadcast(intent);
        if (id == 0) {
            Constant.SIM1_CALL_FORWARD = cfi;
        } else {
            Constant.SIM2_CALL_FORWARD = cfi;
        }
        if (!cfi) {
            NetFailDoUtil.doWithCode(context, 350,
                    id + "呼叫转移状态取消",
                    "");
        }
        super.onCallForwardingIndicatorChanged(cfi);
    }

}
