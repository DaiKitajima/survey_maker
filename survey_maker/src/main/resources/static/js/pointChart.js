$(function(){
	
	// 画面ロード時、チャット生成
	var categoryLst = $("#categoryLst").val().replace("[","").replace("]","").split(",");
	var pointLst = $("#pointLst").val().replace("[","").replace("]","").split(",");
	var maxPoint = ($("#maxPoint").val() == "" ? 100:$("#maxPoint").val());
	var stepSize = ($("#stepSize").val() == "" ? 10:$("#stepSize").val());
	// var myCanvas = "<canvas id='myCanvas' width='" + screen.availWidth + "px' height='"+ screen.availHeight + "px'></canvas>";
	var myCanvas = "<canvas id='myCanvas' width='100%' height='100%'></canvas>";
	var chartElement = $("#point-chart");
	chartElement.append(myCanvas);
	
	// チャットのデータ設定
	const data = {
				  labels:categoryLst,
				  datasets: [{
				    label: '',
				    data: pointLst,
				    fill: true,
				    borderColor: 'rgb(255, 99, 132)',
				    backgroundColor: 'rgba(255, 99, 132, 0.2)',
/*				    pointBackgroundColor: 'rgb(255, 99, 132)',
				    pointBorderColor: '#fff',
				    pointHoverBackgroundColor: '#fff',
				    pointHoverBorderColor: 'rgb(255, 99, 132)'*/
				  }]
				};
				
	// radarタイプチャットのオプション設定
	const config = {
				  type: 'radar',
				  data: data,
				  options: {
					plugins: {
						legend: {
								display: false,
							},
						},
			        scales: {
						  r: {
							beginAtZero: true,
						    //グラフの最小値・最大値
						    min: 0,
						    max: parseInt(maxPoint),
						    
						    //背景色
						    backgroundColor: 'snow',
						    //グリッドライン
						    grid: {
						      color: 'pink',
						    },
						    //アングルライン
						    angleLines: {
						      color: 'green',
						    },
						    //各軸のラベル
						    pointLabels: {
						      color: '#212529',
						    },
						    //ポイントのラベル
						    ticks: {
								display: false,
								showLabelBackdrop: false,
								stepSize: stepSize,
							},
						  },
					},
				  },
				};
				
	// チャット生成(ポイント複数軸（ポイント型）の場合のみ)
	if($("#patternId").val() == "2"){
		new Chart($("#myCanvas"), config);
	}
	
})
