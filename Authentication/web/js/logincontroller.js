(function() {
  var app = angular.module('Authentication');

  app.controller("LoginController", function($location, dataService, loginService, jwtHelper, $sce) {
    var login = this;
    this.loginService = loginService;

    this.apiResponse = "Click Ping Button to Ping Backend API";
    this.apiResponse = $sce.trustAsHtml(login.apiResponse);

    this.redirectToLoginPage = function() {
      $location.path("/login");
    };

    this.redirectToSignUpPage = function() {
      $location.path("/signup");
    };

    this.signUp = function() {

      var requestData = {
        username: login.username,
        password: login.password
      };

      login.username = undefined;
      login.password = undefined;

      var onSuccess = function(response) {
        login.signUpMessage = response.data.message;
      };

      var onFail = function(response) {
        console.log("Failed:" + response);
      };

      dataService.sendRequest("/signup", requestData, onSuccess, onFail);
    };

    this.login = function() {
      var requestData = {
        type: "jwt",
        uid: login.uid,
        pwd: login.pwd
      };

      login.uid = undefined;
      login.pwd = undefined;

      var onSuccess = function(response) {
        if (response.data.resultCode == 0) {
          loginService.onLoginSuccess(response.data.accessToken, response.data.refreshToken);
        }

        login.error = response.data.message;
      };

      var onFail = function(response) {
        console.log("Failed:" + response);
      };

      dataService.sendRequest("/login", requestData, onSuccess, onFail);
    };

    this.logout = function() {
      var requestData = {};

      var onSuccess = function(response) {
        loginService.onLogout();
      };

      var onFail = function(response) {
        console.log("Failed:" + response);
      };

      dataService.sendRequest("/logout", requestData, onSuccess, onFail);
    };

    this.onPingClick = function() {
      var requestData = {
        type: "jwt"
      };

      var onSuccess = function(response) {
        login.apiResponse = "ResultCode:" + response.data.resultCode + ", Message:" + response.data.message;
        login.apiResponse = $sce.trustAsHtml(login.apiResponse);
      };

      var onFail = function(response) {
        console.log("Failed:" + response);
      };

      dataService.sendRequest("/ping", requestData, onSuccess, onFail);
    };

    this.viewAccessToken = function() {
      if (loginService.isAuthenticated()) {
        var accessToken = loginService.getAccessToken();
        var payload = jwtHelper.decodeToken(accessToken);

        var tokenPayload = JSON.stringify(payload);
        tokenPayload = tokenPayload.replace(new RegExp("[{]", 'g'), "{<br>");
        tokenPayload = tokenPayload.replace(new RegExp("[,]", 'g'), ",<br>");
        tokenPayload = tokenPayload.replace(new RegExp("[}]", 'g'), "<br>}");

        var expiry = new Date(payload.exp * 1000);
        var issuedAt = new Date(payload.iat * 1000);

        login.apiResponse = "<pre style='text-align:left'>" + tokenPayload + "</pre>" +
          "<br>iat : " + issuedAt.getDate() + "-" + issuedAt.getMonth() + "-" + issuedAt.getFullYear() + " " + issuedAt.getHours() + ":" + issuedAt.getMinutes() +
          "<br>exp : " + expiry.getDate() + "-" + expiry.getMonth() + "-" + expiry.getFullYear() + " " + expiry.getHours() + ":" + expiry.getMinutes();

      } else {
        login.apiResponse = "No Token Found in Local Storage";
      }

      this.apiResponse = $sce.trustAsHtml(login.apiResponse);
    };

    this.refreshToken = function() {

      if (loginService.isAuthenticated()) {
        var requestData = {
          refreshToken: loginService.getRefreshToken()
        };

        var onSuccess = function(response) {
          if (response.data.resultCode == 0) {
            loginService.onRefreshSuccess(response.data.accessToken);
          }

          login.apiResponse = "ResultCode:" + response.data.resultCode + ", Message:" + response.data.message;
          login.apiResponse = $sce.trustAsHtml(login.apiResponse);
        };

        var onFail = function(response) {
          console.log("Failed:" + response);
        };

        dataService.sendRequest("/refresh", requestData, onSuccess, onFail);
      }
      else{
        login.apiResponse = "No Token Found in Local Storage";
        login.apiResponse = $sce.trustAsHtml(login.apiResponse);
      }
    };

  });


})();
