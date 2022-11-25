$(function () {
    $("#province").change(
        function () {
            let pid = $("#province>option:selected").val()
            $("#city").find('option').remove()
            $.ajax({
                url: serverhost + "/getLocation/"+pid,
                method: "POST",
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType:"json",
                success: function (data) {
                    let citys = data.cities
                    for (let i = 0; i < citys.length; i++) {
                        document.getElementById("city").options.add(new Option(citys[i].name,citys[i].id))
                    }
                },
            })
        }
    );
    $("#searchBtn").click(function () {
        document.getElementById("searchsumbit").click()
    })
    $("#province2").change(
        function () {
            let pid = $("#province2>option:selected").val()
            $("#city2").find('option').remove()
            $.ajax({
                url: serverhost + "/getLocation/"+pid,
                method: "POST",
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType:"json",
                success: function (data) {
                    let citys = data.cities
                    for (let i = 0; i < citys.length; i++) {
                        document.getElementById("city2").options.add(new Option(citys[i].name,citys[i].id))
                    }
                },
            })
        }
    )
    $("#city").change(
        function () {
            let pid = $("#city>option:selected").val()
            $("#county").find('option').remove()
            $.ajax({
                url: serverhost + "/getLocation/"+pid,
                method: "POST",
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType:"json",
                success: function (data) {
                    let citys = data.cities
                    for (let i = 0; i < citys.length; i++) {
                        document.getElementById("county").options.add(new Option(citys[i].name,citys[i].id))
                    }
                },
            })
        }
    )
    $("#city2").change(
        function () {
            let pid = $("#province2>option:selected").val()
            $("#county2").find('option').remove()
            $.ajax({
                url: serverhost + "/getLocation/"+pid,
                method: "POST",
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType:"json",
                success: function (data) {
                    let citys = data.cities
                    for (let i = 0; i < citys.length; i++) {
                        document.getElementById("county2").options.add(new Option(citys[i].name,citys[i].id))
                    }
                },
            })
        }
    )
    //异步请求添加地址
    $("#addAddress").click(function () {
        let data = {}
        let value = $('#addressform').serializeArray()
        $.each(value, function (index, item) {
            data[item.name] = item.value;
        });
        let json = JSON.stringify(data);
        console.log(json)
        $.ajax({
            url: serverhost + "/user/addAddress",
            method: "POST",
            data:json,
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                console.log(data.code)
                if(data.code==="200"){
                    alert("添加成功")
                    window.location.reload()
                }
            },
        })
    });
    //请求更新地址
    $("#addAddress2").click(function () {
        let data = {}
        let value = $('#addressform2').serializeArray()
        $.each(value, function (index, item) {
            data[item.name] = item.value;
        });
        data.method = "fullUpdate"
        let json = JSON.stringify(data);
        console.log(json)
        $.ajax({
            url: serverhost + "/updateAddress",
            method: "POST",
            data:json,
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                console.log(data.code)
                if(data.code==="200"){
                    alert("更新成功")
                    window.location.reload()
                }
            },
        })
    });






    $(".head ul>li").hover(function () {
        var a = $(this).children().length;
        if (a != 1) {
            $(this).children("div").stop().slideToggle(200).end().siblings().children("div").hide()
        } else {
            $(this).children("div").hide()
        }
    });
    $("a.er1").mouseover(function () {
        $(this).siblings("p").slideDown(100)
    }).mouseout(function () {
        $(this).siblings("p").slideUp(100)
    });
    $(".gotop a").hover(function () {
        var a = $(this).hasClass("dh");
        if (a == true) {
            $(this).find("dt").hide().siblings("dd").fadeIn().parents("a").siblings("p").show().animate({left: "-110px"})
        } else {
            $(this).find("dt").hide().siblings("dd").fadeIn().parents("a").siblings("p").hide().animate({left: "-130px"})
        }
    }, function () {
        $(this).find("dt").fadeIn().siblings("dd").hide().parents("a").siblings("p").hide()
    });
    $(window).scroll(function () {
        var a = $(window).scrollTop();
        if (a > 100) {
            $(".toptop").fadeIn()
        } else {
            $(".toptop").fadeOut()
        }
        $(".toptop").click(function () {
            $(window).scrollTop(0)
        })
    });
    $("#login").click(function () {
        $(".login").show();
        $(".msk").show()
    });
    $("#reg").click(function () {
        $(".reg").show();
        $(".msk").show()
    });
    $(".off").click(function () {
        $(".login").hide();
        $(".reg").hide();
        $(".msk").hide()
    });
    $(".goReg").click(function () {
        $(".login").hide();
        $(".reg").show()
    });
    $(".goLogin").click(function () {
        $(".reg").hide();
        $(".login").show()
    })
});
const serverhost = "http://localhost:8080"
function getLocation(){
    $.ajax({
        url: serverhost + "/getLocation/0",
        method: "POST",
        contentType: "application/json;charset=utf-8",
        async: true,
        dataType:"json",
        success: function (data) {
            let citys = data.cities
            for (let i = 0; i < citys.length; i++) {
                document.getElementById("province").options.add(new Option(citys[i].name,citys[i].id))
                document.getElementById("province2").options.add(new Option(citys[i].name,citys[i].id))
            }
        },
    })
}
function readdPutData(addId,user_id){
    $.ajax({
        url: serverhost + "/queryAddress/"+addId,
        method: "POST",
        contentType: "application/json;charset=utf-8",
        async: true,
        dataType:"json",
        success: function (data) {
            let address = data.address;
            let phone = data.mobile;
            let zipcode = data.zipcode;
            let receiveName = data.receiveName;
            let inputs = document.querySelector(".readd").querySelectorAll("input");
            inputs[0].value = user_id
            inputs[1].value = addId
            inputs[2].value = receiveName
            inputs[3].value = phone
            inputs[4].value = zipcode
            let addressText = document.querySelector(".readd").querySelector(".addressText")
            addressText.innerHTML = address
        },
    })
}
function changeLocal(father,local){
    $.ajax({
        url: serverhost + "/getLocation/0",
        method: "POST",
        contentType: "application/json;charset=utf-8",
        async: true,
        dataType:"json",
        success: function (data) {
            let citys = data.cities
            for (let i = 0; i < citys.length; i++) {
                document.getElementById("province").options.add(new Option(citys[i].name,citys[i].id))
                document.getElementById("province2").options.add(new Option(citys[i].name,citys[i].id))
            }
        },
    })
} //TODO 优化
function objectToJson(obj){
    function search() {
        document.getElementById("searchsumbit").click()
    }
}
