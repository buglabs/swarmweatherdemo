function start() {
var req = new XMLHttpRequest();
req.open('GET', '/panda', false);
req.send(null);
if(req.status == 200)
{
	while (document.getElementById("data").hasChildNodes()) {
    		document.getElementById("data").removeChild(document.getElementById("data").lastChild);
	}

	var jsonweather = eval('(' + req.responseText + ')');

	for(var i=0;i<jsonweather.length;i++){

	var d = document.createElement("div");
	d.setAttribute("id", jsonweather[i].resource.id); 
	document.getElementById("data").appendChild(d);
	document.getElementById(jsonweather[i].resource.id).innerHTML = 
		"BUG with ID " + jsonweather[i].resource.id + " " +
		"Temperature: " + jsonweather[i].payload.my_test_feed.currTemp + " " +
		"Humidity: " + jsonweather[i].payload.my_test_feed.currHumid + " " +
		"Time: " + jsonweather[i].payload.my_test_feed.currTime;
	if (parseInt(jsonweather[i].payload.my_test_feed.currTemp) >= 83)
		document. getElementById(jsonweather[i].resource.id) .style. color = "red";
	if (parseInt(jsonweather[i].payload.my_test_feed.currTemp) < 83)
		document. getElementById(jsonweather[i].resource.id) .style. color = "black";
	}
}
else alert(req.status);

t=setTimeout("start()",5000);
}
window.onload = start;


