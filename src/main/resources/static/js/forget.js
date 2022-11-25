$(function () {
    var email_flag = false
    $(".next").click(function () {
        if(!email_flag){
            $(".warn-text").show()
            $(".warn-text").text("该栏不能为空")
            return;
        }else {
            $(".warn-text").hide()
        }
        let data = $(".one").serializeArray()
        let formObject = {};
        $.each(data,function(i,item){
            formObject[item.name] = item.value;
        })
        let flag = false
        $.ajax({
            url: serverhost + "/restPassword",
            method: "POST",
            contentType: "application/json;charset=utf-8",
            data:JSON.stringify(formObject),
            async: true,
            dataType:"json",
            success: function (data) {
                let obj = {}
                obj = data
                if(data.code!=="200"){
                    $(".next").parent("form").find(".warn-text").show()
                    $(".next").parent("form").find(".warn-text").text(obj.msg)
                    flag = true
                    return
                }else {
                    $(".two").show();
                    $(".one").hide();
                    $(".forCon ul li").eq(1).addClass("on").siblings("li").removeClass("on")
                    $(".user-email").text(data.data)
                }
            },
        });
    });
    $(".email").blur(function () {
        let email = $(".email").val()
        let reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
        if(email===undefined){
            $(".warn-text").show()
            $(".warn-text").text("该栏不能为空")
        }else if(!reg.test(email)){
            $(".warn-text").show()
            $(".warn-text").text("邮箱格式错误")
            
        }else{
            $(".warn-text").hide()
            email_flag = true;
        }
    })
})