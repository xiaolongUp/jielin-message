package com.jielin.message.service;

import com.jielin.message.dto.ResponseDto;
import com.jielin.message.synpush.SmsMsgPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 处理用户注册和发送验证service
 *
 * @author yxl
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private SmsMsgPush smsMsgPush;

    public ResponseDto<Boolean> register(String phoneNumber) {
        boolean register = smsMsgPush.register(phoneNumber);
        if (register) {
            return new ResponseDto<>("发送成功");
        } else {
            return new ResponseDto<>(-1, "发送失败");
        }

    }

    //发送验证码
    public ResponseDto<Boolean> smscode(String phoneNumber, String[] param) {
        boolean register = smsMsgPush.smscode(phoneNumber, param);
        if (register) {
            return new ResponseDto<>("发送成功");
        } else {
            return new ResponseDto<>(-1, "发送失败");
        }
    }
}
