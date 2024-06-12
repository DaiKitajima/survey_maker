$(function(){
	const rootPath = window.location.protocol + '//' + window.location.host;
	
	// ディフォルトカラーのコード値設定
	$('#inputColorCode').val($("#surveyColor").val()); 
	$('#shadowColorCode').val($("#surveyResultColor").val()); 
	
	// フォント字体コピー
	$('#fontSizeTag').on('click', function() {
		var content = "";
		content = content + "<span style=\"font-size:12px;\">ここにテキストを入力</span>\n";
		$("#surveyTagContent").val(content);
		$("#surveyTagCreateDialog").modal("show");
	});
	
	// 大見出しコピー
	$('#mainTitleTag').on('click', function() {
		var colorCD = $('#inputColorCode').val(); 
		var content = "";
		content = content + "<div class=\"mt-2 mb-2\" style=\"border-bottom:solid 2px;background-color: white;border-bottom-color:" + colorCD + ";\">\n";
		content = content + "	<span>大見出し</span>\n";
		content = content + "</div>";
		$("#surveyTagContent").val(content);
		$("#surveyTagCreateDialog").modal("show");
	});

	// 小見出し
	$('#subTitleTag').on('click', function() {
		var colorCD = $('#inputColorCode').val(); 
		var content = "";
		content = content + "<div class=\"mt-2 mb-2\" style=\"border-left:solid 5px;background-color: white;border-left-color:" + colorCD + ";\">\n";
		content = content + "	<span class=\"ms-1\">小見出し</span>\n";
		content = content + "</div>";
		$("#surveyTagContent").val(content);
		$("#surveyTagCreateDialog").modal("show");
	});
	
	// 誘導ボタンコピー
	$("#induceBtnTag").on('click', function() {
		var content = "";
		content = content + "<div class=\"row mt-2 mb-2\">\n";
		content = content + "	<div class=\"col d-flex justify-content-center h6\">\n";
		content = content + "		<a href=\"#\" class=\"btn btn-warning btn-lg text-light\" style=\"width:250px;border-radius:40px;\" ><span class=\"h5\">専門家に相談する　▶</span></a>\n";
		content = content + "	</div>\n";
		content = content + "</div>";
		$("#surveyTagContent").val(content);
		$("#surveyTagCreateDialog").modal("show");
	});
	
	// カラー選択フォーカスアウト時、コード値表示
	$("#surveyColor").on('focusout', function() {
		$('#inputColorCode').val($(this).val()); 
	});
	$("#surveyResultColor").on('focusout', function() {
		$('#shadowColorCode').val($(this).val()); 
	});
	
	$('#surveyTagCreateDialogSubmit').on('click', function() {
		$("#surveyTagContent").select();
		if (document.execCommand('copy')) {
		    document.execCommand('copy');
		    alert("クリップボードにコピーしました。");
		}else{
			alert("コピー失敗しました。");
		}
		$("#surveyTagCreateDialog").modal('hide');
	});
	
})
