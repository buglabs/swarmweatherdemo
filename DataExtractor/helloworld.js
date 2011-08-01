function start() {
var req = new XMLHttpRequest();
req.open('GET', '/panda', false);
req.send(null);
if(req.status == 200)
{
	

	var jsonweather = eval('(' + req.responseText + ')');

	for(var i=0;i<jsonweather.length;i++){
	
	document.getElementById(jsonweather[i].resource.id).innerHTML = 
		"Time: " + jsonweather[i].payload.my_test_feed.currTime + "\n" + 
		"Temperature: " + jsonweather[i].payload.my_test_feed.currTemp + "\n" +
		"Wind Direction: " + jsonweather[i].payload.my_test_feed.currWinddir + "\n" +
		"Wind Speed: " + jsonweather[i].payload.my_test_feed.currWindspd + "\n" +
		"Dewpoint: " + jsonweather[i].payload.my_test_feed.currDew + "\n" +
		"Rainfall: " + jsonweather[i].payload.my_test_feed.currRain + "\n" +
		"Barometric Pressure: " + jsonweather[i].payload.my_test_feed.currBPressure + "\n" +
		"Battery Level: " + jsonweather[i].payload.my_test_feed.currBatt + "\n" +
		"Humidity: " + jsonweather[i].payload.my_test_feed.currHumid + "\n";
}

}
t=setTimeout("start()",5000);
}
window.onload = start;




