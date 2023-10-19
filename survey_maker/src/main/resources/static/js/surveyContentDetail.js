$(function(){
	
	var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
	var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
	  return new bootstrap.Popover(popoverTriggerEl)
	})
	
	// =============================================
	// 連続登録用チェックボックス制御
	// =============================================
	// checkitemsのclass属性のものを全選択する
	$('#checkAll').on('click', function() {
		$('.checkItems').prop('checked', $(this).is(':checked'));

	});

	// チェックボックスの全選択・解除
	$('.checkItems').on('click', function() {
		checkBox = ".checkItems";
		// 全チェックボックスの数を取得
		var boxCount = $(checkBox).length;
	    // チェックされているチェックボックスの数を取得
		var checked  = $(checkBox + ':checked').length;
		// チェックボックスがひとつでも外れたら、全選択のチェックボックスを外す
	    if(checked === boxCount) {
	    	$('#checkAll').prop('checked', true);
	    } else {
			$('#checkAll').prop('checked', false);
	    }
	});
	// ========================================================
	// データ更新画面、画像ファイル変更
	// ========================================================
	$("[id^=change]").on('click', function() {
		var key = $(this).attr("id").replace("change","");
		var fileChangeClass = ".fileChange" + key;
		var filePreviewClass = ".preview" + key;
		$(filePreviewClass).hide();
		$(filePreviewClass).children("input").attr("disabled","disabled");

		$(fileChangeClass).show();
		$(fileChangeClass).children("input").attr("disabled",false);
	});

	$("[id^=modoru]").on('click', function() {
		var key = $(this).attr("id").replace("modoru","");
		var fileChangeClass = ".fileChange" + key;
		var filePreviewClass = ".preview" + key;
		$(fileChangeClass).hide();
		$(fileChangeClass).children("input").attr("disabled","disabled");

		$(filePreviewClass).show();
		$(filePreviewClass).children("input").attr("disabled",false);
	});
	
})


/*
	addBtn:追加ボタン
	copyContentEle：コピー元のコンテンツ要素
*/
function contentAdd(addBtn, copyContentEle){
	var copyHtml = $('.' + copyContentEle).prop("outerHTML");
	$(addBtn).before(copyHtml);
	var addedHtml = $(addBtn).prev();
	// コピー先の各要素の値をクリア
	$(addedHtml).find('input').each(function(){
		$(this).val('');
	});
	
	$(addedHtml).find('textarea').each(function(){
		$(this).val('');
	});
	
	$(addedHtml).find("[class*='preview']").each(function(){
		var fileChange = $(this).next();
		var input = fileChange.children("input[type='file']").prop("outerHTML");
		$(this).before(input);
		$(this).prev().prop("disabled",false);
		$(this).remove();
		fileChange.remove();
	});
}

/*
	delBtn:削除ボタン
	delContentEle：削除コンテンツ要素
*/
function contentDel(delBtn,delContentEle){
	var removeContent = $(delBtn).parent().parent();
	if($('.'+delContentEle).length != 1 ){
		removeContent.remove();
	}
}
