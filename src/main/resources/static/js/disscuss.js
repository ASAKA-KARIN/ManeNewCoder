$(function () {
    $("#top").click(setTop);
    $("#wonder").click(setWonder);
    $("#del").click(setDel);
}
)

function like(btn, entityType, entityId,entityUserId,postId) {
    $.ajax({
        url: CONTEXT_PATH + "/like",
        data: {"entityType": entityType, "entityId": entityId,"entityUserId":entityUserId,"postId":postId},
        method: "post",
        async: true,
        success: function (msg) {
            var data = $.parseJSON(msg);

            if (data.code == 0) {
                var isLike = data.likeStatus == 0 ? "赞" : "已赞";
                $(btn).children("i").text(data.likeNum);
                $(btn).children("b").text(isLike);
            }
        }

    })
}
function setTop(){
        var pid = $("#pid").val();
        $.ajax({
            url: CONTEXT_PATH + "/top",
            data: {"pid": pid},
            method: "post",
            async: true,
            success: function (msg) {
                var data = $.parseJSON(msg);
                if (data.code == 0) {
                  $("#top").attr("disabled","disabled");
                }else
                {
                    alert("置顶失败");
                }
            }})
}
function setWonder(){
    var pid = $("#pid").val();
    $.ajax({
        url: CONTEXT_PATH + "/wonder",
        data: {"pid": pid},
        method: "post",
        async: true,
        success: function (msg) {
            var data = $.parseJSON(msg);
            if (data.code == 0) {
                $("#wonder").attr("disabled","disabled");
            }else
            {
                alert("加精失败");
            }
        }})
}
function setDel(){
    var pid = $("#pid").val();
    $.ajax({
        url: CONTEXT_PATH + "/del",
        data: {"pid": pid},
        method: "post",
        async: true,
        success: function (msg) {
            var data = $.parseJSON(msg);
            if (data.code == 0) {
            location.href = CONTEXT_PATH+"/index";
            }else
            {
                alert("删除失败");
            }
        }})
}

