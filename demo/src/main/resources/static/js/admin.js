$(function (){
    $("input[type='checkbox']").on("click", function () {
        var f = $(this).is(":checked");
        var e = $(this).hasClass("checkAll");
        if(f){
            if(e){
                //全选增加
                ObjectList = []
                let ids = document.querySelectorAll(".ids");
                for (let i = 0; i < ids.length ; i++) {
                    ObjectList.push(ids[i].getAttribute("value"))
                    ids[i].checked = true
                }
                console.log(ObjectList)
            }else {
                //单选增加
                ObjectList.push($(this).val())
                console.log(ObjectList)
            }
        }else {
            if(e){
                //全选取消
                ObjectList = []
                let ids = document.querySelectorAll(".ids");
                for (let i = 0; i < ids.length; i++) {
                    ids[i].checked = false
                }
                console.log(ObjectList)
            }else {
                //单选取消
                let value = $(this).val()
                for (let i = 0; i < ObjectList.length ; i++) {
                    if(ObjectList[i]===value){
                        ObjectList.splice(i,1)
                    }
                }
                console.log(ObjectList)
            }
        }
    });
})