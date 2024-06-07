package com.bilibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.dto.MoveCollectDto;
import com.bilibackend.entity.Collection;
import com.bilibackend.entity.CollectionFolder;
import com.bilibackend.service.CollectionFolderService;
import com.bilibackend.service.CollectionService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.validate.AddGroup;
import com.bilibackend.validate.UpdateGroup;
import com.bilibackend.vo.CollectionVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 16:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/collect")
public class CollectController {


    @Autowired
    private CollectionFolderService collectionFolderService;


    @Autowired
    private CollectionService collectionService;

    /**
     * 加入收藏夹吃灰
     * 参数，收藏夹iD，视频id，加锁
     *
     * @return
     */
    @PostMapping("/addCollection")
    public Result collect(@RequestBody Collection collection) {
        boolean save = collectionService.addCollection(collection);
        if (save) return Result.ok();
        return Result.error(ResultCode.INSERT_ERROR);
    }


    /**
     * 强制删除收藏夹，并清空视频
     * 收藏夹id
     *
     * @return
     */
    @GetMapping("/removeCollection")
    public Result removeCollect(@RequestParam("folderId") Long folderId) {
        boolean b = collectionFolderService.removeFolder(folderId);
        if (b) return Result.ok();
        return Result.error(ResultCode.DELETE_ERROR);
    }

    /**
     * 移动收藏内容
     * fromId
     * toId
     * 收藏内容的ids
     *
     * @return
     */
    @PostMapping("/moveCollection")
    public Result batchMoveCollection(@RequestBody @Validated MoveCollectDto moveCollectDto) {
        boolean b = collectionFolderService.batchMoveCollection(moveCollectDto.getFromId(), moveCollectDto.getToId(), moveCollectDto.getIds());
        if (b) return Result.ok();
        return Result.error(ResultCode.UPDATE_ERROR);
    }

    /**
     * 创建收藏夹，
     * 创建时间
     * 参数，uid，
     * 描述
     *
     * @return
     */
    @PostMapping("/createFolder")
    public Result createCollectFolder(@RequestBody @Validated(AddGroup.class) CollectionFolder collectionFolder) {
        boolean save = collectionFolderService.save(collectionFolder);
        if (save) return Result.ok();
        return Result.error(ResultCode.INSERT_ERROR);
    }

    /**
     * 删除收藏夹并清除其中的收藏
     * 参数id
     *
     * @return
     */
    @GetMapping("/removeFolder")
    public Result removeCollectFolder(@Validated @NotNull Long folderId) {
        boolean b = collectionFolderService.removeFolder(folderId);
        if (b) return Result.ok();
        return Result.error(ResultCode.DELETE_ERROR);
    }

    /**
     * 更新收藏夹
     * 更新的时候不用填userId
     *
     * @return
     */
    @PostMapping("/updateFolder")
    public Result renameCollectFolder(@RequestBody @Validated(UpdateGroup.class) CollectionFolder collectionFolder) {
        collectionFolder.setUserId(null);
        boolean update = collectionFolderService.updateById(collectionFolder);
        if (update) return Result.ok();
        return Result.error(ResultCode.UPDATE_ERROR);
    }

    /**
     * 根据收藏夹id查询该收藏夹内的收藏内容
     *
     * @return 参数收藏夹id
     */
    @GetMapping("/detail")
    public Result queryCollection(@RequestParam("folderId") @Valid @NotNull Long folderId) {
        List<CollectionVo> collectionsByFolderId = collectionFolderService.getCollectionsByFolderId(folderId);
        return Result.ok(collectionsByFolderId);
    }

    /**
     * 查询用户的收藏夹
     *
     * @return
     */
    @GetMapping("/getMyFolder")
    public Result queryCollectFolder(Long userId) {
        QueryWrapper<CollectionFolder> collectionFolderQueryWrapper = new QueryWrapper<>();
        collectionFolderQueryWrapper.eq("userId", userId);
        List<CollectionFolder> list = collectionFolderService.list(collectionFolderQueryWrapper);
        return Result.success(list);
    }
}
