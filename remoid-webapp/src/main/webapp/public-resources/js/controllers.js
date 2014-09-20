var remoidControllers = angular.module('remoidControllers', []);

remoidControllers.controller('mainController', [ '$interval', '$cookies', '$scope', '$routeParams', '$http', function($interval, $cookies, $scope, $routeParams, $http) {
	$scope.name = null;

	$scope.socket = null;
	$scope.phone = {};

	$scope.initPhone = function() {
		$scope.connect();
	}

	$scope.moveUpdate = function(x, y) {
		$scope.socket.send(JSON.stringify({
			'op' : 2,
			'x' : x,
			'y' : y
		}));
	}

	$scope.phoneMouseDown = function() {
		$scope.mousedown = true;

		$scope.socket.send(JSON.stringify({
			'op' : 0
		}));
	}

	$scope.phoneMouseUp = function() {
		$scope.mousedown = false;
		
		$scope.socket.send(JSON.stringify({
			'op' : 1
		}));
	}

	$scope.phoneMouseMove = function(phoneId, e) {
		var parentOffset = $("#" + phoneId).parent().offset();
		// or $(this).offset(); if you really just want
		// the current
		// element's offset
		$scope.phone = {
			x : e.pageX - parentOffset.left,
			y : e.pageY - parentOffset.top
		}

		if ($scope.mousedown != undefined) {
			$scope.moveUpdate($scope.phone.x * $scope.phoneXMultiplier, $scope.phone.y * $scope.phoneYMultiplier);
		}
	}

	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	$scope.connect = function() {
		$scope.socket = new WebSocket("ws://" + window.location.host + '/remoid/update');
		$scope.socket.onopen = function(event) {
			$scope.socket.send(JSON.stringify({
				'op' : 4
			}));
		}

		$scope.socket.onmessage = function(event) {
			var data = JSON.parse(event.data);
			if (data.op == 3) {
				if (data.phoneDimensions.length == 0) {
					$('.phone').css("width", 0);
					$('.phone').css("height", 0);

					return;
				}

				var phoneX = data.phoneDimensions[0].x;
				var phoneY = data.phoneDimensions[0].y;

				var xOverY = phoneX / phoneY;

				var widthContainer = phoneY / 3 * xOverY;
				var heightContainer = phoneY / 3;

				$scope.phoneXMultiplier = phoneX / widthContainer;
				$scope.phoneYMultiplier = phoneY / heightContainer;

				$('.phone').css("width", widthContainer);
				$('.phone').css("height", heightContainer);
			}
		}
	}

	function disconnect() {
		console.log("Disconnected");
	}

	function showGreeting(message) {
		var response = document.getElementById('response');
		var p = document.createElement('p');
		p.style.wordWrap = 'break-word';
		p.appendChild(document.createTextNode(message));
		response.appendChild(p);
	}
} ]);