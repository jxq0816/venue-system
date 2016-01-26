package com.bra.modules.reserve.utils;

import com.bra.common.utils.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaobin on 16/1/5.
 */
public class TimeUtils {

    public static final int BENCHMARK = 30;

    /**
     * 获取两个时间的间隔时间数量(按照半小时计算)
     *
     * @param startTime
     * @param endTime
     * @param space     间隔时间
     * @return
     */
    public static int getTimeSpac(String startTime, String endTime, int space) {
        try {
            Date start = DateUtils.parseDate(startTime, "HH:mm:ss");
            Date end = DateUtils.parseDate(endTime, "HH:mm:ss");
            Long t = (end.getTime() - start.getTime()) / (space * 60 * 1000);
            return t.intValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String addMinutes(String time, int minute) throws ParseException {
        Date start = DateUtils.parseDate(time, "HH:mm");
        start = DateUtils.addMinutes(start, minute);
        return DateUtils.formatDate(start, "HH:mm");
    }

    public static int compare(String d1, String d2) {
        int result = d1.compareTo(d2);
        if (result > 0) {
            return 1;
        } else if (result == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    public static List<String> getTimeSpace(String startTime, String endTime) {
        List<String> timeList = TimeUtils.getTimeSpacList(startTime + ":00", endTime + ":00", TimeUtils.BENCHMARK);
        if (timeList.size() == 2 && startTime.equals(endTime)) {
            timeList.remove(0);
        } else {
            timeList.remove(endTime);
        }
        return timeList;
    }

    /**
     * 获取两个时间的间隔时间(半小时算)
     *
     * @param startTime
     * @param endTime
     * @param space     间隔时间(分钟)
     * @return
     * @throws ParseException
     */
    public static List<String> getTimeSpacList(String startTime, String endTime, int space) {
        try {
            List<String> times = Lists.newArrayList();
            int spac = getTimeSpac(startTime, endTime, space);
            Date start = DateUtils.parseDate(startTime, "HH:mm:ss");
            //Date end = DateUtils.parseDate(endTime, "HH:mm:ss");
            //Date spacTime;
            times.add(DateUtils.formatDate(start, "HH:mm"));
            for (int i = 0; i < spac; i++) {
                start = DateUtils.addMinutes(start, space);
                times.add(DateUtils.formatDate(start, "HH:mm"));
            }
            //times.add(DateUtils.formatDate(end, "HH:mm"));
            return times;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取两个时间的间隔时间(半小时算)
     *
     * @param startTime
     * @param endTime
     * @param space     间隔时间(分钟)
     * @return
     * @throws ParseException
     */
    public static List<String> getTimeSpacListValue(String startTime, String endTime, int space) {
        try {
            List<String> times = Lists.newArrayList();
            List<String> timeList = getTimeSpacList(startTime, endTime, space);
            for (int i = 0; i < timeList.size(); i++) {
                if (timeList.size() > i + 1) {
                    times.add(timeList.get(i) + "-" + timeList.get(i + 1));
                } else if (i % 2 == 0) {
                   // times.add(timeList.get(i));
                }
            }
            return times;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public static Map<String, Long> getNextDaysMap(Date now, int next) {
        List<Date> dateList = getNextDays(now, next);
        Map<String, Long> map = Maps.newLinkedHashMap();
        for (Date date : dateList) {
            map.put(DateUtils.formatDate(date, "yyyy-MM-dd") + "&nbsp;" + getWeekOfDate(date), date.getTime());
        }
        return map;
    }

    /**
     * 获取未来几天的时间
     *
     * @param now
     * @param next
     * @return
     */
    public static List<Date> getNextDays(Date now, int next) {
        List<Date> dateList = Lists.newLinkedList();
        Date date;
        for (int i = 0; i < next; i++) {
            date = DateUtils.addDays(now, i);
            dateList.add(date);
        }
        return dateList;
    }

    public static final String[] WEEK_DAYS = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = WEEK_DAYS;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getWeekType(Date dt) {
        String week = getWeekOfDate(dt);
        if ("周六".equals(week)) {
            return "2";
        } else if ("周日".equals(week)) {
            return "3";
        } else {
            return "1";
        }
    }

    public static void main(String[] args) throws ParseException {
        List<String> times = TimeUtils.getTimeSpacListValue("09:00:00", "23:00:00", 30);
        for (String time : times) {
            System.out.println(time);
        }
    }
}
