$(function () {
    $( "#datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    $("#sumbit").click(function () {
        let reg = /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/;
        let birth = $(".birth").val()
        if(!reg.test(birth)){
            alert("日期格式不正确，正确格式为：2022-01-01");
            return
        }
        let data = $("#base-info-form").serializeArray()
        let formObject = {};
        $.each(data,function(i,item){
            formObject[item.name] = item.value;
        })
        $.ajax({
            url: serverhost + "/user/updateBaseInfo",
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
                    window.location.reload()
                }else{
                    alert(data.msg)
                }
            },
        })
    });//个人基础资料修改
    $("#addsumbit").click(function () {
        let data = $("#changeAddress").serializeArray()
        let formObject = {};
        $.each(data,function(i,item){
            formObject[item.name] = item.value;
        });
        $.ajax({
            url: serverhost + "/user/updateAreaReq",
            method: "POST",
            contentType: "application/json;charset=utf-8",
            data:JSON.stringify(formObject),
            async: true,
            dataType:"json",
            success: function (data) {
                if(data.code==="200"){
                    alert("更新成功")
                    window.location.reload()
                }else{
                    alert(data.msg)
                }
            },
        })
    })
})