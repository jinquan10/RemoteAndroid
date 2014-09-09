var coAuthorServices = angular.module('coAuthorServices', [
    'ngResource'
]);

var host = getHost();

function getHost() {
    return window.location.protocol + '//' + window.location.host + '/nwm-coauthor-webapp';
}

coAuthorServices.factory('Schemas', [
        '$resource', function($resource) {
            return $resource(host + '/schemas/:type', {}, {
                getSchemaForCreate : {
                    method : 'GET',
                    params : {
                        type : 'new-story'
                    }
                },
                getSchemaForEntryRequest : {
                    method : 'GET',
                    params : {
                        type : 'request-entry'
                    }
                },
                getPraises : {
                    method : 'GET',
                    params : {
                        type : 'praises'
                    }
                }
            });
        }
]);

coAuthorServices.factory('Story', [
        '$resource', function($resource) {
            return $resource(host + '/stories/:type', {}, {
                create : {
                    method : 'POST'
                },
                getTopViewStories : {
                    method : 'GET',
                    params : {
                        type : 'top-view-stories'
                    },
                    isArray : true
                },
                getStory : {
                    method : 'GET'
                }
            });
        }
]);

coAuthorServices.factory('StoryOperation', [
        '$resource', function($resource) {
            return $resource(host + '/stories/:id/:op', {}, {
                requestEntry : {
                    method : 'POST',
                    params : {
                        op : 'entries'
                    }
                },
                incrementViews : {
                    method : 'POST',
                    params : {
                        op : 'increment-views'
                    }
                },
                pickEntry : {
                    method : 'POST',
                    params : {
                        op : 'pick-entry'
                    }
                }
            });
        }
]);

coAuthorServices.factory('PraisesOperation', [
        '$resource', function($resource) {
            return $resource(host + '/stories/:id/increment-praise/:praise', {}, {
                increment : {
                    method : 'POST'
                },
            });
        }
]);

coAuthorServices.factory('EntryOperation', [
        '$resource', function($resource) {
            return $resource(host + '/stories/:storyId/entries/:entryId/:op', {}, {
                vote : {
                    method : 'POST',
                    params : {
                        op : 'vote'
                    }
                }
            });
        }
]);