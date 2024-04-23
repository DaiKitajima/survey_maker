// バリデーションエラー時の背景色
const VALID_ERROR_BACK_COLOR = "#ffe6e6";

$(function(){
	//=================================
	// DivタグのCollapse
	//=================================
		
	$(".divCollapse").on("click", function(){
		var id = $(this).data("toggle-tag");
		
		if ($("#" + id).css("display") == 'none') {
			// 非表示の場合
			$(this).find(".collapseIcon").removeClass("fa-chevron-down");
			$(this).find(".collapseIcon").addClass("fa-chevron-up");
		} else {
			// 表示の場合
			$(this).find(".collapseIcon").removeClass("fa-chevron-up");
			$(this).find(".collapseIcon").addClass("fa-chevron-down");
		}
		
		$("#" + id).toggle('');
		
	});
	
	// =============================================
	// ファイルアップロード：ファイル選択開くボタン押下時
    // =============================================
	$('.custom-file-input').on('change',function(){
		// ラベルに名前を表示
		if ($(this)[0].files[0].name != undefined) {
			$(this).next('.custom-file-label').html($(this)[0].files[0].name);
		}
	});
	
});

/**
 * 二重サブミット回避
 *
 * @returns
 */
function execSubmit(id) {

	// 更新ボタンを非活性にする
	$("#" + id).prop("disabled", true);

	$("#submitFormId").submit();
}

/**
 * 二重サブミット回避(検索)
 *
 * @returns
 */
function execSubmitSearch(id) {

	// 更新ボタンを非活性にする
	$("#" + id).prop("disabled", true);

	$("#submitFormIdForSearch").submit();
}

/**
 * 必須チェック共通処理
 *
 * @param formId
 * @returns
 */
function requiredCheck(formId) {

	// 必須チェック処理
	// var messageList = new Array();
	var isError = false;

	//背景色ホワイト化
	$(".chRequired").css("background-color","");
	$(".chNumber").css("background-color","");
	$(".chDouble").css("background-color","");

	// 取得したFormの要素内でClassに「chRequired」を指定されているものの要素を
	// 全て取得する

	$(formId).find(".chRequired").each(function() {

		var errorId = "#" + $(this).attr("name") + "Error";
		$(errorId).empty();

		// ①セレクトボックスの場合
		if ($(this).is("SELECT")) {

			// Valueを見る
			if ($(this).val() == null || $(this).val() == "" || $(this).val() < 1) {
				var itemName = $(this).attr("data-itemName");
				// messageList.push("<h5>" + itemName + "を選択して下さい。</h5>");
				$(this).css("background-color", VALID_ERROR_BACK_COLOR);

				isError = true;
			}
		} else if ($(this).is("div")) {

			// ②チェックボックスの場合
			var checkCount = -1;
			$(this).find("input:checkbox").each(function() {
				checkCount = 0;

				// チェック件数のチェック
				if($("input:checkbox:checked").length != 0) {
					checkCount ++;
				}
			});

			if (checkCount == 0) {
				var itemName = $(this).attr("data-itemName");
				// messageList.push("<h5>" + itemName + "のチェックを入れてください。</h5>");
				$(this).css("background-color", VALID_ERROR_BACK_COLOR);
				isError = true;

				// 次の繰り返し
				return true;
			}

			// ③ラジオの場合
			var radioCount = -1;
			$(this).find("input:radio").each(function() {
				radioCount = 0;

				// チェック件数のチェック
				if($("input:radio:checked").length != 0) {
					radioCount ++;
				}
			});

			if (radioCount == 0) {
				var itemName = $(this).attr("data-itemName");
				// messageList.push("<h5>" + itemName + "のチェックを入れてください。</h5>");
				$(this).css("background-color", VALID_ERROR_BACK_COLOR);

				isError = true;
			}

		} else {

			// ④テキストボックス、日付テキストボックス
			if ($(this).is("input")) {

				if ($(this).val() == null || $(this).val() == "") {
					var itemName = $(this).attr("data-itemName");
					// messageList.push("<h5>" + itemName + "を入力して下さい。</h5>");
					$(this).css("background-color", VALID_ERROR_BACK_COLOR);

					isError = true;

				} else if (!$(this).val().match(/\S/g)) {
					// スペースのみ入力されていた場合
					var itemName = $(this).attr("data-itemName");
					// messageList.push("<h5>" + itemName + "に空白文字のみが入力されています。正しく入力して下さい。</h5>");
					$(this).css("background-color", VALID_ERROR_BACK_COLOR);

					isError = true;
				}

				// 次の繰り返し
				return true;

			} else if($(this).is("textarea")) {

				// ⑤テキストエリア
				if($(this).val() == null || $(this).val() == "") {
					var itemName = $(this).attr("data-itemName");
					// messageList.push("<h5>" + itemName + "を入力して下さい。</h5>");
					$(this).css("background-color", VALID_ERROR_BACK_COLOR);

					isError = true;
				}
				// 次の繰り返し
				return true;
			}
		}
	});
	
	// =====================================================
	// 在留カード番号バリデーションチェック
	// =====================================================
	$(formId).find("input.ckNumOfDigitResidenceCard").each(function() {

		var regCardOfDigit = $("input.ckNumOfDigitResidenceCard").val().length;

		// 12桁でない、0桁でないときはエラー
		if(regCardOfDigit != 12 && regCardOfDigit != 0) {

			var itemName = $("input.ckNumOfDigitResidenceCard").attr("data-itemName");
			// messageList.push("<h5>" + itemName + "は12桁で入力して下さい。</h5>");
			$("input.ckNumOfDigitResidenceCard").css("background-color", VALID_ERROR_BACK_COLOR);

			isError = true;
		}

	});

	// =====================================================
	// 数値用バリデーションチェック
	// =====================================================
	$(formId).find(".chNumber").each(function() {

		if ($(this).is("input")) {

			if($(this).val().toString().includes(',')){
				// 3桁区切りカンマがあれば外す
				var numInt = Number($(this).val().replace(/,/g, ''));

			} else {
				var numInt = $(this).val();
			}

			if (numInt == null || numInt == "" || isNaN(numInt))  {
				var itemName = $(this).attr("data-itemName");
				// messageList.push("<h5>" + itemName + "を半角数字で入力して下さい。</h5>");
				$(this).css("background-color", VALID_ERROR_BACK_COLOR);

				isError = true;
			}
		}

	});

	// =====================================================
	// double用バリデーションチェック
	// =====================================================
	$(formId).find(".chDouble").each(function() {

		if ($(this).is("input")) {

			// ★idにdoubleの桁数を付与しておくこと
			var doubleDigit =  $(this).attr('id');
			var digitArray = doubleDigit.toString().split('.');

			// 整数値4桁まで
			var integerDigit = digitArray[0];
			// 小数点以下2桁まで
			var decimalPointDigit = digitArray[1];

			if ($(this).val() == null || $(this).val() == "" || isNaN($(this).val())) {
				// null、数値以外入力チェック
				var itemName = $(this).attr("data-itemName");
				// messageList.push("<h5>" + itemName + "は整数値は" + integerDigit + "桁まで、小数点以下は" + decimalPointDigit + "桁までで入力して下さい。</h5>");
				$(this).css("background-color", VALID_ERROR_BACK_COLOR);

				isError = true;

			} else if($(this).val().toString().includes('.')) {
				// 整数値、小数点以下桁数チェック
				var array = $(this).val().toString().split('.');

				if(array[0].length > integerDigit || array[1].length > decimalPointDigit) {
					var itemName = $(this).attr("data-itemName");
					// messageList.push("<h5>" + itemName + "は整数値は" + integerDigit + "桁まで、小数点以下は" + decimalPointDigit + "桁までで入力して下さい。</h5>");
					$(this).css("background-color", VALID_ERROR_BACK_COLOR);

					isError = true;
				}

			} else if($(this).val().toString().length > integerDigit) {
				// 整数のみで4桁以上の場合
				var itemName = $(this).attr("data-itemName");
				// messageList.push("<h5>" + itemName + "は整数値は" + integerDigit + "桁まで、小数点以下は" + decimalPointDigit + "桁までで入力して下さい。</h5>");
				$(this).css("background-color", VALID_ERROR_BACK_COLOR);

				isError = true;

			}
		}

	});

	// メッセージをダイアログに追加
	//messageList.forEach(message => {
	//	addErrorMessage(message);
	// });

	return isError;
}

/**
 * カラーコードコピー
 *
 * @param コピー元要素
 * @param コピー先要素
 */
function colorCodeCopyToContainer(colorElement, targetElement) {
	const colorInput = targetElement.querySelector("input[type='color']");
	colorInput.value= colorElement.value;
}
