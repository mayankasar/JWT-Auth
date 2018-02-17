(function() {
	var app = angular.module('Authentication', [ "ngRoute", "ngMaterial", "angular-jwt" ]);

	app.config([ '$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {

		$routeProvider.when("/", {
			templateUrl : "pages/home.htm"
		})
		.when("/home", {
			templateUrl : "pages/home.htm"
		})
		.when("/login", {
			templateUrl : "pages/login.htm"
		})
		.when("/signup", {
			templateUrl : "pages/signup.htm"
		})
		.otherwise({
			redirectTo: "/"
		});

		if (window.history && window.history.pushState) {
			$locationProvider.html5Mode({
				enabled : true,
				requireBase : false
			});
		};

	}]);


})();
