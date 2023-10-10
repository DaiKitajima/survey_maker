//bootstrap-datetimepickerの設定
$(function(){

	$('.datetimepicker').datepicker({
		language:'ja',
		format: 'yyyy/mm/dd',
		autoclose: true, // 入力後自動クローズ
		todayBtn: "linked", // 今日ボタン
		todayHighlight: true
		// clearBtn: false, 入力値クリアボタン
	});

});