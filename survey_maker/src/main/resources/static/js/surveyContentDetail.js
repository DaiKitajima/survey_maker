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
	
	// カテゴリー登録・更新サブミート
	$('#categoryＦｏｒｍId').on('submit', function(e) {
		e.preventDefault();
		var submitFlg = true;
		var minFromVal = 100;
		var maxToVal = 0;
		// ポイント範囲チェック(FromとTo値チェック)
		$("input[name*='pointTo']").each(function(){
			var pointFrom = $(this).parent().siblings().find("input[name*='pointFrom']");
			var fromVal = parseInt(pointFrom.val());
			var toVal = parseInt($(this).val());
			if(fromVal >= toVal){
				submitFlg = false;
				alert("ポイント範囲のFROMがTOより小さい値で設定してください。");
			}
			
			// min値取得
			if(fromVal < minFromVal ){
				minFromVal = fromVal;
			}
			// max値取得
			if(toVal > maxToVal ){
				maxToVal = toVal;
			}
		});
		
		// ポイント範囲チェック(FromとTo値チェック)
		if(minFromVal > 0 ){
			submitFlg = false;
			alert("ポイント範囲[0-"+ maxToVal+"]の値を全て設定してください。");
		}
		
		if(submitFlg){
			$(this).unbind('submit').submit();
			// ボタン非活性にする
			$('#categoryBtn').prop("disabled",true);
		}else{
			// ボタン活性にする
			$('#categoryBtn').prop("disabled",false);
			return false;
		}
	});
	
	// カテゴリーコンテンツ削除
	$("a[name^='categoryContentLst'], [name$='delete']").on('click', function(e) {
		e.preventDefault();
		$("#deleteContent").val($(this).prop("name"));
		$("#categoryResultDeleteConfirmDialog").modal("show");
	});
	
	$("#categoryResultDeleteConfirmDialogSubmit").on('click', function() {
		var deleteContentName = $("#deleteContent").val();
		contentDel(null,'categoryContent','categoryContentLst',deleteContentName);
		$("#categoryResultDeleteConfirmDialog").modal("hide");
	});
	
	// 回答コンテンツ削除
	$("a[name^='answerContentLst'], [name$='delete']").on('click', function(e) {
		e.preventDefault();
		$("#deleteContent").val($(this).prop("name"));
		$("#questionAnswerDeleteConfirmDialog").modal("show");
	});
	
	$("#questionAnswerDeleteConfirmDialogSubmit").on('click', function() {
		var deleteContentName = $("#deleteContent").val();
		contentDel(null,'questionContent','answerContentLst',deleteContentName);
		$("#questionAnswerDeleteConfirmDialog").modal("hide");
	});
})


/*
	addBtn:追加ボタン
	copyContentEle：コピー元のコンテンツ要素
	formDataEle:　フォーム要素
*/
function contentAdd(addBtn, copyContentEle,formDataEle){
	var copyHtml = $('.' + copyContentEle).prop("outerHTML");
	$(addBtn).before(copyHtml);
	var addedHtml = $(addBtn).prev();
	// コピー先の各要素の値をクリア
	$(addedHtml).find('.' + formDataEle).each(function(){
		$(this).val('');
	});
	
	// 画像項目の特殊処理
	$(addedHtml).find("[class*='preview']").each(function(){
		var fileChange = $(this).next();
		var input = fileChange.children("input[type='file']").prop("outerHTML");
		$(this).before(input);
		$(this).prev().prop("disabled",false);
		$(this).remove();
		fileChange.remove();
	});
	// フォームデータ調整
	formDataReplace(copyContentEle, formDataEle);
}

/*
	delBtn:削除ボタン
	delContentEle：削除コンテンツ要素
	formDataEle:　フォーム要素
*/
function contentDel(delBtn,delContentEle,formDataEle,deleteContentName){
	var delBtnName = "";
	if(deleteContentName != null ){
		delBtnName = deleteContentName;
	}else{
		delBtnName = $(delBtn).prop("name");
	}
	if($('.'+delContentEle).length != 1 ){
		$('.'+delContentEle).each(function(){
			if($(this).find("[name='"+delBtnName+"']").length > 0 ){
				$(this).remove();
			}
		});

		// フォームデータ調整
		formDataReplace(delContentEle, formDataEle);
	}
}

/*
	contentEle:追加・削除ブロック要素
	formDataEle：サブミート対象データの要素文字
*/
function formDataReplace(contentEle,formDataEle){

	$('.' + contentEle).each(function(index){
		var regex = new RegExp(formDataEle + "\[[0-9]+\]","g");
		var target = formDataEle + "[" + index + "]";
		$(this).find("[name^='"+ formDataEle +"']").each(function(){
			var name = $(this).attr("name");
			$(this).attr("name", name.replace(regex, target));
		});
	});
}