package com.illcc.libbase.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * 日期时间工具类
 */
public class DateUtil {

    //-----------------------------
    public static String YEAR_DATE_FORMAT = "yyyy-MM-dd";
    public static String YEAR_DATE_FORMAT2 = "yyyy-MM-dd HH:mm:ss";


    public static long converTimeStr(String timestr){
        timestr ="2022-"+timestr;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d2=null;

        try {
            d2=sdf.parse(timestr);//将String to Date类型
        } catch (ParseException e) {
            e.printStackTrace();
        }



        return d2.getTime();

    }


    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        String datestr = null;
        SimpleDateFormat df = new SimpleDateFormat(DateUtil.YEAR_DATE_FORMAT);
        datestr = df.format(new Date());
        return datestr;
    }
    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate2() {
        String datestr = null;
        SimpleDateFormat df = new SimpleDateFormat(DateUtil.YEAR_DATE_FORMAT2);
        datestr = df.format(new Date());
        return datestr;
    }


    /**
     * 得到本周周一-周六日期 0为周一 6为周日
     *
     * @return
     */
    public static String getWeek(int day) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + day);
        Date first = cal.getTime();
        return formater.format(first);
    }



    public static String[] getMonthStardAndEndDays() {
        String[] result = new String[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);

        System.out.println(calendar.getTime());

        // 获取当月第一天和最后一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String firstday, lastday;
        Calendar cale = Calendar.getInstance();

        // 获取前月的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        firstday = format.format(cale.getTime());
        // 获取前月的最后一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        lastday = format.format(cale.getTime());
        result[0] = firstday;
        result[1] = lastday;
        return result;
    }


}