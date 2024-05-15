package com.bilibackend.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/13 23:36
 * @Version 1.0
 */
public interface OssService {

    /**
     * 文件上传前检查，为了实现秒传，如果此文件之前上传过，直接返回路径
     *
     * @param md5 前端根据文件计算出的MD5
     * @return 已有返回文件的路径，无则返回空
     */
    String uploadCheck(String md5);

    /**
     * 分片上传，由前端进行分片（必传的参数：file、index（从1开始）、total、md5）
     *
     * @param req req
     * @return 成功返回文件路径，失败返回null
     */
    String partUpload(HttpServletRequest req);


    /**
     * 文件上传的高级接口，无需前端分片（必传的参数：file，md5）
     *
     * @param req req
     * @return 成功返回文件路径，失败返回null
     */
    String transferManagerUpload(HttpServletRequest req);


    /**
     * 取消分片上传，参数为md5
     *
     * @param md5 MD5
     * @return 成功返回true，失败返回false
     */
    boolean cancelSimpleUpload(String md5);

    /**
     * 删除文件
     *
     * @param key key
     * @param md5 md5 md5可以传客户已不传，建议传
     * @return 成功返回true，失败返回false
     */
    boolean deleteFile(String key, String md5);

    /**
     * 判断文件是否存在
     *
     * @param key key
     * @return boolean
     */
    boolean isExist(String key);

    /**
     * 获取md5
     * @param url
     * @return
     */
    String getMd5(String url);

}
