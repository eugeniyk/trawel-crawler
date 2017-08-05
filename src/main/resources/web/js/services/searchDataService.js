angular.module('app.services', []).
  factory('searchDataService', function($http) {

    var crawlerAPI = {};

    crawlerAPI.search = function(query, min, max, sources) {
      return $http({
        method: 'GET',
        url: 'http://localhost:8080/api/search',
        params: { "q": query, "priceMin": min, "priceMax": max, "sources": sources }
      });
//      return $http("/api/search?q=" + query);
    }

    return crawlerAPI;
  });