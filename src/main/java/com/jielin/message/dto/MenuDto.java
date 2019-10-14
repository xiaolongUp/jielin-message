package com.jielin.message.dto;

import com.jielin.message.po.MenuPo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作的tabMenu数据
 */
@Data
@NoArgsConstructor
public class MenuDto implements Serializable {

    private Integer id;

    //名称
    private String name;

    //展示图标
    private String icon;

    //访问地址
    private String url;

    //子节点
    private List<MenuDto> children;

    public MenuDto(MenuPo menuPo){
        this.id = menuPo.getId();
        this.name = menuPo.getName();
        this.icon = menuPo.getIcon();
        this.url = menuPo.getUrl();
        this.children = new ArrayList<>();
    }

    private static final long serialVersionUID = 1L;

}
