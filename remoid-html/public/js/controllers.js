var remoidControllers = angular.module('remoidControllers', []);

remoidControllers.controller('mainController', [ '$interval', '$cookies', '$scope', '$routeParams', '$http', function($interval, $cookies, $scope, $routeParams, $http) {
	$scope.name = null;
	
	var stompClient = null;

	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	$scope.connect = function() {
		var socket = new SockJS('/update');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			setConnected(true);
			console.log('Connected: ' + frame);
//			stompClient.subscribe('/topic/greetings', function(greeting) {
//				showGreeting(JSON.parse(greeting.body).content);
//			});
		});
	}

	function disconnect() {
		stompClient.disconnect();
		setConnected(false);
		console.log("Disconnected");
	}

	$scope.sendName = function() {
		stompClient.send("/app/update", {}, JSON.stringify({
			'name' : $scope.name
		}));
	}

	function showGreeting(message) {
		var response = document.getElementById('response');
		var p = document.createElement('p');
		p.style.wordWrap = 'break-word';
		p.appendChild(document.createTextNode(message));
		response.appendChild(p);
	}
} ]);