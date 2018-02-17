(function() {
  var app = angular.module('Authentication');

  app.service('dataService', function($window, $http, loginService) {

    this.sendRequest = function(uri, requestData, successCallback, failCallback) {

      var headersValue = {'Content-Type': 'application/json'};

      if(loginService.getAccessToken() != undefined ){
        headersValue = {
          'Content-Type': 'application/json',
          'Authorization' : 'Bearer '+ loginService.getAccessToken()
        };
      }

      $http({
        url: '/auth'+uri,
        method: 'POST',
        headers: headersValue,
        data: requestData
      }).then(function(response) {
        successCallback(response);
      }, function(response) {
        failCallback(response);
      });
    };

  });

  app.service('loginService', function($window, $http, jwtHelper) {

    this.authData = {
      isAuthenticated: false,
      isLoaded: false
    };

    this.loadData = function() {
      if (this.authData.isLoaded == false && $window.localStorage.authData != undefined) {
        this.authData = JSON.parse($window.localStorage.authData);
        this.authData.isLoaded = true;
      }
    };

    this.saveData = function() {
      $window.localStorage.authData = JSON.stringify(this.authData);
    };

    this.onLoginSuccess = function(accessToken, refreshToken){
        var tokenPayload = jwtHelper.decodeToken(accessToken);

        this.authData.accessToken = accessToken;
        this.authData.refreshToken = refreshToken;
        this.authData.isAuthenticated = true;
        this.authData.username = tokenPayload.username;
        // var date = jwtHelper.getTokenExpirationDate(accessToken);
        // var bool = jwtHelper.isTokenExpired(accessToken);
        this.saveData();
    };

    this.onRefreshSuccess = function(accessToken){
        var tokenPayload = jwtHelper.decodeToken(accessToken);

        this.authData.accessToken = accessToken;
        this.authData.isAuthenticated = true;
        this.authData.username = tokenPayload.username;

        this.saveData();
    };

    this.isAuthenticated = function(){
        this.loadData();
        return this.authData.isAuthenticated;
    };

    this.getAccessToken = function(){
      this.loadData();
      return this.authData.accessToken;
    };

    this.getRefreshToken = function(){
      this.loadData();
      return this.authData.refreshToken;
    };

    this.onLogout=function(){

      $window.localStorage.removeItem("authData");

      this.authData = {
        isAuthenticated: false,
        isLoaded: false
      };
    };

  });

})();
