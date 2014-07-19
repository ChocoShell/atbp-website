(function() {
  var myApp = angular.module('myApp', []);
  //var data = JSON.parse('public/files/atbp.json');

  myApp.controller('CrudCtrl', function($scope, $http) {
    $http.get('/assets/files/atbp.json')
      .then(function(res){
        $scope.crud = res.data;
        console.log($scope.crud);
      });
  });

  myApp.controller('MainController', function() {
    this.message = 'Whatup';
  });

  myApp.controller('BeltController', ['$http', '$scope', function($http, $scope){
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
    this.belts       = json.Belts;
    this.junk        = json.Junk;
    this.beltJson    = this.belts[3].belt_champions[0].belt;
    this.belt        = "champions";

    this.getJunk = function(junkList) {
      var junkVar = [];
      for(var junkName in junkList) {
        for(var ind in this.junk) {
          if(Object.keys(this.junk[ind])[0] == junkList[junkName].junk_id) {
            junkVar.push(this.junk[ind]);
          }
        }
      }
      return junkVar;
    }

    $scope.collection = ["Item 1", "Item 2"];

    $scope.selectedIndex = 0; // Whatever the default selected index is, use -1 for no selection

    $scope.itemClicked = function ($index) {
      $scope.selectedIndex = $index;
    };

    this.currentJunk  = this.getJunk(this.beltJson.junk);

    this.isSet = function(checkBag) {
      return this.belt === checkBag;
    };

    this.setBelt = function(setBelt) {
      this.belt = setBelt;
      var key = "belt_" + setBelt;
      for(var ind in this.belts) {
        if(Object.keys(this.belts[ind])[0] == key) {
          this.beltJson = this.belts[ind][key][0].belt;
          this.currentJunk = this.getJunk(this.beltJson.junk);
        }
      }
    };

  }]);

  myApp.controller('TableController', function(){
    this.levels = [false, false, false, false, false, false, false, false, false, false];
  });

  myApp.directive('backpackTab', function() {
    return {
      restrict: 'A',
      templateUrl: '/assets/templates/backpack.html'
    }
  });

})();


  