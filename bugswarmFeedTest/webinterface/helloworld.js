function hello() {
 document.getElementById('hello').innerHTML = 'Hello World';
}
function start() {
 document.getElementById('click').onclick = hello;
}
window.onload = start;
