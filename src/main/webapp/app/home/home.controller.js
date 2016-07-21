(function() {
    'use strict';

    angular
        .module('frontendApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService','ProductSearch' ,'$state'];

    function HomeController ($scope, Principal, LoginService,ProductSearch, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }



            $scope.search = function(){

                    vm.q = $scope.searchString;

                    if (vm.q.length > 1) {

                        $scope.results = ProductSearch.query({query:vm.q});
                    }
            }



    }
})();
