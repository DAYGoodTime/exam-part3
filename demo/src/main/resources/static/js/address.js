function addressLoad(count,addobj){
    let you = document.querySelector(".you")
    let addcount = (parseInt(((count+1) / 3)))
    //创建add
    for (let j = 1; j <= addcount; j++) {
        let add = document.createElement("div")
        add.className = "addn " + "add"+j
        you.appendChild(add)
    }
    for (let i = 0; i < count ; i++) {
        let address = addobj[i].address
        let name = addobj[i].receiveName
        let phone = addobj[i].mobile
        let province = addobj[i].province
        let zipcode = addobj[i].zipcode
        let addId = addobj[i].receiveAddressId
        let isDefault = addobj[i].isDefault

        let dizhi = document.createElement("div")
        dizhi.className = "dizhi"
        let diveParent
            if(i<=1){
               diveParent = document.querySelector(".add")
            }else {
                diveParent = document.querySelector(".add"+parseInt(((i+1)/3)))
            }
            diveParent.appendChild(dizhi)

            let dizhis = document.querySelector(".you").querySelectorAll(".dizhi")
            var c = "";
            c = '<p class="addp"><a href="#"  id="readd">修改</a><a href="#" id="deladd">删除</a></p>';
            var jqobj = $(dizhi[i]);
            for (let j = 0; j < 7; j++) {
                let p1 = document.createElement("p")
                dizhis[i].appendChild(p1)
            }
            let ps = dizhis[i].querySelectorAll("p")
            ps[0].innerHTML = name
            ps[1].innerHTML = phone
            ps[2].innerHTML = province
            ps[3].innerHTML = address + "("+zipcode+")"
            ps[4].className = "addp"
            ps[4].style.display= 'none'
            let readd = document.createElement("a")
            readd.href = "#"
            readd.id = "readd"
            readd.innerHTML = "修改"
            ps[4].appendChild(readd)
            let deladd  =document.createElement("a")
            deladd.href = "#"
            deladd.id = "deladd"
            deladd.innerHTML = "删除"
            ps[4].appendChild(deladd)
            ps[5].className = "addId"
            ps[5].innerHTML = addId
            ps[5].style.display= 'none'
            ps[6].className = "isDefault"
            ps[6].innerHTML = isDefault
            ps[6].style.display= 'none'
        }
}