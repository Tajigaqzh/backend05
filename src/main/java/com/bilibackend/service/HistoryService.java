package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.entity.History;
import com.bilibackend.vo.PageResultVo;

import java.util.List;

/**
* @author 20126
* @description 针对表【bili_history】的数据库操作Service
* @createDate 2024-05-16 01:49:03
*/
public interface HistoryService extends IService<History> {

    PageResultVo historyPage(Long page, Long size,Long uid);


    List<History> searchCondition(History history);


    boolean addHistory(History history);


    boolean updateHistory(History history);

}
