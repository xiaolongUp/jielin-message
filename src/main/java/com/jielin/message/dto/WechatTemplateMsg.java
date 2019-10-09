package com.jielin.message.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * 微信公众号的模版消息
 *
 * @author yxl
 */
@Data
@Accessors(chain = true)
public class WechatTemplateMsg {

    //接收者openid
    private String touser;
    //模板ID
    private String template_id;
    //模板跳转链接
    private String url;
    //data数据
    private TemplateItem data;

    //添加新属性
    public WechatTemplateMsg add(String key, String value, String color){
        data.put(key, new Item(value, color));
        return this;
    }

    //转换为字符串
    public String toJsonStr() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


    public WechatTemplateMsg() {
        this.data = new TemplateItem();
    }

    public class TemplateItem extends HashMap<String, Item> {

        private static final long serialVersionUID = -3728490424738325020L;

        TemplateItem() {
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class Item {
        //内容
        private Object value;
        //字体颜色
        private String color;

    }

}
