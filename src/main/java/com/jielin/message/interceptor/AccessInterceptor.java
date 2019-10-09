package com.jielin.message.interceptor;

import com.google.gson.Gson;
import com.jielin.message.config.AccessWhiteListConfig;
import com.jielin.message.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理所有请求访问权限
 *
 * @author yxl
 */
@Component
@Slf4j
public class AccessInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private AccessWhiteListConfig config;

    @Autowired
    private Gson gson;

    private static final List<String> passUri = new ArrayList<>();

    @PostConstruct
    public void initPassUri() {
        passUri.add("^/user.*$");
        passUri.add("^/jg.*$");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean canPass = false;

        //只有通过白名单的ip或者调用部分手机sdk接口可以通过
        boolean whiteList = config.canAccess(request.getRemoteHost());
        for (String uri : passUri) {
            if (request.getServletPath().matches(uri)) {
                canPass = true;
                break;
            }
        }
        canPass = canPass || whiteList;
        if (!canPass) {
            response.setContentType("application/json");
            response.setHeader("Cache-Control", "no-cache, no-store");
            response.setHeader("Pragma", "no-cache");
            long time = System.currentTimeMillis();
            response.setDateHeader("Last-Modified", time);
            response.setDateHeader("Date", time);
            response.setDateHeader("Expires", time);
            response.setStatus(HttpStatus.SC_OK);
            ResponseDto<Object> responseDto = new ResponseDto<>(401, "ip unauthorized！");
            response.getOutputStream().write(gson.toJson(responseDto).getBytes());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }
        return canPass;
    }
}
