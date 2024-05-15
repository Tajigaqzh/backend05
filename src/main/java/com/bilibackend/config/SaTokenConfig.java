package com.bilibackend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 13:45
 * @Version 1.0
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 无需登录即可访问的路径
     * 还要继续加，比如
     * 推荐，查询分类等
     */
    private final String[] anonymous = {
            "/user/login",
            "/user/demo",
            "/user/register",
            //审核视频图片反馈
            "/tencent/callback/ban",
            //srs回调
            "/callback/play",
            "/callback/unPublish",
            "/callback/publish",
            //查询已在线的直播，这个接口需要改造，加入redis缓存
            "/srs/streams",
            "/srs/stop",
            "/video/pageSimple",
            "/video/demo"

//            "/srs/publish",
    };

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
//        List<String> excludePathPattern = new ArrayList<>();
        registry.addInterceptor(new SaInterceptor(handle -> {
                    //登录的，
                    SaRouter.match("/**").notMatch(anonymous).check(r -> {
                        StpUtil.checkLogin();
                    });
                }))
                .addPathPatterns("/**");
    }

}
