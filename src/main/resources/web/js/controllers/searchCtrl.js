angular.module('app.controllers', ['rzModule']).controller('searchCtrl', function ($scope, searchDataService) {
  $scope.search = "";
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

  $scope.loading = false
  $scope.results = []
  $scope.allresults = []

  function doSearch() {
    $scope.loading = true
    searchDataService.search($scope.search, $scope.slider.minValue, $scope.slider.maxValue)
    .then(function onSuccess(response) {
      var results = response.data.results;
      console.log(results);
      $scope.allresults = response.results;
      var allItems = [].concat.apply([], results.map(x => x.items));
      $scope.results = allItems;
    }).finally(function () {
      $scope.loading = false;
    });
  }
});