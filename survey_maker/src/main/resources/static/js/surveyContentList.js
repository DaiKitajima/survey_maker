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

	// ============================================
	// 検索モード制御
	// ============================================
	if($('#searchMode').val() == 1 ){
		$('#submitFormIdForAISearch').show();
		$('#submitFormIdForSearch').hide();
	}else{
		$('#submitFormIdForAISearch').hide();
		$('#submitFormIdForSearch').show();
	}
	
	// 検索モード切り替え AI検索
	$('#aiSearchRadio').on('click', function() {
		$('#submitFormIdForAISearch').show();
		$('#submitFormIdForSearch').hide();
		$('[id^=searchMode]').each(function(){$(this).val(1)});
	});

	// 検索モード切り替え　詳細検索
	$('#detailSeachRadio').on('click', function() {
		$('#submitFormIdForAISearch').hide();
		$('#submitFormIdForSearch').show();
		$('[id^=searchMode]').each(function(){$(this).val(2)});
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

	// =============================================
	// 公開フラグ用チェックボックス制御
	// =============================================
	// checkitemsのclass属性のものを全選択する
	$('#checkAllPublish').on('click', function() {
		$('.checkItemsPublish').prop('checked', $(this).is(':checked'));

	});

	// チェックボックスの全選択・解除
	$('.checkItemsPublish').on('click', function() {
		checkBox = ".checkItemsPublish";
		// 全チェックボックスの数を取得
		var boxCount = $(checkBox).length;
	    // チェックされているチェックボックスの数を取得
		var checked  = $(checkBox + ':checked').length;
		// チェックボックスがひとつでも外れたら、全選択のチェックボックスを外す
	    if(checked === boxCount) {
	    	$('#checkAllPublish').prop('checked', true);
	    } else {
			$('#checkAllPublish').prop('checked', false);
	    }
	});

	// 検索条件クリア
	$('#searchConditionClear').on('click', function() {
		$('.searchSubject').each(function(){
			$(this).val("");
		});
	});

	// 検索Submit
	$('#tableDataSearch').on('click', function() {
		execSubmitSearch('tableDataSearch');
	});

/*	// 編集Submit
	$('#tableDataUpdate').on('click', function() {
		execSubmit('tableDataUpdate');
	});*/

	// データ削除確認
	$('.tableDataDelete').on('click', function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		var idLst = [];
		var tableId = $(document.getElementById('tableInfo.id')).val();
		$('[id^=recordData]').each(function(){
			if($(this).prop('checked')){
				idLst.push($(this).attr('id').replace('recordData',''));
			};
		});
		if(idLst.length == 0 ){
			alert("削除レコードを選択してください。");
		}else{
			var para = '?tableId=' + tableId + '&dataId=' + idLst;
			$('#deleteUrl').val(url + para);
			$("#dataDeleteConfirmDialog").modal("show");
		}
	});

	$('#dataDeleteConfirmDialogSubmit').on('click', function() {
		location.href = $('#deleteUrl').val();
	});


	// リスト一括編集クリック
	$('#listUpdateCheck').on('click', function() {
		if($(this).prop('checked')){
			// ONの場合
			$('table.tableDataList input').attr("disabled" ,false);
			$('table.tableDataList select').attr("disabled" ,false);
			$('.listMode').hide();
			$('.editMode').show();
		}else{
			// OFFの場合
			$('table.tableDataList input').attr("disabled" ,"disabled");
			$('table.tableDataList select').attr("disabled" ,"disabled");
			$('.listMode').show();
			$('.editMode').hide();
		}
	});


	// データリスト一括更新
	$('#tableDataListUpdate').on('click', function() {
		var idLst = [];
		$('[id^=recordData]').each(function(){
			if($(this).prop('checked')){
				idLst.push($(this).attr('id').replace('recordData',''));
			};
		});
		if(idLst.length == 0 || !$('#listUpdateCheck').prop('checked')){
			alert("更新したレコードを選択してください。");
		}else{
			$('#updateIdLst').val(idLst);
			$("#dataListUpdateConfirmDialog").modal("show");
		}
	});

	$('#dataListUpdateConfirmDialogSubmit').on('click', function() {
		// 更新ボタンを非活性にする
		$(this).prop("disabled", true);
		var form = $("#submitFormId")[0];
		if (!form.checkValidity()){
			if (form.reportValidity){
				$(this).prop("disabled", false);
				$("#dataListUpdateConfirmDialog").modal("hide");
				form.reportValidity();
			}
		}else{
			$("#submitFormId").submit();
		}
	});

	// ========================================================
	// テーブルソート初期化（ディフォルト：第一列以外、全項目ソート）
	// ========================================================
	$.tablesorter.defaults.headers = {0: {sorter: false}};
	$(".tablesorter").tablesorter({
		sortRestart : true,
		sortReset   : true
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

// ページクリックイベント
function pageLink(formName, page){
	$("[name='pageNum']").val(page);
	$("[name='pageSize']").val(5); // ディフォルト5件設定
	$("#" + formName).submit();
}

// 画像プレビュー
function showimage(imgId) {
	var source = $("#" + imgId).attr("src");
	var title = $("#" + imgId).attr("alt");
    $("#ShowImage_Form #img_show").html("<image src='" + source + "' class='carousel-inner img-responsive img-rounded' style='cursor: pointer;'/>");
    $("#ShowImage_Form #modalLabel").html(title);
    $("#ShowImage_Form").modal("show");
}

// 画像プレビュー（非同期）
function showimageAsyn(tableId,dataId,Key) {
	var param = "?tableId=" + tableId + "&tableDataId=" + dataId + "&tableFiledName=" + Key;
	// 画像非同期取得
	$.ajax({
		  async: true,
		  url: '/internalApi/tableDetail/tableDataImage' + param,
		  type: 'GET',
		  dataType: 'text',
		  timeout: 30000,
		  success: successCallback,
		  error: errorCallback,
		  complete: completeCallback
		})
		function successCallback(imgB64) {
			if(imgB64 != ""){
				$("#ShowImage_Form #img_show").html("<image src='" + imgB64 + "' class='carousel-inner img-responsive img-rounded' style='cursor: pointer;'/>");
				$("#ShowImage_Form #modalLabel").html(Key);
				$("#ShowImage_Form").modal("show");
			}else{
				$("#ShowImage_Form #img_show").html("<image src='./img/img_error.svg' alt='画像が存在してありません。' class='carousel-inner img-responsive img-rounded' style='cursor: pointer;'/>");
				$("#ShowImage_Form #modalLabel").html(Key);
				$("#ShowImage_Form").modal("show");
			}
		}
		function errorCallback(xhr, status){
		    console.log('error: ' + status);
		}
		function completeCallback(xhr, status){
		    console.log('Ajax Complete!');
		}
}