$(function () {
    function gerProDetail(pid) {
        $.ajax({
            url: serverhost + "/getProDetail/"+pid,
            method: "POST",
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                let obj = data.pro
                console.log(obj)
                let father = document.querySelector(".proDets");
                father.querySelector("#pid").innerHTML = pid
                father.querySelector(".pname").innerHTML = obj.name
                father.querySelector(".pprice").innerHTML ='￥'+obj.fixedPrice
                father.querySelector(".ppicture0").src = obj.picture
                father.querySelector(".ppicture1").src = obj.picture
                father.querySelector(".ppicture2").src = obj.picture
                father.querySelector(".ppicture3").src = obj.picture
                father.querySelector(".ppicture4").src = obj.picture
                father.querySelector(".pstorge").innerHTML = obj.storage
                father.querySelector(".more").href = "/proDetail/"+pid
            },
        })
    }
    function addToCart2() {
        let user_id = document.getElementById("user--id").innerHTML
        if(user_id == null||user_id===""){
            alert("请登录")
            window.location.href = serverhost + "/login"
        }
        let product_id = document.getElementById("pid").innerHTML
        let proAmount = document.getElementById("proAmount").innerHTML
        let object = {"user_id":user_id, "product_id":product_id,"proAmount":proAmount}
        let jsonobj = JSON.stringify(object)
        $.ajax({
                url: serverhost + "/addToCart",
                method: "POST",
                data: jsonobj,
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType:"json",
                success: function (data) {
                    if(data.code==="200"){
                        $(".proIntro").css("border", "none");
                        $(".num .please").hide()
                        alert("添加成功")
                    }
                },
            }
        )
    }

    $("#collection").click(function () {
        let user_id = $("#user_id").text()
        let pid = $("#pid").text()
        if(user_id===""){
            alert("请登录")
            window.location.href = serverhost+"/login";
            return
        }
        let obj = {"user_id":user_id,"pid":pid}
        if(!collection_flag){
            $.ajax({
                url: serverhost + "/user/addCollection",
                method: "POST",
                data: JSON.stringify(obj),
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType:"json",
                success: function (data) {
                    if(data.code==="200"){
                        collection_flag = true
                        $("#collection").attr("src","/static/img/util/collected.png")
                    }else {
                        alert(data.msg)
                    }
                },
            })
        }else {
            $.ajax({
                url: serverhost + "/user/delCollection",
                method: "POST",
                data: JSON.stringify(obj),
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType:"json",
                success: function (data) {
                    if(data.code==="200"){
                        collection_flag = false;
                        $("#collection").attr("src","/static/img/util/collection.png")
                    }else {
                        alert(data.msg)
                    }
                },
            })
        }
    });

    $(".num .sub").click(function () {
        var c = parseInt($(this).siblings("span").text());
        if (c <= 1) {
            $(this).attr("disabled", "disabled")
        } else {
            c--;
            //数量减少操作
            $(this).siblings("span").text(c);
            var d = $(this).parents(".number").prev().text().substring(1);
            $(this).parents(".th").find(".sAll").text("￥" + (c * d).toFixed(2));
        }
    });
    $(".num .add").click(function () {
        var c = parseInt($(this).siblings("span").text());
        if (c >= 5) {
            confirm("限购5件")
        } else {
            c++;
            //数量增加操作
            $(this).siblings("span").text(c);
            var d = $(this).parents(".number").prev().text().substring(1);
            $(this).parents(".th").find(".sAll").text("￥" + (c * d).toFixed(2));
        }
    });

    $(".choice .default").click(function () {
        $(this).siblings("ul").toggle();
        $(this).toggleClass("on")
    });
    $(".choice .select li").click(function () {
        var c = $(this).text();
        $(".choice .default").text(c).removeClass("on");
        $(this).parent("ul").slideUp("fast")
    });
    //排序选择
    $(".choice .select .asce").click(function (){
        //升序选择
        window.location.href = serverhost +"/"+ $("#current-page").text() + "/asce";
    });
    $(".choice .select .desc").click(function (){
        //升序选择
        window.location.href = serverhost +"/" +$("#current-page").text() + "/desc";
    });
    $(".proList li").on("mouseenter", function () {
        var c = "";
        c = '<p class="quick">快速浏览</p>';
        $(this).css("border", "1px solid #000").append(c);
        $(".quick").on("click", function () {
            //快速浏览位置
            let parentNode = document.querySelector(".quick").parentNode;
            let pid = parentNode.querySelector(".pid").innerHTML;
            gerProDetail(pid)
            $(".mask").show();
            $(".proDets").show()
        });
    });
    $(".cart").click(function () {
        if ($(".categ p").hasClass("on")) {
            $(".proDets").hide();
            $(".mask").hide();
            addToCart2();
        }
    })
    $(".proList li").on("mouseleave", function () {
        $(this).find("p").remove();
        $(this).css("border", "1px solid #fff")
    });
    $(".off").click(function () {
        $(".mask").hide();
        $(".proDets").hide();
        $(".pleaseC").hide()
    });
    $(".proIntro .smallImg p img").hover(function () {
        $(this).parents(".smallImg").find("span").remove();
        var d = $(this).position().left;
        var c = $(this).attr("alt");
        $(this).parent("p").addClass("on");
        $(this).parents(".smallImg").append("<span>" + c + "</span>");
        $(".smallImg").find("span").css("left", d)
    }, function () {
        $(this).parents(".smallImg").find("span").remove();
        $(this).parent("p").removeClass("on")
    });
    $(".proIntro .smallImg img").click(function () {
        var c = $(this).attr("data-src");
        $(this).parents(".proCon").find(".proImg").children(".det").attr("src", c).css({
            width: "580px",
            height: "580px"
        });
        $(this).parents(".proCon").find(".proImg").children(".list").attr("src", c).css({
            width: "360px",
            height: "360px"
        });
        $(this).parents(".smallImg").find("span").remove();
        $(this).parent("p").addClass("on").siblings().removeClass("on");
        $(this).off("mouseleave").parent("p").siblings().find("img").on("mouseleave", function () {
            $(this).parents(".smallImg").find("span").remove();
            $(this).parent("p").removeClass("on")
        })
    });
    $(".btns a").click(function () {
        if ($(".categ p").hasClass("on")) {
            if ($(this).children().hasClass("buy")) {
                //购买
                let pid = $("#pid").text()
                let pamount = $("#proAmount").text()
                let user_id = document.getElementById("user_id").innerHTML
                if(user_id===""){
                    alert("请先登录");
                    window.location.href = serverhost + "/login"
                }
                let jsonobj = {"user_id":user_id,"pid":pid,"pamount":pamount}
                $.ajax({
                    url: serverhost + "/user/buyNow",
                    method: "POST",
                    data: JSON.stringify(jsonobj),
                    contentType: "application/json;charset=utf-8",
                    async: true,
                    dataType:"json",
                    success: function (data) {
                        if(data.code==="200"){
                            $(".proIntro").css("border", "none");
                            $(".num .please").hide()
                            window.location.href = serverhost + "/user/Order/"+data.data.bid
                        }else console.log(data)
                    },
            });
        }
        }else {
            $(".proIntro").css("border", "1px solid #c10000");
            $(".num .please").show()
    }})
    $(".smallImg > img").mouseover(function () {
        $(this).css("border", "1px solid #c10000").siblings("img").css("border", "none");
        var c = $(this).attr("data-src");
        $(this).parent().siblings(".det").attr("src", c).css({width: "580px", height: "580px"});
        $(this).parent().siblings(".list").attr("src", c).css({width: "360px", height: "360px"})
    });
    $(".msgTit a").click(function () {
        var c = $(this).index();
        $(this).addClass("on").siblings().removeClass("on");
        $(".msgAll").children("div").eq(c).show().siblings().hide()
    });
    $(".addre").click(function () {
        $(this).addClass("on").siblings().removeClass("on")
        //地址盒子点击事件
        let boundId = $("#boundId").text()
        let user_id = $("#pageUid").text()
        let addId = $(this).children(".boxaddId").text()
        let obj = {"method":"setAddress","boundId":boundId,"uid":user_id,"addId":addId}
        $.ajax({
            url: serverhost + "/updateOrder",
            method: "POST",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                if(data.code ==="200"){
                    console.log("修改成功")
                } else console.log(data.msg)
            },
        })
    });
    $(".way img").click(function () {
        $(this).addClass("on").siblings().removeClass("on")
        //支付方式选择
        let paymentId = $(this).attr('id')
        let boundId = $("#boundId").text()
        let user_id = $("#pageUid").text()
        let obj = {"method":"setPayment","boundId":boundId,"uid":user_id,"paymentId":paymentId}
        $.ajax({
            url: serverhost + "/updateOrder",
            method: "POST",
            data:JSON.stringify(obj),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                if(data.code ==="200"){
                    console.log("修改成功")
                } else console.log(data.msg)
            },
        })

    });
    $(".dis span").click(function () {
        $(this).addClass("on").siblings().removeClass("on")
    });
    var a = $(".ok span").text();

    function b() {
        a--;
        $(".ok span").text(a);
        if (a == 0) {
            window.location.href = serverhost + "/user/myOrder"
        }
    }

    setInterval(b, 1000);

});