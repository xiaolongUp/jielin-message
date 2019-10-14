package com.jielin.message.service;

import com.jielin.message.dao.mysql.MenuDao;
import com.jielin.message.dto.MenuDto;
import com.jielin.message.dto.ResponseDto;
import com.jielin.message.po.MenuPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu操作
 *
 * @author yxl
 */
@Service
public class MenuService {
    @Autowired
    private MenuDao menuDao;

    //获取所有的menu
    public ResponseDto<List<MenuDto>> getAll() {
        List<MenuDto> menuDto = new ArrayList<>();
        List<MenuPo> menuPos = menuDao.selectAllParent();
        for (MenuPo menuPo : menuPos) {
            MenuDto menu = new MenuDto(menuPo);
            List<MenuPo> childPos = menuDao.selectAllChild(menuPo.getId());
            List<MenuDto> childDto = new ArrayList<>();
            childPos.forEach(menuPo1 -> {
                MenuDto child = new MenuDto(menuPo1);
                childDto.add(child);
            });
            menu.setChildren(childDto);
            menuDto.add(menu);
        }
        return new ResponseDto(menuDto);
    }
}
