
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

function getSelectedValue(){
	var choice = document.getElementById('choice').value;
	if(choice=="temp"){
		temp=true;
		humid=false;
		temp_visual = new google.visualization.LineChart(document.getElementById('visualization'));
	}
	if(choice=="humid"){
		humid = true;
		temp=false;
		humid_visual = new google.visualization.LineChart(document.getElementById('visualization'));
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

	var currentTime = new Date();
	var hours = currentTime.getHours();
	var minutes = currentTime.getMinutes();
	var seconds = currentTime.getSeconds();
	if (minutes < 10)
		minutes = "0" + minutes;
	if (seconds < 10)
		seconds = "0" + seconds;
	var time = hours + ":" + minutes + ":" + seconds;

	data = new google.visualization.DataTable();
	data.addColumn('string', 'Time');
	data.addColumn('number', 'panda');
	data.addColumn('number', 'rmb');
	data.addColumn('number', 'ronano');
	data.addRow([time,75, 75, 75]);

	hdata = new google.visualization.DataTable();
	hdata.addColumn('string', 'Time');
	hdata.addColumn('number', 'panda');
	hdata.addColumn('number', 'rmb');
	hdata.addColumn('number', 'ronano');
	hdata.addRow([time, 30, 30, 30]);

	humid_visual = new google.visualization.LineChart(document.getElementById('visualization'));
	temp_visual = new google.visualization.LineChart(document.getElementById('visualization'));


	if(temp){
		temp_visual.draw(data, {
			width: 400, height: 288, title: "Temperature",
			vAxis: {maxValue: 10}});}


	if(humid){
		humid_visual.draw(hdata, {
			width: 400, height: 288, title: "Humidity",
			vAxis: {maxValue: 10}});}

	t = setTimeout(update, 5000);
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

	var panda = 0;
	var rmb = 0;
	var ronano = 0;

	var hpanda = 0;
	var hrmb = 0;
	var hronano = 0;

	if(req.status == 200)
	{
		var jsonweather = eval('(' + req.responseText + ')');

		for(var i=0;i<jsonweather.length;i++){
			if (jsonweather[i].resource.id == "00:50:c2:69:c8:29")
			{
				rmb = parseInt(jsonweather[i].payload.my_test_feed.currTemp);
				hrmb = parseInt(jsonweather[i].payload.my_test_feed.currHumid);
			}
			if (jsonweather[i].resource.id == "00:50:c2:69:c8:2a")
			{
				panda = parseInt(jsonweather[i].payload.my_test_feed.currTemp);
				hpanda = parseInt(jsonweather[i].payload.my_test_feed.currHumid);

			}
			if (jsonweather[i].resource.id == "00:50:c2:69:c8:08")
			{
				ronano = parseInt(jsonweather[i].payload.my_test_feed.currTemp); 
				hronano = parseInt(jsonweather[i].payload.my_test_feed.currHumid); 
			}	
		}



	}
	else alert("badpanda");

	console.log (panda + " " + rmb + " " + ronano);
	console.log (hpanda + " " + hrmb + " " + hronano);

	data.addRow([time, panda, rmb, ronano]);
	hdata.addRow([time, hpanda, hrmb, hronano]);
	if(temp){
		temp_visual.draw(data, {
			width: 400, height: 288, title: "Temperature",
			vAxis: {maxValue: 10}});}
	if(humid){
		humid_visual.draw(hdata, {
			width: 400, height: 288, title: "Humidity",
			vAxis: {maxValue: 10}});}

	t = setTimeout(update, 5000);
}

