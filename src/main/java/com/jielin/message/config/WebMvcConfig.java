package com.jielin.message.config;

import com.jielin.message.interceptor.AccessInterceptor;
import com.jielin.message.util.constant.MsgConstant;
import com.jielin.message.util.version.config.CustomRequestMappingHandlerMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport implements ApplicationContextAware {

    @Autowired
    private AccessInterceptor accessInterceptor;


    //自定义RequestMappingHandlerMapping,处理带版本号的请求
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        CustomRequestMappingHandlerMapping mappingHandlerMapping = new CustomRequestMappingHandlerMapping();
        String activeProfile = super.getApplicationContext().getEnvironment().getActiveProfiles()[0];
        if (MsgConstant.PROD_PROFILE.equalsIgnoreCase(activeProfile)
                || MsgConstant.TEST_PROFILE.equalsIgnoreCase(activeProfile)) {
//            Object[] objects = {accessInterceptor};
//            mappingHandlerMapping.setInterceptors(objects);
        }
        return mappingHandlerMapping;
    }

    //当测试环境和正式环境需要设置调用白名单，使用spring自带RequestMappingHandlerMapping时生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String activeProfile = super.getApplicationContext().getEnvironment().getActiveProfiles()[0];
        if (MsgConstant.PROD_PROFILE.equalsIgnoreCase(activeProfile)
                || MsgConstant.TEST_PROFILE.equalsIgnoreCase(activeProfile)) {
            //白名单安全策略，需要开放的时候启用
            //registry.addInterceptor(accessInterceptor).addPathPatterns("/**");
        }
    }

}
