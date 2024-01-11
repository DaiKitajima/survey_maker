$(function(){
	const rootPath = window.location.protocol + '//' + window.location.host;
	
	/* スクリプトコピーボタン押下*/
	$("#resultUrl").val( rootPath + $("#resultUrl").val() );
	$("#urlCopyBtn").click(function(){
		$("#resultUrl").select();
/*		var copyText = $("#resultUrl").val();
		navigator.clipboard.writeText(copyText);*/
		if (document.execCommand('copy')) {
		    document.execCommand('copy');
		    alert("クリップボードにコピーしました。");
		}else{
			alert("コピー失敗しました。");
		}
	});

	$('#nextQuestion').on('click', function() {
		var radioChk = false;
		var index = $("div.--current").prop("id");
		var questionCnt = $("#questionCnt").val();
		$("div.questionContent"+ index +" input[type='radio']").each(function(){
			if($(this).prop("checked")){
				radioChk = true;
			}
		});
		if(!radioChk){
			alert("質問の回答を選択してください。");
			return false;
		}else{
			// サブミート要否判断
			if( questionCnt == parseInt(index)+1 ){
				// ボタン非活性にする
				$(this).prop("disabled",true);
				$('#submitFormId').submit();
			}else{
				// プログレスバー
				var nextIndex = parseInt(index)+1;
				$("div.--current").addClass("--completed");
				$("div.--current").removeClass("--current");
				$("[id=" + nextIndex + "]").addClass("--current");
				
				// 当該質問を隠れ
				$("div.questionContent"+ index ).hide();
				// 次質問を現す
				$("div.questionContent"+ nextIndex ).show();
				// 「次へ」を「サブミート」ボタンへ変更
				if(questionCnt == parseInt(nextIndex) + 1){
					$(this).text("診断する");
				}
				return false;
			}
		}
	});
	

})
