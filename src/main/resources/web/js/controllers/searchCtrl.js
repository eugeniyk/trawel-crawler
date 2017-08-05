angular.module('app.controllers', ['ui.bootstrap', 'rzModule']).controller('searchCtrl', function ($scope, searchDataService) {
  $scope.search = "";
  $scope.source = {
    craiglist: true,
    kaidee: true
  };
  $scope.doSearch = doSearch;
  $scope.slider = {
    minValue: 30000,
    maxValue: 60000,
    options: {
      floor: 10000,
      ceil: 100000,
      step: 1000
    }
  };

  $scope.imgs = [
    "http://images.craigslist.org/00A0A_bwgBsNp7bye_300x300.jpg",
    "http://images.craigslist.org/00c0c_aWvUUirRtC_300x300.jpg",
    "http://images.craigslist.org/00n0n_5DLs8V42qQG_300x300.jpg"
  ]

  $scope.loading = false
  $scope.results = []
  $scope.allresults = []

  function doSearch() {
    $scope.loading = true
    var sources = Object.keys($scope.source).filter(key => $scope.source[key])
    searchDataService.search($scope.search, $scope.slider.minValue, $scope.slider.maxValue, sources)
    .then(function onSuccess(response) {
      var results = response.data.results;
      results.forEach(function(result) {
          result.items.forEach(function(item) {
            item.lastDate = Date.parse(item.lastDate)
          })
      });
      console.log(results);

      $scope.allresults = results;
      var allItems = [].concat.apply([], results.map(x => x.items));
      $scope.results = allItems;
    }).finally(function () {
      $scope.loading = false;
    });
  }
});