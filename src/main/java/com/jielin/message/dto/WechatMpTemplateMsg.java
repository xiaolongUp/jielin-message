package com.jielin.message.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * 微信小程序的模版消息
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
public class WechatMpTemplateMsg {

    //接收者openid
    private String touser;
    //模板ID
    private String template_id;
    //点击模板卡片后的跳转页面
    private String page;
    //data数据
    private TemplateItem data;

    //添加新属性
    public WechatMpTemplateMsg add(String key, String value) {
        data.put(key, new Item(value));
        return this;
    }

    //转换为字符串
    public String toJsonStr() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


    public WechatMpTemplateMsg() {
        this.data = new TemplateItem();
    }

    public class TemplateItem extends HashMap<String, Item> {

        private static final long serialVersionUID = 1L;

        TemplateItem() {
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class Item {
        //内容
        private Object value;
    }

}
