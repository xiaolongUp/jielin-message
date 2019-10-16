var form;
layui.use(['jquery', 'table', 'form'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    form = layui.form;
    var pageCurr;
    tableIns = table.render({
        elem: '#pushList',
        url: contextPath + "setting/msgPush",
        request: {
            pageName: 'pageNo',
            limitName: 'pageSize'
        },
        parseData: function (res) { //res 即为原始返回的数据
            return {
                "code": res.code, //解析接口状态
                "msg": res.message, //解析提示文本
                "count": res.data.total, //解析数据长度
                "data": res.data.data //解析数据列表
            };
        },
        limits: [10, 15, 20],
        page: true,
        cols: [[
            {type: 'numbers'},
            {field: 'name', title: '操作名称', align: 'center'},
            {field: 'operateType', title: '操作类型', align: 'center'},
            {field: 'optionValue', title: '推送类型', align: 'center'},
            {field: 'enable', title: '状态', align: 'center'},
            {title: '操作', align: 'center', toolbar: '#optBar'}
        ]],
        done: function (res, curr, count) {
            $("[data-field='enable']").children().each(function () {
                if ($(this).text() == "true") {
                    $(this).html('<span class="layui-btn layui-btn-normal layui-btn-xs">启用</span>')
                } else if ($(this).text() == "false") {
                    $(this).html('<span class="layui-btn layui-btn-danger layui-btn-xs">停用</span>')
                }
            });
            $("[data-field='operateType']").children().each(function () {
                if ($(this).text() == "101") {
                    $(this).text("系统下单成功")
                } else if ($(this).text() == "102") {
                    $(this).text("派单成功")
                }else if ($(this).text() == "103") {
                    $(this).text("出发")
                }else if ($(this).text() == "104") {
                    $(this).text("订单完成")
                }else if ($(this).text() == "105") {
                    $(this).text("代客下单成功")
                }else if ($(this).text() == "106") {
                    $(this).text("服务组出发")
                }else if ($(this).text() == "107") {
                    $(this).text("派单成功，发送短信给用户")
                }else if ($(this).text() == "108") {
                    $(this).text("周期订余量不足提醒")
                }else if ($(this).text() == "109") {
                    $(this).text("切换服务者")
                }else if ($(this).text() == "110") {
                    $(this).text("周期订单子订单取消通知客户")
                }else if ($(this).text() == "111") {
                    $(this).text("取消订单通知客户")
                }else if ($(this).text() == "112") {
                    $(this).text("取消订单通知客服")
                }else if ($(this).text() == "113") {
                    $(this).text("取消订单通知悦姐")
                }
            });
            $("[data-field='optionValue']").children().each(function () {
                if ($(this).text() == "1") {
                    $(this).text("短信推送")
                } else if ($(this).text() == "2") {
                    $(this).text("微信公众推送")
                } else if ($(this).text() == "3") {
                    $(this).text("app推送")
                }
            });
            pageCurr = curr;
        }
    });


    //删除数据
    function delRecord(obj, id) {
        layer.confirm('您确定要删除吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.post(contextPath + "setting/msgPush", {"_method": 'DELETE', "id": id}, function (data) {
                if (data.code == 0) {
                    layer.alert(data.message, function () {
                        layer.closeAll();
                        load(obj);
                    });
                } else {
                    layer.alert(data.message);
                }
            });
        }, function () {
            layer.closeAll();
        });
    }


    //监听工具栏
    table.on('tool(editTable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'del') {
            //删除
            delRecord(data, data.id);
        } else if (obj.event === 'edit') {
            //编辑
            addRecord(data, "编辑");
        }
    });


    //监听搜索框
    form.on('submit(searchSubmit)', function (data) {
        //重新加载table
            load(data);
        return false;//阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    //监听提交
    form.on('submit(addSubmit)', function(){
        $('#addOperateType').removeAttr("disabled");
        $('#optionValue').removeAttr("disabled");
        var $form = $("#addForm");
        var data = getFormData($form);
        console.log(data);
        $.ajax({
            type: "post",
            data: JSON.stringify(data),
            contentType: "application/json;charset=UTF-8",
            url: contextPath + "setting/msgPush",
            async: false,
            dataType:'json',
            success: function (data) {
                if (data.code == 0) {
                    layer.alert(data.message, {
                        icon: 1
                    }, function(){
                        layer.closeAll();
                        load(null);
                    });
                } else {
                    layer.alert(data.message);
                }
                cleanUser();
            },
            error: function () {
                layer.alert("操作请求错误，请您稍后再试",function(){
                    layer.closeAll();
                    //加载load方法
                    load(null);//自定义
                });
            }
        });
        return false;
    });

    //重新加载
    function load(obj) {
        if (obj == null){
            tableIns.reload({
                page: {
                    curr: pageCurr //从当前页码开始
                }
            });
        } else {
            //重新加载table
            tableIns.reload({
                where: obj.field
                , page: {
                    curr: pageCurr //从当前页码开始
                }
            });
        }
    }
});



//添加、编辑数据
function addRecord(data, title) {
    if (data == null || data == "") {
        $('#addOperateType').removeAttr("disabled");
        $('#optionValue').removeAttr("disabled");
        $("#id").val(null);
        $('#addOperateType').siblings("div.layui-form-select").find('dl').find("dd[lay-value='']").click();
        $('#optionValue').siblings("div.layui-form-select").find('dl').find("dd[lay-value='']").click();
        $("input[name=enable]:eq(0)").attr("checked",true);
        form.render('select');
        form.render('radio');
    } else {
        $("#id").val(data.id);
        $("#username").val(data.name);
        $("#addOperateType").val(data.operateType);
        $('#addOperateType').attr("disabled","disabled");
        $("#optionValue").val(data.optionValue);
        $('#optionValue').attr("disabled","disabled");
        form.render('select');//layui动态选中
        $('[name=enable]').each(function(i,item){
            if($(item).val()==data.enable.toString()){
                $(item).prop('checked',true);
                form.render('radio');
            }
        });
    }

    layer.open({
        type: 1,
        title: title,
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['550px'],
        content: $('#addRecord'),
        end: function () {
            cleanUser();
        }
    });
}

function cleanUser(){
    $("#id").val(null);
    $("#username").val("");
    var select = "dd[lay-value='']";
    $('#addOperateType').siblings("div.layui-form-select").find('dl').find(select).click();
    $('#optionValue').siblings("div.layui-form-select").find('dl').find(select).click();
    $("input[name=enable]:eq(0)").attr("checked",true);
    form.render();
}

//获取from当中所有的参数，拼接成为一个json
function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var jsonStr = {};

    $.map(unindexed_array, function(n, i){
        jsonStr[n['name']] = n['value'];
    });
    return jsonStr;
}