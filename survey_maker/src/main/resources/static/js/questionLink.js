$(function(){
	// =============================================
	// 画面初期表示時、リンク種別よりリンク先内容を事前設定
	// =============================================
	$('select[id^="linkType"]').each(function() {
		var index = $(this).prop("id").replace("linkType","");
		var idIndex = index.substring(0,index.indexOf("_"));
		var questionId = $("#questionId" + idIndex).val();
		var linkTo = $("#linkTo" + index);
		// 初期時、タイプ選択あり、リンク先がなし、かつタイプが質問リンクの場合
		if($(this).val() == 1 && linkTo.val() == null ){
			linkTo.empty();
			linkTo.append($("#toQuestionLst option").clone());
			linkTo.find("option").each(function(){
				if($(this).val() == questionId){
					$(this).remove();
				}
			});
		}
		// 結果リンクの場合
		if($(this).val() == 2 && linkTo.val() == null ){
			linkTo.empty();
			linkTo.append($("#toResultLst option").clone());
		}
	});
	
	// =============================================
	// リンク種別選択時、リンク先内容を動的に変更
	// =============================================
	$('select[id^="linkType"]').on('change', function() {
		var index = $(this).prop("id").replace("linkType","");
		var idIndex = index.substring(0,index.indexOf("_"));
		var questionId = $("#questionId" + idIndex).val();
		var linkTo = $("#linkTo" + index);
		// 質問リンクの場合
		if($(this).val() == 1 ){
			linkTo.empty();
			linkTo.append($("#toQuestionLst option").clone());
			linkTo.find("option").each(function(){
				if($(this).val() == questionId){
					$(this).remove();
				}
			});
		}
		// 結果リンクの場合
		if($(this).val() == 2 ){
			linkTo.empty();
			linkTo.append($("#toResultLst option").clone());
		}
		linkTo.find("option:first").prop("selected",true);
	});
	
	// =============================================
	// リンク情報登録時
	// =============================================
	$('#questionLinkFormId').on('submit', function(e) {
		e.preventDefault();
		var questionLinkIndex = 0;
		$("tbody tr").each(function(){
			var liCnt = 0;
			$(this).find("td li.questionLinkType").each(function(index){
				var formIndex = questionLinkIndex + index;
				$(this).find("[name^='questionLinkLst']").each(function(){
					var name = $(this).attr("name");
					var regex = new RegExp("questionLinkLst" + "\[[0-9]+\]","g");
					var target = "questionLinkLst" + "[" + formIndex + "]";
					$(this).attr("name", name.replace(regex, target));
				});
				liCnt++;
			});
			
			$(this).find("td li.questionLinkTo").each(function(index){
				var formIndex = questionLinkIndex + index;
				$(this).find("[name^='questionLinkLst']").each(function(){
					var name = $(this).attr("name");
					var regex = new RegExp("questionLinkLst" + "\[[0-9]+\]","g");
					var target = "questionLinkLst" + "[" + formIndex + "]";
					$(this).attr("name", name.replace(regex, target));
				});
			});
			questionLinkIndex = questionLinkIndex + liCnt;
		});
		
		$(this).unbind('submit').submit();
		// ボタン非活性にする
		$('#questionLinkBtn').prop("disabled",true);
	})
	
})
