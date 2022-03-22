$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	var userId = $(btn).prev().val();
	if($(btn).hasClass("btn-info")) {
		$.ajax({
			url: CONTEXT_PATH+"/follow",
			data:{"entityType":3,"entityId":userId},
			method:"post",
			async: true,
			success: function(msg) {
				var data =  $.parseJSON(msg);
				if(data.code == 0)
				{// 关注TA
					$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
				}else
				{
					alert(msg);
				}
			}
		});

	} else {
		// 取消关注
		$.ajax({
			url: CONTEXT_PATH+"/unfollow",
			data:{"entityType":3,"entityId":userId},
			method:"post",
			async: true,
			success: function(msg) {
				var data =  $.parseJSON(msg);
				if(data.code == 0)
				{// 关注TA
					$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
				}else
				{
					alert(msg);
				}
			}
		});
	}
}