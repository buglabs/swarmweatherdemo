function hello() {

 	var xmlHttp;
if (window.XMLHttpRequest) {
 xmlHttp = new XMLHttpRequest();
} else if (window.ActiveXObject) {
 try {
  xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
 } catch(e) {
  try {
   xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
  } catch {
   throw(e);
  }
 }
}
	xmlHttp.open('GET', '/panda', false);
	xmlHttp.send(null);



alert(xmlHttp.responseText);

  
  
 document.getElementById('hello').innerHTML = 'Hello World';
 
}
function start() {
 document.getElementById('click').onclick = hello;
}
window.onload = start;
