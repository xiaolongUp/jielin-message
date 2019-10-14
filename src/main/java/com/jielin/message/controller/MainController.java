package com.jielin.message.controller;

import com.jielin.message.dto.MenuDto;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 登录登出以及主页的控制器
 *
 * @author yxl
 */
@Controller
public class MainController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/menu")
    @ResponseBody
    public ResponseDto<List<MenuDto>> getMenu() {
        return menuService.getAll();
    }
}
