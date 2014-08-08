myApp.controller('TwitchController', function($http, $scope) {

    $scope.streams = [];
    $scope.init = function () {
      $scope.streams = [];
      $http.jsonp("https://api.twitch.tv/kraken/search/streams?q=%22adventure%20time%20battle%20party%22&callback=JSON_CALLBACK").success(
        function(data) {
          $scope.streams = data.streams;
        }).error(function(data) {});
    }

  });
