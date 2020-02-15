package com.jielin.message.interceptor;

import com.jielin.message.util.MsgConstant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置拦截url
 *
 * @author yxl
 */
@Component
public class InterceptorConfiguration implements WebMvcConfigurer, ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private AccessInterceptor accessInterceptor;

    //当测试环境和正式环境需要设置调用白名单
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String activeProfile = context.getEnvironment().getActiveProfiles()[0];
        if (MsgConstant.PROD_PROFILE.equalsIgnoreCase(activeProfile)
                || MsgConstant.TEST_PROFILE.equalsIgnoreCase(activeProfile)) {
            //白名单安全策略，需要开放的时候启用
            //registry.addInterceptor(accessInterceptor).addPathPatterns("/**");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
