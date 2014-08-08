var myApp = angular.module('myApp', []);

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

myApp.factory('DataService', function() {
  return json;
});

myApp.directive('heroTab', function() {
  return {
    restrict: 'A',
    templateUrl: '/assets/templates/heroes.html'
  }
});

myApp.directive('backpackTab', function() {
  return {
    restrict: 'A',
    templateUrl: '/assets/templates/backpack.html'
  }
});

  