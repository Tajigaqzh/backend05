package com.bilibackend.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.entity.History;
import com.bilibackend.mapper.HistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 13:06
 * @Version 1.0
 */
@Slf4j
@Component
public class HistoryTask {
    @Autowired
    private HistoryMapper historyMapper;


    /**
     * 凌晨刷新，删除20天之前的历史记录
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredHistory() {
        long time = DateUtil.offsetDay(new Date(), -20).getTime();
        QueryWrapper<History> historyQueryWrapper = new QueryWrapper<>();
        historyQueryWrapper.ge("recordTime",time);
        historyMapper.delete(historyQueryWrapper);
    }


}
