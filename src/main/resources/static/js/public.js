const serverhost = "http://localhost:8080"
window.onload=function () {
    //加载商品购物车数量
    let numbers = $("#shopping-number").text()
    document.querySelector(".gwc").setAttribute("numbers",numbers);
    try{
        onload1();
    }catch (e) {
        //并不是所有页面都会有onload1
    }
    //其他页面的onload函数
    if($(".like").length>0){//推荐加载
        guestLike();
        jQuery(".bottom").slide({
            titCell: ".hd ul",
            mainCell: ".bd .likeList",
            autoPage: true,
            autoPlay: false,
            effect: "leftLoop",
            autoPlay: true,
            vis: 1
        });
    }
};
$(function () {
    loadTopBar()
    //地址地区选择js
    $("#province").change(function () {
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
        });
    $("#province2").change(function () {
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
    })
    $("#city").change(function () {
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
    })
    $("#city2").change(function () {
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
    })
    //搜索栏
    $("#searchBtn").click(function () {
        document.getElementById("searchsumbit").click()
    })

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
function loadTopBar() {
    //首页TopBar的加载
    //请求获取首页数据
    var bar_data;
    $.ajax({
        url: serverhost + "/queryTopBar",
        method: "POST",
        contentType: "application/json;charset=utf-8",
        async: false,
        dataType:"json",
        success: function (data) {
            if(data.code==="200"){
                bar_data = data.data
            }else {
                alert(data.msg);
            }
        }})
    let bott = $("#bott");
    let pro_list = $("#all-pro")
    let MainCategory = bar_data.MainCategory;
    let hasChildrenCategory = bar_data.hasChildrenCategory;
    let noChildrenCategory = bar_data.noChildrenCategory;
    //所有大分类

    //在所有商品中展示大分类,使用sList
    for (let i = 0; i < MainCategory.length; i++) {
        let obj = MainCategory[i]
        let a = document.createElement("a")
        let dl = document.createElement("dl")
        let dt = document.createElement("dt")
        let img = document.createElement("img")
        let dd = document.createElement("dd")
        a.href = serverhost + "/" + obj.parent_category
        dd.innerHTML = obj.parent_name
        img.src = "/static/img/nav1.jpg" //TODO 图片地址暂时写死
        dt.append(img)
        dl.append(dt)
        dl.append(dd)
        a.append(dl)
        pro_list.append(a)
    }
    //有子分类的使用sList2
    for (let i = 0; i < hasChildrenCategory.length; i++) {
        let obj = hasChildrenCategory[i]
        let li = document.createElement("li")
        let a = document.createElement("a")
        let div1 = document.createElement("div")
        let div2 = document.createElement("div")
        div1.className = "sList2"
        div2.className = "clearfix"
        for (let j = 0; j < obj.children.length; j++) {
            let a = document.createElement("a")
            a.href = serverhost + "/" + obj.parent_id + "/" + obj.children[j].children_category
            a.innerHTML = obj.children[j].children_name
            div2.append(a)
        }
        div1.append(div2)
        a.href = serverhost + "/" + obj.parent_id
        a.innerHTML = obj.parent_name
        li.append(a)
        li.append(div1)
        bott.append(li)
    }
    //没子分类的直接输出
    for (let i = 0; i < noChildrenCategory.length; i++) {
        let li = document.createElement("li")
        let a = document.createElement("a")
        a.href = serverhost + "/" + noChildrenCategory[i].parent_category
        a.innerHTML = noChildrenCategory[i].parent_name
        li.append(a)
        bott.append(li)
    }
}
function logout(){
    var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
    if(keys) {
        for(var i = keys.length; i--;){
            document.cookie = keys[i] + '=null; expires=' + new Date(0).toString()+";"}
    }//window.location.href = serverhost + "/user/logout"
    window.location.href = serverhost + "/user/logout"
}
function guestLike() {
    //获取商品
    var List = []
    let user_id = $("#user_id").text()
    let obj = {"uid":user_id}
    $.ajax({
        url: serverhost + "/guestYouLike",
        method: "POST",
        data:JSON.stringify(obj),
        contentType: "application/json;charset=utf-8",
        async: false,
        dataType:"json",
        success: function (data) {
            List = data.data
        },
    })
    let like = $(".likeList .like-box")//216x216
    let recommend = $(".seeList")//161x243
    for (let i = 0; i < List.length; i++) {
        let a = document.createElement("a")
        a.href = serverhost + "/proDetail/" + List[i].productId
        let dl = document.createElement("dl")
        let dt = document.createElement("dt")
        let img = document.createElement("img")
        img.style.width = '161px'
        img.style.height = '243px'
        img.src = List[i].picture
        dt.append(img)
        let dd1 = document.createElement("dd")
        let dd2 = document.createElement("dd")
        dd1.innerHTML = List[i].name
        dd2.innerHTML = '￥' + List[i].fixedPrice
        dl.append(dt)
        dl.append(dd1)
        dl.append(dd2)
        a.append(dl)
        recommend.append(a)
        let a2 = a.cloneNode(true);
        let img2 = a2.querySelector("img")
        img2.style.width = '216px'
        img2.style.height = '216px'
        like.append(a2)
    }
}

