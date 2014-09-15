var remoidControllers = angular.module('remoidControllers', []);

remoidControllers.controller('mainController', [ '$interval', '$cookies', '$scope', '$routeParams', '$http', function($interval, $cookies, $scope, $routeParams, $http) {
	$scope.name = null;

	$scope.stompClient = null;
	$scope.socket = null;
	$scope.stomp = Stomp;
	$scope.phone = {};
	
	$scope.moveUpdate = function(x, y) {
		$scope.stompClient.send("/app/update", {}, JSON.stringify({
			'op' : 2,
			'x' : x,
			'y' : y
		}));
	}

	$scope.phoneMouseDown = function() {
		$scope.mousedown = true;
		
		$scope.stompClient.send("/app/update", {}, JSON.stringify({
			'op' : 0
		}));
	}

	$scope.touchUpUpdate = function() {
		$scope.stompClient.send("/app/update", {}, JSON.stringify({
			'op' : 1
		}));
	}

	$scope.phoneMouseMove = function(phoneId, e) {
		var parentOffset = $("#" + phoneId).parent().offset();
		// or $(this).offset(); if you really just want the current
		// element's offset
		$scope.phone = {
			x : e.pageX - parentOffset.left,
			y : e.pageY - parentOffset.top
		} 
		
		if ($scope.mousedown) {
			$scope.moveUpdate($scope.phone.x, $scope.phone.y);
		}
	}
	
	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	$scope.connected = function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
//		stompClient.subscribe('/topic/greetings', function(greeting) {
//			showGreeting(JSON.parse(greeting.body).content);
//		});
	};

	$scope.connect = function() {
		$scope.socket = new SockJS('/update');
		$scope.stompClient = $scope.stomp.over($scope.socket);
		$scope.stompClient.connect();
	}

	function disconnect() {
		stompClient.disconnect();
		setConnected(false);
		console.log("Disconnected");
	}

	$scope.sendName = function() {
		$scope.stompClient.send("/app/update", {}, JSON.stringify({
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