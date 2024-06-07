package com.bilibackend.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/17 4:29
 * @Version 1.0
 */
public class HpDateUtils {
    public static List<String> getRedisDateKey(Integer days) {
        return HpDateUtils.getRedisDateKey(days, null);
    }


    public static List<String> getRedisDateKey(Integer days, String prefix) {
        Date currentDate = new Date();
        ArrayList<String> stringArrayList = new ArrayList<>();
        if (days < 0) {
            for (int i = days; i <= 0; i++) {
                if (prefix != null) {
                    stringArrayList.add(prefix + DateUtil.offsetDay(currentDate, i).toString("yyyy-MM-dd"));
                } else {
                    stringArrayList.add(DateUtil.offsetDay(currentDate, i).toString("yyyy-MM-dd"));
                }
            }
        }
        if (days >= 0) {
            for (int i = 0; i <= days; i++) {
                if (prefix != null) {
                    stringArrayList.add(prefix + DateUtil.offsetDay(currentDate, i).toString("yyyy-MM-dd"));
                } else {
                    stringArrayList.add(DateUtil.offsetDay(currentDate, i).toString("yyyy-MM-dd"));
                }
            }
        }
        return stringArrayList;
    }

    /**
     * @param interval  间隔天数 default 4
     * @param lastTimes 获取前面的几个记录 default 1
     * @return
     */
    public static List<String> getLatestLastRedisKey(Integer interval, Integer lastTimes) {
        return HpDateUtils.getLatestLastRedisKey(interval,lastTimes,null,null);
    }

    public static List<String> getLatestLastRedisKeyWithSuffix(Integer interval, Integer lastTimes, String suffix) {
        return HpDateUtils.getLatestLastRedisKey(interval, lastTimes, null, suffix);
    }

    public static List<String> getLatestLastRedisKeyWithPrefix(Integer interval, Integer lastTimes, String prefix) {
        return HpDateUtils.getLatestLastRedisKey(interval, lastTimes, prefix, null);
    }

    public static List<String> getLatestLastRedisKey(Integer interval, Integer lastTimes, String prefix, String suffix) {
        if (ObjectUtil.isNull(interval)) interval = 4;
        if (ObjectUtil.isNull(lastTimes)) lastTimes = 1;
        Date date = new Date();
        DateTime dateTime = new DateTime(date);

        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i <= lastTimes; i++) {
            DateTime last = DateUtil.offsetDay(dateTime, -(interval) * i);
            String s = DateUtil.offsetDay(last, -(last.dayOfMonth() % interval)).toString("yyyy-MM-dd");
            if (suffix != null && prefix != null) {
                strings.add(prefix + s + suffix);
            } else if (suffix != null) {
                strings.add(0, s + suffix);
            } else if (prefix != null) {
                strings.add(0, prefix + s);
            } else {
                strings.add(0, s);
            }
        }

        return strings;
    }
}
