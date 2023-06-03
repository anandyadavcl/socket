var stompClient = null;

function connect() {
    var socket = new SockJS('/logs');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/logs', function (greeting) {
            showGreeting(greeting.body);
        });
    });
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': ''}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$( window ).on( "load", connect );

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendName(); });
});