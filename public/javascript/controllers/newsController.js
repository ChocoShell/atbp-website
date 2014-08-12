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

  // Searches for the latest Post with the phrase Patch Notes and decodes the HTML symbols into proper html.
  $scope.latestPatch = (function() {
    for( var i = 0; i < news.data.children.length; i++) {
      if(news.data.children[i].data.title.search("Patch Notes") > -1) {
        var patch = news.data.children[i].data.selftext_html.replace(/&lt;/g, '<');
        patch = patch.replace(/&gt;/g, '>').replace(/#39;/g, "'").replace(/nbsp;/g, "");
        patch = patch.replace(/&amp;/g, "").replace(/amp;/g, "&").replace(/quot;/g, '"');
        // Adds link to reddit Patch Notes thread
        patch = patch.replace("<p><strong>", "<p><strong><a href='"+news.data.children[i].data.url+"'>");
        patch = patch.replace("</strong></p>", "</a></strong><p>");
        link = news.data.children[i].data;
        return { 'news': patch, 'link': link};
      }
    }
  })();
});