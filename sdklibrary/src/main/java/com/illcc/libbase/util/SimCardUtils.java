package com.illcc.libbase.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.illcc.ndk2.Tools;

import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;

public class SimCardUtils {


    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean isMobileDataOpen(Context context) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();

            Method method = ownerClass.getMethod("getMobileDataEnabled");
            return (Boolean) method.invoke(mConnectivityManager);
        } catch (Exception e) {
            return false;
        }
    }

    public static int getDefatultSlotId(Context context) {
        if (!isMobileDataOpen(context)) {
            return -1;
        }
        int dataSubId = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dataSubId = SubscriptionManager.getDefaultDataSubscriptionId();
            } else {
                dataSubId = getDataSubId(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return SubscriptionManager.getSlotIndex(dataSubId);
        }
        return dataSubId;
    }

    private static int getDataSubId(Context context) {
        int defaultDataSlotId = getDefaultDataSlotId(context);
        try {
            Object obj = Class.forName("android.telephony.SubscriptionManager").getDeclaredMethod("getSubId", int.class)
                    .invoke(null, defaultDataSlotId);
            if (obj != null) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                    return (int) (((long[]) obj)[0]);
                }
                return ((int[]) obj)[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultDataSlotId;
    }

    private static int getDefaultDataSlotId(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(context.getApplicationContext());
            if (subscriptionManager != null) {
                try {
                    Class<?> subClass = Class.forName(subscriptionManager.getClass().getName());
                    Method getSubID = subClass.getMethod("getDefaultDataSubscriptionInfo");
                    SubscriptionInfo subInfo = (SubscriptionInfo) getSubID.invoke(subscriptionManager);
                    if (subInfo != null) {
                        return subInfo.getSimSlotIndex();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Class cls = Class.forName("android.telephony.SubscriptionManager");
                Method getSubId;
                try {
                    getSubId = cls.getDeclaredMethod("getDefaultDataSubId");
                } catch (NoSuchMethodException e) {
                    getSubId = cls.getDeclaredMethod("getDefaultDataSubscriptionId");
                }
                int subId = (int) getSubId.invoke(null);
                int slotId;
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                    Method getSlotId = cls.getDeclaredMethod("getSlotId", long.class);
                    slotId = (int) getSlotId.invoke(null, (long) subId);
                } else {
                    Method getSlotId = cls.getDeclaredMethod("getSlotId", int.class);
                    slotId = (int) getSlotId.invoke(null, subId);
                }
                return slotId;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @SuppressLint("MissingPermission")
    public static int getSubcriptionId(Context context, int slotid) {
        int count = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //版本在21及以上
            SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
            int size = getSimCardCount(context);
            for (int i = 0; i < size; i++) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return 0;
                }
                if (slotid == i) {
                    SubscriptionInfo sir = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
                    if (sir != null) {
                        return sir.getSubscriptionId();
                    }

                }
            }
        }
        return -1;
    }

    /**
     * 是否有simCard1
     * simSlotIndex 0-卡1  1-卡2
     *
     * @param context
     * @return
     */
    public static boolean hasSimCard(Context context, int simSlotIndex) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                SubscriptionManager sm = SubscriptionManager.from(context);
                @SuppressLint("MissingPermission") List<SubscriptionInfo> sis = sm.getActiveSubscriptionInfoList();
                if (sis != null && sis.size() > 0) {
                    for (int i = 0; i < sis.size(); i++) {
                        SubscriptionInfo si = sis.get(i);
                        if (si.getSimSlotIndex() == simSlotIndex) {
                            result = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;  //false_指定卡没有
    }

    /**
     * 获取运营商
     * simSlotIndex =k1=0   卡2 =1
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getSimIccId(Context context, int simSlotIndex) {
        String iccid = "";
        try {
            if (hasSimCard(context, simSlotIndex)) {
                return Tools.getIccId3(context, simSlotIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iccid;
    }


    public static final String NO_PHONE_STATE_PERMISSION = "没有获取电话状态的权限";


    public static String getSimSerialNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        boolean allow = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        Log.d("ALLOW", "-----" + allow);
        if (allow) {
            return telephonyManager.getSimSerialNumber();
        } else {
            return NO_PHONE_STATE_PERMISSION;
        }
    }


    @SuppressLint("MissingPermission")
    public static int getSimCardCount(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);//得到电话管理器实例
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int phoneCount = mTelephonyManager.getPhoneCount();
            return phoneCount;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                int simCount = SubscriptionManager.from(context).getActiveSubscriptionInfoCount();
                return simCount;
            }
        }
        return 1;
    }

    //这个用于获取sim数量
    @SuppressLint("MissingPermission")
    public static void call(Context context, int id, String telNum) {
        try {
            TelecomManager telecomManager = null;
            List<PhoneAccountHandle> phoneAccountHandleList = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//版本在21及以上
                telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (telecomManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                    }
                }
            }
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse(telNum));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本在23及以上
                if (phoneAccountHandleList != null && phoneAccountHandleList.size() >= 2) {
                    intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandleList.get(id));
                }
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public static boolean callAXB(Context context, int id, String telNum) {

        try {
            TelecomManager telecomManager = null;
            List<PhoneAccountHandle> phoneAccountHandleList = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//版本在21及以上
                telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (telecomManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();

                    }
                }
            }
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse(telNum));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本在23及以上
                if (phoneAccountHandleList != null && phoneAccountHandleList.size() == 2) {
                    intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandleList.get(id));
                }
                context.startActivity(intent);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}


