myApp.controller('BeltController', ['$scope', 'DataService', function($scope, DataService){
  
  this.belts    = DataService.Belts;
  this.junk     = DataService.Junk;
  this.beltJson = this.belts[3].belt_champions[0].belt;
  this.belt     = "champions";

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