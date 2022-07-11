package com.illcc.libbase.util;


import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    public static final String KEY_TOKEN = "token";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_A_NUMBER = "a_number";
    public static final String KEY_X_NUMBER = "x_number";
    public static final String KEY_IMAG = "image";
    public static final String KEY_NAME = "name";
    public static final String KEY_X_XIAOHAO = "x_xiaohao";
    public static final String KEY_ACTIVITY = "keyacitive";

    public static final String KEY_A_POSITON = "aposition";
    public static final String KEY_CALL_EDNTIME_STR = "callendtimestr";

    public static final String KEY_AUTO_LOGINICCID = "autologiniccid";

    public static final String KEY_WAIT_CALCEL = "waitcancel";
    public static final String KEY_WAIT_CALCEL_XNUMBER = "waitcancelxnumber";

    public static final String KEY_CUSTOM_TIME = "keycustomtime";
    public static final String EVENT_CANCEL_FORWARD = "event_cancle";

    public static final String AGREE_ALERT = "请先同意用户协议";

    //本机校验
    public static final String SELFJIAOYAN_BUSSI = "f8eee44ded7c4948b4b6a15e9ed52263";

    public static  List<String> saveRecord = new ArrayList<>();

    public static final String CALL_CENTEREFRESH = "callcentralrefresh";

    public static final String ALERT_JIAOYAN = "校验时请确保主叫卡开启流量";

    public static final String ERROR_INTER = "接口调用异常";


    public static final String KEY_WAIT_CALL_FORWARD_TYPE = "key_wait_time_type";
    public static final String KEY_EXPROE_TIME = "exptime";
    public static final String MODEL_FUM10 = "PFUM10";
    public static final String JIAOYAN_TIME = "jiaoyantime";
    public static final String KEY_ACTIONTAG = "actiontag";


    public static final String FORWARD_SELECT_VALUE = "forwardsuccess";
    public static final String KEY_WAIT_TIME = "keywaittime";

    public static final String KEY_RECORDPATH = "recordpath";



    public static String CANCEL_FORWARD_TAG;

    public static final String OPTION = "option";

    public static final String JOBSERVICE_NAME = "com.illcc.ic_call.servce.XLManagerService2";
    public static boolean SIM1_CALL_FORWARD = false;
    public static boolean SIM2_CALL_FORWARD = false;

    public static final String EVENT_REGIST_CALL_STATE_LISTEN = "regisetcallstatelisten";


    public static final String KEY_MAINCARD = "maincard";
    final static String SHRE_FILE_NAME = "icc_share";

    public static final String SHOW_DISMISS = "show-dismiss";

    public static final int FRSH_TIME_NOW = 0;


    public static final String SAVEKEY_TYPE_CALL = "call";
    public static final int CAMERA_REQUEST_CODE = 909;


    public static final String EVENT_TAKE_PROGRESS = "event_take_progress";


    public static final String EVENT_PLAY_VOICE = "event_play_voice";

    public static final int EVENT_PLAY_VOICE_PLAY = 1;
    public static final int EVENT_PLAY_VOICE_PAUSE = 2;
    public static final int EVENT_PLAY_VOICE_CHANGE = 3;
    public static final int EVENT_PLAY_VOICE_RESTART = 4;

    public static final int REQUEST_CAMERA = 909;
    public static final int PICS_REQUEST_CODE = 100;
    public static final String EXTRA_INT_ID = "extra_int_id";
    public static final String EXTRA_INT_ID2 = "extra_int_id2";
    public static final String ICON_TJ = "icon_tj.png";
    public static final String EVENT_DEL_NUMBERLIST_ITEM = "event_delete_numberlist_item";
    public static final String EVENT_FROM_MYDOC_PRESS_PIC = "event_from_mydoc_presspic";
    public static String NO_NEED_REQUEST = "当前为最新版本，无需更新";
    public static String FANKUI_SUCCESS = "您的意见我们已经收到，感谢您的反馈！";
    public static String SHARE_KEY_RECORD_NEW = "share-key-record-new";


    public static final String ICON_SMALL_ADD = "icon_small_add.png";
    public static final String ICON_OPEN_AUTO = "icon_open_auto.png";
    public static final String EVENT_UPDATE_MOBILE = "event_update_mobile";


    public static final String EXTRA_NICK = "extranick";


    public static final String UN_SELECT_MAIN_CARD = "请选择主叫卡所在卡槽";


    public static final String ICON_TODAY_TASK_S = "icon_today_task_s.png";

    public static final String ICON_YWC_S = "icon_ywc_s.png";
    public static final String ICON_YWC = "icon_ywc.png";


    public static final float RADIUS_HEIGHT = 0.7f;
    public static final float RADIUS_WIDTH = 0.65f;

    public static final String ICON_CLOSE_AUTO = "icon_close_auto.png";


    public static final String EVENT_UPDAT_RECORD_PATH2 = "event_update_record_path2";

    public static final String EVENT_UPDAT_RECORD_PATH = "event_update_record_path";

    public static final String ICON_TASK_S = "icon_task_s.png";


    public static final String ICON_WWC_S = "icon_wwc_s.png";
    public static final String ICON_WWC = "icon_wwc.png";


    public static final String ICON_CALL_SETTING = "icon_call_setting.png";

    public static final String ICON_SETTING = "icon_setting.png";


    public static final String ICON_USER_DEFAULT = "icon_user_default.png";


    public static final String ICON_RIGHT_ARROW_WHTIE = "icon_right_arrow_white.png";


    public static final String ICON_RIGHT_ARROR = "icon_right_arror.png";


    //直呼
    public static final String CALL_TYPE_ZHIHU = "callTypeZhihu";

    public static final String CALL_TYPE_OTHER = "";
    public static final String ICON_P2_ICONNOTE = "icon_note.png";
    public static final String ICON_P2_SIM1 = "icon_sim_1.png";
    public static final String ICON_P2_ICON_PHONE = "icon_phone.png";


    public static final String DIR_RECORD = "record";


    public static final String ICON_BEGIN_ICON_UPDATE_CLOSE = "begin_icon_update_close.png";


    public static final String ICON_UPDATE_BG = "begin_icon_update_bg.png";


    public static final String ICON_SPLASH_WORD = "icon_spalsh_word.png";
    public static final String KEY_CALL_SERVICE = "key_call_service";
    public static final String KEY_CALL_SERVICE_DO = "key_call_service_do";


    public static final String ICON_BASE = "";


    public static final String ICON_BACK = ICON_BASE + "icon_back.png";
    public static final String ICON_BACK_WHITE = ICON_BASE + "icon_back_white.png";
    public static final String CAMERA_REQUEST_STR = "camerasrequeststr";


    public static final String PICS_REQUEST_STR = "picsrequeststr";

    public static final String STR_SET_RECORDING = "请自行前往系统打开通话录音";

    public static final String PASSWORD_RULE = "密码至少八个字符";


    public static final String MAIN_NAME = "乐联";


    public static final int ACHE_VALUE_CARD_0 = 0;
    public static final int ACHE_VALUE_CARD_1 = 1;
    public static final int ACHE_VALUE_CARD_2 = 2;
    //任选值
    public static final int ACHE_VALUE_CARD_3 = 3;


    public static final String CALL_TYPE_SIP = "sip";

    public static final String EVENT_REFRSH_NOTE_LIST = "event_refresh_note_list";
    public static final String EVENT_LOADMORE_NOTE_LIST = "event_loadmore_note_list";

    public static final String EVENT_READ_EXCEL_RESULT = "event_read_excel_result";

    public static final String EVENT_FRESH_PHONELIST = "event_fresh_phonelist";


    public static final String EVENT_UPIMGE_OCR = "event_upimage_ocr";
    public static final String EVENT_UPIMGE_OCR_RESULT = "event_upimage_ocr_result";


    public static final String SHARE_FILE_NAME = "kk";

    public static final int NET_OK = 200;


    public static final String EVENT_GETACTIVITY_RESULT = "event_getactivity_reseult";
    public static final String EVENT_UPDATE_USERLOCALLTYPES = "event_update_userlocal_calltypes";


    public static final String KEY_CALL_TYPE = "keycalltype";

    public static String APP_NAME = "乐联";


    public static final String EXTRA_PLUGIN_PARAM = "extra_plugin_param";

    public static final String EVENT_SELECT_PICS_MULTI = "event_select_pics_multi";


    //显示设置
    public static final String EVENT_SKIP_SETTING = "event_skip_setting";


    public static final String EVENT_UPDATE_USERNAME = "event_update_username";
    public static final String EVENT_UPDATE_USERHEAD = "event_update_userhead";


    public static final String EVENT_GET_PICS = "event_get_pics";

    public static int PIC_MAX = 1;


    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceRunning(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(Integer.MAX_VALUE);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


}
