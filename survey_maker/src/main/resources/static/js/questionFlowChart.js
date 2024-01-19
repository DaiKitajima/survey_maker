$(function(){
	// フローチャートデータ
	var defaultFlowchartData = eval('(' + $("#flowchartData").val() + ')') ;
	
	var $flowchart = $('#flowchartworkspace');
	var $container = $flowchart.parent();
	
    var cx = $flowchart.width() / 2;
    var cy = $flowchart.height() / 2;
    
    // Panzoom initialization...
    $flowchart.panzoom();
    
    // Centering panzoom
    $flowchart.panzoom('pan', -cx + $container.width() / 2, -cy + $container.height() / 2);

    // Panzoom zoom handling...
    var possibleZooms = [0.5, 0.75, 1, 2, 3];
    var currentZoom = 1;
    // 拡大と縮小
/*    $container.on('mousewheel.focal', function( e ) {
        e.preventDefault();
        var delta = (e.delta || e.originalEvent.wheelDelta) || e.originalEvent.detail;
        var zoomOut = delta ? delta < 0 : e.originalEvent.deltaY > 0;
        currentZoom = Math.max(0, Math.min(possibleZooms.length - 1, (currentZoom + (zoomOut * 2 - 1))));
        $flowchart.flowchart('setPositionRatio', possibleZooms[currentZoom]);
        $flowchart.panzoom('zoom', possibleZooms[currentZoom], {
            animate: true,
            focal: e
        });
    });*/

	// Apply the plugin on a standard, empty div...
	$flowchart.flowchart({
		data: defaultFlowchartData,
		defaultSelectedLinkColor: '#ff0000',
		grid: 5,
		multipleLinksOnInput: true,
		multipleLinksOnOutput: true,
		linkWidth: 3
	});

/*	function getOperatorData($element) {
		var nbInputs = parseInt($element.data('nb-inputs'), 10);
		var nbOutputs = parseInt($element.data('nb-outputs'), 10);
		var data = {
			properties: {
				title: $element.text(),
				inputs: {},
				outputs: {}
			}
		};

		var i = 0;
		for (i = 0; i < nbInputs; i++) {
			data.properties.inputs['input_' + i] = {
				label: 'Input ' + (i + 1)
			};
		}
		for (i = 0; i < nbOutputs; i++) {
			data.properties.outputs['output_' + i] = {
				label: 'Output ' + (i + 1)
			};
		}

		return data;
	}*/



	//-----------------------------------------
	//--- operator and link properties
	//--- start
/*	var $operatorProperties = $('#operator_properties');
	$operatorProperties.hide();
	var $linkProperties = $('#link_properties');
	$linkProperties.hide();
	var $operatorTitle = $('#operator_title');
	var $linkColor = $('#link_color');*/

	$flowchart.flowchart({
		onOperatorSelect: function(operatorId) {
			$("#selectedOperatorId").val(operatorId);
			return true;
		},
		onOperatorUnselect: function() {
			$("#selectedOperatorId").val("");
			return true;
		},
		onLinkSelect: function(linkId) {
			$("#selectedLinkId").val(linkId);
			return true;
		},
		onLinkUnselect: function() {
			$("#selectedLinkId").val("");
			return true;
		},
		onLinkCreate: function(linkId,linkData){
			if(linkId.toString().indexOf("link_") > -1 ) return true;
			linkData["contentId"] = $("#contentId").val();
			$.ajax({
			  async: true,
			  url: "/internalApi/questionLinkCreate",
			  type: 'POST',
			  dataType: 'text',
			  data: JSON.stringify(linkData),
			  timeout: 30000,
			  contentType: 'application/json;charset=utf-8',
			  success: successCallback,
			  error: errorCallback,
			  complete: completeCallback
			})
			function successCallback(newLinkId) {
				if(newLinkId == -1){
					alert('既存のリンクを削除してから、再度リンクを新規作成してください。※１回答は１質問に繋げます。');
					$("[data-link_id=" + linkId + "]").parent().remove();
				}else if(newLinkId == null || newLinkId == ""){
					alert('リンク設定が失敗しました。');
					$("[data-link_id=" + linkId + "]").parent().remove();
				}else if(parseInt(newLinkId)){
					$("g.flowchart-link").each(function(){
						var dataLinkId = $(this).attr("data-link_id");
						if(dataLinkId == linkId){
							// 画面上ID変換
							var createId = "link_" + newLinkId;
							$(this).attr("data-link_id",createId);
							// 内部システム上に新規データのID追加
							delete linkData.contentId;
							$flowchart.flowchart('createLink', createId, linkData);
						}
					});
				}
				
				// 自動生成されたID情報削除
				var data = $flowchart.flowchart('getData');
				delete data.links[linkId];
				$flowchart.flowchart('setData', data);
			}
			function errorCallback(xhr, status){
			    console.log('error: ' + status);
			    alert('エラーが発生しました。');
			}
			function completeCallback(xhr, status){
			    console.log('questionLinkCreate Complete!');
			}
			return true;
		} 
	});

/*	$operatorTitle.keyup(function() {
		var selectedOperatorId = $flowchart.flowchart('getSelectedOperatorId');
		if (selectedOperatorId != null) {
			$flowchart.flowchart('setOperatorTitle', selectedOperatorId, $operatorTitle.val());
		}
	});

	$linkColor.change(function() {
		var selectedLinkId = $flowchart.flowchart('getSelectedLinkId');
		if (selectedLinkId != null) {
			$flowchart.flowchart('setLinkMainColor', selectedLinkId, $linkColor.val());
		}
	});*/
	//--- end
	//--- operator and link properties
	//-----------------------------------------

	//-----------------------------------------
	//--- delete operator / link button
	//--- start
	$('#questionDelBtn').click(function(e) {
		e.preventDefault();
		var url = $(this).prop("href");
		var operator = $("#selectedOperatorId").val();
		var link = $("#selectedLinkId").val();
		if((operator == "" && link == "") || operator == "result"){
			alert("質問または質問リンクを選択してください。");
		}else{
			$flowchart.flowchart('deleteSelected');
			var param = "&questionId=" + operator.replace("operator","") + "&linkId=" + link.replace("link_","");
			$.get( url + param );
		}
	});
	/*$flowchart.parent().siblings('.delete_selected_button').click(function() {
		$flowchart.flowchart('deleteSelected');
	});*/
	//--- end
	//--- delete operator / link button
	//-----------------------------------------



	//-----------------------------------------
	//--- create operator button
	//--- start
/*	var operatorI = 0;
	$flowchart.parent().siblings('.create_operator').click(function() {
		var operatorId = 'created_operator_' + operatorI;
		var operatorData = {
			top: ($flowchart.height() / 2) - 30,
			left: ($flowchart.width() / 2) - 100 + (operatorI * 10),
			properties: {
				title: 'Operator ' + (operatorI + 3),
				inputs: {
					input_1: {
						label: 'Input 1',
					}
				},
				outputs: {
					output_1: {
						label: 'Output 1',
					}
				}
			}
		};

		operatorI++;

		$flowchart.flowchart('createOperator', operatorId, operatorData);

	});*/
	//--- end
	//--- create operator button
	//-----------------------------------------




	//-----------------------------------------
	//--- draggable operators
	//--- start
	//var operatorId = 0;
	var $draggableOperators = $('.draggable_operator');
	$draggableOperators.draggable({
		cursor: "move",
		opacity: 0.7,

		// helper: 'clone',
		appendTo: 'body',
		zIndex: 1000,

		helper: function(e) {
			var $this = $(this);
			var data = getOperatorData($this);
			return $flowchart.flowchart('getOperatorElement', data);
		},
		stop: function(e, ui) {
			var $this = $(this);
			var elOffset = ui.offset;
			var containerOffset = $container.offset();
			if (elOffset.left > containerOffset.left &&
				elOffset.top > containerOffset.top &&
				elOffset.left < containerOffset.left + $container.width() &&
				elOffset.top < containerOffset.top + $container.height()) {

				var flowchartOffset = $flowchart.offset();

				var relativeLeft = elOffset.left - flowchartOffset.left;
				var relativeTop = elOffset.top - flowchartOffset.top;

				var positionRatio = $flowchart.flowchart('getPositionRatio');
				relativeLeft /= positionRatio;
				relativeTop /= positionRatio;

				var data = getOperatorData($this);
				data.left = relativeLeft;
				data.top = relativeTop;

				$flowchart.flowchart('addOperator', data);
			}
		}
	});
	//--- end
	//--- draggable operators
	//-----------------------------------------


	//-----------------------------------------
	//--- save and load
	//--- start
/*	function Flow2Text() {
		var data = $flowchart.flowchart('getData');
		$('#flowchart_data').val(JSON.stringify(data, null, 2));
	}
	$('#get_data').click(Flow2Text);

	function Text2Flow() {
		var data = JSON.parse($('#flowchart_data').val());
		$flowchart.flowchart('setData', data);
	}
	$('#set_data').click(Text2Flow);*/

/*	// global localStorage
	function SaveToLocalStorage() {
		if (typeof localStorage !== 'object') {
			alert('local storage not available');
			return;
		}
		Flow2Text();
		localStorage.setItem("stgLocalFlowChart", $('#flowchart_data').val());
	}
	$('#save_local').click(SaveToLocalStorage);

	function LoadFromLocalStorage() {
		if (typeof localStorage !== 'object') {
			alert('local storage not available');
			return;
		}
		var s = localStorage.getItem("stgLocalFlowChart");
		if (s != null) {
			$('#flowchart_data').val(s);
			Text2Flow();
		}
		else {
			alert('local storage empty');
		}
	}
	$('#load_local').click(LoadFromLocalStorage);*/
	//--- end
	//--- save and load
	//-----------------------------------------
	
	// 位置保存
	$('#positionSaveBtn').click(function(e) {
		e.preventDefault();
		var url = $(this).prop("href");
		var data = $flowchart.flowchart('getData');
		
		$.ajax({
		  async: true,
		  url: url,
		  type: 'POST',
		  dataType: 'text',
		  data: JSON.stringify(data.operators, null, 2),
		  timeout: 30000,
		  contentType: 'application/json;charset=utf-8',
		  success: successCallback,
		  error: errorCallback,
		  complete: completeCallback
		})
		function successCallback(successFlg) {
			if(successFlg == "true"){
				alert('位置保存が完了しました。');
			}else{
				alert('位置保存が失敗しました。');
			}
		}
		function errorCallback(xhr, status){
		    console.log('error: ' + status);
		    alert('エラーが発生しました。');
		}
		function completeCallback(xhr, status){
		    console.log('question position save Complete!');
		}
	});
});