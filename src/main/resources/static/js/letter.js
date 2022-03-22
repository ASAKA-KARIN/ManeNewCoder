$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.ajax({
		url: CONTEXT_PATH+"/message",
		data:{"toName":toName,"content":content},
		method:"post",
		async: true,
		success: function(msg) {
			var data =  $.parseJSON(msg);
			if(data.code != 0)
			{
				$("#hintBody").text(data.msg);
			}else
			{
				$("#hintBody").text("发送成功")
			}
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
					window.location.reload();
			},2000);
		}
	});
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}