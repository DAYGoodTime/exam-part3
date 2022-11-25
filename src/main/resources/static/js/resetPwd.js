$(function () {
    var flag = false;
    var serverhost = "http://localhost:8080";
    $(".c-pwd").blur(function () {
        let pwd = $(".pwd").val()
        let c_pwd = $(this).val()
        if(pwd!==c_pwd){
            $(".warn-text").css("display","inline-block")
            $(".warn-text").text("新密码与确认密码不一致")
        }else {
            $(".warn-text").hide()
            flag = true
        }
    })
    $(".next").click(function () {
        if(!flag){
            return
        }
        let data = $("#pwd-form").serializeArray()
        let formObject = {};
        $.each(data,function(i,item){
            formObject[item.name] = item.value;
        })
        $.ajax({
            url: serverhost + "/restPasswordByEmailReq",
            method: "POST",
            contentType: "application/json;charset=utf-8",
            data:JSON.stringify(formObject),
            async: true,
            dataType:"json",
            success: function (data) {
                let obj = {}
                obj = data
                if(data.code==="200"){
                    alert("更新成功,即将跳转至登录页面")
                    setTimeout(function() {
                        window.location.href = serverhost + "/login"
                        }, 2000);

                }else{
                    alert(data.msg)
                }
            },
        })
    })
})