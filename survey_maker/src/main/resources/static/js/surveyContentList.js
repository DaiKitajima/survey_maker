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

	// データ削除確認
	$("[id^='contentDelBtn']").on('click', function(e) {
		e.preventDefault();
		$('.remind-msg').text(''); 
		var url = $(this).attr('href');
		$('#deleteDataURL').val(url); 
		$("#dataDeleteConfirmDialog").modal("show");
	});
	// 質問削除確認
	$('#questionDelBtn').on('click', function(e) {
		e.preventDefault();
		$('.remind-msg').text(''); 
		var url = $(this).attr('href');
		var questionIdLst = [];
	    // チェックされているチェックボックスの値を取得
		$(".checkItems:checked").each(function(){
			questionIdLst.push($(this).val());
		});
		
		// チェックボックスがひとつでも未チェックしたら、メッセージ出す
	    if(questionIdLst.length == 0) {
	    	alert("削除対象を選択してください。");
	    }else{
			url = url + "?questionIdLst=" + questionIdLst;
			$('#deleteDataURL').val(url); 
			$('.remind-msg').text('※診断質問を削除しますと、ポイント設定不足になる可能性があります。質問追加、各質問のポイント調整などで対応してください。'); 
			$("#dataDeleteConfirmDialog").modal("show");
		}
	});
	
	// 軸削除確認
	$('[id^=categoryDelBtn]').on('click', function(e) {
		e.preventDefault();
		$('.remind-msg').text(''); 
		var url = $(this).attr('href');
		$('#deleteDataURL').val(url); 
		$('.remind-msg').text('※診断軸を削除しますと、質問のポイント採点の更新が必要になります。且つ、実施済みの診断結果にこの軸の結果が取得できなくなります。'); 
		
		$("#dataDeleteConfirmDialog").modal("show");
	});
	
	$('#dataDeleteConfirmDialogSubmit').on('click', function() {
		location.href = $('#deleteDataURL').val();
		$(this).prop("disabled", true);
		$('.remind-msg').text(''); 
	});
	
	// シミュレーションボタン押下
	$('.simulationBtn').on('click', function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		$('#simulationURL').val(url); 
		var fubiFlg = false;
		$('.simuCk').each(function(){
			fubiFlg = true;
			return false;
		});
		// 提示メッセージを表示
		if(fubiFlg){
			$(".fubi_msg").show();
		}else{
			$(".fubi_msg").hide();
		}
		$("#simulationConfirmDialog").modal("show");
	});
	
	$('#simulationConfirmDialogSubmit').on('click', function() {
		window.open($('#simulationURL').val());
		$("#simulationConfirmDialog").modal('hide');
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
	
	$("#questionContentTable").tableDnD({
		onDragClass: "reorder_rows_onDragClass"
	});
	
	$("#questionOrderUpdateForm").on('submit', function(e) {
		e.preventDefault();
		var orderArray = [];
		$("table#questionContentTable tbody tr").each(function(index){
			var questionId = -1;
			$(this).find(".checkItems").each(function(){
				questionId = $(this).prop("value");
			});
			var orderNo = index + 1;
			orderArray.push({"questionId": questionId,"orderNo": orderNo});
		});
		var newInput = $('<input>').attr({
						type: 'hidden',
						name: 'orderJson',
						value: JSON.stringify(orderArray)
						});
		$(this).append(newInput);
		$(this).unbind('submit').submit();
		// ボタン非活性にする
		$('#questionOrderUpdateBtn').prop("disabled",true);
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
	// 既存内容クリア
	$("#ShowImage_Form #img_show").html("");
	$("#ShowImage_Form #modalLabel").html("");
	
	if(source != null){
		$("#ShowImage_Form #img_show").html("<image src='" + source + "' class='carousel-inner img-responsive img-rounded' style='cursor: pointer;'/>");
	}
    if(title != null){
		$("#ShowImage_Form #modalLabel").html(title);
	}
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