$(function(){
	
	// 画面ロード時、チャット生成
	var categoryLst = $("#categoryLst").val().replace("[","").replace("]","").split(",");
	var pointLst = $("#pointLst").val().replace("[","").replace("]","").split(",");
	// var myCanvas = "<canvas id='myCanvas' width='" + screen.availWidth + "px' height='"+ screen.availHeight + "px'></canvas>";
	var myCanvas = "<canvas id='myCanvas' width='100%' height='100px'></canvas>";
	var chartElement = $("#point-chart");
	chartElement.append(myCanvas);
	
	// チャットのデータ設定
	const data = {
				  labels:categoryLst,
				  datasets: [{
				    label: '軸別ポイント図',
				    data: pointLst,
				    fill: true,
				    backgroundColor: 'rgba(255, 99, 132, 0.2)',
				    borderColor: 'rgb(255, 99, 132)',
				    pointBackgroundColor: 'rgb(255, 99, 132)',
				    pointBorderColor: '#fff',
				    pointHoverBackgroundColor: '#fff',
				    pointHoverBorderColor: 'rgb(255, 99, 132)'
				  }]
				};
				
	// radarタイプチャットのオプション設定
	const config = {
				  type: 'radar',
				  data: data,
				  options: {
				    elements: {
				      line: {
				        borderWidth: 3
				      }
				    }
				  },
				};
				
	// チャット生成
	new Chart($("#myCanvas"), config);
})
