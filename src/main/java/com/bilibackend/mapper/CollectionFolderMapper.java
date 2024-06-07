package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibackend.entity.CollectionFolder;
import com.bilibackend.vo.CollectionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 20126
 * @description 针对表【bili_collection_folder】的数据库操作Mapper
 * @createDate 2024-05-16 17:00:11
 * @Entity com.bilibackend.entity.CollectionFolder
 */
@Mapper
public interface CollectionFolderMapper extends BaseMapper<CollectionFolder> {
    List<CollectionVo> getVide0ByFolderId(@Param("folderId") Long folderId);
}




