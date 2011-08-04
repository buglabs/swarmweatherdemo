function start() {
var req = new XMLHttpRequest();
req.open('GET', '/panda', false);
req.send(null);
if(req.status == 200)
{
	var jsonweather = eval('(' + req.responseText + ')');

	for(var i=0;i<jsonweather.length;i++){
	
	document.getElementById(jsonweather[i].resource.id + "light").innerHTML = 
		jsonweather[i].payload.my_test_feed.currLight;

	document.getElementById(jsonweather[i].resource.id + "temp").innerHTML = 
		jsonweather[i].payload.my_test_feed.currTemp;
	if (parseInt(jsonweather[i].payload.my_test_feed.currTemp) >= 83)
		document. getElementById(jsonweather[i].resource.id + "temp") .style. color = "red";
	if (parseInt(jsonweather[i].payload.my_test_feed.currTemp) < 83)
		document. getElementById(jsonweather[i].resource.id + "temp") .style. color = "black";

	document.getElementById(jsonweather[i].resource.id + "winddir").innerHTML = 
		jsonweather[i].payload.my_test_feed.currWinddir;

	document.getElementById(jsonweather[i].resource.id + "windspd").innerHTML = 
		jsonweather[i].payload.my_test_feed.currWindspd;

	document.getElementById(jsonweather[i].resource.id + "dew").innerHTML = 
		jsonweather[i].payload.my_test_feed.currDew;

	document.getElementById(jsonweather[i].resource.id + "rain").innerHTML = 
		jsonweather[i].payload.my_test_feed.currRain;

	document.getElementById(jsonweather[i].resource.id + "pressure").innerHTML = 
		jsonweather[i].payload.my_test_feed.currBPressure;

	document.getElementById(jsonweather[i].resource.id + "humidity").innerHTML = 
		jsonweather[i].payload.my_test_feed.currHumid;
	}
}
else alert(req.status);

t=setTimeout("start()",5000);
}
window.onload = start;




