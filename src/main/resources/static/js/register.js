var serverhost = "http://localhost:8080";
$(function () {
    $( "#datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    //flag判断
    var flag_pwd = false //密码判断
    var flag_email = false //邮箱判断
    var flag_birth = false //生日判断
    var flag_phone = false //手机号判断

    $("#sumbit").click(function () {
        let allTrue = flag_pwd && flag_email && flag_birth
        if (!allTrue){
            $(".msg-warn").css("display","block")
            $(".msg-warn").css("color","red")
            $(".msg-warn").text("仍然有信息未完善")
            return
        }
        let data = $(".reg-form").serializeArray()
        let formObject = {};
        $.each(data,function(i,item){
            formObject[item.name] = item.value;
        });
        $.ajax({
            url: serverhost + "/registerReq",
            method: "POST",
            data:JSON.stringify(formObject),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                if(data.code==="200"){
                    $(".msg-warn").css("display","block")
                    $(".msg-warn").text("注册成功,即将跳转到登录页面")
                    setTimeout(function() { gotoLogin(); }, 2000);
                }else {
                    $(".msg-warn").css("display","block")
                    $(".msg-warn").css("color","red")
                    $(".msg-warn").text(data.msg)

                }
            },
        })
    });//注册提交事件

    $(".pwd").blur(function () {
        let pwd = $(".pwd").val()
        if(pwd!==""){
            let reg = /^.*(?=.{6,16})(?=.*\d)(?=.*[A-Za-z])(?=.*[!@#$%^&*_? ]).*$/;
            if(!reg.test(pwd)){
                $(".msg-warn").show().text("至少6-16个字符,需使用字母、数字和符号组合")
                flag_pwd = false;
            }else {
                $(".pwd-warn").hide()
                flag_pwd = true
            }
        }
    })
    $(".confirm-pwd").blur(function () {
        let cPwd = $(this).val()
        let pwd = $(".pwd").val()
        if(cPwd!==pwd){
            $(".pwd-warn").show()
            $(".pwd-warn").text("密码与确认密码不一致")
        }else {
            $(".pwd-warn").hide()
             flag_pwd = true
        }
    });//确认密码事件
    $(".email").blur(function () {
        let email = $(this).val()
        let reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
        if(!reg.test(email)){
            $(".email-warn").show()
            $(".email-warn").text("邮箱地址格式不正确")
        }else {
            $(".email-warn").hide()
            flag_email = true
        }
    });//邮箱检测事件
    $(".birth").blur(function () {
        let birth = $(this).val()
        let reg = /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/;
        if(!reg.test(birth)){
            $(".birth-warn").show()
            $(".birth-warn").text("日期格式不正确,例子:1911-11-01")
        }else {
            $(".birth-warn").hide()
            flag_birth = true
        }
    });//日期格式检测事件
    $(".bttn").click(function () {
        $(".nickname").val('用户-'+randomString(10))
    })
    $(".phone").blur(function () {
        let reg = /^1[34578]\d{9}$/
        let phone = $(".phone").val()
        if(!reg.test(phone)&&phone!==""){
            $(".msg-warn").show()
            $(".msg-warn").text("手机号格式不正确")
        }
    })
});
function randomString(length) {
    var str = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var result = '';
    for (var i = length; i > 0; --i)
        result += str[Math.floor(Math.random() * str.length)];
    return result;
}