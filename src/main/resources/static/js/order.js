$(function () {
    $(".pay").click(function () {

        let pay_flag=$(".addre").hasClass("on")

        if(!pay_flag){
            alert("请选择收货地址")
            return
        }
        let boundId = $("#boundId").text()
        let user_id = $("#pageUid").text()
        let obj = {"boundId":boundId,"uid":user_id}
        console.log(obj)
        $.ajax({
            url: serverhost + "/user/OrderPayReq",
            method: "POST",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                console.log(data)
                if(data.code==="200"){
                    if(data.method==="wechat"){
                        let strUrl = "https://pay.shazure.com/payservice/board?partnerno="+data.partnerno
                        window.location.href = strUrl;
                    }
                    if(data.method==="alipay"){
                        const div = document.createElement('div')
                        div.innerHTML = data.data
                        document.body.appendChild(div)
                        document.forms[3].submit()
                    }
                }else alert(data.msg)
            },
        })
    });//支付事件
});