$(function () {
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
            a();
            b()
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
            a();
            b()
        }
    });

    function a() {
        var c = 0;
        var d = $(".th input[type='checkbox']:checked").length;
        if (d == 0) {
            $("#all").text("￥" + parseFloat(0).toFixed(2))
        } else {
            $(".th input[type='checkbox']:checked").each(function () {
                var e = $(this).parents(".pro").siblings(".sAll").text().substring(1);
                c += parseFloat(e);
                $("#all").text("￥" + c.toFixed(2))
            })
        }
    }

    function b() {
        var e = 0;
        var c = $(".th input[type='checkbox']:checked").parents(".th").find(".num span");
        var d = c.length;
        if (d == 0) {
            $("#sl").text(0)
        } else {
            c.each(function () {
                e += parseInt($(this).text());
                $("#sl").text(e)
            })
        }
        if ($("#sl").text() > 0) {
            $(".count").css("background", "#c10000")
        } else {
            $(".count").css("background", "#8e8e8e")
        }
    }

    $("input[type='checkbox']").on("click", function () {
        var f = $(this).is(":checked");
        var e = $(this).hasClass("checkAll");
        if (f) {
            if (e) {
                //全选操作
                checkAll();
                b();
                a()
            } else {
                //单选操作
                let pamount = $(this).parents(".th").find(".fl")[5].innerHTML
                let pid = $(this).parents(".th").find("#pid")[0].innerHTML
                let index = $(this).parents(".th").find("#pindex")[0].innerHTML
                let obj = {"pid":pid,"productAmount":pamount,"index":index}
                ShoppingList.push(obj)
                console.log("add")
                $(this).checked = true;
                var c = $("input[type='checkbox']:checked").length;
                var d = $("input").length - 1;
                if (c == d) {
                    $("input[type='checkbox']").each(function () {
                        this.checked = true
                    })
                }
                b();
                a()
            }
        } else {
            if (e) {
                //取消全选
                $("input[type='checkbox']").each(function () {
                    try{
                        let index = $(this).parents(".th").find("#pindex")[0].innerHTML
                        ShoppingList.map((val,i) =>{
                            if(val.index === index){
                                ShoppingList.splice(i,1)
                            }
                        });
                        console.log("remove")
                        this.checked = false
                        b();
                        a()
                    }catch (e){}
                });
            } else {
                //取消单选
                let index = $(this).parents(".th").find("#pindex")[0].innerHTML
                ShoppingList.map((val,i) =>{
                    if(val.index === index){
                        ShoppingList.splice(i,1)
                    }
                });
                console.log("remove")
                $(this).checked = false;
                var c = $(".th input[type='checkbox']:checked").length;
                var d = $("input").length - 1;
                if (c < d) {
                    $(".checkAll").attr("checked", false)
                }
                b();
                a()
            }
        }
    });
    $(".choiceAll").click(function () {
        var e = $(".checkAll").prop('checked');
        if(!e){
            checkAll();
            $(".checkAll").prop("checked",true);
            b();
            a();
        }
    });
    $(".btns .cart").click(function () {
        if ($(".categ p").hasClass("on")) {
            var c = parseInt($(".num span").text());
            var d = parseInt($(".goCart span").text());
            $(".goCart span").text(c + d)
        }
    });
    $(".del").click(function () {
        if ($(this).parent().parent().hasClass("th")) {
            $(".mask").show();
            $(".tipDel").show();
            index = $(this).parents(".th").index() - 1;
            $(".cer").click(function () {
                $(".mask").hide();
                $(".tipDel").hide();
                $(".th").eq(index).remove();
                $(".cer").off("click");
                if ($(".th").length == 0) {
                    $(".table .goOn").show()
                }
                //删除操作
                del()
            })
        } else {
            if ($(".th input[type='checkbox']:checked").length == 0) {
                $(".mask").show();
                $(".pleaseC").show()
            } else {
                $(".mask").show();
                $(".tipDel").show();
                $(".cer").click(function () {
                    $(".th input[type='checkbox']:checked").each(function (c) {
                        index = $(this).parents(".th").index() - 1;
                        $(".th").eq(index).remove();
                        if ($(".th").length == 0) {
                            $(".table .goOn").show()
                        }
                    });
                    $(".mask").hide();
                    $(".tipDel").hide();
                    b();
                    a()
                })
            }
        }
    });
    $(".delAll").click(function () {
        if ($(this).parent().parent().hasClass("th")) {
            $(".mask").show();
            $(".tipDel").show();
            index = $(this).parents(".th").index() - 1;
            $(".cer").click(function () {
                $(".mask").hide();
                $(".tipDel").hide();
                $(".th").eq(index).remove();
                $(".cer").off("click");
                if ($(".th").length == 0) {
                    $(".table .goOn").show()
                }
            });
        } else {
            if ($(".th input[type='checkbox']:checked").length == 0) {
                $(".mask").show();
                $(".pleaseC").show()
            } else {
                $(".mask").show();
                $(".tipDel").show();
                $(".cer").click(function () {
                    $(".th input[type='checkbox']:checked").each(function (c) {
                        index = $(this).parents(".th").index() - 1;
                        $(".th").eq(index).remove();
                        if ($(".th").length == 0) {
                            $(".table .goOn").show()
                        }
                    });
                    $(".mask").hide();
                    $(".tipDel").hide();
                    b();
                    a()
                    //批量删除操作
                    console.log("批量删除")
                    let user_id = document.getElementById("user_id").innerHTML
                    let jsonobj = {"user_id":user_id,"products":ShoppingList}
                    $.ajax({
                        url: serverhost + "/delCart",
                        method: "POST",
                        data: JSON.stringify(jsonobj),
                        contentType: "application/json;charset=utf-8",
                        async: true,
                        dataType:"json",
                        complete: function (data) {
                             window.location.href = serverhost+data.responseText
                        },
                    })
                })
            }
        }
    });
    $(".cancel").click(function () {
        $(".mask").hide();
        $(".tipDel").hide()
    })
    $(".pleaseC .off").click(function () {
        $(".mask").hide();
        $(".pleaseC").hide()
    })
});
function checkAll() {
    $("input[type='checkbox']").each(function () {
        try{
            let pamount = $(this).parents(".th").find(".fl")[5].innerHTML
            let pid = $(this).parents(".th").find("#pid")[0].innerHTML
            let index = $(this).parents(".th").find("#pindex")[0].innerHTML
            let obj = {"pid":pid,"productAmount":pamount,"index":index}
            ShoppingList.push(obj)
            console.log("add")
            this.checked = true
            b();
            a()
        }catch (e){}
    });
}