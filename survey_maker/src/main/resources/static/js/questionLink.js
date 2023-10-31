$(function(){
	// =============================================
	// リンク種別選択時、リンク先内容を動的に変更
	// =============================================
	$('select[id^="linkType"]').on('change', function() {
		var index = $(this).prop("id").replace("linkType","");
		// 質問リンクの場合
		if($(this).val() == 1 ){
			
		}
		// 結果リンクの場合
		if($(this).val() == 2 ){
			
		}
	});
})
