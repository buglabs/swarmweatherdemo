google.load('visualization', '1.0', {'packages':['corechart']});
google.setOnLoadCallback(drawVisualization);

var t;
var data;
var hdata;
var temp = new Boolean();
var humid = new Boolean();
temp = true;
humid = false;
var temp_visual;
var humid_visual;

var temperature;
var humidity;
var order;

function getSelectedValue(){
	var choice = document.getElementById('choice').value;
	if(choice=="temp"){
		temp=true;
		humid=false;
		isMap=false;
		temp_visual = new google.visualization.LineChart(document.getElementById('visualization'));
		drawChart();
	}
	if(choice=="humid"){
		humid = true;
		temp=false;
		isMap=false;
		humid_visual = new google.visualization.LineChart(document.getElementById('visualization'));
		drawChart();
	}
}

function setAndReset(box) {
	myOptions = new Array(document.FormName.choice);
	document.FormName.hiddenInput.value = box.value;
	for (i=0; i<myOptions.length; i++) {
		if (myOptions[i].name != box.name) {
			myOptions[i].selectedIndex = 0;
		}
	}
}


function drawVisualization() {

/*var latlng = new google.maps.LatLng(-34.397, 150.644);
    		var myOptions = {
      		zoom: 8,
      		center: latlng,
      		mapTypeId: google.maps.MapTypeId.ROADMAP
    		};
    		new google.maps.Map(document.getElementById("panda"),
        	myOptions);*/

	var currentTime = new Date();
	var hours = currentTime.getHours();
	var minutes = currentTime.getMinutes();
	var seconds = currentTime.getSeconds();
	if (minutes < 10)
		minutes = "0" + minutes;
	if (seconds < 10)
		seconds = "0" + seconds;
	var time = hours + ":" + minutes + ":" + seconds;

	var req = new XMLHttpRequest();
	req.open('GET', '/panda', false);
	req.send(null);

	data = new google.visualization.DataTable();
	hdata = new google.visualization.DataTable();
	data.addColumn('string', 'Time');
	hdata.addColumn('string', 'Time');

	order = [];	

	if(req.status == 200)
	{
		var jsonweather = eval('(' + req.responseText + ')');

		for(var i=0;i<jsonweather.length;i++){
			data.addColumn('number', jsonweather[i].resource.id);
			hdata.addColumn('number', jsonweather[i].resource.id);
			order.push(jsonweather[i].resource.id);
		}
	}

	humid_visual = new google.visualization.LineChart(document.getElementById('visualization'));
	temp_visual = new google.visualization.LineChart(document.getElementById('visualization'));

	t = setTimeout(update, 5000);
}

function drawChart() {
	if(temp){
		temp_visual.draw(data, {
			width: 425, height: 350, title: "Temperature",
			vAxis: {maxValue: 10}});}
	if(humid){
		humid_visual.draw(hdata, {
			width: 425, height: 350, title: "Humidity",
			vAxis: {maxValue: 10}});}
}

function update() {

	console.log("update is called yo");
	var currentTime = new Date();
	var hours = currentTime.getHours();
	var minutes = currentTime.getMinutes();
	var seconds = currentTime.getSeconds();
	if (minutes < 10)
		minutes = "0" + minutes;
	if (seconds < 10)
		seconds = "0" + seconds;
	var time = hours + ":" + minutes + ":" + seconds;

	var req = new XMLHttpRequest();
	req.open('GET', '/panda', false);
	req.send(null);

	var j = 0;

	if(req.status == 200)
	{
		var jsonweather = eval('(' + req.responseText + ')');
		
		var newdata = new Array(jsonweather.length + 1);
		var newhdata = new Array(jsonweather.length + 1);
		newdata[0] = time;
		newhdata[0] = time;

		for(var i=0;i<jsonweather.length;i++){
			
			j = order.indexOf(jsonweather[i].resource.id);

			newdata[j+1] = parseInt(jsonweather[i].payload.my_test_feed.currTemp);
			newhdata[j+1] = parseInt(jsonweather[i].payload.my_test_feed.currHumid);
		
		}

	}

	console.log(newdata);
	console.log(newhdata);
	data.addRow(newdata);
	hdata.addRow(newhdata);
	drawChart();

	t = setTimeout(update, 5000);
}
