var ws = null;
var url = "ws://localhost:8080/echo";
function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('echo').disabled = !connected;
}
function connect() {
    ws = new WebSocket(url);
    ws.onopen = function () {
        setConnected(true);
    };
    ws.onmessage = function (event) {
        log(event.data);
    };
    ws.onclose = function (event) {
        setConnected(false);
        log('提示: 关闭连接.');
    };
}
function disconnect() {
    if (ws != null) {
        ws.close();
        ws = null;
    }
    setConnected(false);
}
function echo() {
    if (ws != null) {
        var message = document.getElementById('message').value;
        log('发送: ' + message);
        ws.send(message);
    } else {
        alert('尚未建立连接，请单击“连接”按钮建立连接！');
    }
}
function log(message) {
    var console = document.getElementById('logging');
    var p = document.createElement('p');
    p.appendChild(document.createTextNode(message));
    console.appendChild(p);
    while (console.childNodes.length > 12) {
        console.removeChild(console.firstChild);
    }
    console.scrollTop = console.scrollHeight;
}
