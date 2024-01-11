$(function(){
	const rootPath = window.location.protocol + '//' + window.location.host;
	const toNextQestion = "/internalApi/getSurveyQuestionContent";
	const toResult = "/surveyContentDetail/surveySimulationForFlow/result";
	
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

	/* フローパタンの場合、回答リンク押下時*/
	$("body").on("click", "[id^=nextQuestion]", function(e){
		e.preventDefault();
		var contentId = $("#contentId").val();
		var index = $(this).attr("id").replace("nextQuestion","");
		var type = $("#linkType" + index).val();
		var linkTo = $("#linkTo" + index).val();
		var url = "";
		// 次質問の場合、ajax呼び出しで画面質問再設定
		if(type == $("#NEXT_QUESTION").val()){
			url = rootPath + toNextQestion + "?contentId=" + contentId + "&linkTo=" + linkTo;
			$.ajax({
			  async: true,
			  url: url,
			  type: 'GET',
			  dataType: 'json',
			  timeout: 30000,
			  contentType: 'application/json;charset=utf-8',
			  success: successCallback,
			  error: errorCallback,
			  complete: completeCallback
			})
			function successCallback(jsonData) {
				if(jsonData != ""){
					var question = jsonData;
					// 質問
					$("#questionContent").text("質問" + question.questionOrderNo + "、" + question.questionTitle);
					//　質問画像
					$("#questionImg").prop("src",question.questionImageBase64);
					$("#questionImg").prop("alt",question.questionImage);
					// 回答
					var answerContentHtml = "";
					$.each(question.answerContentLst, function(index,answer){
					    var linkType = -1;
					    var linkTo = -1;
					    $.each(question.questionLinkLst,function(index1,link){
							if(answer.answerId == link.answerId){
								linkType = link.linkType;
								linkTo = link.linkTo;
								return false;
							}
						})
						
						answerContentHtml = answerContentHtml.concat("<div class='form-check mb-2'>");
						answerContentHtml = answerContentHtml.concat("  <input type='hidden' id='linkType" + index + "' value='"+ linkType +"'>");
						answerContentHtml = answerContentHtml.concat("  <input type='hidden' id='linkTo" + index + "' value='"+ linkTo +"'>");
						answerContentHtml = answerContentHtml.concat("  <label class='form-check-label'>");
						answerContentHtml = answerContentHtml.concat("    <a href='#' class='btn btn-sm btn-info' style='width:150px;' id='nextQuestion"+ index + "'>"+ answer.answer +"</a>");
						answerContentHtml = answerContentHtml.concat("  </label>");
						answerContentHtml = answerContentHtml.concat("</div>");
						
					});
					$(".answerContent .form-check").each(function(){$(this).remove()});
					$(".answerContent").append(answerContentHtml);
				}
			}
			function errorCallback(xhr, status){
			    console.log('error: ' + status);
			    alert('エラーが発生しました。');
			}
			function completeCallback(xhr, status){
			    console.log('got next question Complete!');
			}
		}else if(type == $("#SURVEY_RESULT").val()){
			// 評価結果の場合、結果画面へ遷移
			url = rootPath + toResult + "?contentId=" + contentId + "&linkTo=" + linkTo;
			location.href = url;
			//　ボタン非活性にする
			$(this).prop("disabled",true);
		}
	});


})
