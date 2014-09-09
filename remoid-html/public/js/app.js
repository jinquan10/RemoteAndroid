var remoidApp = angular.module('remoidApp', [
        'ngRoute', 'remoidControllers', 'remoidServices', 'ngCookies'
]);

remoidApp.config([
        '$routeProvider', function($routeProvider) {
            $routeProvider.when('/main', {
                templateUrl : 'partials/main.html',
                controller : 'mainController'
            }).otherwise({
                redirectTo : '/main'
            });
        }
]);