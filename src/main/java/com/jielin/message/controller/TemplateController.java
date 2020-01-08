package com.jielin.message.controller;


import com.jielin.message.dao.mongo.TemplateDao;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private TemplateDao templateDao;

    @PostMapping("/")
    public ResponseDto addTemplate(@RequestBody Template template) {
        templateDao.insert(template);
        return new ResponseDto();
    }

}
