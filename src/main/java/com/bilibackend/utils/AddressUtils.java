package com.bilibackend.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取地址类
 *
 * @author ruoyi
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);


    public static final String GBK = "GBK";


    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip) {
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }

        try {
            String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", GBK);
            if (StringUtils.isEmpty(rspStr)) {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            JSONObject obj = JSONObject.parseObject(rspStr);
            return obj.getString("addr");

//            String region = obj.getString("pro");
//            String city = obj.getString("city");

//            System.out.println(obj.getString("addr"));
//            return String.format("%s %s", region, city);
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", e);
        }

        return UNKNOWN;
    }

    public static void main(String[] args) {
//        182.92.81.46   150.158.122.237

        String realAddressByIP = getRealAddressByIP("150.158.122.237");
        System.out.println(realAddressByIP);
    }
}
