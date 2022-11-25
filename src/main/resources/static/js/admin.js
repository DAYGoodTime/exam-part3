$(function (){
    var pwd_flag = false; //密码修改flag
    $("input[type='checkbox']").on("click", function () {
        var f = $(this).is(":checked");
        var e = $(this).hasClass("checkAll");
        if(f){
            if(e){
                //全选增加
                ObjectList = []
                let ids = document.querySelectorAll(".ids");
                for (let i = 0; i < ids.length ; i++) {
                    ObjectList.push(ids[i].getAttribute("value"))
                    ids[i].checked = true
                }
                console.log(ObjectList)
            }else {
                //单选增加
                ObjectList.push($(this).val())
                console.log(ObjectList)
            }
        }else {
            if(e){
                //全选取消
                ObjectList = []
                let ids = document.querySelectorAll(".ids");
                for (let i = 0; i < ids.length; i++) {
                    ids[i].checked = false
                }
                console.log(ObjectList)
            }else {
                //单选取消
                let value = $(this).val()
                for (let i = 0; i < ObjectList.length ; i++) {
                    if(ObjectList[i]===value){
                        ObjectList.splice(i,1)
                    }
                }
                console.log(ObjectList)
            }
        }
    });//checkbox检查
    $(".price").blur(function () {
        let val = $(this).val();
        let reg = new RegExp('^([0-9]+|[0-9]{1,3}(,[0-9]{3})*)(.[0-9]{1,2})?$');
        if(val===undefined||val===''||!reg.test(val)){
            $(".price-text").css("display","inline-block")
            $(".price-text").text("格式不正确")
            pro_flag = false
        }else {
            $(".price-text").hide()
            pro_flag = true
        }
    });//添加商品的价格检测
    $(".storage").blur(function () {
        let val = $(this).val();
        let reg = new RegExp('^\\+?[0-9][0-9]*$');
        if(val===undefined||val===''||!reg.test(val)){
            $(".storage-text").css("display","inline-block")
            $(".storage-text").text("格式不正确")
            pro_flag = false
        }else {
            $(".storage-text").hide()
            pro_flag = true
        }
    });//添加商品的库存数量检测
    $("#admin-sumbit").click(function (){
        let data = {}
        let value = $('#admin-form').serializeArray()
        $.each(value, function (index, item) {
            data[item.name] = item.value;
        });
        $.ajax({
            url: serverhost + "/adminLoginReq",
            method: "POST",
            data:JSON.stringify(data),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                console.log(data.code)
                if(data.code==="200"){
                    $(".msg-warn").css("display","inline-block");
                    $(".msg-warn").text("登录成功")
                    setTimeout(function() {
                        window.location.href = serverhost + "/admin"
                    }, 2000);
                }else {
                    $(".msg-warn").css("display","inline-block");
                    $(".msg-warn").text(data.msg)
                }
            },
        })
    });//管理员登录
    $("#pwd-sumbit").click(function (){
        if (!pwd_flag){
            $(".msg-warn").css("display","inline-block");
            $(".msg-warn").text("邮箱格式错误")
            return
        }else {
            $(".msg-warn").hide()
        }
        let data = {}
        let value = $('#pwd-form').serializeArray()
        $.each(value, function (index, item) {
            data[item.name] = item.value;
        });
        $.ajax({
            url: serverhost + "/restPassword",
            method: "POST",
            data:JSON.stringify(data),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                console.log(data.code)
                if(data.code==="200"){
                    $("#pwd-form").hide()
                    $(".two").show()
                    $(".user-email").text(data.data)
                }else {
                    $(".msg-warn").css("display","inline-block");
                    $(".msg-warn").text(data.msg)
                }
            },
        })
    });//管理员密码修改
    $("#email").blur(function () {
        let email = $(this).val();
        let reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
        if(!reg.test(email)){
            $(".msg-warn").css("display","inline-block");
            $(".msg-warn").text("邮箱格式错误")
            pwd_flag = false;
        }else {
            pwd_flag = true;
            $(".msg-warn").hide()
        }
    });//管理员密码修改邮箱格式检测
    $("#category-form").find(".parent_name").blur(function () {
        let parent_name = $("#category-form").find(".parent_name").val();
        let parent_id = $("#category-form").find(".parent_id").val();
        if(parent_name==="" || parent_id ===""){
            $("#category-form").find(".parent-text").css("display","inline-block")
            $("#category-form").find(".parent-text").text("父类id和name不能为空")
            cate_flag = false
        }else {
            $("#category-form").find(".parent-text").hide()
            cate_flag = true
        }
    });//分类添加检测
    $("#category-form").find(".children_name").blur(function () {
        let children_id = $("#category-form").find(".children_id").val();
        let children_name = $("#category-form").find(".children_name").val();
        if(children_id==="" && children_name !==""){
            $("#category-form").find(".children-text").css("display","inline-block")
            $("#category-form").find(".children-text").text("子类id不能为空")
            cate_flag = false
        }else if(children_name==="" && children_id !=="") {
            $("#category-form").find(".children-text").css("display","inline-block")
            $("#category-form").find(".children-text").text("子类名字不能为空")
            cate_flag = false
        }else {
            $("#category-form").find(".children-text").hide()
            cate_flag = true
        }
    });//分类添加检测
    $("#cate-sumbit").click(function () {
        cate_sumbit()
    });//分类添加事件
    $(".recovery").click(function () {
        let fileName = $(this).parent().find(".fileName").text()
        if(fileName===undefined||fileName===null){
            alert("文件名获取失败")
            return
        }
        recovery(fileName);
    });//恢复按钮事件
    $(".del-bk").click(function () {
        let name = $(this).parent().find(".fileName").text()
        let obj = {"fileName":name};
        $.ajax({
            url: serverhost + "/admin/delSqlBKReq",
            method: "POST",
            contentType: "application/json;charset=utf-8",
            data:JSON.stringify(obj),
            async: false,
            dataType:"json",
            success: function (data) {
                if(data.code==="200"){
                    alert("删除成功")
                    window.location.reload()
                }else {
                    alert(data.msg)
                }
            },
        })
    });//删除备份事件
    $(".category-sumbit").click(function () {
        let data = $("#category-form").serializeArray()
        let formObject = {};
        $.each(data,function(i,item){
            formObject[item.name] = item.value;
        });
        let obj = {"obj":formObject}
        $.ajax({
            url: serverhost + "/admin/modifyCategoryReq",
            method: "POST",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                if(data.code==="200"){
                    alert("修改成功")
                    window.location.href = serverhost + "/admin/categoryPanel"
                }else {
                    alert(data.msg)
                }
            },
        })
    })//分类修改提交事件
    $(".btn2").click(function () {
        let search = $(".keyword").val()
        window.location.href = serverhost +"/admin/"+ currentPage + "Panel/"+ curPage + "/" + search
        if(search!=="all"){
            let value = $("#user-opt>option:selected").val()
            let search = $(".keyword").val()
            if(search===""||search===undefined){
                window.location.href = serverhost + "/admin/" + currentPage +"Panel/" +curPage +"/opt/"+ value
            }else {
                window.location.href = serverhost + "/admin/" + currentPage +"Panel/" +curPage +"/"+ search +"/"+ value
            }
        }
    })//搜索事件
    $("#user-opt").change(function () {
        let value = $("#user-opt>option:selected").val()
        let search = $(".keyword").val()
        if(search===""||search===undefined){
            window.location.href = serverhost + "/admin/" + currentPage +"Panel/" +curPage +"/opt/"+ value
        }else {
            window.location.href = serverhost + "/admin/" + currentPage +"Panel/" +curPage +"/"+ search +"/"+ value
        }
    })//User-option选择事件
    $("#status-opt").change(function () {
        let value = $("#status-opt>option:selected").val()
        window.location.href = serverhost + "/admin/orderPanel/" + curPage + "/" + value
    })//User-option选择事件
});
const serverhost = "http://localhost:8080"
function user_sumbit() {
    let data = $("#myform").serializeArray()
    for (let i = 0; i < data.length; i++) {
        if(data[i].value===undefined||data[i].value===''){
            $(".warn-text").css("display","inline-block")
            $(".warn-text").text("还有参数未填写")
            return;
        }
    }
    $(".warn-text").hide()
    let formData = new FormData();
    var file = $("#file")[0].files[0];
    $.each(data,function(i,item){
        formData.append(item.name,item.value)
    })
    formData.append("file",file)
    $.ajax({
        type:"post",
        enctype: 'multipart/form-data',
        url: serverhost +"/admin/addUserReq",
        processData:false,
        contentType:false,
        data:formData,
        dataType: 'json',
        success:function (data) {
            if(data.code==="200"){
                alert("添加成功")
                window.location.href = serverhost + "/admin/userPanel"
            }else {
                alert(data.msg)
            }
        }
    })
} //用户添加函数
var pro_flag = false
function pro_sumbit() {
    let data = $("#myform").serializeArray()
    for (let i = 0; i < data.length; i++) {
        if(data[i].value===undefined ||data[i].value===''&& data[i].name!=='catalog' && pro_flag){
            $(".warn-text").css("display","inline-block")
            $(".warn-text").text("还有参数未填写")
            return;
        }
    }
    $(".warn-text").hide()
    let formData = new FormData();
    var file = $("#file")[0].files[0];
    $.each(data,function(i,item){
        formData.append(item.name,item.value)
    })
    formData.append("file",file)
    $.ajax({
        type:"post",
        enctype: 'multipart/form-data',
        url: serverhost +"/admin/addProReq",
        processData:false,
        contentType:false,
        data:formData,
        dataType: 'json',
        success:function (data) {
            if(data.code==="200"){
                alert("添加成功")
                window.location.href = serverhost + "/admin/productPanel"
            }else {
                alert(data.msg)
            }
        }
    })
} //产品添加函数
var cate_flag = false
function cate_sumbit(){
    if(!cate_flag){
        return
    }
    let data = $("#category-form").serializeArray()
    let formObject = {};
    $.each(data,function(i,item){
        formObject[item.name] = item.value;
    });
    $.ajax({
        url: serverhost + "/admin/addCategoryReq",
        method: "POST",
        data:JSON.stringify(formObject),
        contentType: "application/json;charset=utf-8",
        async: true,
        dataType:"json",
        success: function (data) {
            if(data.code==="200"){
                alert("添加成功")
                window.location.href = serverhost + "/admin/categoryPanel"
            }else {
                alert(data.msg)
            }
        },
    })
} //分类创建函数
function recovery(fileName) {
    let obj = {"fileName":fileName};
    $.ajax({
        url: serverhost + "/admin/recoveryReq",
        method: "POST",
        contentType: "application/json;charset=utf-8",
        data:JSON.stringify(obj),
        async: false,
        dataType:"json",
        success: function (data) {
            if(data.code==="200"){
                alert("恢复成功")
                window.location.reload()
            }else {
                alert(data.msg)
            }
        },
    })
} //恢复数据库函数
function backupNow() {
    $.ajax({
        url: serverhost + "/admin/backupNow",
        method: "POST",
        contentType: "application/json;charset=utf-8",
        async: false,
        dataType:"json",
        success: function (data) {
            if(data.code==="200"){
                alert("备份成功")
                window.location.reload()
            }else {
                alert(data.msg)
            }
        },
    })
} //立即备份函数
