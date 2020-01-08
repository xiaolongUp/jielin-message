package com.jielin.message.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jielin.message.dao.mysql.MsgPushDao;
import com.jielin.message.dto.PageData;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.MsgPushPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 配置相关的service
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "msgPush")
public class SettingService {

    @Autowired
    private MsgPushDao msgPushDao;

    @Cacheable(key = "#root.methodName+':'+#operateType+':'+#platform+':'+#userType")
    public List<MsgPushPo> selectEnableByCondition(Integer operateType, Integer platform, String userType) {
        return msgPushDao.selectEnableByCondition(operateType, platform, userType);
    }

    @Cacheable(key = "#root.methodName+':'+#pageNo+':'+#pageSize")
    public ResponseDto<PageData<MsgPushPo>> selectPage(int pageNo, int pageSize) {
        ResponseDto<PageData<MsgPushPo>> responseDto = new ResponseDto<>();
        //获取分页的数据信息
        PageHelper.startPage(pageNo, pageSize);
        List<MsgPushPo> msgPushPos = msgPushDao.selectAll();
        PageInfo<MsgPushPo> pageInfo = new PageInfo<>(msgPushPos);
        //封装自定义的分页对象
        PageData<MsgPushPo> pageData = new PageData<>(pageInfo.getTotal(), msgPushPos);
        responseDto.setBody(pageData);
        return responseDto;
    }

    @Cacheable(key = "#root.methodName+':'+#pageNo+':'+#pageSize+':'+#po.toString()")
    public ResponseDto<PageData<MsgPushPo>> selectPageByCondition(int pageNo, int pageSize, MsgPushPo po) {
        ResponseDto<PageData<MsgPushPo>> responseDto = new ResponseDto<>();
        //获取分页的数据信息
        PageHelper.startPage(pageNo, pageSize);
        List<MsgPushPo> msgPushPos = msgPushDao.selectAllByCondition(po);
        PageInfo<MsgPushPo> pageInfo = new PageInfo<>(msgPushPos);
        //封装自定义的分页对象
        PageData<MsgPushPo> pageData = new PageData<>(pageInfo.getTotal(), msgPushPos);
        responseDto.setBody(pageData);
        return responseDto;
    }

    @CacheEvict(allEntries = true)
    public ResponseDto deleteById(Integer id) {
        ResponseDto<String> responseDto;
        if (Optional.ofNullable(id).isPresent()) {
            int count = msgPushDao.deleteById(id);
            if (count > 0) {
                responseDto = new ResponseDto<>();
                responseDto.setBody("删除成功！");
            } else {
                responseDto = new ResponseDto<>("删除的数据不存在！");
            }
        } else {
            responseDto = new ResponseDto<>("删除的数据id不能为null!");
        }
        return responseDto;
    }

    @CacheEvict(allEntries = true)
    public ResponseDto addRecord(MsgPushPo msgPushPo) {
        ResponseDto<String> responseDto;
        List<MsgPushPo> msgPushPos = msgPushDao.selectAllByCondition(msgPushPo);
        if (msgPushPos.size() > 0) {
            return new ResponseDto("不可以重复插入！");
        }

        int result = msgPushDao.addRecord(msgPushPo);

        if (result > 0) {
            responseDto = new ResponseDto<>();
            responseDto.setBody("新增成功！");
        } else {
            responseDto = new ResponseDto<>("新增失败！");
        }
        return responseDto;
    }

    @CacheEvict(allEntries = true)
    public ResponseDto updateRecord(MsgPushPo msgPushPo) {
        ResponseDto<String> responseDto;
        int result = msgPushDao.updateRecord(msgPushPo);

        if (result > 0) {
            responseDto = new ResponseDto<>();
            responseDto.setBody("更新成功！");
        } else {
            responseDto = new ResponseDto<>("更新失败！");
        }
        return responseDto;
    }
}
