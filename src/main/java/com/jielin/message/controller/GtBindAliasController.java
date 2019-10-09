package com.jielin.message.controller;

import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.GtAliasPo;
import com.jielin.message.synpush.UniPush.UniPushAliasHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            return new ResponseDto<>(-1, "别名绑定失败！");
        }

    }

    //查看是否绑定了别名
    @GetMapping("/alias")
    public ResponseDto<Object> hasBindAlias(@RequestParam("cid") String cid) {
        List<GtAliasPo> gtAliasPos = uniPushAliasHandler.hasBindAlias(cid);

        if (gtAliasPos.size() > 0) {
            return new ResponseDto<>();
        } else {
            return new ResponseDto<>(-1, "该别名暂未绑定！");
        }
    }


}
