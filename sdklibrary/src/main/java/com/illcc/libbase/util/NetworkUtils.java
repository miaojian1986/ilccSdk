package com.illcc.libbase.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.illcc.libnet.okhttp.listener.DisposeDataListener;
import com.illcc.libnet.okhttp.request.RequestParams;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.WeakHashMap;

public class NetworkUtils {
    public static final String NET_TYPE_WIFI = "WIFI";
    public static final String NET_TYPE_MOBILE = "MOBILE";
    public static final String NET_TYPE_NO_NETWORK = "no_network";
    public static ConnectivityManager conManager;

    private Context mContext = null;

    public NetworkUtils(Context pContext) {
        this.mContext = pContext;
    }

    public static final String IP_DEFAULT = "0.0.0.0";

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    private static long lastrequesttime = 0;
    private static String lasturl = "";
    private static WeakHashMap<String, Object> lastparamap = new WeakHashMap<>();

    static boolean isLastRequestparm(String url, Map<String, Object> requestparam) {
        return url.equals(lasturl) && requestparam.equals(lastparamap);
    }
    static boolean isShorTime() {
        return System.currentTimeMillis() - lastrequesttime <= 2000;
    }
    void checkShortRequest(String url, RequestParams params, DisposeDataListener disposeDataListener){
        //TODO
    }


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }


    public static boolean isConnectInternet(final Context pContext) {
        conManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }

        return false;
    }

    //同意上传和有条件以及符合设定网络条件就可以上传
    public static boolean canUploadRecord(Context c) {
        if (getConnectivityStatus(c) == 1) {//仅wifi
            return true;
        }
        return false;
    }

    //同意上传和有条件以及符合设定网络条件就可以上传
    public static boolean canUploadRecordN(Context c) {

        String isagree = NewSharePUtil.getValueWithContext(c, Constant.SHARE_KEY_RECORD_NEW);
        if (isagree != null && isagree.equals("1") && getConnectivityStatus(c) == 1) {
            return true;
        }
        return false;
    }


    public static boolean isConnectWifi(final Context pContext) {
        conManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conManager.getActiveNetworkInfo();
        //判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        int netType = -1;
        if (info != null) {
            netType = info.getType();
        }
        if (netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    public static boolean isConnectMoblie(final Context pContext) {
        conManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conManager.getActiveNetworkInfo();
        //判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        int netType = -1;
        if (info != null) {
            netType = info.getType();
        }
        if (netType == ConnectivityManager.TYPE_MOBILE) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    public static boolean isConnectMoblieAndWiFI(final Context pContext) {
        conManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conManager.getActiveNetworkInfo();
        //判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        int netType = -1;
        if (info != null) {
            netType = info.getType();
        }
        if (netType == ConnectivityManager.TYPE_MOBILE || netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    public static String getNetTypeName(final int pNetType) {
        switch (pNetType) {
            case 0:
                return "unknown";
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA: Either IS95A or IS95B";
            case 5:
                return "EVDO revision 0";
            case 6:
                return "EVDO revision A";
            case 7:
                return "1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDen";
            case 12:
                return "EVDO revision B";
            case 13:
                return "LTE";
            case 14:
                return "eHRPD";
            case 15:
                return "HSPA+";
            default:
                return "unknown";
        }
    }

    public static String getIPAddress() {
        try {
            final Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaceEnumeration.hasMoreElements()) {
                final NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();

                final Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();

                while (inetAddressEnumeration.hasMoreElements()) {
                    final InetAddress inetAddress = inetAddressEnumeration.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

            return NetworkUtils.IP_DEFAULT;
        } catch (final SocketException e) {
            return NetworkUtils.IP_DEFAULT;
        }
    }

    public String getConnTypeName() {
        conManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NET_TYPE_NO_NETWORK;
        } else {
            return networkInfo.getTypeName();
        }
    }

}
