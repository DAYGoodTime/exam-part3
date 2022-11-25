$(function () {
    $(".edit").click(function () {
        //编辑地址按钮
        let addId = $(this).parent().parent().parent().children(".boxaddId").text()
        let user_id = $("#pageUid").text()
        readdPutData(addId,user_id)
        $(".mask").show();
        $(".readd").show()
    });
    $(".bc>input").click(function () {
        if ($(this).val() == "保存") {
            $(".mask").hide();
            $(".adddz").hide();
            $(".bj").hide();
            $(".xg").hide();
            $(".remima").hide();
            $(".pj").hide();
            $(".chak").hide()
            $(".readd").hide()
        } else {
            $(".mask").hide();
            $(".adddz").hide();
            $(".readd").hide();
            $(".bj").hide();
            $(".xg").hide();
            $(".remima").hide();
            $(".pj").hide();
            $(".chak").hide()
        }
    });
    $("#wa li").click(function () {
        $(this).addClass("on").siblings().removeClass("on");
        var a = $(this).find("a").text();
        $(".dkuang").find("p.one").each(function () {
            var b = $(this).text();
            if (a == b) {
                $(this).parent(".dkuang").show().siblings(".dkuang").hide()
            }
            $("#wa li").eq(0).click(function () {
                $(".dkuang").show()
            })
        })
    });
    $(".sx div:gt(0)").hide();
    $(".sx div").each(function (a) {
        if ($(this).html() == "") {
            var b = $("#pro li").eq(a).find("a").text();
            var c = "";
            c = '<div class="noz">当前没有' + b + "。</div>";
            $(this).html(c)
        }
    });
    $("#pro li").click(function () {
        $(this).addClass("on").siblings().removeClass("on");
        var a = $(this).index();
        $(".sx > div").eq(a).show().siblings().hide()
    });
    $(".sx dl dd").find("a").click(function () {
        if ($(this).text() == "评价") {

            //评价窗口
            let uid = $(this).parent().parent().find(".uid").text()
            let pid = $(this).parent().parent().find(".pid").text()
            $(".pjform").children(".uid").val(uid);
            $(".pjform").children(".pid").val(pid);
            $(".mask").show();
            $(".pj").show();

        } else {
            if ($(this).text() == "查看评价") {
                //查看评价窗口
                let c_id = $("#comment-id").text();
                let obj = {"comment_id":c_id}
                $.ajax({
                    url: serverhost + "/queryComment",
                    method: "POST",
                    data:JSON.stringify(obj),
                    contentType: "application/json;charset=utf-8",
                    async: true,
                    dataType:"json",
                    success: function (data) {
                        if(data.code==="200"){
                            let comment = data.data
                            let likes = comment.likes
                            $("#like").val(likes)
                            $("#comment").val(comment.context)
                            for (var c = 0; c < 5; c++) {
                                if (c <= likes) {
                                    $("#xin2").find("a").eq(c).find("img").attr("src", "/static/img/hxin.png")
                                } else {
                                    $("#xin2").find("a").eq(c).find("img").attr("src", "/static/img/xin.png")
                                }
                            }
                            $(".mask").show();
                            $(".chak").show()
                        } else alert(data.msg)
                    },
                })
            } else {
                $(".mask").hide();
                $(".pj").hide();
                $(".chak").hide()
            }
        }
    });
    $("#xin").each(function (a) {
        $("#xin").eq(a).children("a").click(function () {
            var b = $(this).index();
            console.log("likes:",b)
            $("#likes").val(b)
            for (var c = 0; c < 5; c++) {
                if (c <= b) {
                    $("#xin").eq(a).find("a").eq(c).find("img").attr("src", "/static/img/hxin.png")
                } else {
                    $("#xin").eq(a).find("a").eq(c).find("img").attr("src", "/static/img/xin.png")
                }
            }
        })
    });
    $("#xin2").each(function (a) {
        $("#xin2").eq(a).children("a").click(function () {
            var b = $(this).index();
            $("#like").val(b)
            for (var c = 0; c < 5; c++) {
                if (c <= b) {
                    $("#xin2").eq(a).find("a").eq(c).find("img").attr("src", "/static/img/hxin.png")
                } else {
                    $("#xin2").eq(a).find("a").eq(c).find("img").attr("src", "/static/img/xin.png")
                }
            }
        })
    });
    $("#edit1").click(function () {
        $(".mask").show();
        $(".bj").show()
    });
    $("#edit2").click(function () {
        $(".mask").show();
        $(".xg").show()
    });
    $("#avatar").click(function () {
        $(".mask").show();
        $(".avatar").show()
    });
    $(".gb").click(function () {
        $(".mask").hide();
        $(".bj").hide();
        $(".xg").hide();
        $(".remima").hide();
        $(".avatar").hide();
        $(".pj").hide();
        $(".chak").hide()
    });
    $("#addxad").click(function () {
        //添加地址区域
        $(".mask").show();
        $(".adddz").show()

    });
    $(".vewwl").hover(function () {
        $(this).children(".wuliu").fadeIn(100)
    }, function () {
        $(this).children(".wuliu").fadeOut(100)
    })

    $(".sx .delColl").click(function (){
        let pid = $(".sx").find(".pid").text();
        let user_id = $(".sx").find(".uid").text()
        let obj = {"user_id":user_id,"pid":pid}
        let thisObj = $(this)
        $.ajax({
            url: serverhost + "/user/delCollection",
            method: "POST",
            data: JSON.stringify(obj),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType:"json",
            success: function (data) {
                if(data.code==="200"){
                    alert("删除成功")
                    thisObj.parent().parent(".fl").remove()
                    if($(".sx").find(".fl").length===0){
                        $(".sx .empty").show()
                    }
                }else {
                    alert(data.msg)
                }
            },
        })
    })//删除收藏操作
});
