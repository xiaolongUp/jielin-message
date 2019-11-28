package com.jielin.message.controller;

import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.GtAliasPo;
import com.jielin.message.synpush.UniPush.UniPushAliasHandler;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 个推绑定用户的别名信息
 *
 * @author yxl
 */
@RestController
@RequestMapping("/gt")
public class GtBindAliasController {

    @Autowired
    private UniPushAliasHandler uniPushAliasHandler;

    //绑定用户别名信息
    @PostMapping("/alias")
    public ResponseDto<Boolean> bindSingleAlias(@RequestBody GtAliasPo gtAliasPo) {

        Boolean result = uniPushAliasHandler.bindSingleAlias(gtAliasPo);

        if (result) {
            return new ResponseDto<>();
        } else {
            return new ResponseDto<>("别名绑定失败！");
        }

    }

    //查看是否绑定了别名
    @GetMapping("/alias")
    public ResponseDto<Object> hasBindAlias(@RequestParam("cid") String cid,
                                            @RequestParam("phone") String phone,
                                            @RequestParam("appType") String appType) {
        List<GtAliasPo> gtAliasPos = uniPushAliasHandler.hasBindAlias(cid, phone, appType);

        if (gtAliasPos.size() > 0) {
            return new ResponseDto<>();
        } else {
            return new ResponseDto<>("该别名暂未绑定！");
        }
    }


    //查看是否绑定了别名
    @GetMapping("/hasAlias")
    public Boolean BindAlias(@RequestParam("cid") String cid,
                             @RequestParam("phone") String phone,
                             @RequestParam("appType") String appType) {
        ResponseDto<Object> dto = hasBindAlias(cid, phone, appType);
        return dto.getCode() == 0;
    }

}
