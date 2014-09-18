var remoidServices = angular.module('remoidServices', [
    'ngResource'
]);

var host = getHost();

function getHost() {
    return window.location.protocol + '//' + window.location.host + '/miw-remoid';
}
