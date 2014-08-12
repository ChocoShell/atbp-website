var myApp = angular.module('myApp', ['ngSanitize']);

// (Down)loads local Json file 
var json = (function () {
  var json = null;
  $.ajax({
    'async': false,
    'global': false,
    'url': "/assets/files/atbp.json",
    'dataType': "json",
    'success': function (data) {
      json = data;
    }
  });
  return json;
})();

// Makes JSON data available to controllers
myApp.factory('DataService', function() {
  return json;
});

// Main Characters Page
myApp.directive('heroTab', function() {
  return {
    restrict: 'A',
    templateUrl: '/assets/templates/heroes.html'
  }
});

// For the Build/Backpack page
myApp.directive('backpackTab', function() {
  return {
    restrict: 'A',
    templateUrl: '/assets/templates/backpack.html'
  }
});

  