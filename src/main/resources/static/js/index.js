$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.ajax({
		url: CONTEXT_PATH+"/post",
		data:{"title":title,"content":content},
		type:"post",
		async: true,
		success: function(msg) {
		var data =  $.parseJSON(msg);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code == 0)
				{
					window.location.reload();
				}
			},2000);
		}
	});

}