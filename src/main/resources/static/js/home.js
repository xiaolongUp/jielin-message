layui.config({
    base: './js/'
}).extend({ //设定模块别名
    menu: 'menu',
    admin: 'admin'
});
layui.use(['jquery','admin', 'menu'], function () {
    var $ = layui.jquery,
        menu = layui.menu,
        admin = layui.admin;
    $(function () {
        // 左侧导航区域（可配合layui已有的垂直导航）
        $.ajax({
            url: contextPath + "menu",
            type: 'get',
            dataType: 'json',
            success: function(res) {
                menu.getJsonMenu(res);
            },
            error: function(){
                layer.alert("系统异常，请联系管理员", function () {
                    //退出
                    window.location.href = "/logout";
                });
            }
        });
    });
});