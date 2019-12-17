package com.jielin.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TextDingMsg {

    private String msgtype = "text";

    private Text text;

    @Getter
    @Setter
    @AllArgsConstructor
    public static
    class Text {
        //内容
        private String content;
    }
}
