$(function(){
	/* スクリプトコピーボタン押下*/
	$("#urlCopyBtn").click(function(){
		var copyText = $("#resultUrl").val();
		navigator.clipboard.writeText(copyText);
		alert("クリップボードにコピーしました。");
	});

	/* フローパタンの場合、回答リンク押下時*/
	$("#nextQuestion").click(function(e){
		e.preventDefault();
		alert("ajaxで次質問取得して設定");
	});

	$('#submitFormId').on('submit', function(e) {
		e.preventDefault();
		
		$(this).unbind('submit').submit();
		// ボタン非活性にする
		$('#questionLinkBtn').prop("disabled",true);
	});
	

})
