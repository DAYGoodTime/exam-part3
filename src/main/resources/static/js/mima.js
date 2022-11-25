$(function () {
    var pwd = false;
    $(".sumbit").click(function () {
        if(!pwd){
            return;
        }
        let method = $(this).parent("form").children(".method").val()
        let data = $(this).parent("form").serializeArray()
        let formObject = {};
        $.each(data,function(i,item){
            formObject[item.name] = item.value;
        });
        console.log("方式:"+method)
        if(method==="password"){
            let confirmPwd = $(this).parent("form").find(".confirmPwd").val()
            let newPwd = $(this).parent("form").find(".newPwd").val()
            var form = $(this).parent("form")
            if(confirmPwd!==newPwd){
                $(this).parent("form").children(".msg-warn").show.text("确认密码与新密码不一致")
            }else {
                $(".msg-warn").hide()
                $.ajax({
                    url: serverhost + "/user/resetPwd/"+uid,
                    method: "POST",
                    contentType: "application/json;charset=utf-8",
                    data:JSON.stringify(formObject),
                    async: true,
                    dataType:"json",
                    success: function (data) {
                        console.log(data)
                        let obj = {}
                        obj = data
                        if(data.code==="200"){
                            alert("更新成功")
                            window.location.href = serverhost + "/login"
                        }else{
                            if(obj.msg==="验证码错误"){
                                form.children(".msg-warn").show().text(obj.msg)
                                form.find(".vertifycode").click()
                            }else {
                                form.children(".msg-warn").show().text(obj.msg)
                            }
                        }
                    },
                })
            }
        }
        if(method==="email"){
            var reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
            let email = $(this).parent("form").find(".email").val()
            if(!reg.test(email)){
                $(this).parent("form").children(".msg-warn").show().text("邮箱格式不正确")
                return
            }else {
                $(this).parent("form").children(".msg-warn").hide()
            }
            let data = $(this).parent("form").serializeArray()
            let formObject = {};
            var form = $(this).parent("form")
            $.each(data,function(i,item){
                formObject[item.name] = item.value;
            });
            $.ajax({
                url: serverhost + "/user/resetPwd/"+uid,
                method: "POST",
                contentType: "application/json;charset=utf-8",
                data:JSON.stringify(formObject),
                async: true,
                dataType:"json",
                success: function (data) {
                    console.log(data)
                    let obj = {}
                    obj = data
                    if(data.code==="200"){
                        $("#email-confirm").hide()
                        $("#show-email").show()
                        $(".user-email").text(data.data)
                    }else{
                        if(obj.msg==="验证码错误"){
                            form.children(".msg-warn").text(obj.msg)
                            form.children(".vertifycode").click()
                            $(".way").hide()
                        }else {
                            form.children(".msg-warn").text(obj.msg)
                        }
                    }
                },
            })
        }
    });//密码修改检测
    $(".newPwd").blur(function () {
       let reg = /^.*(?=.{6,16})(?=.*\d)(?=.*[A-Za-z])(?=.*[!@#$%^&*_? ]).*$/;
       let newPwd = $(".newPwd").val()
       if(newPwd!==""){
           if(!reg.test(newPwd)){
               $(".msg-warn").show().text("密码强度不足")
               pwd = false;
           }else {
               $(".msg-warn").hide()
               pwd = true
           }
       }
    })//密码强度检测
    $(".confirmPwd").blur(function () {
        let newPwd = $(".newPwd").val()
        let confirmPwd = $(".confirmPwd").val()
        if(newPwd!==confirmPwd){
            $(".msg-warn").show().text("确认密码与新密码不一致")
            pwd = false;
        }else {
            $(".msg-warn").hide()
            pwd = true;
        }
    })//确认密码检测
})