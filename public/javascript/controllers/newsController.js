myApp.controller('NewsController', function($http, $scope, $sce) {
  // Pulls submitted data from bradido's reddit account
  news = (function () {
    var news = null;
    $.ajax({
      'async': false,
      'global': false,
      'url': "http://www.reddit.com/user/bradido/submitted.json",
      'dataType': "json",
      'success': function (data) {
        news = data;
      }
    });
    return news;
  })();

  var toUTCDate = function(date){
    var _utc = new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),  date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
    return _utc;
  };

  // Searches for the latest Post with the phrase Patch Notes and decodes the HTML symbols into proper html.
  $scope.latestPatch = (function() {
    for( var i = 0; i < news.data.children.length; i++) {
      if(news.data.children[i].data.title.search("Patch ") > -1) {
        var title = news.data.children[i].data.title;

        var created = new Date(parseInt(news.data.children[i].data.created)*1000);

        var patch = news.data.children[i].data.selftext_html.replace(/&lt;/g, '<');
        patch = patch.replace(/&gt;/g, '>').replace(/#39;/g, "'").replace(/nbsp;/g, "");
        patch = patch.replace(/&amp;/g, "").replace(/amp;/g, "&").replace(/quot;/g, '"');
        
        var link = news.data.children[i].data.url;
        return { 'title': title, 'news': patch, 'link': link, 'raw': news, 'created': created};
      }
    }
  })();
});