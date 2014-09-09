var coAuthorControllers = angular.module('coAuthorControllers', []);

coAuthorControllers.controller('mainController', [
        '$interval', '$cookies', '$scope', '$routeParams', '$http', 'Schemas', 'Story', 'StoryOperation', 'EntryOperation', 'PraisesOperation',
        function($interval, $cookies, $scope, $routeParams, $http, Schemas, Story, StoryOperation, EntryOperation, PraisesOperation) {

            $scope.storyForCreateModel = {};
            $scope.entryRequestModel = {};

            $scope.loggedIn = ($cookies.Authorization != undefined);

            $http.defaults.headers.common['TimeZoneOffsetMinutes'] = new Date().getTimezoneOffset();
            $http.defaults.headers.common['Authorization'] = $cookies.Authorization;

            $scope.storyFilter = null;
            $scope.modalContent = null;
            $scope.currStory = null;

            var cursorInterval = null;
            var cursorInited = false;

            // - put this in the main.html somewhere
            getTopViewStories();
            getPraisesSchema();

            $scope.praisesSchema = null;

            function getPraisesSchema() {
                Schemas.getPraises(function(res) {
                    $scope.praisesSchema = res;
                });
            }

            $scope.incrementPraise = function(key) {
                PraisesOperation.increment({
                    id : $scope.currStory.id,
                    praise : key
                }, null, function(res) {
                    $scope.currStory['praises'] = res;
                });
            }

            $scope.clickedTextArea = function() {
                resetPotentialEntriesState();

                $("#storyBody").animate({
                    scrollTop : $(window).scrollTop() + $(window).height()
                }, 0);
            }

            $scope.countDownPotentialEntries = function() {
                if ($scope.currStory.nextEntryAvailableAt != undefined) {
                    $scope.currStoryCountdown = countdown(null, $scope.currStory.nextEntryAvailableAt, countdown.MINUTES | countdown.SECONDS, 0).toString();
                    $scope.pickEntryCounter = $interval(function() {
                        if (new Date().getTime() > $scope.currStory.nextEntryAvailableAt) {
                            $interval.cancel($scope.pickEntryCounter);
                            $scope.pickEntry($scope.currStory.id);
                        }

                        $scope.currStoryCountdown = countdown(null, $scope.currStory.nextEntryAvailableAt, countdown.MINUTES | countdown.SECONDS, 0).toString();
                    }, 1000);
                } else {
                    if (new Date().getTime() > $scope.currStory.nextEntryAvailableAt) {
                        $interval.cancel($scope.pickEntryCounter);
                        $scope.pickEntry($scope.currStory.id);
                    }
                }
            }

            $scope.pickEntry = function(storyId) {
                StoryOperation.pickEntry({
                    id : storyId
                }, null, function(res) {
                    $scope.currStory = res;
                    $("#storyBody").animate({
                        scrollTop : $(window).scrollTop() + $(window).height()
                    }, 0);
                });
            }

            $scope.requestEntry = function() {
                var storyId = $scope.currStory.id;

                $("#nextEntry").text("");

                $("#submitEntryButton").prop("disabled", true);

                StoryOperation.requestEntry({
                    id : storyId
                }, $scope.entryRequestModel, function(res) {
                    $scope.entryRequestModel.entry = null;
                    $('#entryRequestTextArea').val("");
                    $scope.currStory = res;
                    $scope.countDownPotentialEntries();
                    setRequestEntryValidation();
                });
            };

            $scope.voteForEntry = function(storyID, entryID) {
                EntryOperation.vote({
                    storyId : storyID,
                    entryId : entryID
                }, null, function(res) {
                    Story.getStory({
                        type : storyID
                    }, function(res) {
                        $scope.currStory = res;
                    });
                });
            }

            $scope.showGetStoryModal = function(storyId, index) {
                $scope.currStoryIndex = index;

                $scope.modalContent = 'modalLoading';
                $("#modal").modal();

                StoryOperation.incrementViews({
                    id : storyId
                }, null);

                Story.getStory({
                    type : storyId
                }, function(res) {
                    $scope.currStory = res;
                    $scope.modalContent = 'viewStory';

                    $scope.countDownPotentialEntries();
                });

                if ($scope.entryRequestSchemaDisplay == null) {
                    Schemas.getSchemaForEntryRequest(function(res) {
                        $scope.entryRequestSchema = res;
                        $scope.entryRequestSchemaDisplay = getSchemaDisplay(res);

                        setRequestEntryValidation();
                    });
                } else {
                    setRequestEntryValidation();
                }
            };

            function setRequestEntryValidation() {
                $scope.$watch('modalContent', function(newVal, oldValue) {
                    if (newVal === 'viewStory') {
                        $("#storyBody").css("max-height", window.screen.height - 600);
                        bindCharsRemaining($scope.entryRequestSchema['entry'].maxLength, '#entryRequestCharsRemaining', '#entryRequestTextArea');
                        bindCharsRequired($scope.entryRequestSchema['entry'].minLength, '#entryRequestCharsRequired', '#entryRequestTextArea');
                    }
                });
            }
            ;

            function setNewStoryValidation() {
                $scope.$watch('modalContent', function(newVal, oldValue) {
                    if (newVal === 'newStory') {
                        bindCharsRemaining($scope.storySchemaForCreate['entry'].maxLength, '#newStoryCharsRemaining', '#newStoryTextarea');
                        bindCharsRequired($scope.storySchemaForCreate['entry'].minLength, '#newStoryCharsRequired', '#newStoryTextarea');
                    }
                });
            }
            ;

            $scope.selectedStoryFilter = function(v) {
                $scope.storyFilter = v;
            };

            $scope.showNewStoryModal = function loadNewStorySchemaFn() {
                $scope.modalContent = 'modalLoading';
                $("#modal").modal();

                if ($scope.storySchemaForCreateDisplay == null) {
                    Schemas.getSchemaForCreate(function(res) {
                        $scope.storySchemaForCreate = res;
                        $scope.storySchemaForCreateDisplay = getSchemaDisplay(res);

                        $scope.modalContent = 'newStory';
                        setNewStoryValidation();
                    });
                } else {
                    $scope.modalContent = 'newStory';
                    setNewStoryValidation();
                }
            };

            $scope.createStory = function createStoryFn() {
                Story.create($scope.storyForCreateModel, function(res) {
                    $('#modal').modal('hide');
                    $scope.storyForCreateModel = {};
                    getTopViewStories();
                });
            }

            function getTopViewStories() {
                Story.getTopViewStories(function(res) {
                    $scope.stories = res;
                });
            }

            $scope.getPublicStoryClass = function() {
                if ($scope.loggedIn) {
                    return "col-md-6";
                } else {
                    return "col-md-12";
                }
            }

            $scope.initStoryTextArea = function() {
                $("#submitEntryButton").prop("disabled", true);

                $('#entryRequestTextArea').keyup(function() {
                    $scope.submitStoryCallback();
                });
            }

            $scope.submitStoryCallback = function() {
                var text = $('#entryRequestTextArea').val();

                if ($scope.entryRequestSchemaDisplay == null) {
                    Schemas.getSchemaForEntryRequest(function(res) {
                        $scope.entryRequestSchema = res;
                        $scope.entryRequestSchemaDisplay = getSchemaDisplay(res);
                    });
                }

                if (text.length >= $scope.entryRequestSchema['entry'].minLength) {
                    $("#submitEntryButton").prop("disabled", false);
                } else {
                    $("#submitEntryButton").prop("disabled", true);
                }

                $('#nextEntry').text(text);
                $scope.entryRequestModel['entry'] = text;

                $("#storyBody").animate({
                    scrollTop : $(window).scrollTop() + $(window).height()
                }, 0);
            }

            $scope.initCursor = function() {
                if (cursorInterval == null) {
                    cursorInterval = $interval(function() {
                        if ($("#nextEntryCursor").is(":visible")) {
                            $("#nextEntryCursor").hide();
                        } else {
                            $("#nextEntryCursor").show();
                        }
                    }, 500);
                }
            }

            $scope.ellipse = function(text, len, defaultTo) {
                if (text.length > len) {
                    return text.substring(0, len - 1) + "...";
                } else if (defaultTo) {
                    return text + "...";
                }

                return text;
            }

            var peekEntryId = null;

            $scope.peekPotentialEntry = function(peekEntry) {
                if (peekEntryId == null || peekEntryId != peekEntry.id) {
                    if ($('#nextEntry').hasClass("peek-entry")) {
                        $('#nextEntry').text(peekEntry.entry);
                    } else {
                        $('#nextEntry').text(peekEntry.entry).addClass("peek-entry");
                    }

                    peekEntryId = peekEntry.id;

                    $("#storyBody").animate({
                        scrollTop : $(window).scrollTop() + $(window).height()
                    }, 0);

                    $('.potential-entry-clicked').removeClass("potential-entry-clicked");
                    $('#nextEntry').removeClass("next-entry");
                    $('#potentialEntry' + peekEntryId).addClass("potential-entry-clicked");
                } else {
                    resetPotentialEntriesState();

                    $("#storyBody").animate({
                        scrollTop : $(window).scrollTop() + $(window).height()
                    }, 0);
                }
            }

            function resetPotentialEntriesState() {
                $('#nextEntry').addClass("next-entry");
                $('#nextEntry').removeClass("peek-entry");
                $('#nextEntry').text($('#entryRequestTextArea').val());
                $('.potential-entry-clicked').removeClass("potential-entry-clicked");

                peekEntryId = null;
            }
        }
]);